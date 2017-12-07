package com.ty.services;

import com.ty.core.beans.message.resp.Article;
import com.ty.core.beans.message.resp.NewsMessage;
import com.ty.dao.PubweixinMapper;
import com.ty.dao.UserInfoMapper;
import com.ty.entity.Msg;
import com.ty.entity.Pubweixin;
import com.ty.entity.UserInfo;
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
                // 处理文本消息
                respContent = handelText(Content, appid, fromUserName);
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
                    if (eventKey.equals("11")) {
//                        respContent = "天气预报菜单项被点击！";
                    } else if (eventKey.equals("12")) {
//                        respContent = "公交查询菜单项被点击！";
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
                    //回复消息入库
                    msgService.saveMsg(appid, fromUserName, respContent);
                } else {
                    respMessage = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respMessage;
    }

    // 接收用户发送的文本消息内容
    public String handelText(String content, String appid, String openid) {
        String respContent = "";
        respContent = content;
        return respContent;
    }
    
    /**
     * 临时发送图文的方法
     * @param fromUserName 应用appid
     * @param toUserName 用户openid
     * @return
     */
    public String sendArticle(String fromUserName, String toUserName) {
    	String respMessage = null;
        // 创建图文消息  
        NewsMessage newsMessage = new NewsMessage();
        newsMessage.setToUserName(fromUserName);  
        newsMessage.setFromUserName(toUserName);  
        newsMessage.setCreateTime(new Date().getTime());  
        newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);  
        newsMessage.setFuncFlag(0);  

        List<Article> articleList = new ArrayList<Article>();
        /*
        // 单图文消息  
        Article article = new Article();  
        article.setTitle("标题");  
        article.setDescription("描述内容");  
        article.setPicUrl("图片URL");  
        article.setUrl("跳转链接");  
        articleList.add(article);  
        // 设置图文消息个数  
        newsMessage.setArticleCount(articleList.size());  
        // 设置图文消息包含的图文集合  
        newsMessage.setArticles(articleList);  
        // 将图文消息对象转换成xml字符串  
        respMessage = MessageUtil.newsMessageToXml(newsMessage);  
        */
        /////////////////////////华丽分割线\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        /*
        // 多图文消息  
        Article article1 = new Article();  
        article1.setTitle("天才少年");  
        article1.setDescription("来自天才少年的测试");  
        article1.setPicUrl("http://img4q.duitang.com/uploads/item/201408/08/20140808183448_Bn2Fr.thumb.700_0.jpeg");  
        article1.setUrl("http://baidu.com");  

        Article article2 = new Article();  
        article2.setTitle("标题2");  
        article2.setDescription("");  
        article2.setPicUrl("图片URL");  
        article2.setUrl("跳转URL");  

        Article article3 = new Article();  
        article3.setTitle("标题3");  
        article3.setDescription("");  
        article3.setPicUrl("图片URL");  
        article3.setUrl("跳转URL");  

        articleList.add(article1);  
        articleList.add(article2);  
        articleList.add(article3);  
        newsMessage.setArticleCount(articleList.size());  
        newsMessage.setArticles(articleList);  
        respMessage = MessageUtil.newsMessageToXml(newsMessage);
        */
        return respMessage;
    }
    
    /**
     * 关键词自动回复
     * @param content
     * @param fromUserName
     * @param toUserName
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
        
        if (content.equalsIgnoreCase("JXB-178") || content.equalsIgnoreCase("JXB178") || content.equalsIgnoreCase("178")) {
            Article article = new Article();  
            article.setTitle("JXB-178");  
            article.setDescription("JXB-178产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JXB178.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207737967&idx=1&sn=6c7d69ecf30095dcd1d14df4dd115af5#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("JXB-180T") || content.equalsIgnoreCase("JXB180T") || content.equalsIgnoreCase("180T")
                || content.equalsIgnoreCase("JXB-180（语音版）") || content.equalsIgnoreCase("JXB180（语音版）") || content.equalsIgnoreCase("180语音")) {
            Article article = new Article();  
            article.setTitle("JXB-180T");  
            article.setDescription("JXB-180（语音版）产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JXB180T.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207735950&idx=1&sn=6f52d08f552fe89ae9a044bf12cfe4f8#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("JXB-180") || content.equalsIgnoreCase("JXB180") || content.equalsIgnoreCase("180")) {
            Article article = new Article();  
            article.setTitle("JXB-180");  
            article.setDescription("JXB-180产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JXB180.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207735950&idx=1&sn=6f52d08f552fe89ae9a044bf12cfe4f8#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("JXB-182T") || content.equalsIgnoreCase("JXB182T") || content.equalsIgnoreCase("182T")
                || content.equalsIgnoreCase("JXB-182（语音版）") || content.equalsIgnoreCase("JXB182（语音版）") || content.equalsIgnoreCase("182语音")) {
            Article article = new Article();  
            article.setTitle("JXB-182T");  
            article.setDescription("JXB-182（语音版）产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JXB182T.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207736449&idx=1&sn=e3abedb1f2b26a71295f7f6c6d66aa0e#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("JXB-182") || content.equalsIgnoreCase("JXB182") || content.equalsIgnoreCase("182")) {
            Article article = new Article();  
            article.setTitle("JXB-182");  
            article.setDescription("JXB-182产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JXB182.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207736299&idx=1&sn=b13ba28086bf163d639ddfb49b80eb23#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("JXB-183") || content.equalsIgnoreCase("JXB183") || content.equalsIgnoreCase("183")) {
            Article article = new Article();  
            article.setTitle("JXB-183");  
            article.setDescription("JXB-183产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JXB183.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207736811&idx=1&sn=aaf0be5ba5281f1d74fdee3a4ebb3b3c#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("JXB-186") || content.equalsIgnoreCase("JXB186") || content.equalsIgnoreCase("186")) {
            Article article = new Article();  
            article.setTitle("JXB-186");  
            article.setDescription("JXB-186产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JXB186.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207736925&idx=1&sn=8d5aae8db9d6f6c4aa337f967fd465e1#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("JXB-188") || content.equalsIgnoreCase("JXB188") || content.equalsIgnoreCase("188")) {
            Article article = new Article();  
            article.setTitle("JXB-188");  
            article.setDescription("JXB-188产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JXB188.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207737242&idx=1&sn=080a18b7a81a79d828f8eedcbce567be#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("JXB-189") || content.equalsIgnoreCase("JXB189") || content.equalsIgnoreCase("189")) {
            Article article = new Article();  
            article.setTitle("JXB-189");  
            article.setDescription("JXB-189产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JXB189.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207738363&idx=1&sn=93c4cb034513d681216361d9cd0598b5#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("JXB-190") || content.equalsIgnoreCase("JXB190") || content.equalsIgnoreCase("190")) {
            Article article = new Article();  
            article.setTitle("JXB-190");  
            article.setDescription("JXB-190产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JXB190.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207738770&idx=1&sn=cfffa6aef6aeca3607d7657d9eefc225#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("JXB-191") || content.equalsIgnoreCase("JXB191") || content.equalsIgnoreCase("191")) {
            Article article = new Article();  
            article.setTitle("JXB-191");  
            article.setDescription("JXB-191产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JXB191.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207739512&idx=1&sn=60693464379e7b2692992bb4e8c46506#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("JXB-192") || content.equalsIgnoreCase("JXB192") || content.equalsIgnoreCase("192")) {
            Article article = new Article();  
            article.setTitle("JXB-192");  
            article.setDescription("JXB-192产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JXB192.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207739966&idx=1&sn=08aca7db013f67a2f1d316bae22d4ff4#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("ET001")) {
            Article article = new Article();  
            article.setTitle("ET001");  
            article.setDescription("ET001产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/ET001.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207737571&idx=1&sn=13c54a105402aef77cf1d42a1fbe2878#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage);
        } else if (content.equalsIgnoreCase("DT001")) {
            Article article = new Article();  
            article.setTitle("DT001");  
            article.setDescription("DT001产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/DT001.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207737377&idx=1&sn=e60427afa0d7362b5d0c2f7b3a3390f7#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage);
        } else if (content.equalsIgnoreCase("DT007") || content.equalsIgnoreCase("007")) {
            Article article = new Article();  
            article.setTitle("DT007");  
            article.setDescription("DT007产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/DT007.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=503866424&idx=1&sn=f9e8bc682e90688aedaab79df4819d54#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage);
        } else if (content.equalsIgnoreCase("DT008") || content.equalsIgnoreCase("008")) {
            Article article = new Article();  
            article.setTitle("DT008");  
            article.setDescription("DT008产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/DT008.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=503866440&idx=1&sn=45f54f038006174115ab47aa25d4cb6f#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage);
        } else if (content.equalsIgnoreCase("JC-809") || content.equalsIgnoreCase("JC809") || content.equalsIgnoreCase("809")) {
            Article article = new Article();  
            article.setTitle("JC-809");  
            article.setDescription("JC-809产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/JC809.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207737799&idx=1&sn=d1d1a3d7ced421aa6133fc4f47f998f2#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("LB-828") || content.equalsIgnoreCase("LB828") || content.equalsIgnoreCase("828")) {
            Article article = new Article();  
            article.setTitle("LB-828");  
            article.setDescription("LB-828产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/LB828.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=207740075&idx=1&sn=42a3db9c9737eada85364de85d2a3d09#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("PM001")) {
            Article article = new Article();  
            article.setTitle("PM001");  
            article.setDescription("PM001产品使用说明");  
            article.setPicUrl("http://wx.berrcom.cc/img/PM001.jpg");  
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=401141153&idx=1&sn=6d27dce1684ce78cf5447d5f67d240a8#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        } else if (content.equalsIgnoreCase("TB001")) {
            Article article = new Article();
            article.setTitle("TB001");
            article.setDescription("TB001产品使用说明");
            article.setPicUrl("http://wx.berrcom.cc/img/TB001.jpg");
            article.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMzU4ODg1Mw==&mid=401140731&idx=1&sn=185674c84f453ea29089dbc074982f7d#rd");  
            articleList.add(article);  
            newsMessage.setArticleCount(articleList.size());  
            newsMessage.setArticles(articleList);  
            respMessage = MessageUtil.newsMessageToXml(newsMessage); 
        }
        return respMessage;
    }
}