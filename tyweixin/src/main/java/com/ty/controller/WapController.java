package com.ty.controller;

import com.gen.framework.common.controller.SysManagerController;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.services.ChannelApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/wap")
public class WapController {
    private final Logger logger = LoggerFactory.getLogger(WapController.class);

    @Autowired
    private ChannelApiService channelApiService;
    @GetMapping("/to-bind-telphone")
    public String toBindTelphone(){
        return "pages/wap/bindingPhone";
    }
    @PostMapping("/do-send-vaild-code")
    public ResponseVO doSendVaildCode(String telphone){
        try {
            return this.channelApiService.sendVaildCode(telphone);
        }catch (Exception e){
            logger.error("WapController->doSendVaildCode->系统异常",e);

        }
        return new ResponseVO(-1,"发送失败",null);
    }

    @PostMapping("/do-bind")
    public ResponseVO doBind(String telphone,String code,String openid){
        try {
            ResponseVO res= this.channelApiService.vaildeCode(telphone,code);
            if(res.getReCode()==1){
                //this.channelApiService.bind()
            }
            return this.channelApiService.sendVaildCode(telphone);
        }catch (Exception e){
            logger.error("WapController->doBind->系统异常",e);

        }
        return new ResponseVO(-1,"绑定失败",null);
    }
}
