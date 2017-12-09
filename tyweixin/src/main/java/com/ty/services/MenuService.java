package com.ty.services;

import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.core.pojo.Button;
import com.ty.core.pojo.CommonButton;
import com.ty.core.pojo.Menu;
import com.ty.dao.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 微信菜单Service
 * @author Jacky
 *
 */
@Service
public class MenuService {
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private MenuMapper menuMapper;


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

    /**
     * 根据appid查询自定义菜单
     * @param appid 应用id
     * @return
     */
    public List<com.ty.entity.Menu> findList(String appid){
        return menuMapper.findList(appid);
    }

    /**
     * 根据主键id查询子菜单
     * @param id 菜单主键id
     * @return
     */
    public List<com.ty.entity.Menu> findListById(Integer id){
        return menuMapper.findListById(id);
    }

    /**
     * 根据菜单主键查询菜单配置
     * @param id 主键
     * @return
     */
    public com.ty.entity.Menu selectById(int id){
        return menuMapper.selectById(id);
    }

    /**
     * 插入或更新菜单
     * @param menu
     * @return
     */
    public ResponseVO saveOrUpdate(com.ty.entity.Menu menu){
        ResponseVO vo=new ResponseVO();
        com.ty.entity.Menu pw = menuMapper.selectById(menu.getId());
        if(pw!=null){
            menuMapper.update(menu);
            vo.setReMsg("修改成功");
        }else{
            menuMapper.insert(menu);
            vo.setReMsg("创建成功");
        }
        vo.setReCode(1);
        return vo;
    }

    /**
     * 删除菜单
     * @param menu
     * @return
     */
    public ResponseVO delete(com.ty.entity.Menu menu){
        ResponseVO vo=new ResponseVO();
        int res = menuMapper.delete(menu);
        if(res>0){
            vo.setReCode(1);
            vo.setReMsg("删除成功");
        }else{
            vo.setReCode(-2);
            vo.setReMsg("删除失败");
        }
        return vo;
    }

}
