package com.gen.framework.common.services;

import com.gen.framework.common.beans.CommonCountBean;
import com.gen.framework.common.beans.CommonSearchBean;
import com.gen.framework.common.dao.CommonMapper;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO saveUser(){

    }

}
