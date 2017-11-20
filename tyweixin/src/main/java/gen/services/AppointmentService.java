package gen.services;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import gen.beans.AppointmentBean;
import gen.framework.common.beans.CommonChildBean;
import gen.framework.common.beans.CommonCountBean;
import gen.framework.common.beans.CommonInsertBean;
import gen.framework.common.beans.CommonSearchBean;
import gen.framework.common.beans.CommonUpdateBean;
import gen.framework.common.dao.CommonMapper;
import gen.framework.common.util.Page;

@Service
public class AppointmentService {
	
	@Autowired
	private CommonMapper commonMapper;
	
	public JSONObject checkRole(String equipmentid,String userid){
		JSONObject result=new  JSONObject();
		LinkedHashMap condtion_check_role=new LinkedHashMap();
		condtion_check_role.put("equipmentid,=", equipmentid);
		condtion_check_role.put("userid,=", userid);
		condtion_check_role.put("status,=", 1);
		long num_check_role=this.commonMapper.selectCount(new CommonCountBean("em_equipuser",condtion_check_role));
		if(num_check_role==0){
			result.put("retCode", "-9");
			result.put("retMsg", "抱歉，你没有这个设备的预约权限");

			return result;
		}
		result.put("retCode", "1");
		result.put("retMsg", "yes");
		return result;
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public String submit(AppointmentBean appointmentBean){
		JSONObject result=new  JSONObject();
		if(appointmentBean==null){
			result.put("retCode", "-7");
			result.put("retMsg", "请填写预约信息");
			return result.toJSONString();
		}
		JSONObject callMap=checkRole(appointmentBean.getEquipmentid(),appointmentBean.getUserid());
		if(callMap.getString("retCode").equals("-9")){
			return callMap.toJSONString();
		}
		if(StringUtils.isBlank(appointmentBean.getProjectname())){
			result.put("retCode", "-1");
			result.put("retMsg", "项目名为空");
			return result.toJSONString();
		}
		if(StringUtils.isBlank(appointmentBean.getProjecttypecode())){
			result.put("retCode", "-2");
			result.put("retMsg", "项目类型为空");
			return result.toJSONString();
		}
		if(appointmentBean.getBegintime()==null){
			result.put("retCode", "-3");
			result.put("retMsg", "开始时间为空");
			return result.toJSONString();
		}
		if(appointmentBean.getEndtime()==null){
			result.put("retCode", "-4");
			result.put("retMsg", "结束时间为空");
			return result.toJSONString();
		}
		if(StringUtils.isBlank(appointmentBean.getSamplename())){
			result.put("retCode", "-5");
			result.put("retMsg", "样品名称为空");
			return result.toJSONString();
		}
		if(appointmentBean.getSamples()==null || appointmentBean.getSamples()<=0){
			result.put("retCode", "-6");
			result.put("retMsg", "样品数量不能为空或者小于1");
			return result.toJSONString();
		}

		LinkedHashMap condtion=new LinkedHashMap();
		condtion.put("((", null);
		condtion.put("begintime,<=",appointmentBean.getBegintime());
		condtion.put("and", null);
		condtion.put("endtime,>=",appointmentBean.getBegintime());
		condtion.put(") or (", null);
		condtion.put("begintime,<= ",appointmentBean.getEndtime());
		condtion.put("and ", null);
		condtion.put("endtime,>= ",appointmentBean.getEndtime());
		condtion.put("))", null);
		condtion.put("and  ", null);
		condtion.put("equipmentid", appointmentBean.getEquipmentid());
		CommonCountBean ccb=new CommonCountBean("em_appointment_wx",condtion);
		
		
		ccb.setAuto(false);
		long num=this.commonMapper.selectCount(ccb);
		if(num>0){
			result.put("retCode", "-7");
			result.put("retMsg", "时间段已经被预约了，请重新选一个");
			return result.toJSONString();
		}
				
		appointmentBean.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		appointmentBean.setStatus(0);
		appointmentBean.setApplytime(new Date());
		CommonInsertBean cib=new CommonInsertBean("em_appointment_wx", appointmentBean);
		
		this.commonMapper.insertObject(cib);
		result.put("retCode", "1");
		result.put("retMsg", "申请预约成功");
		return result.toJSONString();
		
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public String assess(String assess,String aid){
		AppointmentBean appointmentBean=new AppointmentBean();
		appointmentBean.setId(aid);
		appointmentBean.setAssess(StringUtils.isBlank(assess)?"无":assess);
		return this.update(appointmentBean,"评价成功");
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public String shenpi(String id,String auditmessage,Integer status){
		JSONObject result=new  JSONObject();

		if(status==null){
			result.put("retCode", "-20");
			result.put("retMsg", "请选择审批类型");
			return result.toJSONString();
		}
		AppointmentBean appointmentBean=new AppointmentBean();
		appointmentBean.setId(id);
		appointmentBean.setAuditmessage(auditmessage);
		appointmentBean.setStatus(status);
		appointmentBean.setAudittime(new Date());
		return this.update(appointmentBean,"审批成功");
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public String update(AppointmentBean appointmentBean,String retMsg){
		JSONObject result=new  JSONObject();
		if(appointmentBean==null){
			result.put("retCode", "-7");
			result.put("retMsg", "请填写修改的资料");
			return result.toJSONString();
		}
		if(StringUtils.isBlank(appointmentBean.getId())){
			result.put("retCode", "-8");
			result.put("retMsg", "流水号为空");
			return result.toJSONString();
		}
	/*	Map<String,Object> params=new HashMap<String,Object>();
		if(appointmentBean.getBegintime()!=null && appointmentBean.getEndtime()!=null){
			params.put("begintime", appointmentBean.getBegintime());
			params.put("endtime", appointmentBean.getEndtime());
			result.put("retMsg", "修改成功");
		}else if(appointmentBean.getStatus()!=null && appointmentBean.getStatus()==-1){
			params.put("status", appointmentBean.getStatus());
			result.put("retMsg", "取消成功");
		}*/
		
		Map<String,Object> condition=new HashMap<String,Object>();
		condition.put("id", appointmentBean.getId());
		CommonUpdateBean cub=new CommonUpdateBean("em_appointment_wx", appointmentBean, condition);
		this.commonMapper.updateObject(cub);
		result.put("retCode", "1");
		result.put("retMsg", retMsg);
		
		return result.toJSONString();
		
	}
	public Page list(String userid,Integer pageNum,Integer pageSize)throws Exception{
	
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

		return page;
	}
	public Page managerList(String userid,Integer pageNum,Integer pageSize)throws Exception{
		
		Page page=new Page(pageNum, pageSize);
		Map<String,Object> condition=null;;

		if(StringUtils.isNotBlank(userid)){
			 condition=new HashMap<String,Object>();
			condition.put("userid,=", userid);
		}
		condition.put("status,=", 1);
		CommonSearchBean csb=new CommonSearchBean("em_appointment_wx","applytime  DESC",null, page.getStartRow(),page.getEndRow(),null,new CommonChildBean("em_manageequip", "equipmentid", "equipmentid", condition));
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
	public String appoCircs(String equipmentid,Date date) throws Exception{
		JSONObject result=new  JSONObject();
		
		if(StringUtils.isBlank( equipmentid)){
			result.put("retCode", "-8");
			result.put("retMsg", "缺少流水号参数");
			return result.toJSONString();
		}
		LinkedHashMap<String,Object>  condition=new LinkedHashMap<String,Object>();
		
		if(date==null){
			date=DateUtils.parseDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd 00:00:00"), "yyyy-MM-dd HH:mm:ss");
		}
		

	Date new2=DateUtils.parseDate(DateFormatUtils.format(date, "yyyy-MM-dd 23:59:59"), "yyyy-MM-dd HH:mm:ss");
		condition.put(" (", null);
		condition.put("(", null);
		condition.put("begintime,<=", date);
		condition.put("and", null);
		condition.put("endtime,>=", date);
		condition.put(") or (", null);
		condition.put("begintime,<= ", new2);
		condition.put("and ", null);
		condition.put("endtime,>= ", new2);
		condition.put(")", null);
		condition.put(") ", null);
		condition.put("and  ", null);
		condition.put("equipmentid", equipmentid);
		CommonSearchBean csb=new CommonSearchBean("em_appointment_wx","applytime  DESC","date_format(begintime,'%Y%c%d%H%i') begindate,date_format(endtime,'%Y%c%d%H%i') enddate,date_format(audittime,'%Y%c%d %H%i') auditdate", null,null,condition);
		csb.setAuto(false);


		result.put("retCode", "1");
		result.put("retMsg", "success");
		result.put("result", this.commonMapper.selectObjects(csb));
	
		return result.toJSONString();
	}
	public static void main(String[] args) {
		String key="wef";
		System.out.println(") ".matches("[)]"));
	}
}
