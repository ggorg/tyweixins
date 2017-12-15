package com.ty.timer;

import com.gen.framework.common.vo.ResponseVO;
import com.ty.config.Globals;
import com.ty.services.MessageService;
import com.ty.services.TyRedPacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TyTimer {

    private final Logger logger = LoggerFactory.getLogger(TyTimer.class);

    @Autowired
    private TyRedPacketService tyRedPacketService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private Globals globals;

    @Scheduled(cron="0 15 12 ? * MON")
    public void pullRedPacketTimer(){
        try {
            logger.info("TyTimer->pullRedPacketTimer->拉取充值红包->开始");
            ResponseVO<Map> responseVO=tyRedPacketService.pullRedPacket();
            if(responseVO.getReCode()==1){
                logger.info("TyTimer->pullRedPacketTimer->拉取充值红包->推送图文到微信公众号->请求->{}",responseVO);
               ResponseVO resMSg= messageService.sendMessage(responseVO.getData(),"充值红包","翼支付回馈用户充值红包",globals.getDefaultPicUrl(),"open-red-packet","");
                logger.info("TyTimer->pullRedPacketTimer->拉取充值红包->推送图文到微信公众号->响应->{}",resMSg);
            }else{
                logger.info("TyTimer->pullRedPacketTimer->拉取充值红包->{}",responseVO);
            }
        }catch (Exception e){
            logger.error("TyTimer->pullRedPacketTimer->系统异常",e);
        }
    }
}
