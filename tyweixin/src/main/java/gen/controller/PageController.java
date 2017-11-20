package gen.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

import gen.framework.common.TestDirective;
import gen.services.DirectoryDataService;

@Controller
@RequestMapping("/pages")
public class PageController {
	
	private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);
	

	
	@Autowired
	private DirectoryDataService directoryDataService;
	@RequestMapping("toEquipmentList")
	public String toEquipmentList(ModelMap model){
		try {
			ArrayList list=(ArrayList)this.directoryDataService.get("dept");

			model.addAttribute("deptListTop10", list.subList(0, 10));
			model.addAttribute("deptList",JSONObject.toJSONString(list.subList(10, list.size())));
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("PageController.toEquipmentList",e);
		}


		return "pages/equipmentList";
	}
	@RequestMapping("toTest")
	public String toTest(ModelMap model){
		model.addAttribute("abc", new TestDirective());
		return "pages/test";
	}
	
	@RequestMapping("toLogin")
	public String toLogin(ModelMap model,HttpServletRequest request){
		/*String referer=request.getHeader("referer");
		System.out.println(request.getRequestURI());
		if(referer==null && request.getRequestURI().startsWith("/manager/")){
			referer=request.getRequestURI();
		}else if(referer==null || referer.equals(loginurl)){
			referer=weburl;
		}
		model.addAttribute("jumpurl", referer);*/
		return "pages/login";
	}
	@RequestMapping("toShenPi")
	public String toShenPi(String eid){
		return "pages/manager/shenpi";
	}
	@RequestMapping("toEditUser")
	public String toEditUser(String eid){
		return "pages/manager/editUser";
	}
	@RequestMapping("toEquipmentCircs")
	public String toEquipmentCircs(String eid){
		return "pages/equipmentCircs";
	}
	@RequestMapping("toAssess")
	public String toAssess(String aid){
		return "pages/assess";
	}

	@RequestMapping("toEditAppol")
	public String toEditAppol(String eid,ModelMap model){
		try {
			ArrayList projectlist=(ArrayList)this.directoryDataService.get("projecttype");
			ArrayList useTypeCodelist=(ArrayList)this.directoryDataService.get("useTypeCode");
			model.addAttribute("projecttype",projectlist);
			model.addAttribute("useTypeCode",useTypeCodelist);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("PageController.toEditAppol",e);
		}

		return "pages/appo";
	}
}
