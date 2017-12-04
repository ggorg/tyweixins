package com.gen.framework.common.interceptor;

import com.gen.framework.common.beans.SysMenuBean;
import com.gen.framework.common.thymeleaf.Tools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Value("${gen.framework.menus.filter.urls}")
    private String filterUrls;
    private List filterList;



    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception{
        String path=request.getRequestURI();
        if(filterList==null && StringUtils.isNotBlank(filterUrls)){
            filterList= Arrays.asList(filterUrls.replaceAll(" ","").split(","));
        }
        if(!filterList.contains(path)){
            if(!Tools.isLogin()){
                response.sendRedirect("/");
                return false;
            }else{
                List menus=Tools.getUserPowerMenu();
                if(menus!=null && !menus.isEmpty()){
                    SysMenuBean menu=null;
                    for(Object obj:menus){
                        menu=(SysMenuBean)obj;
                        if(menu.getmUrl().equals(path)){
                            return true;
                        }
                    }
                    response.sendRedirect("/404");
                    return false;
                }
            }
        }

        return true;
    }
}
