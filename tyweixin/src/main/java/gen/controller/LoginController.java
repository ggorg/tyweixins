package gen.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import gen.services.UserService;

@Controller

public class LoginController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
		@Autowired
		private UserService userService;
	
		@RequestMapping("/ajaxLogin")
		@ResponseBody
		public String login(String account,String password,String jumpurl,HttpSession session){
			try {
				Map callbackMap=new HashMap();
				String callback=userService.login(account, password,jumpurl,callbackMap);
				if(callback.indexOf("登录成功")>-1){
					
					session.setAttribute("loginInfo", callbackMap.get("userInfo"));
					session.setAttribute("isManager", callbackMap.get("isManager"));
				}
				return callback;
			} catch (Exception e) {
				logger.error("LoginController.login",e);
				e.printStackTrace();
				return "{\"retCode\":-1,\"retMsg\":\"系统出现异常\"}";
			}
		}
		@RequestMapping("/doLogout")
		public String logout(HttpSession session){
			session.removeAttribute("loginInfo");
			session.invalidate();
			return "pages/login";
		}
}
