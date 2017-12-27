package com.gen.framework.common.services;

import com.gen.framework.common.beans.*;
import com.gen.framework.common.dao.CommonMapper;
import com.gen.framework.common.dao.MenuPowerMapper;
import com.gen.framework.common.util.BeanToMapUtil;
import com.gen.framework.common.util.MenuMapComparator;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SysManagerService extends CommonService{

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private MenuPowerMapper menuPowerMapper;

    @Autowired
    private CacheManager cacheManager;
    public Page getUserPage(Integer pageNum)throws Exception{
        return this.commonPage("baseUser","createTime desc",pageNum,10,new HashMap<>());
    }
    public List getAllUser(){
        return this.commonList("baseUser","createTime desc",null,null,new HashMap<>());
    }
    public SysUserBean getUserById(Integer uid)throws Exception{
        Map map=this.commonObjectBySingleParam("baseUser","id",uid);
        SysUserBean userBean=new SysUserBean();
        if(map!=null){

            userBean.setId((Integer) map.get("id"));
            userBean.setuName((String)map.get("uName"));
            Integer disabled=(Integer)map.get("disabled");
            userBean.setDisabled(disabled==null || disabled==1?false:true);
        }

        return userBean;
        //return this.commonObjectBySingleParam("baseUser","id",uid,SysUserBean.class);

    }
    public ResponseVO disabledUser(SysUserBean sysUserBean)throws  Exception{

        Map setParams=new HashMap();
        setParams.put("disabled",sysUserBean.getDisabled());
       return this.commonUpdateBySingleSearchParam("baseUser",setParams,"id",sysUserBean.getId());

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveUser(SysUserBean sysUserBean)throws  Exception{
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
        params.put("updateTime",new Date());
        if(sysUserBean.getId()!=null && sysUserBean.getId()>0){
            params.remove("disabled");
            return this.commonUpdateBySingleSearchParam("baseUser",params,"id",sysUserBean.getId());
        }else{
            params.put("createTime",new Date());

            params.put("disabled",false);
           Map map= this.commonObjectBySingleParam("baseUser","uName",sysUserBean.getuName());
           if(map!=null){
               vo.setReCode(-2);
               vo.setReMsg("用户已经存在，请换一个");
               return vo;
           }
            return this.commonInsertMap("baseUser",params);
        }
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
        if(sysRoleBean.getId()==null && count>0){
            vo.setReCode(-2);
            vo.setReMsg("角色名已经重复，请重新换一个");
            return vo;
        }
        Map roleMap=BeanToMapUtil.beanToMap(sysRoleBean);
        roleMap.put("updateTime",new Date());
        if(sysRoleBean.getId()!=null && sysRoleBean.getId()>0){

            vo=this.commonUpdateBySingleSearchParam(roleTable,roleMap,"id",sysRoleBean.getId());
            vo.setData(sysRoleBean.getId());
            this.commonDelete("baseUserRole","rId",sysRoleBean.getId());
            vo.setReMsg("修改角色成功");
        }else{
            roleMap.put("createTime",new Date());
            vo=this.commonInsertMap(roleTable,roleMap);
            vo.setReMsg("创建角色成功");
        }

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

        return vo;
    }

    public Map getRoleById(Integer rId)throws Exception{
        Map data=new HashMap();
        data.put("roleObject",this.commonObjectBySingleParam("baseRole","id",rId,SysRoleBean.class));
        List<Map> list=getAllUser();
        List<Map> urList=this.commonObjectsBySingleParam("baseUserRole","rId",rId);
        Integer userId=null;
        Integer ur_uid=null;
        if(list!=null && !list.isEmpty()){
            for(Map map:list){
                if(map.containsKey("id")){
                    userId=(Integer) map.get("id");
                    for(Map ur:urList){
                        ur_uid=(Integer) ur.get("uId");
                        if(ur_uid==userId){
                            map.put("isThisRole",true);
                        }
                    }
                }
            }
        }
        data.put("userList",list);
        return data;

    }
    public Page getRolePage(Integer pageNum)throws Exception{

        return this.commonPage("baseRole","createTime desc",pageNum,10,new HashMap<String,Object>());
    }
    public SysMenuBean getMenuById(Integer mId)throws Exception{
       return  this.commonObjectBySingleParam("baseMenu","id",mId,SysMenuBean.class);
    }
    public Page getMenuPage(Integer pageNum)throws Exception{

        return this.commonPage("baseMenu","mSort asc",pageNum,10,new HashMap<String,Object>());
    }
    public TreeSet getMenuPage()throws Exception{

        return handlePower(null);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO saveMenu(SysMenuBean sysMenuBean){
        ResponseVO vo=new ResponseVO();
        try {
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
            params.put("updateTime",new Date());
            if(sysMenuBean.getId()!=null && sysMenuBean.getId()>0){
                vo=this.commonUpdateBySingleSearchParam("baseMenu",params,"id",sysMenuBean.getId());;

            }else{
                params.put("createTime",new Date());
                vo=this.commonInsertMap("baseMenu",params);
            }
            return vo;
        }finally {
            if(vo.getReCode()==1){
                Cache cache=cacheManager.getCache("allMenu");
                cache.put("'all'",getAllMenu());
            }
        }





    }
    @Cacheable(value="allMenu",key="'all'")
    public List getAllMenu(){
        return this.commonList("baseMenu","mSort asc",null,null,new HashMap<>());
    }
    public SysRoleMenuBean getPower(Integer rId)throws Exception{
        return this.commonObjectBySingleParam("baseRoleMenus","rId",rId,SysRoleMenuBean.class);

    }
    public TreeSet handlePower(Integer rId)throws Exception{
        List menuList=this.commonList("baseMenu","mSort asc",null,null,new HashMap<>());
        CopyOnWriteArrayList<Map> linkeMenuList=new CopyOnWriteArrayList(menuList);
        TreeSet<Map> topMenus=new TreeSet(new MenuMapComparator());
        Integer mParentId=null;
        Integer mId=null;
        Integer childMid=null;
        List srm=null;
        if(rId!=null){
            srm=this.commonObjectsBySingleParam("baseRoleMenus","rId",rId);
        }



        for(Map m:linkeMenuList){
            if(m.containsKey("mParentId")){
                mParentId=(Integer)m.get("mParentId");
                if(mParentId==-1){
                    mId =(Integer)m.get("id");
                    if(srm!=null)m.put("isPower",this.isPower(mId,srm));
                    topMenus.add(m);
                    linkeMenuList.remove(m);
                    for(Map child:linkeMenuList){
                        if(child.containsKey("mParentId") && m.containsKey("id")){
                            mParentId =(Integer)child.get("mParentId");
                            childMid=(Integer)child.get("id");
                            if(mParentId==mId){
                                if(srm!=null) child.put("isPower",this.isPower(childMid,srm));
                                m.put("isChild",true);
                                topMenus.add(child);
                                linkeMenuList.remove(child);
                            }

                        }
                    }
                }
            }
        }

        return topMenus;
    }
    private boolean isPower(Integer mId,List<Map> roleMenu){
        if(roleMenu!=null && !roleMenu.isEmpty()){
            Integer roleMenu_mid=null;
            for(Map map:roleMenu){
                if(map.containsKey("mId")){
                    roleMenu_mid=(Integer) map.get("mId");
                    if(roleMenu_mid==mId){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public ResponseVO savePower(Integer rId,Integer[] mIds)throws Exception{
        ResponseVO vo=new ResponseVO();

        if(rId==null || rId <= 0){
            vo.setReCode(-2);
            vo.setReMsg("请选择一个角色");
            return vo;
        }
        if(mIds==null){
            vo.setReCode(-2);
            vo.setReMsg("请选择授权菜单");
            return vo;
        }
        long count=this.commonCountBySingleParam("baseRole","id",rId);
        if(count==0){
            vo.setReCode(-2);
            vo.setReMsg("角色不存在");
            return vo;
        }
        SysRoleMenuBean srm=null;
        this.commonDelete("baseRoleMenus","rId",rId);
        for(Integer mid:mIds){
            srm=new SysRoleMenuBean();
            srm.setrId(rId);
            srm.setmId(mid);
            this.commonInsert("baseRoleMenus",srm);
        }

        vo.setReCode(1);
        vo.setReMsg("授权成功");
        return vo;
    }
    public ResponseVO login(SysUserBean sysUserBean)throws Exception{
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
        Map searchMap=new HashMap();
        searchMap.put("uName",sysUserBean.getuName());
        searchMap.put("uPassword",sysUserBean.getuPassword());
        searchMap.put("disabled",false);

        List list=this.commonList("baseUser",null,null,null,searchMap);
        if(list!=null && !list.isEmpty()){
            vo.setReCode(1);
            vo.setReMsg("登录成功");
            Map user=(Map) list.get(0);
            vo.setData(user);
            return vo;
        }
        vo.setReCode(-2);
        vo.setReMsg("登录失败");
        return vo;
    }
    @Cacheable(value = "commonCache",key = "#uid")
    public List getPowerMenu(Integer uid){
        Cache cache=cacheManager.getCache("commonCache");

        return this.menuPowerMapper.queryById(uid);
    }
    public void resetPowerByRid(Integer rid)throws Exception{
        List<Map> lur=this.commonObjectsBySingleParam("baseUserRole","rid",rid);
        if(lur!=null && lur.size()>0){

            Cache cache=cacheManager.getCache("commonCache");
            Integer uid;

            for(Map map:lur){
                uid=(Integer)map.get("uId");
                if(cache.get(uid)!=null){
                    cache.put(uid,getPowerMenu(uid));
                }

            }

        }
    }
    public void resetPowerByMid(Integer mid)throws Exception{
        List<Integer> uidList=this.menuPowerMapper.queryUidByMid(mid);
        if(uidList!=null && uidList.size()>0){
            Cache cache=cacheManager.getCache("commonCache");
            for(Integer uid:uidList){
                if(cache.get(uid)!=null){
                    cache.put(uid,getPowerMenu(uid));
                }

            }
        }
    }
}
