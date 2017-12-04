package com.ty.services;

import com.ty.dao.PubweixinMapper;
import com.ty.entity.Pubweixin;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 定时任务
 * 
 * @author Jacky
 * 
 */
@Service
@Lazy(false)
@Component
public class TaskJob {

    private static final Logger logger = Logger.getLogger(TaskJob.class);
    
    @Autowired
    private WeixinUserService weixinUserService;
    @Autowired
    private PubweixinMapper pubweixinMapper;
    @Autowired
    private MsgService msgService;

    /** 每天凌晨两点执行更新微信定时任务 */
    @Scheduled(cron = "0 0 2 * * ?")
    public void weixinScheduler() {
        try {
            List<Pubweixin> list = pubweixinMapper.select();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            for (Pubweixin pubweixin : list) {
                weixinUserService.updateOrSaveUser(pubweixin.getAppid()); //更新微信用户资料
                //msgService.deleteUnCollect(); //清除5天前未收藏消息定时任务
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}