package com.ty.timer;

import com.gen.framework.common.vo.ResponseVO;
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

    @Scheduled(cron="0 0/1 * * * ?")
    public void pullRedPacketTimer(){
        try {
            ResponseVO<Map> responseVO=tyRedPacketService.pullRedPacket();
            if(responseVO.getReCode()==1){
               // messageService.sendMessage(responseVO.getData(),"充值红包","翼支付大赠送")
            }
        }catch (Exception e){
            logger.error("TyTimer->pullRedPacketTimer->系统异常",e);
        }
    }
}
