package com.gen.framework.common.services;

import com.gen.framework.common.beans.SysRoleBean;
import com.gen.framework.common.beans.SysUserBean;
import com.gen.framework.common.dao.CommonMapper;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class SysManagerService extends CommonService{

    @Autowired
    private CommonMapper commonMapper;


    public Page getUserList(Integer pageNum)throws Exception{
        return this.commonList("baseUser","createTime desc",pageNum,10,new HashMap<>());
    }
    public Map getUserById(String uid){
        return null;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO saveUser(SysUserBean sysUserBean){
        ResponseVO vo=new ResponseVO();
        if(StringUtils.isBlank(sysUserBean.getuName())){
            vo.setReCode(-2);
            vo.setReMsg("用户名为空");
            return vo;
        }
        if(StringUtils.isBlank(sysUserBean.getuPassword())){
            vo.setReCode(-2);
            vo.setReMsg("密码为空");
            return vo;
        }
        return this.commonInsert("baseUser",sysUserBean);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO saveRole(SysRoleBean sysRoleBean){
        ResponseVO vo=new ResponseVO();
        if(StringUtils.isBlank(sysRoleBean.getrName())){
            vo.setReCode(-2);
            vo.setReMsg("角色名为空");
            return vo;
        }

        return this.commonInsert("baseRole",sysRoleBean);
    }
    public Page getRoleList(Integer pageNum)throws Exception{

        return this.commonList("baseRole","createTime desc",pageNum,10,new HashMap<String,Object>());
    }

}
