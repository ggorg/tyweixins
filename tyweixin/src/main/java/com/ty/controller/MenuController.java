package com.ty.controller;

import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.core.pojo.Button;
import com.ty.core.pojo.CommonButton;
import com.ty.core.pojo.ComplexButton;
import com.ty.core.pojo.ViewButton;
import com.ty.entity.Menu;
import com.ty.entity.Pubweixin;
import com.ty.services.MenuService;
import com.ty.services.PubWeixinService;
import com.ty.services.WeixinInterfaceService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 微信自定义菜单
 * Created by Jacky on 2017/12/4.
 */
@Controller
@RequestMapping(value = "weixin/menu")
public class MenuController {
    private static final Logger logger = Logger.getLogger("MenuController");
    @Autowired
    private MenuService menuService;
    @Autowired
    private PubWeixinService pubWeixinService;
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;

    @RequestMapping(value = {"list", ""})
    public String list(String appid, Model model) {
        List<Pubweixin>pubweixinList = pubWeixinService.findPubweixinAll();
        if(appid == null || appid.equals("")){
            if(pubweixinList.size()>0){
                appid = pubweixinList.get(0).getAppid();
            }
        }
        List<Menu> list =  menuService.findList(appid);
        model.addAttribute("menuList",list);
        model.addAttribute("appid",appid);
        model.addAttribute("pubweixinList",pubweixinList);
        return "pages/manager/weixin/menu";
    }

    @GetMapping("edit")
    public String edit(Integer id,Integer parent_id,String appid,Model model){
        try {
            if(id != null && id >0){
                model.addAttribute("menuObject",this.menuService.selectById(id));
            }else if(parent_id != null && parent_id != -1){
                Menu parent_menu = this.menuService.selectById(parent_id);
                Menu menu = new Menu();
                menu.setParent_id(parent_id);
                appid = parent_menu.getAppid();
                menu.setAppid(appid);
                model.addAttribute("menuObject",menu);
            }else if(parent_id == -1){
                Menu menu = new Menu();
                menu.setParent_id(parent_id);
            }
            model.addAttribute("appid",appid);
        }catch (Exception e){
            logger.error("MenuController->edit->系统异常",e);

        }
        return "pages/manager/weixin/menuEdit";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public ResponseVO save(Menu menu, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            return menuService.saveOrUpdate(menu);
        } catch (Exception e) {
            logger.error("MenuController->save->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ResponseVO delete(Menu menu, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            return this.menuService.delete(menu);
        } catch (Exception e) {
            logger.error("MenuController->delete->系统异常",e);
            return new ResponseVO(-1,"删除失败",null);
        }
    }
    @RequestMapping(value = "release")
    @ResponseBody
    public ResponseVO release(String appid, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 从数据库取出该公众号配置好的菜单,组成json调用微信接口生成菜单
            List<Menu> menuList = menuService.findList(appid);
            JSONObject jsonObject = new JSONObject();
            com.ty.core.pojo.Menu m = new com.ty.core.pojo.Menu();
            //父级菜单
            List<Button>list = new ArrayList<Button>();
            for(Menu menu:menuList){
                if(menu.getParent_id()==-1){
                    List<Menu> submenuList= menuService.findListById(menu.getId());
                    if(submenuList.size()>0){
                        ComplexButton btn = new ComplexButton();
                        btn.setName(menu.getName());
                        //子菜单
                        List<Button>sublist = new ArrayList<Button>();
                        for(Menu submenu:submenuList){
                            if(submenu.getType().equals("click")){
                                CommonButton cb = new CommonButton();
                                cb.setName(submenu.getName());
                                cb.setType(submenu.getType());
                                cb.setKey(submenu.getKey());
                                sublist.add(cb);
                            }else if(submenu.getType().equals("view")){
                                ViewButton vbtn = new ViewButton();
                                vbtn.setName(submenu.getName());
                                vbtn.setType(submenu.getType());
                                vbtn.setUrl(submenu.getUrl());
                                sublist.add(vbtn);
                            }
                        }
                        btn.setSub_button((Button[])sublist.toArray(new Button[sublist.size()]));
                        list.add(btn);
                    }else{
                        if(menu.getType().equals("click")){
                            CommonButton btn = new CommonButton();
                            btn.setName(menu.getName());
                            btn.setType(menu.getType());
                            btn.setKey(menu.getKey());
                            list.add(btn);
                        }else if(menu.getType().equals("view")){
                            ViewButton vbtn = new ViewButton();
                            vbtn.setName(menu.getName());
                            vbtn.setType(menu.getType());
                            vbtn.setUrl(menu.getUrl());
                            list.add(vbtn);
                        }
                    }
                }
            }
            m.setButton((Button[])list.toArray(new Button[list.size()]));
            JSONObject js = weixinInterfaceService.createMenu(appid,m);
            //成功返回 {"errcode":0,"errmsg":"ok"}
            //失败返回 {"errcode":40055,"errmsg":"invalid button url domain hint: [lmkCQA0454vr21]"}
            if(js.containsKey("errcode") && js.getInteger("errcode") == 0){
                return new ResponseVO(1,"菜单发布成功",null);
            }else{
                String errmsg = "";
                if(js.containsKey("errmsg")) errmsg = js.getString("errmsg");
                return new ResponseVO(-1,"菜单发布失败"+errmsg,null);
            }
        } catch (Exception e) {
            logger.error("MenuController->release->系统异常",e);
            return new ResponseVO(-1,"菜单发布失败",null);
        }
    }
    /**
     * 组装菜单数据
     *
     * @return
     */
    private static com.ty.core.pojo.Menu getMenu() {
        CommonButton btn11 = new CommonButton();
        btn11.setName("天气预报");
        btn11.setType("click");
        btn11.setKey("11");

        CommonButton btn12 = new CommonButton();
        btn12.setName("公交查询");
        btn12.setType("click");
        btn12.setKey("12");

        CommonButton btn13 = new CommonButton();
        btn13.setName("周边搜索");
        btn13.setType("click");
        btn13.setKey("13");

        CommonButton btn14 = new CommonButton();
        btn14.setName("历史上的今天");
        btn14.setType("click");
        btn14.setKey("14");

        CommonButton btn21 = new CommonButton();
        btn21.setName("歌曲点播");
        btn21.setType("click");
        btn21.setKey("21");

        CommonButton btn22 = new CommonButton();
        btn22.setName("经典游戏");
        btn22.setType("click");
        btn22.setKey("22");

        CommonButton btn23 = new CommonButton();
        btn23.setName("美女电台");
        btn23.setType("click");
        btn23.setKey("23");

        CommonButton btn24 = new CommonButton();
        btn24.setName("人脸识别");
        btn24.setType("click");
        btn24.setKey("24");

        CommonButton btn25 = new CommonButton();
        btn25.setName("聊天唠嗑");
        btn25.setType("click");
        btn25.setKey("25");

        CommonButton btn31 = new CommonButton();
        btn31.setName("Q友圈");
        btn31.setType("click");
        btn31.setKey("31");

        CommonButton btn32 = new CommonButton();
        btn32.setName("电影排行榜");
        btn32.setType("click");
        btn32.setKey("32");

        CommonButton btn33 = new CommonButton();
        btn33.setName("幽默笑话");
        btn33.setType("click");
        btn33.setKey("33");

        ComplexButton mainBtn1 = new ComplexButton();
        mainBtn1.setName("生活助手");
        mainBtn1.setSub_button(new CommonButton[] { btn11, btn12, btn13, btn14 });

        ComplexButton mainBtn2 = new ComplexButton();
        mainBtn2.setName("休闲驿站");
        mainBtn2.setSub_button(new CommonButton[] { btn21, btn22, btn23, btn24, btn25 });

        ComplexButton mainBtn3 = new ComplexButton();
        mainBtn3.setName("更多体验");
        mainBtn3.setSub_button(new CommonButton[] { btn31, btn32, btn33 });

        /**
         * 这是公众号xiaoqrobot目前的菜单结构，每个一级菜单都有二级菜单项<br>
         *
         * 在某个一级菜单下没有二级菜单的情况，menu该如何定义呢？<br>
         * 比如，第三个一级菜单项不是“更多体验”，而直接是“幽默笑话”，那么menu应该这样定义：<br>
         * menu.setButton(new Button[] { mainBtn1, mainBtn2, btn33 });
         */
        com.ty.core.pojo.Menu menu = new com.ty.core.pojo.Menu();
        menu.setButton(new Button[] { mainBtn1, mainBtn2, mainBtn3 });

        return menu;
    }
}
