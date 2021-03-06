package com.gen.framework.common.config;

import com.gen.framework.common.interceptor.LoginInterceptor;
import com.ty.interceptor.BindIterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter{
	

	@Autowired
	private LoginInterceptor loginInterceptor;
	@Autowired
	private BindIterceptor bindIterceptor;
	public void addInterceptors(InterceptorRegistry registry) {
		//System.out.println(	123);
		registry.addInterceptor(loginInterceptor);
		registry.addInterceptor(bindIterceptor);

	}
}
