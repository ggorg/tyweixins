package com.ty.controller;

import com.gen.framework.common.util.Tools;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.services.TyBalanceService;
import com.ty.services.TyBindService;
import com.ty.services.TyRedPacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping(value = "/wap")
public class WapController {
    private final Logger logger = LoggerFactory.getLogger(WapController.class);

    @Autowired
    private TyBindService tyBindService;

    @Autowired
    private TyBalanceService tyBalanceService;

    @Autowired
    private TyRedPacketService redPacketService;
    @GetMapping("/to-bind-telphone")
    public String toBindTelphone(){
        Tools.noCachePage();
        return "pages/wap/bindingPhone";
    }
    @PostMapping("/do-send-vaild-code")
    @ResponseBody
    public ResponseVO doSendVaildCode(String telphone, @SessionAttribute("openid")String openid){
        try {
            return this.tyBindService.sendVaildCode(telphone,openid);
        }catch (Exception e){
            logger.error("WapController->doSendVaildCode->系统异常",e);

        }
        return new ResponseVO(-1,"获取验证码失败",null);
    }

    @PostMapping("/do-bind")
    @ResponseBody
    public ResponseVO doBind(String telphone,String code,@SessionAttribute("openid")String openid){
        try {
            ResponseVO res= this.tyBindService.vaildeCode(telphone,code,openid);
            if(res.getReCode()==1){
                return tyBindService.bind(telphone,openid);
                //this.channelApiService.bind()
            }
            return res;
        }catch (Exception e){
            logger.error("WapController->doBind->系统异常",e);

        }
        return new ResponseVO(-1,"绑定失败",null);
    }
    @GetMapping("/to-myself-center")
    @ResponseBody
    public ResponseVO toMyselfCenter(@SessionAttribute("openid")String openid){
        try {
            return this.tyBalanceService.getBalance(openid);
        }catch (Exception e){
            logger.error("WapController->toMyselfCenter->系统异常",e);
        }
        return new ResponseVO(-1,"操作失败",null);
    }
    @GetMapping("/to-balance-detail")
    public String toBalanceDetail(@SessionAttribute("openid")String openid, Model model){
        try {
            model.addAllAttributes((Map)this.tyBalanceService.getBalanceDetail(openid).getData());
        }catch (Exception e){
            logger.error("WapController->toBalanceDetail->系统异常",e);
        }
        return "pages/wap/balanceInquiries";
    }
    @GetMapping("/to-pull-red-packet")
    @ResponseBody
    public ResponseVO  toPullRedPacket(){
        try {
            return redPacketService.pullRedPacket();
        }catch (Exception e){
            logger.error("WapController->toPullRedPacket->系统异常",e);
            return new ResponseVO(-1,"红包获取失败",null);
        }

    }
    @GetMapping("/to-open-red-packet")
    public String toOpenRedPacket(){
        return "pages/wap/cashRedpacket";
    }


    @PostMapping("/do-open-red-packet")
    @ResponseBody
    public ResponseVO doOpenRedPacket(@SessionAttribute("openid")String openid){
        try {
            return this.redPacketService.openRedPacket(openid);
        }catch (Exception e){
            logger.error("WapController->doOpenRedPacket->系统异常",e);
            return new ResponseVO(-1,"红包领取失败",null);
        }
    }
}
