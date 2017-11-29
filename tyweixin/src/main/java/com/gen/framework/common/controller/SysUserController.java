package com.gen.framework.common.controller;

import com.gen.framework.common.beans.SysMenuBean;
import com.gen.framework.common.beans.SysRoleBean;
import com.gen.framework.common.beans.SysUserBean;
import com.gen.framework.common.services.SysManagerService;
import com.gen.framework.common.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/sys")
public class SysUserController {
    private final Logger logger = LoggerFactory.getLogger(SysUserController.class);
    @Autowired
    private SysManagerService sysManagerService;

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
    public String toMemuEdit(String rId,Model model){
        try {

        }catch (Exception e){
            logger.error("SysUserController->toMemuEdit",e);
        }
        return "pages/manager/system/menuEdit";
    }
    @PostMapping("/menu/do-create-menu")
    @ResponseBody
    public ResponseVO doCreateMenu(SysMenuBean sysMenuBean){
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
}
