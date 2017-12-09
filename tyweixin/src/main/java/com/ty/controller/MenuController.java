package com.ty.controller;

import com.gen.framework.common.vo.ResponseVO;
import com.ty.entity.Menu;
import com.ty.entity.Pubweixin;
import com.ty.services.MenuService;
import com.ty.services.PubWeixinService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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
            //TODO 从数据库取出该公众号配置好的菜单,组成json调用微信接口生成菜单
            List<Menu> menuList = menuService.findList(appid);
            for(Menu menu:menuList){

            }
            return new ResponseVO(1,"菜单发布成功",null);
        } catch (Exception e) {
            logger.error("MenuController->release->系统异常",e);
            return new ResponseVO(-1,"菜单发布失败",null);
        }
    }
}
