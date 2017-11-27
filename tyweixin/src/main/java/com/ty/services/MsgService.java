package com.ty.services;

import com.alibaba.fastjson.JSONObject;
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
	 * 修改消息
	 * @param msg
	 * @return
	 */
	public Msg update(Msg msg){
		msgMapper.update(msg);
		return msgMapper.selectById(msg);
	}
    
    /**
     * 消息列表
     * 
     * @param page
     * @param msg
     * @return
     */
    public Page<Msg> findList(Page<Msg> page, Msg msg) {
        // 设置分页参数
        List<Msg> msgList = msgMapper.findList(msg);
        // 执行分页查询
        page.setResult(msgList);
        return page;
    }
    
    /**
     * 回复消息列表
     * 
     * @param page
     * @param msg
     * @return
     */
    public Page<Msg> replyList(Page<Msg> page, Msg msg) {
        List<Msg> msgList = msgMapper.replyList(msg);
        // 执行分页查询
        page.setResult(msgList);
        return page;
    }
    
    /**
     * 发送文本信息
     * @param appid 应用id
     * @param openid 用户openid
     * @param content 文本内容
     * @return
     */
    public JSONObject textMessage(String appid,String openid, String content){
    	JSONObject json = new JSONObject();
    	json = weixinInterfaceService.textMessage(appid,openid, content);
    	//回复信息成功则入库
    	if(json.containsKey("errcode") && json.getInteger("errcode")==0){
    		this.saveMsg(appid, openid, content);
    		this.markMsg(appid, openid);
    	}
    	return json;
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
