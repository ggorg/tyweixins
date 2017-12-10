package com.ty.controller;

import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.entity.Message;
import com.ty.entity.Pubweixin;
import com.ty.services.MessageService;
import com.ty.services.PubWeixinService;
import com.ty.services.WeixinInterfaceService;
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
 * 图文设置
 * Created by Jacky on 2017/12/4.
 */
@Controller
@RequestMapping(value = "weixin/message")
public class MessageController {
    private static final Logger logger = Logger.getLogger("MessageController");
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private PubWeixinService pubWeixinService;

    @RequestMapping(value = {"list", ""})
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, String appid, Model model) {
        List<Pubweixin> pubweixinList = pubWeixinService.findPubweixinAll();
        if(appid == null || appid.equals("")){
            if(pubweixinList.size()>0){
                appid = pubweixinList.get(0).getAppid();
            }
        }
        Page<Message> list =  messageService.findList(pageNo,appid);
        List<Message> messageList =  list.getResult();
        model.addAttribute("menuList",messageList);
        model.addAttribute("appid",appid);
        model.addAttribute("pubweixinList",pubweixinList);
        return "pages/manager/weixin/menu";
    }

    @GetMapping("edit")
    public String edit(Integer id,Integer parent_id,String appid,Model model){
        try {
            if(id != null && id >0){
                model.addAttribute("messageObject",this.messageService.selectById(id));
            }else if(parent_id != null && parent_id != -1){
                Message parent_message = this.messageService.selectById(parent_id);
                Message message = new Message();
                message.setParent_id(parent_id);
                appid = parent_message.getAppid();
                message.setAppid(appid);
                model.addAttribute("menuObject",message);
            }else if(parent_id == -1){
                Message message = new Message();
                message.setParent_id(parent_id);
            }
            model.addAttribute("appid",appid);
        }catch (Exception e){
            logger.error("MessageController->edit->系统异常",e);
        }
        return "pages/manager/weixin/menuEdit";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public ResponseVO save(Message message, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            return messageService.saveOrUpdate(message);
        } catch (Exception e) {
            logger.error("MessageController->save->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ResponseVO delete(Integer id, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            return this.messageService.delete(id);
        } catch (Exception e) {
            logger.error("MessageController->delete->系统异常",e);
            return new ResponseVO(-1,"删除失败",null);
        }
    }
}
