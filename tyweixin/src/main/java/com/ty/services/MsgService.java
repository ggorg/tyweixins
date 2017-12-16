package com.ty.services;

import com.gen.framework.common.util.Page;
import com.ty.dao.MsgMapper;
import com.ty.entity.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * 消息管理
 * 
 * @author Jacky
 * 
 */
@Service
public class MsgService {

	@Autowired
	private WeixinInterfaceService weixinInterfaceService;
	@Autowired
	private MsgMapper msgMapper;

	/** 消息入库 */
	public int save(Msg msg) {
		return msgMapper.save(msg);
	}

    /**
     * 根据appid查询关键字回复
     * @param pageNum 页数
     * @param msg 消息实体类
     * @return
     */
    public Page findList(Integer pageNum, Msg msg){
        Page<Msg>page = new Page<Msg>(pageNum,15);
        List<Msg> list = msgMapper.findList(page,msg);
        int total = msgMapper.findListCount(msg);
        page.setResult(list);
        page.setTotal(total);
        return page;
    }

    /**
     * 将公众号回复的信息入库
     * @param appid
     * @param openid
     * @param content
     */
    public void saveMsg(String appid,String openid, String content){
    	Msg msg = new Msg();
    	msg.setAppid(appid);
    	msg.setOpenid(openid);
    	msg.setContent(content);
    	msg.setOfficial("1");
    	msg.setMsgtype("1");
    	msg.setCreateDate(new Date());
    	//回复信息成功则入库
		msgMapper.save(msg);
    }
    
    /**
     * 删除五天前状态为未收藏消息
     * @return
     */
    public int deleteUnCollect(){
    	return msgMapper.deleteUnCollect();
    }
    
    /**
     * 更新历史消息回复状态
     * 
     * @param appid
     * @param openid
     */
    public void markMsg(String appid, String openid) {
        Msg msg = new Msg();
        msg.setAppid(appid);
        msg.setOpenid(openid);
        //更新历史消息回复状态
        msgMapper.markMsg(msg);
    }
}
