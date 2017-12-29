package com.ty.controller;

import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.services.CacheService;
import com.gen.framework.common.util.Page;
import com.ty.entity.Pubweixin;
import com.ty.entity.UserInfo;
import com.ty.services.PubWeixinService;
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
import java.util.List;

/**
 * 微信用户资料查询接口
 */
@Controller
@RequestMapping(value = "weixin/user")
public class WeixinUserController{

    @Autowired
    private WeixinUserService weixinUserService;
    @Autowired
    private PubWeixinService pubWeixinService;
    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = {"list", ""})
    public String list(UserInfo userInfo,@RequestParam(defaultValue = "1") Integer pageNo, String appid, Model model) {
        List<Pubweixin> pubweixinList = pubWeixinService.findPubweixinAll();
        if(appid == null){
            appid = (String) cacheService.get("appid");
            if(appid == null || appid.equals("")){
                if(pubweixinList.size()>0){
                    appid = pubweixinList.get(0).getAppid();
                }
            }
        }else{
            cacheService.set("appid",appid);
        }
        userInfo.setAppid(appid);
        Page<UserInfo> page = weixinUserService.findUser(pageNo,userInfo);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("appid",appid);
        model.addAttribute("userPage", page);
        model.addAttribute("pubweixinList",pubweixinList);
        return "pages/manager/weixin/user";
    }

    /**
     * 供异步调用的微信用户数据
     * @param userInfo 筛选条件
     * @param pageNo 页数
     * @param appid 应用id
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value="userInfoPage", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Page<UserInfo> userInfoPage(UserInfo userInfo,@RequestParam(defaultValue = "1") Integer pageNo, String appid, Model model){
        if(appid == null){
            appid = cacheService.get("appid").toString();
        }
        Page<UserInfo> page = weixinUserService.findUser(pageNo,userInfo);
        return page;
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
