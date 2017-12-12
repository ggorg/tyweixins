package com.gen.framework.common.controller;

import com.gen.framework.common.beans.SysMenuBean;
import com.gen.framework.common.beans.SysRoleBean;
import com.gen.framework.common.beans.SysUserBean;
import com.gen.framework.common.services.SysManagerService;
import com.gen.framework.common.util.Tools;
import com.gen.framework.common.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys")
public class SysManagerController {
    private final Logger logger = LoggerFactory.getLogger(SysManagerController.class);
    @Autowired
    private SysManagerService sysManagerService;
    @GetMapping
    public String toIndex(){
        return "pages/manager/system/blankFrame";
    }
    @GetMapping("/user/to-list")
    public String toUserList(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("userPage",this.sysManagerService.getUserPage(pageNo));
        }catch (Exception e){
            logger.error("SysUserController->toUserList",e);
        }
        return "pages/manager/system/user";
    }
    @PostMapping("/user/do-edit-user")
    @ResponseBody
    public ResponseVO doEditUser(SysUserBean sysUserBean){
        try {
            return this.sysManagerService.saveUser(sysUserBean);
        }catch (Exception e){
            logger.error("SysUserController->doCreateUser->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }
    }

    @GetMapping("/user/to-edit")
    public String toUserEdit(Integer uId, Model model){
        try {
            model.addAttribute("userObject",this.sysManagerService.getUserById(uId));
        }catch (Exception e){
            logger.error("SysUserController->toUserEdit->系统异常",e);

        }

        return "pages/manager/system/userEdit";
    }

    @PostMapping("/user/do-disabled-user")
    @ResponseBody
    public ResponseVO doDisabledUser(SysUserBean sysUserBean){
        try {
            return this.sysManagerService.disabledUser(sysUserBean);
        }catch (Exception e){
            logger.error("SysUserController->doDisabledUser->系统异常",e);
            return new ResponseVO(-1,"操作失败",null);
        }
    }

    @GetMapping("/role/to-edit")
    public String toRoleEdit(Integer rId,Model model){
        try {
            model.addAllAttributes(this.sysManagerService.getRoleById(rId));
        }catch (Exception e){
            logger.error("SysUserController->toRoleEdit->系统异常",e);

        }
        return "pages/manager/system/roleEdit";
    }

    @GetMapping("/role/to-list")
    public String toRoleList(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("rolePage",this.sysManagerService.getRolePage(pageNo));
        }catch (Exception e){
            logger.error("SysUserController->toRoleList",e);
        }
        return "pages/manager/system/role";
    }
    @PostMapping("/role/do-edit-role")
    @ResponseBody
    public ResponseVO doEditRole(SysRoleBean sysRoleBean, Integer[] uId){
        try {
            return this.sysManagerService.saveRole(sysRoleBean,uId);
        }catch (Exception e){
            logger.error("SysUserController->doCreateRole->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }

    }


    @GetMapping("/menu/to-list")
    public String toMemuList(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("menuPage",this.sysManagerService.getMenuPage(pageNo));
        }catch (Exception e){
            logger.error("SysUserController->toMemuList",e);
        }
        return "pages/manager/system/menu";
    }
    @GetMapping("/menu/to-edit")
    public String toMmuEdit(Integer id,Model model){
        try {
            if(id!=null && id>0)model.addAttribute("menuObject",this.sysManagerService.getMenuById(id));
        }catch (Exception e){
            logger.error("SysUserController->toMemuEdit",e);
        }
        return "pages/manager/system/menuEdit";
    }
    @PostMapping("/menu/do-edit-menu")
    @ResponseBody
    public ResponseVO doEditMenu(SysMenuBean sysMenuBean){
        try {
            return this.sysManagerService.saveMenu(sysMenuBean);
        }catch (Exception e){
            logger.error("SysUserController->doCreateMenu->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }

    }

    @GetMapping("/role/to-menu-power")
    public String toMenuPower(Integer rId,Model model){
        try {
           // model.addAttribute("menuPage",this.sysManagerService.getAllMenu());
            model.addAttribute("menuPage",this.sysManagerService.handlePower(rId));
        }catch (Exception e){
            logger.error("SysUserController->toMenuPower->系统异常",e);
        }
        return "pages/manager/system/rolePower";
    }
    @PostMapping("/role/do-power")
    @ResponseBody
    public ResponseVO doPower(Integer rId,Integer[] mId){
        try{
            return this.sysManagerService.savePower(rId,mId);
        }catch (Exception e){
            logger.error("SysUserController->doPower->系统异常",e);
            return new ResponseVO(-1,"授权失败",null);
        }
    }
    @GetMapping("/to-login")
    public String toLogin(){
        return "pages/manager/system/login";
    }

    @GetMapping("/to-logout")
    public String toLogout(){
        Tools.clearLoginSession();
        return "redirect:/";
    }
    @PostMapping("/do-login")
    @ResponseBody
    public ResponseVO doLogin(SysUserBean sysUserBean){

        try{
            ResponseVO<Map> vo=this.sysManagerService.login(sysUserBean);
            if(vo.getReCode()!=1){
                return vo;
            }
            Tools.setSession("user",vo.getData());
            List<SysMenuBean> menus=this.sysManagerService.getPowerMenu((Integer)vo.getData().get("id"));
            Tools.setSession("userPower",menus);
            Map jumpMap=new HashMap();
            jumpMap.put("jumpUrl",menus.get(0).getmUrl());
            vo.setData(jumpMap);
            return vo;
        }catch (Exception e){
            logger.error("SysUserController->doLogin->系统异常",e);
            return new ResponseVO(-1,"登录失败",null);
        }
    }
}
