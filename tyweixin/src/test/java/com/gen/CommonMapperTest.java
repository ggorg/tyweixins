package com.gen;

import com.gen.framework.common.beans.CommonCountBean;
import com.gen.framework.common.beans.CommonSearchBean;
import com.gen.framework.common.dao.CommonMapper;
import com.gen.framework.common.util.Page;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CommonMapperTest {

    @Autowired
    private CommonMapper commonMapper;

    @Test
    public void list()throws Exception{
        String userid="";
        Integer pageNum=1;
        Integer pageSize=10;

        Page page=new Page(pageNum, pageSize);
        Map<String,Object> condition=null;;

        if(StringUtils.isNotBlank(userid)){
            condition=new HashMap<String,Object>();
            condition.put("userid", userid);
        }
        CommonSearchBean csb=new CommonSearchBean("em_appointment_wx","applytime  DESC",null, page.getStartRow(),page.getEndRow(),condition);
        CommonCountBean ccb = new CommonCountBean();

        PropertyUtils.copyProperties(ccb, csb);
        long count = commonMapper.selectCount(ccb);
        if(count>0){
            List list=this.commonMapper.selectObjects(csb);
            page.setResult(list);
            page.setTotal(count);
        }


    }
}
