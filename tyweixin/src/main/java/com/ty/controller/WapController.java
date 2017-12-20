package com.ty.controller;

import com.gen.framework.common.util.Tools;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.entity.UserInfo;
import com.ty.services.*;
import com.ty.timer.TyTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private TyActivityRedPacketService redPacketService;



    @Autowired
    private TyVoucherService tyVoucherService;

    @Autowired
    private WeixinUserService weixinUserService;

    @Autowired
    private TyTimer tyTimer;
    @GetMapping("/to-bind-telphone")
    public String toBindTelphone(){
        Tools.noCachePage();

        return "pages/wap/bindingPhone";
    }
    @PostMapping("/do-send-vaild-code")
    @ResponseBody
    public ResponseVO doSendVaildCode(String telphone){
        try {
            return this.tyBindService.sendVaildCode(telphone,Tools.getOpenidByThreadLocal());
        }catch (Exception e){
            logger.error("WapController->doSendVaildCode->系统异常",e);

        }
        return new ResponseVO(-1,"获取验证码失败",null);
    }

    @PostMapping("/do-bind")
    @ResponseBody
    public ResponseVO doBind(String telphone,String code){
        try {
            ResponseVO res= this.tyBindService.vaildeCode(telphone,code,Tools.getOpenidByThreadLocal());
            if(res.getReCode()==1){
                return tyBindService.bind(telphone,Tools.getOpenidByThreadLocal());
                //this.channelApiService.bind()
            }
            return res;
        }catch (Exception e){
            logger.error("WapController->doBind->系统异常",e);

        }
        return new ResponseVO(-1,"绑定失败",null);
    }
    @GetMapping("/to-myself-center")
    public String toMyselfCenter(Model model){
        try {

            ResponseVO resBlance=this.tyBalanceService.getBalance(Tools.getOpenidByThreadLocal());
            if(resBlance.getReCode()==1){
                model.addAttribute("blanceSum",resBlance.getData());

            }
            ResponseVO resRp=this.redPacketService.getRedPacketSum(Tools.getOpenidByThreadLocal());
            if(resRp.getReCode()==1){
                model.addAttribute("rpSum",resRp.getData());

            }
            UserInfo user=weixinUserService.selectByopenid(Tools.getOpenidByThreadLocal());
            model.addAttribute("name",user.getNickname());
            if(resBlance.getReCode()==1 && resRp.getReCode()==1){
                Map map=tyBindService.checkIsBind(Tools.getOpenidByThreadLocal());
                if(map!=null){
                    model.addAttribute("bind",map.get("tuTelphone"));

                }
            }


        }catch (Exception e){
            logger.error("WapController->toMyselfCenter->系统异常",e);
        }
        return "pages/wap/personal";
    }
    @GetMapping("/to-balance-detail")
    public String toBalanceDetail( Model model){
        try {
            model.addAllAttributes((Map)this.tyBalanceService.getBalanceDetail(Tools.getOpenidByThreadLocal()).getData());
        }catch (Exception e){
            logger.error("WapController->toBalanceDetail->系统异常",e);
        }
        return "pages/wap/balanceInquiries";
    }

    @GetMapping("/to-open-red-packet")
    public String toOpenRedPacket(Model model){
        try {
            ResponseVO<List> res=this.redPacketService.findRedPacketRecord(Tools.getOpenidByThreadLocal(),false);
            List data=res.getData();
            if(data!=null && !data.isEmpty() && data.size()>0){
                model.addAttribute("rpNum",data.size());
            }else{
                model.addAttribute("rpNum",0);
                model.addAttribute("isOpen",true);
            }

        }catch (Exception e){
            logger.error("WapController->toOpenRedPacket->系统异常",e);
        }

        return "pages/wap/cashRedpacket";
    }


    @GetMapping("/do-open-red-packet")
    public String doOpenRedPacket(Model model){
        try {
            ResponseVO<List> res=this.redPacketService.openRedPacket(Tools.getOpenidByThreadLocal());
            if(res.getReCode()==1){
                model.addAttribute("rpNum",res.getData().size());
                model.addAttribute("isOpen",true);
            }else if(res.getReCode()==-3){
                model.addAttribute("rpNum",0);
                model.addAttribute("isOpen",true);
            }else{
                return toError(model,res.getReMsg());
            }
        }catch (Exception e){
            logger.error("WapController->doOpenRedPacket->系统异常",e);

        }
        return "pages/wap/cashRedpacket";
    }
    @GetMapping("/to-red-packet-record")
    public String toRedPacketRecord(Model model){
        //this.redPacketService.
        try {
            ResponseVO res=this.redPacketService.findRedPacketRecord(Tools.getOpenidByThreadLocal(),true);
            model.addAttribute("rpRecord",res.getData());
        }catch (Exception e){
            logger.error("WapController->toRedPacketRecord->系统异常",e);
           // return toError(model,"系统");
        }
        return "pages/wap/redpacketRecord";
    }
    @GetMapping("/to-voucher")
    public String toVoucher(Model model){
        try {
            model.addAllAttributes(this.tyVoucherService.findVoucheies(Tools.getOpenidByThreadLocal()));
        }catch (Exception e){
            logger.error("WapController->toVoucher->系统异常",e);
        }
        return "pages/wap/voucherQuery";

    }
    @GetMapping("/to-error")
    public String toError(Model model,String msg){
        model.addAttribute("msg",msg);
        return "pages/wap/common/error";
    }
}
