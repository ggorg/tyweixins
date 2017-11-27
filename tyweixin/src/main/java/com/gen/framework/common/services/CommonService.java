package com.gen.framework.common.services;

import com.gen.framework.common.beans.CommonCountBean;
import com.gen.framework.common.beans.CommonInsertBean;
import com.gen.framework.common.beans.CommonSearchBean;
import com.gen.framework.common.dao.CommonMapper;
import com.gen.framework.common.util.BeanToMapUtil;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class CommonService {

    @Autowired
    private CommonMapper commonMapper;

    public ResponseVO commonInsert(String tableName,Object bean){
        ResponseVO vo=new ResponseVO();
        Map params= BeanToMapUtil.beanToMap(bean);
        params.put("createTime",new Date());
        params.put("updatetime",new Date());
        params.put("isDelete",false);
        this.commonMapper.insertObject(new CommonInsertBean(tableName,params));
        vo.setReCode(1);
        vo.setReMsg("创建成功");
    }

    public Page commonList(String tableName,String ordername,Integer pageNum,Integer pageSize,Map<String,Object> condition)throws Exception{
        Page page=new Page(pageNum,pageSize);

        CommonSearchBean csb=new CommonSearchBean(tableName,"null",null, page.getStartRow(),page.getEndRow(),condition);
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
}
