package gen.services;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import gen.beans.UserBean;
import gen.framework.common.beans.CommonCountBean;
import gen.framework.common.beans.CommonInsertBean;
import gen.framework.common.beans.CommonSearchBean;
import gen.framework.common.beans.CommonUpdateBean;
import gen.framework.common.dao.CommonMapper;
import gen.framework.common.util.Page;

@Service
public class UserService {
	@Autowired
	private CommonMapper commonMapper;
	
	@Value("${gen.framework.manager.urls}")
	private String managerurls;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public String add(UserBean user){
		JSONObject result=new  JSONObject();
		if(user==null){
			result.put("retCode", "-7");
			result.put("retMsg", "请填写用户信息");
			return result.toJSONString();
		}
		if(StringUtils.isBlank(user.getAccount())){
			result.put("retCode", "-10");
			result.put("retMsg", "请填写用户信息");
			return result.toJSONString();
		}
		Map<String,Object> condition=new HashMap<String,Object>();
		condition.put("account", user.getAccount());
		CommonCountBean csb=new CommonCountBean("em_user",condition);
		long size=this.commonMapper.selectCount(csb);
		if(size>0){
			result.put("retCode", "-11");
			result.put("retMsg", "此账号名已经被用了");
			return result.toJSONString();
		}
		user.setPassword("123456");
		user.setUserid(UUID.randomUUID().toString().replaceAll("-", ""));
		user.setRegistertime(new Date());
		CommonInsertBean cib=new CommonInsertBean("em_user",user);
		this.commonMapper.insertObject(cib);
		result.put("retCode", "1");
		result.put("retMsg", "添加成功");
		return result.toJSONString();
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public String update(UserBean user,String currentUserid){
		JSONObject result=new  JSONObject();
		if(user==null){
			result.put("retCode", "-7");
			result.put("retMsg", "请填写用户信息");
			return result.toJSONString();
		}
		if(StringUtils.isBlank(currentUserid)){
			result.put("retCode", "-13");
			result.put("retMsg", "请登录");
			return result.toJSONString();
		}

		Map<String,Object> condition=new HashMap<String,Object>();
		condition.put("account", user.getAccount());
		CommonCountBean csb=new CommonCountBean("em_user",condition);
		long size=this.commonMapper.selectCount(csb);
		if(size>0){
			result.put("retCode", "-11");
			result.put("retMsg", "此账号名已经被用了");
			return result.toJSONString();
		}
		condition.remove("account");
		condition.put("userid", currentUserid);
		CommonUpdateBean cub=new CommonUpdateBean("em_user", user, condition);
		this.commonMapper.updateObject(cub);
		result.put("retCode", "2");
		result.put("retMsg", "修改成功");
		return result.toJSONString();

	}
	public String login(String account,String password,String jumpurl,Map callbackMap){
		JSONObject result=new  JSONObject();
		if(StringUtils.isBlank(account) || StringUtils.isBlank(password)){
			result.put("retCode", "-14");
			result.put("retMsg", "请输入用户名或者密码");
			return result.toJSONString();
		}
		
		Map<String,Object> condition=new HashMap<String,Object>();
		condition.put("account", account);
		condition.put("password", password);
		CommonSearchBean csb=new CommonSearchBean("em_user", null, "userid,account,name", null,null,condition);
		List userIdList=this.commonMapper.selectObjects(csb);
		if(userIdList!=null && !userIdList.isEmpty()){
			Map usermap=(Map)userIdList.get(0);
			callbackMap.put("userInfo",usermap );
			result.put("retCode", "1");
			result.put("retMsg", "登录成功");
			condition.clear();
			condition.put("userid,=", usermap.get("userid"));
			long n=this.commonMapper.selectCount(new CommonCountBean("em_manageequip", condition));
			
			callbackMap.put("isManager", n>0);
			
			if((n>0 || account.equals("sysadmin")) && !jumpurl.startsWith("/manager/")){
				//List<String> managerUrlList=Arrays.asList(managerurls.replaceAll(" ", "").split(","));
				jumpurl=managerurls.replaceAll(" ", "").split(",")[0];
			}
			result.put("jumpurl", jumpurl);
		}else{
			result.put("retCode", "-15");
			result.put("retMsg", "登录失败");
		}
		return result.toJSONString();
	}
	public Page list(Integer pageNum, Integer pageSize)throws Exception{
		Page page=new Page(pageNum, pageSize);
		Map<String,Object> condition=null;;


		CommonSearchBean csb=new CommonSearchBean("em_user","REGISTERTIME  DESC",null, page.getStartRow(),page.getEndRow(),condition);
		CommonCountBean ccb = new CommonCountBean();

		PropertyUtils.copyProperties(ccb, csb);
		long count = commonMapper.selectCount(ccb);
		if(count>0){
			List list=this.commonMapper.selectObjects(csb);
			System.out.println(list);
			page.setResult(list);
			page.setTotal(count);
		}

		return page;
	}
	
}
