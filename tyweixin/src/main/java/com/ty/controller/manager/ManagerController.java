package com.ty.controller.manager;

import com.gen.framework.common.vo.ResponseVO;
import com.ty.entity.TyActivity;
import com.ty.services.TyActivityRedPacketService;
import com.ty.services.TyActivityService;
import com.ty.timer.TyTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    private final Logger logger = LoggerFactory.getLogger(ManagerController.class);
    @Autowired
    private TyActivityRedPacketService tyRedPacketService;
    @Autowired
    private TyActivityService tyActivityService;

    @GetMapping("/to-count-red-packet")
    public String toCountRedPacket(Model model){
        try {
            model.addAttribute("redPacketList",this.tyRedPacketService.countRedPacket());

        }catch (Exception e){
            logger.error("ManagerController->toCountRedPacket->系统异常",e);
        }
        return "pages/manager/count/redpacket";
    }
    @PostMapping("/do-pull-activity")
    @ResponseBody
    public ResponseVO doPullActivity(){
        try {
            return tyActivityService.pullActivity();
        }catch (Exception e){
            logger.error("WapController->toPullRedPacket->系统异常",e);
            return new ResponseVO(-1,"红包获取失败",null);
        }

    }
    @GetMapping("/to-pull-activity")
    public String toPullActivity(Model model,@RequestParam(defaultValue = "1") Integer pageNum){
        try {
            model.addAttribute("actPage",tyActivityService.getActivityPage(pageNum));

        }catch (Exception e){
            logger.error("WapController->toPullActivity->系统异常",e);

        }

        return "pages/manager/activity/activity";
    }
    @GetMapping("/to-activity")
    public String toActivity(String idStr,Model model){
        try {
            model.addAttribute("act",tyActivityService.getActivity(idStr).getData());

        }catch (Exception e){
            logger.error("WapController->toActivity->系统异常",e);

        }

        return "pages/manager/activity/activityEdit";
    }
    @PostMapping("/do-save-activity")
    @ResponseBody
    public ResponseVO saveActivity(Integer taMinCost,Integer taMaxCost,String idStr){
        try {
            return this.tyActivityService.updateActivity(idStr,taMaxCost,taMinCost,false);
        }catch (Exception e){
            logger.error("WapController->saveActivity->系统异常",e);
            return new ResponseVO(-1,"保存失败",null);
        }

    }
    @PostMapping("/do-save-push-activity")
    @ResponseBody
    public ResponseVO savePushActivity(Integer taMinCost,Integer taMaxCost,String idStr){
        try {
            return this.tyActivityService.updateActivity(idStr,taMaxCost,taMinCost,true);
        }catch (Exception e){
            logger.error("WapController->saveActivity->系统异常",e);
            return new ResponseVO(-1,"保存失败",null);
        }
    }
}
