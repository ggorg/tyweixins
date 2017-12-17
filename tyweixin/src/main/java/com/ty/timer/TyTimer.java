package com.ty.timer;

import com.gen.framework.common.task.TimeTaskBase;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.config.Globals;
import com.ty.services.MessageService;
import com.ty.services.TyRedPacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TyTimer implements TimeTaskBase {

    private final Logger logger = LoggerFactory.getLogger(TyTimer.class);

    @Autowired
    private TyRedPacketService tyRedPacketService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private Globals globals;

    // @Scheduled(cron="0/40 * *  * * ? ")
    @Scheduled(cron="0 15 12 * * 1")
    public void execute() {
        pullRedPacketTimer();
    }

    /**
     *
     */


    public ResponseVO pullRedPacketTimer(){
        try {

            logger.info("TyTimer->pullRedPacketTimer->拉取充值红包->开始");
            ResponseVO<Map> responseVO=tyRedPacketService.pullRedPacket();
            if(responseVO.getReCode()==1){
                logger.info("TyTimer->pullRedPacketTimer->拉取充值红包->推送图文到微信公众号->请求->{}",responseVO);
               ResponseVO resMSg= messageService.sendMessage(responseVO.getData(),"充值红包","翼支付回馈用户充值红包",globals.getDefaultPicUrl(),"open-red-packet","");
               logger.info("TyTimer->pullRedPacketTimer->拉取充值红包->推送图文到微信公众号->响应->{}",resMSg);
               if(resMSg.getReCode()==-1){
                   return resMSg;
               }
               return new ResponseVO(1,"执行成功",null);
            }else{
                logger.info("TyTimer->pullRedPacketTimer->拉取充值红包->{}",responseVO);
                return responseVO;
            }

        }catch (Exception e){
            logger.error("TyTimer->pullRedPacketTimer->系统异常",e);
            return new ResponseVO(-1,"执行失败",null);
        }
    }
}
