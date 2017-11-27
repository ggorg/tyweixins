package com.gen.framework.common.controller;

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
            model.addAttribute("userPage",this.sysManagerService.getUserList(pageNo));
        }catch (Exception e){
            logger.error("SysUserController->toUserList",e);
        }
        return "pages/manager/system/user";
    }
    @PostMapping("/user/do-create-user")
    @ResponseBody
    public ResponseVO doCreateUser(SysUserBean sysUserBean){
        try {
            return this.sysManagerService.saveUser(sysUserBean);
        }catch (Exception e){
            logger.error("SysUserController->doCreateUser->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }


    }
    @GetMapping("/user/to-edit")
    public String toUserEdit(){
        return "pages/manager/system/userEdit";
    }
}
