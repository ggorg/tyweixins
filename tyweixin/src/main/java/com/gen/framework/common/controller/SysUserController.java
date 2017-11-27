package com.gen.framework.common.controller;

import com.gen.framework.common.services.SysManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/sys")
public class SysUserController {
    private final Logger logger = LoggerFactory.getLogger(SysUserController.class);
    @Autowired
    private SysManagerService sysManagerService;

    @GetMapping("/user/to-list")
    public String toUserList(@RequestParam(defaultValue = "1") Integer pageNo, ModelAndView model){
        try {
            model.addObject("userPage",this.sysManagerService.getUserList(pageNo));
        }catch (Exception e){
            logger.error("SysUserController->toUserList",e);
        }
        return "pages/manager/system/user";
    }
    @PostMapping("/user/do-create-user")
    public String doCreateUser(){
        return null;
    }
    @GetMapping("/user/to-edit")
    public String toUserEdit(){
        return "pages/manager/system/userEdit";
    }
}
