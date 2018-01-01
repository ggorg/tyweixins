package com.ty.controller;

import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.services.CacheService;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.entity.Pubweixin;
import com.ty.entity.Tags;
import com.ty.entity.UserInfo;
import com.ty.services.PubWeixinService;
import com.ty.services.TagsService;
import com.ty.services.WeixinInterfaceService;
import com.ty.services.WeixinUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 微信用户资料查询接口
 */
@Controller
@RequestMapping(value = "weixin/user")
public class WeixinUserController{
    private static final Logger logger = Logger.getLogger("WeixinUserController");
    @Autowired
    private WeixinUserService weixinUserService;
    @Autowired
    private PubWeixinService pubWeixinService;
    @Autowired
    private TagsService tagsService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;

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
        cacheService.set("userSearch",userInfo);
        userInfo.setAppid(appid);
        Page<UserInfo> page = weixinUserService.findUser(pageNo,userInfo);
        Tags tag = new Tags();
        tag.setAppid(appid);
        model.addAttribute("tagsList",tagsService.findListAll(tag));
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("appid",appid);
        model.addAttribute("userPage", page);
        model.addAttribute("pubweixinList",pubweixinList);
        return "pages/manager/weixin/user";
    }

    @GetMapping("edit")
    public String edit(UserInfo userInfo,String batch,Model model){
        try {
            String appid = "";
            //批量修改微信用户标签
            if(batch != null && batch.equals("1")) {
                UserInfo userSearch = (UserInfo) cacheService.get("userSearch");
                appid = userSearch.getAppid();
            }else{
                if(userInfo != null){
                    userInfo = weixinUserService.selectByopenid(userInfo.getOpenid());
                    appid = userInfo.getAppid();
                    model.addAttribute("userInfoObject",userInfo);
                }
            }
            Tags tag = new Tags();
            tag.setAppid(appid);
            model.addAttribute("batch",batch);
            model.addAttribute("tagsList",tagsService.findListAll(tag));
        }catch (Exception e){
            logger.error("WeixinUserController->edit->系统异常",e);

        }
        return "pages/manager/weixin/userEdit";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public ResponseVO save(UserInfo userInfo,String batch) {
        try {
            //批量修改微信用户标签
            if(batch != null && batch.equals("1")){
                UserInfo userSearch = (UserInfo)cacheService.get("userSearch");
                if(userSearch !=null){
                    List<UserInfo>userInfoList = weixinUserService.findUserAll(userSearch);
                    List<String>openid = new ArrayList<String>();
                    for(UserInfo ui:userInfoList){
                        openid.add(ui.getOpenid());
                    }
                    for(String tagid:userInfo.getTagid_list().split(",")){
                        JSONObject json = weixinInterfaceService.batchTaggingMembers(userSearch.getAppid(),Integer.valueOf(tagid),openid);
                    }
                    return new ResponseVO(1,"成功",null);
                }else{
                    return new ResponseVO(-2,"查询条件为空",null);
                }
            }else{
                //单个更新微信用户标签
                return weixinUserService.update(userInfo);
            }
        } catch (Exception e) {
            logger.error("WeixinUserController->save->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }
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
    @RequestMapping(value="alluser", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
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
