package com.ty.controller;

import com.gen.framework.common.vo.ResponseVO;
import com.ty.entity.Menu;
import com.ty.services.MenuService;
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

    @RequestMapping(value = {"list", ""})
    public String list(String appid, Model model) {
        List<Menu> list =  menuService.findList(appid);
        model.addAttribute("menuList",list);
        return "pages/manager/weixin/menu";
    }

    @GetMapping("edit")
    public String edit(Integer id,Model model){
        try {
            if(id != null && id >0){
                model.addAttribute("menu",this.menuService.selectById(id));
            }
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
}
