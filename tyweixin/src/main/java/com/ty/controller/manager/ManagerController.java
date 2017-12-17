package com.ty.controller.manager;

import com.gen.framework.common.vo.ResponseVO;
import com.ty.controller.WapController;
import com.ty.services.TyRedPacketService;
import com.ty.timer.TyTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    private final Logger logger = LoggerFactory.getLogger(ManagerController.class);
    @Autowired
    private TyRedPacketService tyRedPacketService;
    @Autowired
    private TyTimer tyTimer;

    @GetMapping("/to-count-red-packet")
    public String toCountRedPacket(Model model){
        try {
            model.addAttribute("redPacketList",this.tyRedPacketService.countRedPacket());

        }catch (Exception e){
            logger.error("ManagerController->toCountRedPacket->系统异常",e);
        }
        return "pages/manager/count/redpacket";
    }
    @PostMapping("/to-pull-red-packet")
    @ResponseBody
    public ResponseVO toPullRedPacket(){
        try {
            return tyTimer.pullRedPacketTimer();
        }catch (Exception e){
            logger.error("WapController->toPullRedPacket->系统异常",e);
            return new ResponseVO(-1,"红包获取失败",null);
        }

    }
}
