package com.ty.controller;

import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.util.Page;
import com.ty.entity.UserInfo;
import com.ty.services.WeixinUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信用户资料查询接口
 */
@Controller
@RequestMapping(value = "weixin/user")
public class WeixinUserController{

    @Autowired
    private WeixinUserService weixinUserService;

    @RequestMapping(value = {"list", ""})
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, Model model) {
        Page<UserInfo> page = weixinUserService.findUser(pageNo,new UserInfo());
        model.addAttribute("userPage", page);
        return "pages/manager/weixin/user";
    }

    /**
     * 修改微信用户备注
     * 
     * @param appid
     * @param openid
     * @param remark
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"updateRemark"})
    public JSONObject updateRemark(String appid, String openid, String remark, HttpServletRequest request, HttpServletResponse response) {
        return weixinUserService.updateRemark(appid, openid, remark);
    }

    /**
     * 更新关注该公众号用户表信息
     * @param appid 应用id
     * @return
     */
    @ResponseBody
    @RequestMapping(value="alluser", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getAllUser(String appid) {
        JSONObject result;
        if (appid != null) {
            result = new JSONObject();
            JSONObject  jsonObject = weixinUserService.updateOrSaveUser(appid);
            result.put("retCode", 1);
            result.put("retMsg", String.format("关注该公众账号的总用户数:%d 初始化入库", jsonObject.getInteger("total")));
        } else {
            result = new JSONObject();
            result.put("retCode", -1);
            result.put("retMsg", "参数有误");
        }
        return result.toString();
    }
}
