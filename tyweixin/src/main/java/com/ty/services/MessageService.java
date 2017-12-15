package com.ty.services;

import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.core.beans.message.resp.Article;
import com.ty.dao.MessageMapper;
import com.ty.entity.Message;
import com.ty.util.CustomMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 图文消息
 */
@Service
public class MessageService{
    @Autowired
    private MessageMapper messageMapper;
	@Autowired
	private WeixinInterfaceService weixinInterfaceService;
    
    /**
     * 添加图文消息
     * @param message
     * @return
     */
    @Transactional(readOnly = false)
	public ResponseVO saveOrUpdate(Message message){
		ResponseVO vo=new ResponseVO();
    	int ret = 0;
    	Message m = messageMapper.selectById(message.getId());
    	if(m!=null){
    		ret = messageMapper.update(message);
			vo.setReMsg("修改成功");
		}else{
			ret = messageMapper.insert(message);
			vo.setReMsg("创建成功");
		}
		vo.setReCode(1);
    	return vo;
    }
    /**
     * 删除图文消息
     * @param id
     * @return
     */
    @Transactional(readOnly = false)
    public ResponseVO delete(Integer id){
		ResponseVO vo=new ResponseVO();
    	int res = messageMapper.delete(id);
		if(res>0){
			vo.setReCode(1);
			vo.setReMsg("删除成功");
		}else{
			vo.setReCode(-2);
			vo.setReMsg("删除失败");
		}
    	return vo;
    }
    
    /**
     * 分页查询图文消息列表
     * @param pageNum 当前页数
     * @param appid 应用id
     * @return
     */
    public Page findList(Integer pageNum,String appid){
		Page<Message> page = new Page<Message>(pageNum,10);
		List<Message> list = messageMapper.findList(page,appid);
		int total = messageMapper.findListCount(appid);
		page.setResult(list);
		page.setTotal(total);
		return page;
	}

    /**
     * 根据id返回图文消息
     * @param id 主键id
     * @return
     */
    public Message selectById(Integer id){ return messageMapper.selectById(id); }

    /**
     * 发送图文信息
     * @param openid 用户openid
     * @param id
     */
    public ResponseVO sendMessage(String openid,Integer id){
        Message message = messageMapper.selectById(id);
        List<Message>messageList = messageMapper.findListById(id);
        List<Article> list = new ArrayList<Article>();
        messageList.add(0,message);
        for(Message m:messageList){
            Article article = new Article();
            article.setTitle(m.getTitle());
            article.setDescription(m.getDescription());
            article.setUrl(m.getUrl());
            article.setPicUrl(m.getPicurl());
            list.add(article);
        }
        String content = CustomMessage.NewsMsg(openid,list);
        weixinInterfaceService.sendMessage(message.getAppid(),content);
        return new ResponseVO(1,"成功",null);
    }

    /**
     * 批量推送图文接口
     * @param appid 公众号应用id
     * @param openids 用户openid集合
     * @param title 图文标题
     * @param description 图文描述
     * @param url 跳转url
     * @param picurl 图片url
     * @return
     */
    public ResponseVO sendMessage(String appid,List<String>openids,String title,String description,String url,String picurl){
        List<Article> list = new ArrayList<Article>();
        Article article = new Article();
        article.setTitle(title);
        article.setDescription(description);
        article.setUrl(url);
        article.setPicUrl(picurl);
        list.add(article);
        try {
            for(String openid:openids){
                String content = CustomMessage.NewsMsg(openid,list);
                weixinInterfaceService.sendMessage(appid,content);
            }
            return new ResponseVO(1,"成功",null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVO(-1,"失败",null);
        }
    }

}
