package com.gen.framework.common.services;

import com.gen.framework.common.beans.SysMenuBean;
import com.gen.framework.common.beans.SysRoleBean;
import com.gen.framework.common.beans.SysUserBean;
import com.gen.framework.common.beans.SysUserRoleBean;
import com.gen.framework.common.dao.CommonMapper;
import com.gen.framework.common.util.BeanToMapUtil;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysManagerService extends CommonService{

    @Autowired
    private CommonMapper commonMapper;


    public Page getUserPage(Integer pageNum)throws Exception{
        return this.commonPage("baseUser","createTime desc",pageNum,10,new HashMap<>());
    }
    public List getAllUser(){
        return this.commonList("baseUser","createTime desc",null,null,new HashMap<>());
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
       Map params= BeanToMapUtil.beanToMap(sysUserBean);
        params.put("createTime",new Date());
        params.put("updateTime",new Date());
        params.put("disabled",false);
        return this.commonInsertMap("baseUser",params);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO saveRole(SysRoleBean sysRoleBean,Integer[] uIds){
        String roleTable="baseRole";
        ResponseVO vo=new ResponseVO();
        if(StringUtils.isBlank(sysRoleBean.getrName())){
            vo.setReCode(-2);
            vo.setReMsg("角色名为空");
            return vo;
        }
        long count=this.commonCountBySingleParam(roleTable,"rName",sysRoleBean.getrName());
        if(count>0){
            vo.setReCode(-2);
            vo.setReMsg("角色名已经重复，请重新换一个");
            return vo;
        }
        Map roleMap=BeanToMapUtil.beanToMap(sysRoleBean);
        roleMap.put("createTime",new Date());
        roleMap.put("updateTime",new Date());

        vo=this.commonInsertMap(roleTable,roleMap);
        if(vo.getReCode()==1){
            Map userRole=new HashMap();
            if(uIds!=null){
                SysUserRoleBean ur=null;
                for(Integer uid:uIds){
                    ur=new SysUserRoleBean();
                    ur.setrId((Integer) vo.getData());
                    ur.setuId(uid);
                    this.commonInsert("baseUserRole",ur);
                }
            }

        }
        vo.setReCode(1);
        vo.setReMsg("创建角色成功");
        return vo;
    }
    public Page getRolePage(Integer pageNum)throws Exception{

        return this.commonPage("baseRole","createTime desc",pageNum,10,new HashMap<String,Object>());
    }

    public Page getMenuPage(Integer pageNum)throws Exception{

        return this.commonPage("baseMenu","mSort asc",pageNum,10,new HashMap<String,Object>());
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO saveMenu(SysMenuBean sysMenuBean){
        ResponseVO vo=new ResponseVO();
        if(StringUtils.isBlank(sysMenuBean.getmName())){
            vo.setReCode(-2);
            vo.setReMsg("菜单名为空");
            return vo;
        }
        if(StringUtils.isBlank(sysMenuBean.getmUrl())){
            vo.setReCode(-2);
            vo.setReMsg("菜单地址为空");
            return vo;
        }
        if(sysMenuBean.getmParentId()==null || sysMenuBean.getmParentId()==0){
            sysMenuBean.setmParentId(-1);
        }
        if(sysMenuBean.getmSort()==null || sysMenuBean.getmSort()==0){
            sysMenuBean.setmSort(1);
        }
        Map params= BeanToMapUtil.beanToMap(sysMenuBean);
        params.put("createTime",new Date());
        params.put("updateTime",new Date());

        return this.commonInsertMap("baseMenu",params);
    }
}
