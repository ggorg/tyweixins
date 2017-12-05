package com.ty.controller;

import com.gen.framework.common.controller.SysManagerController;
import com.gen.framework.common.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/wap")
public class WapController {
    private final Logger logger = LoggerFactory.getLogger(WapController.class);
    @GetMapping("/to-bind-telphone")
    public String toBindTelphone(){
        return "pages/wap/bindingPhone";
    }
    @PostMapping("/do-send-vaild-code")
    public ResponseVO doSendVaildCode(String telphone){
        try {
            return new ResponseVO(1,"发送成功",null);
        }catch (Exception e){
            logger.error("WapController->doSendVaildCode->系统异常",e);

        }
        return new ResponseVO(-1,"发送失败",null);
    }
}
