package com.ty.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.dao.TagsMapper;
import com.ty.entity.Tags;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户标签管理
 */
@Service
public class TagsService {

    private static final Logger logger = Logger.getLogger(TagsService.class);
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private TagsMapper tagsMapper;


    /**
     * 分页查询微信列表
     * 
     * @param pageNum 分页信息
     * @return 微信用户列表
     */
    public Page<Tags> findUser(Integer pageNum,Tags tags) {
        Page<Tags> page = new Page<Tags>(pageNum,10);
        // 设置分页参数
        List<Tags> tagsList = tagsMapper.findList(page,tags);
        int total =  tagsMapper.findListCount(tags);
        // 执行分页查询
        page.setResult(tagsList);
        page.setTotal(total);
        return page;
    }

    /**
     * 创建标签
     * @param tags
     * @return
     */
    @Transactional(readOnly = false)
    public ResponseVO save(Tags tags) {
        ResponseVO vo = new ResponseVO();
        JSONObject json = weixinInterfaceService.tagsCreate(tags.getAppid(), tags.getName());
        if(json.containsKey("errcode")){
            vo.setReCode(-1);
            vo.setReMsg(json.getString("errmsg"));
        }else{
            tags.setId(json.getJSONObject("tag").getInteger("id"));
            int res = tagsMapper.insert(tags);
            if(res>0){
                vo.setReCode(1);
                vo.setReMsg("成功");
                vo.setData(tags);
            }else{
                vo.setReCode(-2);
                vo.setReMsg("入库失败");
            }
        }
    	return vo;
    }

    public ResponseVO update(Tags tags) {
        ResponseVO vo = new ResponseVO();
        JSONObject json = weixinInterfaceService.tagsUpdate(tags.getAppid(), tags.getId(),tags.getName());
        if(json.containsKey("errcode")){
            vo.setReCode(-1);
            vo.setReMsg(json.getString("errmsg"));
        }else{
            tags.setId(json.getJSONObject("tag").getInteger("id"));
            int res = tagsMapper.insert(tags);
            if(res>0){
                vo.setReCode(1);
                vo.setReMsg("成功");
                vo.setData(tags);
            }else{
                vo.setReCode(-2);
                vo.setReMsg("入库失败");
            }
        }
        return vo;
    }
    
    /**
     * 根据appid和id查询标签
     * 
     * @param entity
     * @return
     */
    public Tags selectId(Tags entity) {
        return tagsMapper.selectById(entity);
    }
}