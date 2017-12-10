package com.ty.services;

import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.dao.MessageMapper;
import com.ty.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
    		m.setCreate_date(new Date());
    		ret = messageMapper.update(m);
			vo.setReMsg("修改成功");
		}else{
			m.setCreate_date(new Date());
			ret = messageMapper.insert(m);
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

}
