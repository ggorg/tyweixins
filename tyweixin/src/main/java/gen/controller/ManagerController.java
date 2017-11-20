package gen.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import gen.beans.UserBean;
import gen.services.AppointmentService;
import gen.services.UserService;

@Controller
@RequestMapping("/manager")
public class ManagerController {
	private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);
	@Autowired
	private AppointmentService appointmentService;
	@Autowired
	private UserService userService;
	
	@RequestMapping("toAppointmentList")
	public String toCommon(
			@SessionAttribute("loginInfo")Map<String,String> loginInfo,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
			ModelMap model){
		try {
			if(loginInfo.get("account").equals("sysadmin")){
				model.addAttribute("appoPage", appointmentService.list(null, pageNum, pageSize));
			}else{
				model.addAttribute("appoPage", appointmentService.managerList(loginInfo.get("userid"), pageNum, pageSize));
			}
			
		} catch (Exception e) {
			logger.error("ManagerController.toCommon",e);
			// TODO: handle exception
			e.printStackTrace();
		}
	
		return "pages/manager/commonPage";
	}
	@RequestMapping("toUserList")
	public String userList(
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
			ModelMap model){
		try {
			model.addAttribute("userPage", this.userService.list(pageNum, pageSize));
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("ManagerController.userList",e);
			e.printStackTrace();
		}
		return "pages/manager/commonPage";
	}
	@RequestMapping("ajaxShenpi")
	@ResponseBody
	public String shenpi(String id,String auditmessage,Integer status){
		try {

			return this.appointmentService.shenpi( id, auditmessage, status);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("ManagerController.shenpi",e);
			e.printStackTrace();
			return "{\"retCode\":-1,\"retMsg\":\"系统出现异常\"}";
		}
	}
	@RequestMapping("ajaxAddUser")
	@ResponseBody
	public String addUser(UserBean user){
		try {
			
			return this.userService.add(user);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("ManagerController.addUser",e);
			e.printStackTrace();
			return "{\"retCode\":-1,\"retMsg\":\"系统出现异常\"}";
		}
	}
}
