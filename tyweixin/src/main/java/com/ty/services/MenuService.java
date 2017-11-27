package com.ty.services;

import com.alibaba.fastjson.JSONObject;
import com.ty.core.pojo.Button;
import com.ty.core.pojo.CommonButton;
import com.ty.core.pojo.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 微信菜单Service
 * @author Jacky
 *
 */
@Service
public class MenuService {
	@Autowired
	private WeixinInterfaceService weixinInterfaceService;
	
	/**
	 * 普通
	 * @param appid 应用ID
	 * @param name 菜单名称
	 * @param type click：点击推事件
	 * @param key
	 * @return
	 */
	public JSONObject commonButton(String appid, String name, String type, String key){
		JSONObject jsonObject = new JSONObject();
		CommonButton btn = new CommonButton();
		btn.setName(name);
		btn.setType(type);
		btn.setKey(key);
		Menu menu = new Menu();
		menu.setButton(new Button[] { btn });
		jsonObject = weixinInterfaceService.createMenu(appid, menu);
		return jsonObject;
	}
	/**
	 * 获取自定义菜单配置接口
	 * @param appid 应用id
	 * @return
	 */
	public JSONObject getCurrentSelfMenu(String appid){
		return weixinInterfaceService.getCurrentSelfMenu(appid);
	}
}
