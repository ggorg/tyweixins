package com.ty.controller;

import com.gen.framework.common.util.Page;
import com.ty.entity.Msg;
import com.ty.entity.Pubweixin;
import com.ty.services.MsgService;
import com.ty.services.PubWeixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 微信消息查询接口
 */
@Controller
@RequestMapping(value = "weixin/msg")
public class WeixinMsgController {

    @Autowired
    private MsgService msgService;
    @Autowired
    private PubWeixinService pubWeixinService;

    @RequestMapping(value = {"list", ""})
    public String list(Msg msg,@RequestParam(defaultValue = "1") Integer pageNo, String appid, Model model) {
        List<Pubweixin> pubweixinList = pubWeixinService.findPubweixinAll();
        if(appid == null || appid.equals("")){
            if(pubweixinList.size()>0){
                appid = pubweixinList.get(0).getAppid();
            }
        }
        msg.setAppid(appid);
        Page<Msg> page = msgService.findList(pageNo,msg);
        model.addAttribute("msg", msg);
        model.addAttribute("msgPage", page);
        model.addAttribute("appid",appid);
        model.addAttribute("pubweixinList",pubweixinList);
        return "pages/manager/weixin/msg";
    }

}
