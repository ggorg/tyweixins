package com.ty.controller;

import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.entity.Pubweixin;
import com.ty.services.PubWeixinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 微信公众号接口
 * @author Jacky
 *
 */
@Controller
@RequestMapping(value = "/weixin/public")
public class PubWeixinController{
    private final Logger logger = LoggerFactory.getLogger(PubWeixinController.class);
    @Autowired
    private PubWeixinService weixinPublicService;

    @GetMapping("edit")
    public String edit(String appid,Model model){
        try {
            model.addAttribute("pubweixin",this.weixinPublicService.selectByAppid(appid));
        }catch (Exception e){
            logger.error("PubWeixinController->toUserEdit->系统异常",e);

        }
        return "pages/manager/weixin/publicEdit";
    }

    @RequestMapping(value = {"list", ""})
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, Model model) {
        try {
            Page<Pubweixin> page = weixinPublicService.findPubweixin(pageNo);
            model.addAttribute("userPage", page);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "pages/manager/weixin/public";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public ResponseVO save(Pubweixin pubweixin, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            pubweixin.setUsable("1");
            return this.weixinPublicService.saveOrUpdate(pubweixin);
        } catch (Exception e) {
            logger.error("PubWeixinController->save->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ResponseVO delete(Pubweixin pubweixin, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            return this.weixinPublicService.delete(pubweixin);
        } catch (Exception e) {
            logger.error("PubWeixinController->delete->系统异常",e);
            return new ResponseVO(-1,"删除失败",null);
        }
    }
}
