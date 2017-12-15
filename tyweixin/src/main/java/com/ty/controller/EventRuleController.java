package com.ty.controller;

import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.entity.EventRule;
import com.ty.entity.Pubweixin;
import com.ty.services.EventRuleService;
import com.ty.services.MessageService;
import com.ty.services.PubWeixinService;
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
import java.util.List;

/**
 * 微信事件处理-回复规则
 * Created by Jacky on 2017/12/4.
 */
@Controller
@RequestMapping(value = "weixin/event")
public class EventRuleController {
    private static final Logger logger = Logger.getLogger("EventRuleController");
    @Autowired
    private EventRuleService eventRuleService;
    @Autowired
    private PubWeixinService pubWeixinService;
    @Autowired
    private MessageService messageService;

    @RequestMapping(value = {"list", ""})
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, String appid, Model model) {
        List<Pubweixin>pubweixinList = pubWeixinService.findPubweixinAll();
        if(appid == null || appid.equals("")){
            if(pubweixinList.size()>0){
                appid = pubweixinList.get(0).getAppid();
            }
        }
        Page<EventRule> eventRulePage =  eventRuleService.findList(pageNo,appid);
        model.addAttribute("eventRulePage",eventRulePage);
        model.addAttribute("appid",appid);
        model.addAttribute("pubweixinList",pubweixinList);
        return "pages/manager/weixin/eventRule";
    }

    @GetMapping("edit")
    public String edit(Integer id,String appid,Model model){
        try {
            if(id != null && id >0){
                EventRule eventRule = eventRuleService.selectById(id);
                appid = eventRule.getAppid();
                model.addAttribute("eventRuleObject",eventRule);
            }
            model.addAttribute("appid",appid);
            model.addAttribute("messageList",messageService.findListAll(appid));
        }catch (Exception e){
            logger.error("EventRuleController->edit->系统异常",e);

        }
        return "pages/manager/weixin/eventRuleEdit";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public ResponseVO save(EventRule eventRule, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            return eventRuleService.saveOrUpdate(eventRule);
        } catch (Exception e) {
            logger.error("EventRuleController->save->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ResponseVO delete(Integer id, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            return this.eventRuleService.delete(id);
        } catch (Exception e) {
            logger.error("EventRuleController->delete->系统异常",e);
            return new ResponseVO(-1,"删除失败",null);
        }
    }
}
