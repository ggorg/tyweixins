package com.ty.services;

import com.gen.framework.common.util.Page;
import com.ty.dao.PubweixinMapper;
import com.ty.entity.Pubweixin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 微信公众号相关操作服务
 */
@Service
public class PubWeixinService {
    @Autowired
    private PubweixinMapper pubweixinMapper;
    
    /**
     * 添加公众号
     * @param pubweixin
     * @return
     */
    @Transactional(readOnly = false)
	public int saveOrUpdate(Pubweixin pubweixin){
    	int ret = 0;
    	Pubweixin pw = pubweixinMapper.selectByAppid(pubweixin.getAppid());
    	if(pw!=null){
    		pubweixin.setId(pw.getId());
    		pubweixin.setDel_flag("0");
    		pubweixin.setUpdate_date(new Date());
    		ret = pubweixinMapper.update(pubweixin);
    	}else{
    		pubweixin.setCreate_date(new Date());
    		pubweixin.setUpdate_date(new Date());
    		ret = pubweixinMapper.insert(pubweixin);
    	}
    	return ret;
    }
    /**
     * 将公众号删除标志设置为1 已删除
     * @param appid
     * @return
     */
    @Transactional(readOnly = false)
    public int deletePubweixinByAppid(String appid){
    	int ret = 0;
    	Pubweixin pw = pubweixinMapper.selectByAppid(appid);
    	if(pw!=null){
    		pw.setDel_flag("1");;
    		ret = pubweixinMapper.update(pw);
    	}else{
    		ret = -2;
    	}
    	return ret;
    }
    
    /**
     * 分页查询微信公众号列表
     * @param page
     * @param pubweixin
     * @return
     */
    public Page<Pubweixin> findPubweixin(Page<Pubweixin> page, Pubweixin pubweixin) {
        // 设置分页参数
        // 执行分页查询
        page.setResult(pubweixinMapper.findList(pubweixin));
        return page;
    }

    /**
     * 无分页查询微信公众号列表
     *
     * @param pubweixin
     * @return
     */
    public List<Pubweixin> findPubweixin(Pubweixin pubweixin) {
        List<Pubweixin> list = pubweixinMapper.findList(pubweixin);
        return list;
    }
    
    /**
     * 根据APPID返回公众号
     * @param appid
     * @return
     */
    public Pubweixin selectByAppid(String appid){
    	return pubweixinMapper.selectByAppid(appid);
    }

}
