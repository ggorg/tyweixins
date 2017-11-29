package com.ty.controller;

import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.entity.Pubweixin;
import com.ty.services.PubWeixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信公众号接口
 * @author Jacky
 *
 */
@Controller
@RequestMapping(value = "/weixin/public")
public class PubWeixinController{

    @Autowired
    private PubWeixinService weixinPublicService;

    @GetMapping("edit")
    public String toUserEdit(String uId){
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

    /**
     * 添加微信公众号
     * @param appid 应用id
     * @param appsecret 应用密钥
     * @param name 公众号名称
     * @return 添加成功/失败
     */
    @ResponseBody
 	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public ResponseVO addPubweixin(String appid, String appsecret, String name){
        ResponseVO vo=new ResponseVO();
    	// 检验提交数据的有效性
        if (appid == null || appid.equals("") || appsecret == null || appsecret.equals("")) {
            vo.setReCode(-2);
            vo.setReMsg("参数有误");
        } else {
        	Pubweixin pubweixin = new Pubweixin();
        	pubweixin.setName(name);
        	pubweixin.setAppid(appid);
        	pubweixin.setAppsecret(appsecret);
            vo = this.weixinPublicService.saveOrUpdate(pubweixin);
        }
        return vo;
    	
    }
    
    @RequestMapping(value = "save")
    public String save(Pubweixin pubweixin, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
    	if (pubweixin.getAppid() == null || pubweixin.getAppid().equals("")
    			|| pubweixin.getAppsecret() == null || pubweixin.getAppsecret().equals("")
    			|| pubweixin.getName() == null || pubweixin.getName().equals("")) {
//            addMessage(model, "名称、应用ID、应用密钥、原始ID不能为空");
            return form(pubweixin, model);
        }else {
        	pubweixin.setUsable("1");
    		this.weixinPublicService.saveOrUpdate(pubweixin);
        }
//        addMessage(redirectAttributes, "保存公众号'" + pubweixin.getAppid() + "'成功");
        return "pages/manager/weixin/public";
    }

    @RequestMapping(value = "delete")
    public String delete(Pubweixin pubweixin, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
    	this.weixinPublicService.deletePubweixinByAppid(pubweixin.getAppid());
//    	addMessage(redirectAttributes, "删除公众号'" + pubweixin.getAppid() + "'成功");
    	return "pages/manager/weixin/public";
    }
    /**
     * 将公众号删除标志设置为1 已删除
     * @param appid 应用id
     * @return 成功/失败
     */
    @ResponseBody
 	@RequestMapping(value = "/del", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String delPubweixin(String appid){
    	int ret = 0;
        String retMsg = "未知错误!";
    	// TODO 检验提交数据的有效性
        if (appid == null || appid.equals("")) {
            ret = -1;
            retMsg = "参数有误";
        } else {
    		ret = this.weixinPublicService.deletePubweixinByAppid(appid);
            if (ret >= 1) {
                retMsg = String.format("操作成功, 影响行数: %d", ret);
            } else {
                retMsg = "操作失败";
            }
        }
    	JSONObject retJson = new JSONObject();
        retJson.put("retCode", ret);
        retJson.put("retMsg", retMsg);
        return retJson.toString();
    	
    }

    @ResponseBody
    @RequestMapping(value = {"listData"})
    public Page<Pubweixin> listData(Pubweixin pubweixin, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<Pubweixin> page = null;
        try {
            int pageNum = Integer.valueOf(request.getAttribute("pageNum").toString());
            page = weixinPublicService.findPubweixin(pageNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }
    
    @RequestMapping(value = "form")
    public String form(Pubweixin pubweixin, Model model) {
    	if(pubweixin.getAppid()==null){
    		pubweixin = new Pubweixin();
    	}else{
    		pubweixin = weixinPublicService.selectByAppid(pubweixin.getAppid());
    	}
        model.addAttribute("pubweixin", pubweixin);
        return "modules/weixin/pubweixinForm";
    }
}
