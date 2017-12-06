package com.gen.framework.common.interceptor;

import com.gen.framework.common.beans.SysMenuBean;
import com.gen.framework.common.services.SysManagerService;
import com.gen.framework.common.thymeleaf.Tools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Value("${gen.framework.menus.filter.urls}")
    private String filterUrls;
   // private List filterList;

    @Value("${gen.framework.manager.url.prefix}")
    private String managerPrefixUrls;
   // private List managerPrefixUrlsList;
    @Autowired
    private SysManagerService sysManagerService;



    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception{
        String path=request.getRequestURI();
        String prefixPath=path.replaceAll("^(/[^/]+).*$","$1");
        if(filterUrls.indexOf("*")>-1 && filterUrls.indexOf(".")==-1){
            filterUrls=filterUrls.replaceAll("[*]",".*");
        }
       /* if(path.matches("^("+filterUrls+").*$")){

        }
        if(filterList==null && StringUtils.isNotBlank(filterUrls)){
            filterList= Arrays.asList(filterUrls.replaceAll(" ","").split(","));
        }
        if(managerPrefixUrlsList==null && StringUtils.isNotBlank(managerPrefixUrls)){
            managerPrefixUrlsList= Arrays.asList(managerPrefixUrls.replaceAll(" ","").split(","));
        }*/

        if(prefixPath.matches("^("+managerPrefixUrls+")$") && !path.matches("^("+filterUrls+")$")){
            if(!Tools.isLogin()){
                response.sendRedirect("/");
                return false;
            }else{
                List<Map> allMenus=sysManagerService.getAllMenu();
                List<SysMenuBean> menus=Tools.getUserPowerMenu();
                if(allMenus!=null){
                    String mUrl=null;
                    for(Map am:allMenus){
                        mUrl=(String)am.get("mUrl");
                        if(path.equals(mUrl)){
                            if(menus!=null && !menus.isEmpty()){
                                for(SysMenuBean sysMenuBean:menus){
                                    if(sysMenuBean.getmUrl().equals(path)){
                                        return true;
                                    }
                                }
                            }
                            response.sendRedirect("/404");
                            return false;

                        }
                    }
                }
            }
        }

        return true;
    }

}
