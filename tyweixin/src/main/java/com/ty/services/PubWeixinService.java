package com.ty.services;

import com.gen.framework.common.dao.CommonMapper;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.dao.PubweixinMapper;
import com.ty.entity.Pubweixin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 微信公众号相关操作服务
 */
@Service
public class PubWeixinService extends CommonService {
    @Autowired
    private PubweixinMapper pubweixinMapper;
	@Autowired
	private CommonMapper commonMapper;
    
    /**
     * 添加公众号
     * @param pubweixin
     * @return
     */
    @Transactional(readOnly = false)
	public ResponseVO saveOrUpdate(Pubweixin pubweixin){
		ResponseVO vo=new ResponseVO();
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
		vo.setReCode(1);
		vo.setReMsg("创建公众号成功");
    	return vo;
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
     * @param pageNum
     * @return
     */
    public Page findPubweixin(Integer pageNum) throws Exception{
		return this.commonPage("weixin_public","update_date desc",pageNum,10,new HashMap<>());
	}

    /**
     * 无分页查询微信公众号列表
     *
     * @param pubweixin
     * @return
     */
    public List<Pubweixin> findPubweixinAll(Pubweixin pubweixin) {
        List<Pubweixin> list = pubweixinMapper.findListAll(pubweixin);
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
