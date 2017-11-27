package com.gen.framework.common.services;

import com.gen.framework.common.beans.CommonCountBean;
import com.gen.framework.common.beans.CommonInsertBean;
import com.gen.framework.common.beans.CommonSearchBean;
import com.gen.framework.common.beans.SysUserBean;
import com.gen.framework.common.dao.CommonMapper;
import com.gen.framework.common.util.BeanToMapUtil;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysManagerService {

    @Autowired
    private CommonMapper commonMapper;


    public Page getUserList(Integer pageNum)throws Exception{

        Page page=new Page(pageNum, 10);
        Map<String,Object> condition=null;;


        CommonSearchBean csb=new CommonSearchBean("baseUser","null",null, page.getStartRow(),page.getEndRow(),new HashMap<String,Object>());
        CommonCountBean ccb = new CommonCountBean();

        PropertyUtils.copyProperties(ccb, csb);
        long count = commonMapper.selectCount(ccb);
        if(count>0){
            List list=this.commonMapper.selectObjects(csb);
            page.setResult(list);
            page.setTotal(count);
        }

        return page;
    }
    public Map getUserById(String uid){
        return null;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO saveUser(SysUserBean sysUserBean){
        ResponseVO vo=new ResponseVO();
        if(StringUtils.isBlank(sysUserBean.getUname())){
            vo.setReCode(-2);
            vo.setReMsg("用户名为空");
            return vo;
        }
        if(StringUtils.isBlank(sysUserBean.getUpassword())){
            vo.setReCode(-2);
            vo.setReMsg("密码为空");
            return vo;
        }
        Map params= BeanToMapUtil.beanToMap(sysUserBean);
        params.put("createTime",new Date());
        params.put("updatetime",new Date());
        params.put("isDelete",false);
        this.commonMapper.insertObject(new CommonInsertBean("baseUser",params));
        vo.setReCode(1);
        vo.setReMsg("创建成功");
        return vo ;
    }

}
