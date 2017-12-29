package com.ty.services;

import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.dao.PushMapper;
import com.ty.entity.Push;
import com.ty.util.CustomMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 推送策略
 * @author Jacky
 *
 */
@Service
public class PushService {
    @Autowired
    private PushMapper pushMapper;
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private MessageService messageService;

    /**
     * 根据appid查询推送策略
     * @param pageNum 页数
     * @param appid 应用id
     * @return
     */
    public Page findList(Integer pageNum,String appid){
        Page<Push>page = new Page<Push>(pageNum,15);
        List<Push> list = pushMapper.findList(page,appid);
        int total = pushMapper.findListCount(appid);
        page.setResult(list);
        page.setTotal(total);
        return page;
    }

    /**
     * 不分页查询推送策略
     * @param appid
     * @return
     */
    public List<Push> findList(String appid){ return pushMapper.findListAll(appid); }


    /**
     * 根据主键查询推送策略
     * @param id 主键
     * @return
     */
    public Push selectById(int id){
        return pushMapper.selectById(id);
    }

    /**
     * 插入或更新推送策略
     * @param push
     * @return
     */
    public ResponseVO saveOrUpdate(Push push){
        ResponseVO vo=new ResponseVO();
        Push pw = pushMapper.selectById(push.getId());
        if(pw!=null){
            pushMapper.update(push);
            vo.setReMsg("修改成功");
        }else{
            pushMapper.insert(push);
            vo.setReMsg("创建成功");
        }
        vo.setReCode(1);
        return vo;
    }

    /**
     * 删除推送策略
     * @param id 主键
     * @return
     */
    public ResponseVO delete(Integer id){
        ResponseVO vo=new ResponseVO();
        int res = pushMapper.delete(id);
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
     * 定时调用执行推送策略
     * @return
     */
    public void pushTask(){
        ResponseVO vo=new ResponseVO();
        List<Push>pushList = pushMapper.findPushList();
        for(Push push:pushList){
            int push_type = push.getPush_type();
            //推送文字
            if(push_type == 1){
                if(StringUtils.isNotBlank(push.getOpenids())){
                    for (String openid:push.getOpenids().split(",")){
                        String content = CustomMessage.TextMsg(openid,push.getPush_content());
                        JSONObject json = weixinInterfaceService.sendMessage(push.getAppid(),content);
                    }
                }

                //推送图文
            }else if(push_type == 2){
                if(StringUtils.isNotBlank(push.getOpenids())){
                    for (String openid:push.getOpenids().split(",")){
                        messageService.sendMessage(openid,push.getPush_messageid());
                    }
                }

                //推送模板消息
            }else if(push_type ==3){

            }
            push.setPush_state(1);
            pushMapper.update(push);
        }
    }

}
