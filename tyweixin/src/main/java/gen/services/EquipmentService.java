package gen.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import gen.framework.common.beans.CommonChildBean;
import gen.framework.common.beans.CommonCountBean;
import gen.framework.common.beans.CommonSearchBean;
import gen.framework.common.dao.CommonMapper;
import gen.framework.common.util.Page;

@Service
public class EquipmentService {
	
	@Autowired
	private CommonMapper commonMapper;
	
	public String equipmentInfo(String id){
		Map<String,Object> condition=new HashMap<String,Object>();
		condition.put("ID", id);//em_department
		CommonSearchBean csb=new CommonSearchBean("em_equipment",null,"t1.*,ct0.name sname",null,null,condition,
				new CommonChildBean("em_subject", "code", "SUBJECTCODE", null));
		List lists=this.commonMapper.selectObjects(csb);
		if(lists!=null && !lists.isEmpty()){
			return JSONObject.toJSONString(lists.get(0));
		}
		return null;
	}
	
	public String equipmentList(String typeName,String subjectName,String deptid,Integer pageNum,Integer pageSize) throws Exception{
		Page page=new Page(pageNum, pageSize);
		Map<String,Object> condition=new HashMap<String,Object>(); 

		if(StringUtils.isNotBlank(deptid)){
			
			condition.put("DEPARTMENTID", deptid);
		}
		condition.put("status,=", 1);
		
		Map<String,Object> condition_assettype=null;
		if(StringUtils.isNotBlank(typeName)){
			condition_assettype=new HashMap<String,Object>();
			
			condition_assettype.put("NAME,like", typeName.replaceAll("%", "")+"%");
		}
		Map<String,Object> condition_subject=null;
		if(StringUtils.isNotBlank(subjectName)){
			condition_subject=new HashMap<String,Object>();
			
			condition_subject.put("NAME,like", subjectName.replaceAll("%", "")+"%");
		}
		

		CommonSearchBean commonSearchBean=new CommonSearchBean("em_equipment","LASTUPDATE DESC","t1.NAME ename,t1.MODEL emodel,t1.CODE ecode,t1.id eid,t1.DESCRIPTION ,PATH,CONTENTTYPE",page.getStartRow(),page.getEndRow(),condition,
				new CommonChildBean("em_assettype", "code", "ASSETTYPECODE", condition_assettype),
				new CommonChildBean("em_subject", "code", "SUBJECTCODE", condition_subject),
				new CommonChildBean("(select * from em_equipmentphoto ee GROUP BY ee.EQUIPMENTID)", "EQUIPMENTID", "ID", null));

		CommonCountBean ccb = new CommonCountBean();

		PropertyUtils.copyProperties(ccb, commonSearchBean);
		long count = commonMapper.selectCount(ccb);
		if(count>0){
			List list=this.commonMapper.selectObjects(commonSearchBean);
			page.setResult(list);
			page.setTotal(count);
		}
		
		
		return JSONObject.toJSONString(page);
	}
}
