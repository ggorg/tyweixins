package com.ty.controller.manager;

import com.gen.framework.common.util.MyEncryptUtil;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.config.Globals;
import com.ty.controller.WapController;
import com.ty.entity.Message;
import com.ty.entity.PropagateVO;
import com.ty.entity.TyActivity;
import com.ty.services.*;
import com.ty.timer.TyTimer;
import com.ty.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    private final Logger logger = LoggerFactory.getLogger(ManagerController.class);
    @Autowired
    private TyActivityRedPacketService tyRedPacketService;
    @Autowired
    private TyActivityService tyActivityService;

    @Autowired
    private PubWeixinService pubWeixinService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private TyUserService tyUserService;

    @Autowired
    private Globals globals;

    @Autowired
    private WapController wapController;
    @GetMapping("/to-count-red-packet")
    public String toCountRedPacket(Model model){
        try {
            model.addAttribute("redPacketList",this.tyRedPacketService.countRedPacket());

        }catch (Exception e){
            logger.error("ManagerController->toCountRedPacket->系统异常",e);
        }
        return "pages/manager/count/redpacket";
    }
    @PostMapping("/do-pull-activity")
    @ResponseBody
    public ResponseVO doPullActivity(){
        try {
            return tyActivityService.pullActivity();
        }catch (Exception e){
            logger.error("WapController->toPullRedPacket->系统异常",e);
            return new ResponseVO(-1,"红包获取失败",null);
        }

    }
    @GetMapping("/to-pull-activity")
    public String toPullActivity(Model model,@RequestParam(defaultValue = "1") Integer pageNum){
        try {
            model.addAttribute("actPage",tyActivityService.getActivityPage(pageNum));

        }catch (Exception e){
            logger.error("WapController->toPullActivity->系统异常",e);

        }

        return "pages/manager/activity/activity";
    }
    @GetMapping("/to-activity")
    public String toActivity(String idStr,Model model){
        try {
            String ids= MyEncryptUtil.getRealValue(idStr);
            if(ids!=null){
                model.addAttribute("act",tyActivityService.getActivity(Integer.parseInt(ids)).getData());
            }


        }catch (Exception e){
            logger.error("WapController->toActivity->系统异常",e);

        }

        return "pages/manager/activity/activityEdit";
    }
    @PostMapping("/do-save-activity")
    @ResponseBody
    public ResponseVO saveActivity(Integer taMinCost,Integer taMaxCost,String idStr,Integer taNumber){
        try {
            return this.tyActivityService.updateActivity(idStr,taMaxCost,taMinCost,taNumber,false);
        }catch (Exception e){
            logger.error("WapController->saveActivity->系统异常",e);
            return new ResponseVO(-1,"保存失败",null);
        }

    }
    @PostMapping("/do-save-push-activity")
    @ResponseBody
    public ResponseVO savePushActivity(Integer taMinCost,Integer taMaxCost,String idStr,Integer taNumber){
        try {
            return this.tyActivityService.updateActivity(idStr,taMaxCost,taMinCost,taNumber,true);
        }catch (Exception e){
            logger.error("WapController->saveActivity->系统异常",e);
            return new ResponseVO(-1,"保存失败",null);
        }
    }
    @GetMapping("/to-push-info")
    public String getPushInfo(Model mode,Integer id){
        try {
            mode.addAttribute("apps",pubWeixinService.findPubweixinAll());
            mode.addAttribute("pros",this.tyActivityService.getAllActivity());
            Map map=this.tyActivityService.getPropagateById(id);
            if(map!=null && map.size()>0){

                mode.addAttribute("actid",map.get("actId"));
            }

        }catch (Exception e){

        }
        return "pages/manager/activity/activityPush";
    }
    @PostMapping("/get-msg-by-appid")
    @ResponseBody
    public ResponseVO getMsgByAppid(String appid){
        try {
            List list=messageService.findListAll(appid);
            if(list!=null && list.size()>0){
                return new ResponseVO(1,"获取成功",list);
            }
        }catch (Exception e){
            logger.error("ManagerController->getMsgByAppid->系统异常",e);
            return new ResponseVO(-1,"获取失败",null);
        }
         return new ResponseVO(-2,"获取失败",null);

    }
    @PostMapping("/do-activity-bind-msg")
    @ResponseBody
    public ResponseVO doActivityBindMsg(Integer actid,Integer msgid,Integer proid){
        try {
           ResponseVO<TyActivity> responseAct= this.tyActivityService.getActivity(actid);

           if(responseAct.getReCode()==1){
               ResponseVO retPro=this.tyActivityService.updatePropagateAct(proid,actid);
               if(retPro.getReCode()==1){
                  // TyActivity tyActivity=responseAct.getData();
                   Message msg=this.messageService.selectById(msgid);
                   msg.setUrl(CommonUtil.createOauthUrl(msg.getAppid(),globals.getRedirectUri(),"pro-page",proid));
                   ResponseVO re=this.messageService.saveOrUpdate(msg);
               }


               return new ResponseVO(1,"绑定消息成功",null);
           }
        }catch (Exception e){
            logger.error("ManagerController->doActivityBindMsg->系统异常",e);
            return new ResponseVO(-1,"绑定消息失败",null);
        }
        return new ResponseVO(-2,"绑定消息失败",null);
    }

    @PostMapping("/do-activity-bind-msg-and-Push")
    @ResponseBody
    public ResponseVO doActivityBindMsgAndPush(Integer actid,Integer msgid,Integer proid){
        try {
            ResponseVO rv=this.doActivityBindMsg(actid,msgid,proid);
            if(rv.getReCode()==1){
                List<Map> list=this.tyUserService.getAllUser();
                if(list!=null && list.size()>0){
                    for(Map map:list){
                        if(map.containsKey("tuOpenId") && map.containsKey("tuTelphone")){
                           String tuOpenid= (String)map.get("tuOpenId");
                           String tuTelphone=(String)map.get("tuTelphone");
                           if(StringUtils.isNotBlank(tuOpenid) && StringUtils.isNotBlank(tuOpenid)){
                               this.messageService.sendMessage(tuOpenid,msgid);
                           }

                        }

                    }
                    return new ResponseVO(1,"绑定消息并推送成功",null);

                }

            }
        }catch (Exception e){
            logger.error("ManagerController->doActivityBindMsg->系统异常",e);
            return new ResponseVO(-1,"绑定消息并推送失败",null);
        }
        return new ResponseVO(-2,"绑定消息并推送失败",null);
    }
    @GetMapping("/tyuser/to-list")
    public String toTyUserList(@RequestParam(defaultValue = "1") Integer pageNo,String tuTelphone, Model model){
        try {

            model.addAttribute("userPage",this.tyUserService.getUserPage(pageNo,tuTelphone));
        }catch (Exception e){
            logger.error("SysUserController->toUserList",e);
        }
        return "pages/manager/tyuser/user";
    }

    @GetMapping("/activity/to-propagate-list")
    public String toPropagateList(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {

            model.addAttribute("proPage",this.tyActivityService.getPropagatePage(pageNo));
        }catch (Exception e){
            logger.error("SysUserController->toPropagatePage",e);
        }
        return "pages/manager/propagates/list";
    }
    @GetMapping("/activity/to-eidt-propagate")
    public String toEditPropagate(Integer id, Model model){
        try{
            model.addAttribute("pro",this.tyActivityService.getPropagateById(id));
        }catch (Exception e){
            logger.error("SysUserController->toEditPropagate",e);
        }
        return "pages/manager/propagates/edit";
    }
    @PostMapping("/activity/do-eidt-propagate")
    @ResponseBody
    public ResponseVO doEditPropagate(PropagateVO propagateVO){
        try {
            this.tyActivityService.editPropagate(propagateVO);
            return new ResponseVO(1,"提交成功",null);
        }catch (Exception e){
            logger.error("SysUserController->doEditPropagate",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }
    @PostMapping("/activity/do-del-propagate")
    @ResponseBody
    public ResponseVO doDelPropagate(Integer id){
        try {
            this.tyActivityService.deletePropagate(id);
            return new ResponseVO(1,"删除成功",null);
        }catch (Exception e){
            logger.error("SysUserController->doDelPropagate",e);
            return new ResponseVO(-1,"删除失败",null);
        }
    }
    @GetMapping("/activity/to-preview-propagate")
    public String toPreviewPropagate(String proid,Model model){
        try {
           this.wapController.toPropagate(model,proid);
        }catch (Exception e){
            logger.error("SysUserController->toPreviewPropagate",e);

        }
        return "pages/manager/propagates/preview";
    }
}
