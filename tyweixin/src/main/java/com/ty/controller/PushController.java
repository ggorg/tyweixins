package com.ty.controller;

import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.entity.Push;
import com.ty.entity.Pubweixin;
import com.ty.entity.UserInfo;
import com.ty.services.MessageService;
import com.ty.services.PubWeixinService;
import com.ty.services.PushService;
import com.ty.services.WeixinUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 推送策略
 * Created by Jacky on 2017/12/4.
 */
@Controller
@RequestMapping(value = "weixin/push")
public class PushController {
    private static final Logger logger = Logger.getLogger("PushController");
    @Autowired
    private PushService pushService;
    @Autowired
    private PubWeixinService pubWeixinService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private WeixinUserService weixinUserService;

    @RequestMapping(value = {"list", ""})
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, String appid, Model model) {
        List<Pubweixin>pubweixinList = pubWeixinService.findPubweixinAll();
        if(appid == null || appid.equals("")){
            if(pubweixinList.size()>0){
                appid = pubweixinList.get(0).getAppid();
            }
        }
        Page<Push> pushPage =  pushService.findList(pageNo,appid);
        model.addAttribute("pushPage",pushPage);
        model.addAttribute("appid",appid);
        model.addAttribute("pubweixinList",pubweixinList);
        return "pages/manager/weixin/push";
    }

    @GetMapping("edit")
    public String edit(UserInfo userInfo,@RequestParam(defaultValue = "1") Integer pageNo, Integer id, String appid, Model model){
        try {
            if(id != null && id >0){
                Push push = pushService.selectById(id);
                appid = push.getAppid();
                model.addAttribute("pushObject",push);
            }else{
                model.addAttribute("pushObject",null);
            }
            model.addAttribute("messageList",messageService.findListAll(appid));
            model.addAttribute("appid",appid);
            Page<UserInfo> page = weixinUserService.findUser(pageNo,userInfo);
            model.addAttribute("userInfo", userInfo);
            model.addAttribute("userPage", page);
        }catch (Exception e){
            logger.error("PushController->edit->系统异常",e);
        }
        return "pages/manager/weixin/pushEdit";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public ResponseVO save(Push push,String ptime, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (ptime != null && !ptime.equals("")){
                push.setPush_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ptime));
            }
            return pushService.saveOrUpdate(push);
        } catch (Exception e) {
            logger.error("PushController->save->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ResponseVO delete(Integer id, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            return this.pushService.delete(id);
        } catch (Exception e) {
            logger.error("PushController->delete->系统异常",e);
            return new ResponseVO(-1,"删除失败",null);
        }
    }
}
