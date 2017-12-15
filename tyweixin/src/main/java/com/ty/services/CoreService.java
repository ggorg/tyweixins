package com.ty.services;

import com.ty.core.beans.message.resp.Article;
import com.ty.core.beans.message.resp.NewsMessage;
import com.ty.dao.PubweixinMapper;
import com.ty.dao.UserInfoMapper;
import com.ty.entity.*;
import com.ty.util.MessageUtil;
import com.ty.util.WeixinUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 核心服务类
 */
@Service
public class CoreService {
    
    private static final Logger logger = Logger.getLogger(CoreService.class);
            
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private WeixinUtil weixinUtil;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private PubweixinMapper pubweixinMapper;
    @Autowired
    private WeixinUserService weixinUserService;
    @Autowired
    private MsgService msgService;
    @Autowired
    private EventKeyService eventKeyService;
    @Autowired
    private EventRuleService eventRuleService;
    @Autowired
    private MessageService messageService;

    /**
     * 处理微信发来的请求
     * 
     * @param request
     * @return
     */
    public String processRequest(HttpServletRequest request) {
        String respMessage = null;
        // 第三方用户唯一凭证
        String appid = "";
        try {
            // 默认返回的文本消息内容
            String respContent = "";
            System.out.println("request = [" + request + "]");
            // xml请求解析
            Map<String, String> requestMap = MessageUtil.parseXml(request);
            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号原始ID
            String toUserName = requestMap.get("ToUserName");
            Pubweixin pubweixin = pubweixinMapper.selectByOpenid(toUserName);
            appid = pubweixin.getAppid();
            // 消息类型
            String msgType = requestMap.get("MsgType");
            // 接收到的消息
            String Content = requestMap.get("Content");

            // 回复文本消息
            com.ty.core.beans.message.req.TextMessage textMessage = new com.ty.core.beans.message.req.TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(new Date().getTime());
            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);

            Msg msg = new Msg();
            msg.setAppid(appid);
            msg.setOpenid(fromUserName);
            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                msg.setContent(Content);
                msg.setCreateDate(new Date());
                //入库消息管理
                msgService.save(msg);
                // 关键词回复处理
                if (respContent == null || respContent.length() == 0)
                    respMessage = autoReplys(Content, fromUserName, toUserName);
            }
            // 图片消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                //图片标识
                msg.setMsgtype("2");
                //图片链接
                String imageurl = requestMap.get("PicUrl");
                msg.setFilepath(imageurl);
                msg.setCreateDate(new Date());
                //入库消息管理
                msgService.save(msg);
            }
            // 地理位置消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
//                respContent = "您发送的是地理位置消息！";
            }
            // 链接消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
//                respContent = "您发送的是链接消息！";
            }
            // 音频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
//                respContent = "您发送的是音频消息！";
            	msg.setMsgtype("5");
            	msg.setContent("[音频]");
                msg.setCreateDate(new Date());
                //入库消息管理
                msgService.save(msg);
            }
            // 视频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VIDEO)) {
            	msg.setMsgtype("4");
            	msg.setContent("[视频]");
                msg.setCreateDate(new Date());
                //入库消息管理
                msgService.save(msg);
            }
            // 小视频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_SHORTVIDEO)) {
            	msg.setMsgtype("5");
            	msg.setContent("[小视频]");
                msg.setCreateDate(new Date());
                //入库消息管理
                msgService.save(msg);
                
            }
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");
                String eventKey = requestMap.get("EventKey");
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    //这里获取用户信息
                    UserInfo ui = userInfoMapper.selectByopenid(fromUserName);
                    if (ui != null) {
                        //曾经关注过的用户
                        UserInfo userInfo = new UserInfo();
                        userInfo.setOpenid(fromUserName);
                        userInfo.setSubscribe("1");
                        userInfo.setSubscribe_time(new Date());
                        userInfoMapper.update(userInfo);
                    } else {
                        UserInfo userInfo = weixinInterfaceService.getUserInfo(appid, fromUserName);
                        // 保存微信用户资料入库
                        weixinUserService.save(userInfo);
                    }

                    //扫描场景码关注时处理
                    if (eventKey.startsWith("qrscene_")) {
                        // 取出场景ID
                        String scene_id = eventKey.split("qrscene_")[1];
                    }
                    EventRule eventRule = eventRuleService.getSubscribe(appid);
                    if(eventRule != null){
                        // 判断回复规则里的回复文字字段是否为空,优先级别为文字,然后是图文
                        if(eventRule.getContent() != null && !eventRule.getContent().equals("")){
                            respContent = eventRule.getContent();
                        }else{
                            respContent =messageService.sendArticle(fromUserName,toUserName,eventRule.getMessage_id());
                        }
                    }
                }
                // 取消订阅
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    //取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                    UserInfo userInfo = new UserInfo();
                    userInfo.setOpenid(fromUserName);
                    userInfo.setSubscribe("0");
                    userInfoMapper.update(userInfo);
                }
                // 自定义菜单点击事件
                else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                    // 事件KEY值，与创建自定义菜单时指定的KEY值对应
                    List<EventKey> eventKeys = eventKeyService.findList(appid);
                    for(EventKey ek:eventKeys){
                        // 轮询处理关键字回复
                        int match = ek.getMatch();
                        //匹配规则，1 完全匹配，0 模糊匹配
                        if((match == 1 && eventKey.equals(ek.getKey())) || (match == 0 && eventKey.indexOf(ek.getKey())!= -1)){
                            //根据回复规则id查询返回回复规则实体bean
                            EventRule er = eventRuleService.selectById(ek.getRule_id());
                            // 判断回复规则里的回复文字字段是否为空,优先级别为文字,然后是图文
                            if(er.getContent() != null && !er.getContent().equals("")){
                                respContent = er.getContent();
                            }else{
                                respContent =messageService.sendArticle(fromUserName,toUserName,er.getMessage_id());
                            }
                        }
                    }
                }
                // 二维码扫描事件
                else if (eventType.equals(MessageUtil.EVENT_TYPE_SCAN)) {
                    // 取出场景ID
                    String scene_id = requestMap.get("EventKey");
                }
            }
//            if (respContent == null || respContent.equals("")) {
//                respContent = "亲，暂不能识别您发送的指令(´･ω･｀)";
//            }
            // 返回给微信
            if (respMessage == null || respMessage.length() == 0) {
                if (respContent != null && respContent.length() > 0) {
                    textMessage.setContent(respContent);
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                } else {
                    EventRule eventRule = eventRuleService.getAutoreply(appid);
                    if(eventRule != null){
                        // 判断回复规则里的回复文字字段是否为空,优先级别为文字,然后是图文
                        if(eventRule.getContent() != null && !eventRule.getContent().equals("")){
                            respContent = eventRule.getContent();
                        }else{
                            respContent =messageService.sendArticle(fromUserName,toUserName,eventRule.getMessage_id());
                        }
                    }
                    respMessage = respContent;
                }
                //回复消息入库
                msgService.saveMsg(appid, fromUserName, respContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respMessage;
    }

    /**
     * 关键词自动回复
     * @param content
     * @param fromUserName 应用appid
     * @param toUserName 用户openid
     * @return
     */
    public String autoReplys(String content, String fromUserName, String toUserName) {
        String respMessage = null;
        // 创建图文消息  
        NewsMessage newsMessage = new NewsMessage();
        newsMessage.setToUserName(fromUserName);  
        newsMessage.setFromUserName(toUserName);  
        newsMessage.setCreateTime(new Date().getTime());  
        newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);  
        newsMessage.setFuncFlag(0);  

        List<Article> articleList = new ArrayList<Article>();
        List<EventKey> eventKeys = eventKeyService.findList(fromUserName);
        for(EventKey ek:eventKeys){
            // 轮询处理关键字回复
            int match = ek.getMatch();
            //匹配规则，1 完全匹配，0 模糊匹配
            if((match == 1 && content.equals(ek.getKey())) || (match == 0 && (content.indexOf(ek.getKey())!= -1 || content.equalsIgnoreCase(ek.getKey())))){
                //根据回复规则id查询返回回复规则实体bean
                EventRule er = eventRuleService.selectById(ek.getRule_id());
                // 判断回复规则里的回复文字字段是否为空,优先级别为文字,然后是图文
                if(er.getContent() != null && !er.getContent().equals("")){
                    respMessage = er.getContent();
                }else{
                    respMessage =messageService.sendArticle(fromUserName,toUserName,er.getMessage_id());
                }
            }
        }
        return respMessage;
    }
}