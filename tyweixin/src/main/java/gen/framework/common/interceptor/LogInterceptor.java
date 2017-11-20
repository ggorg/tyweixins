package gen.framework.common.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;

/**
 * 记录请求相关日志
 * @author cancheung
 *
 */
@Component
public class LogInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = Logger.getLogger(LogInterceptor.class);
	private ThreadLocal<Long> startTime = new ThreadLocal<>();
	
	@Value("${gen.framework.login.intercept.urls}")
	private String interUrl;
	
	@Value("${gen.framework.manager.urls}")
	private String managerurls;

	@Value("${gen.framework.login.url}")
	private String loginUrl;
	
	@Value("${gen.framework.logout.url}")
	private String logoutUrl;
	
	@Value("${gen.framework.web.urls}")
	private String webUrls;
	
	private static final String toRegex=".*/to[^?./]+.*";
	private static final String doRegex=".*/do[^?./]+.*";
	private static final String ajaxRegex=".*/ajax[^?./]+.*";
	
	private List<String> urlList;
	private List<String> managerUrlList;
	private List<String> webUrlsList;
	

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String path=request.getRequestURI();
		String referer=request.getHeader("referer");
		if(StringUtils.isNotBlank(referer))referer=referer.replaceAll("http://[^/]+", "");
		if(urlList==null)urlList=Arrays.asList(interUrl.replaceAll(" ","").split(","));
		if(managerUrlList==null)managerUrlList=Arrays.asList(managerurls.replaceAll(" ", "").split(","));
		if(webUrlsList==null)webUrlsList=Arrays.asList(webUrls.replaceAll(" ", "").split(","));
		if(path.startsWith("/manager/")){
			
			if(managerUrlList.contains(path)){
				Map<String,String> loginInfo=getLoginInfo(request);
				if(loginInfo==null){
					this.setLoginToJumpUrl(path, request, response);
					
					return false;
				}else if("sysadmin".equals(loginInfo.get("account")) || isManager(request)){
					return true;
				}else{
					this.setLoginToJumpUrl(path, request, response);
					
					return false;
				}
			}
			return false;

		}
		if(interUrl!=null){
			
			
			if(urlList.contains(path)){
				Map<String,String> loginInfo=getLoginInfo(request);

				if(loginInfo==null){
					this.setLoginToJumpUrl(path, request, response);
					return false;
				}
			}
		}
		if(request.getAttribute("jumpurl")!=null && StringUtils.isBlank(referer) && path.equals(loginUrl)){
			return true;
		}else if(StringUtils.isBlank(referer) && path.equals(loginUrl)){
			Map<String,String> loginInfo=getLoginInfo(request);
			if(loginInfo==null){
				request.setAttribute("jumpurl", webUrlsList.get(0));
				return true;
			}else{
				response.sendRedirect(webUrlsList.get(0));
				return false;
			}
		}else if(webUrlsList.contains(referer) && path.equals(loginUrl)){
			Map<String,String> loginInfo=getLoginInfo(request);
			if(loginInfo==null){
				request.setAttribute("jumpurl", referer);
				return true;
			}else{
				response.sendRedirect(referer);
				return false;
			}
		}
		if(path.equals(logoutUrl)){
			request.setAttribute("jumpurl", referer);
		}
		return true;
	}
	private Map<String,String> getLoginInfo(HttpServletRequest request){
		HttpSession session=request.getSession();
		
		Map<String,String> loginInfo=(Map<String,String>)session.getAttribute("loginInfo");
		return loginInfo;
		
	}
	private boolean isManager(HttpServletRequest request){
		HttpSession session=request.getSession();
		
		Boolean isManager=(Boolean)session.getAttribute("isManager");
		if(isManager==null){
			isManager=false;
		}
		return isManager;
		
	}
	private void setLoginToJumpUrl(String path,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(path.matches(toRegex) || path.matches(doRegex)){
			request.setAttribute("jumpurl", path);
			request.getRequestDispatcher(loginUrl).forward(request, response);
		}else if(path.matches(ajaxRegex)){
			PrintWriter out=response.getWriter();	
			JSONObject jsonObject=new  JSONObject();
			jsonObject.put("retCode", 10000);
			jsonObject.put("jumpurl", loginUrl);
			out.print(jsonObject.toJSONString());
			out.close();
			out.flush();
		}
	}
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}
	public static void main(String[] args) {
		System.out.println("/wegweg/doxxxxxx".matches(".*/to[^?.]+.*"));
	}

}