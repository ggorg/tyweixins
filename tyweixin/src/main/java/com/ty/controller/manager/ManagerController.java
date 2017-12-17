package com.ty.controller.manager;

import com.ty.controller.WapController;
import com.ty.services.TyRedPacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    private final Logger logger = LoggerFactory.getLogger(ManagerController.class);
    @Autowired
    private TyRedPacketService tyRedPacketService;

    @GetMapping("/to-count-red-packet")
    public String toCountRedPacket(Model model){
        try {
            model.addAttribute("redPacketList",this.tyRedPacketService.countRedPacket());

        }catch (Exception e){
            logger.error("ManagerController->toCountRedPacket->系统异常",e);
        }
        return "pages/manager/count/redpacket";
    }
}
