package com.ty.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")		
public class DemoController {
	public DemoController() {
		System.out.println("0000000000");
		// TODO Auto-generated constructor stub
	}
	@GetMapping
	public String demo(){
		return "pages/manager/table_complete";
	}

}
