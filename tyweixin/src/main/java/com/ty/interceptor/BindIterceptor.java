package com.ty.interceptor;

import com.gen.framework.common.beans.SysMenuBean;
import com.gen.framework.common.services.SysManagerService;
import com.gen.framework.common.util.Tools;
import com.ty.services.TyBindService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Component

public class BindIterceptor extends HandlerInterceptorAdapter {
    @Value("${gen.framework.wap.bind.filter.urls}")
    private String filterUrls;
   // private List filterList;

    @Value("${gen.framework.wap.url.prefix}")
    private String wapPrefixUrls;
   // private List managerPrefixUrlsList;
    @Autowired
    private TyBindService tyBindService;



    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception{
        String path=request.getRequestURI();
        String prefixPath=path.replaceAll("^(/[^/]+).*$","$1");
        if(filterUrls.indexOf("*")>-1 && filterUrls.indexOf(".")==-1){
            filterUrls=filterUrls.replaceAll("[*]",".*");
        }


        if(prefixPath.matches("^("+wapPrefixUrls+")$") && !path.equals("/wap/to-sessionTimeOut")){
            String openid=Tools.setOpenidByThreadLocal(request.getParameter("token"));
            if(StringUtils.isBlank(openid)){
                response.sendRedirect("/wap/to-sessionTimeOut");
                return false;
            }

            if(!path.matches("^("+filterUrls+")$") && tyBindService.checkIsBind(openid)==null){
                response.sendRedirect("/wap/to-bind-telphone");
                return false;
            }
        }

        return true;
    }

}
