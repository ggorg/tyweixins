package com.ty.controller;

import com.alibaba.fastjson.JSONObject;
import com.ty.entity.Pubweixin;
import com.ty.entity.UserInfo;
import com.ty.services.PubWeixinService;
import com.ty.services.WeixinUserService;
import com.ty.util.WeixinUtil;
import org.apache.commons.codec.binary.Base32;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 网页授权获取用户基本信息
 *
 * @author Jacky
 */
@Controller
@RequestMapping(value = "weixin")
public class WeixinOauthController {
    private static final Logger logger = Logger.getLogger("WeixinOauthController");

    @Autowired
    private WeixinUtil weixinUtil;
    @Autowired
    private PubWeixinService pubWeixinService;
    @Autowired
    private WeixinUserService weixinUserService;

    /**
     * 通过code换取网页授权access_token
     */
    private static final String ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    /** 刷新access_token */
    //private static final String REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
    
    /**
     * 获取用户资料
     */
    private static final String USERINFO = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

    @RequestMapping(value = "oauth")
    public String oauth(String code, String state, HttpServletRequest request, RedirectAttributes attributes,HttpSession session,Model model) {
        // 判断用户是否授权
        if (code != null && code.length() > 0) {
            // 解析经过Base32编码后传递过来的json参数,state长度最长为128
            //{"appid":"wx695033b74d733b6f","page":"redpack"}  Base32编码 PMRGC4DQNFSCEORCO54DMOJVGAZTGYRXGRSDOMZTMI3GMIRMEJYGCZ3FEI5CE4TFMRYGCY3LEJ6Q
            JSONObject json = JSONObject.parseObject(new String(new Base32().decode(state)));
            String appid = json.getString("appid");
            // 根据传递过来的appid匹配数据库中公众号
            //Pubweixin pubweixin = pubWeixinService.selectByAppid(json.get("appid").toString());
            Pubweixin pubweixin = pubWeixinService.selectByAppid(appid);
            if (pubweixin != null) {
                String requestUrl = ACCESS_TOKEN.replace("APPID", pubweixin.getAppid()).replace("SECRET", pubweixin.getAppsecret()).replace("CODE", code);

                // 通过code换取网页授权access_token
                JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, "GET", null);
                // 如果授权则继续，没有授权则直接返回
                if (jsonObject != null && jsonObject.containsKey("openid")) {
                    /** 关注状态，0未关注，1已关注，-1数据库没数据 */
                    int subscribe = -1;
                    /*用户openid */
                    String openid = jsonObject.get("openid").toString();
                    UserInfo ui = weixinUserService.selectByopenid(openid);
                    if (ui == null) {
                        subscribe = -1;
                    } else {
                        subscribe = ui.getSubscribe().equals("关注") ? 1 : 0;
                    }

                    // 拥有获取用户信息权限
                    if (subscribe == -1 && jsonObject.get("scope").toString().contains("snsapi_userinfo")) {
                        // TODO 校验access_token是否有效，无效调用刷新获取access_token（非必须）
                        JSONObject userinfo = this.userinfo(jsonObject.get("access_token").toString(), openid);
                        ui = weixinUtil.parseJsonToUserInfo(userinfo, openid, pubweixin.getAppid());
                        ui.setSubscribe("0");
                        // 用户信息入库
                        weixinUserService.save(ui);
                    }
                }
            }
            //TODO 这里根据state传递过来的参数page,跳转到对应页面
            if(json.containsKey("page")){
                String page = json.getString("page");
                if("redpack".equals(page)){
                    return InternalResourceViewResolver.REDIRECT_URL_PREFIX + "pages/manager/weixin/menu";
                }
            }
        }

        // 所有请求都跳转到此页面上
        return InternalResourceViewResolver.REDIRECT_URL_PREFIX + "/html/main.html";
    }

    /**
     * 拉取用户信息
     *
     * @param access_token token
     * @param openid       微信 OPENID
     * @return 用户信息
     */
    private JSONObject userinfo(String access_token, String openid) {
        String requestUrl = USERINFO.replace("ACCESS_TOKEN", access_token).replace("OPENID", openid);
        return WeixinUtil.httpRequest(requestUrl, "GET", null);
    }

    public static void main(String[] args) {
        Base32 base32 = new Base32();
        String abc = "{\"appid\":\"wx695033b74d733b6f\",\"page\":\"redpack\"}";
        logger.info(abc);
        abc = base32.encodeAsString(abc.getBytes()).replace("=", "");
        logger.info(abc);
        byte[] debytes = base32.decode(abc);
        logger.info(new String(debytes));
    }
}