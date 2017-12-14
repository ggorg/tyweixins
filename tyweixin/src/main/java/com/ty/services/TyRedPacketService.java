package com.ty.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.beans.CommonChildBean;
import com.gen.framework.common.beans.CommonSearchBean;
import com.gen.framework.common.exception.GenException;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.util.Tools;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.config.Globals;
import com.ty.entity.TyRedPacket;
import com.ty.entity.TyUser;
import com.ty.entity.UserInfo;
import com.ty.enums.ActEnum;
import com.ty.util.HttpUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class TyRedPacketService extends CommonService {

    @Autowired
    private Globals globals;

    @Autowired
    private WeixinUserService weixinUserService;

    public ResponseVO<List> pullRedPacket()throws Exception{
        Map condition=new HashMap();
        condition.put("subscribe",1);
        CommonSearchBean csb=new CommonSearchBean("ty_user",null,new CommonChildBean("weixin_user","openid","tuOpenId",condition));
        csb.setCustom("t1.*");
        List<Map> objs=this.getCommonMapper().selectObjects(csb);
        List openidList=new ArrayList();
        if(objs!=null && !objs.isEmpty()){
            JSONObject param=null;
            String callBackStr=null;
            Map insertMap=null;
            for(Map paramMap:objs){
                if(paramMap.containsKey("tuTelphone")){
                    openidList.add(paramMap.get("tuOpenId"));
                    param=new JSONObject();
                    param.put("pay_user",paramMap.get("tuTelphone"));
                    param.put("act_code", ActEnum.act4.getCode());
                    if(globals.getPullRedPacketUrl().startsWith("http")){
                        callBackStr=HttpUtil.doPost(globals.getPullRedPacketUrl(),param.toJSONString());
                    }else{
                        callBackStr= FileUtils.readFileToString(new File(globals.getPullRedPacketUrl()));
                    }
                    if(StringUtils.isNotBlank(callBackStr)){
                        JSONObject callBackJson=JSONObject.parseObject(callBackStr);
                        if(callBackJson.containsKey("status") && callBackJson.getString("status").equals("0")){
                            if(callBackJson.containsKey("data") && !callBackJson.getJSONArray("data").isEmpty()){
                                JSONArray jsonArray=callBackJson.getJSONArray("data");
                                JSONObject dataJson=null;
                                for(int i=0;i<jsonArray.size();i++){
                                    insertMap=new HashMap();
                                    dataJson=jsonArray.getJSONObject(i);
                                    TyRedPacket rp=this.commonObjectBySingleParam("ty_red_packet","trFromId",dataJson.getString("packetId"),TyRedPacket.class);

                                    insertMap.put("tUid",paramMap.get("id"));
                                    insertMap.put("trAmount",Integer.parseInt(dataJson.getString("packetValue")));
                                    insertMap.put("trActivityId",dataJson.getString("efCampaignId"));
                                    insertMap.put("trFromId",dataJson.getString("packetId"));
                                    insertMap.put("trSendDate", DateUtils.parseDate(dataJson.getString("sendDate"),"yyyy-MM-dd HH:mm:ss"));
                                    insertMap.put("trIsOpen",false);
                                    insertMap.put("updateTime",new Date());
                                    if(rp==null){
                                        this.commonInsertMap("ty_red_packet",insertMap);
                                    }else{
                                        this.commonUpdateBySingleSearchParam("ty_red_packet",insertMap,"id",rp.getId());
                                    }

                                }

                            }

                        }
                    }
                }
            }
            return new ResponseVO(1,"红包获取成功",openidList);
        }
        return new ResponseVO(-2,"红包获取失败",null);
    }
    public ResponseVO openRedPacket(String openid)throws Exception{
        UserInfo user=this.weixinUserService.selectByopenid(openid);
        if(user==null || StringUtils.isBlank(user.getSubscribe()) || user.getSubscribe()=="未关注"){
            return new ResponseVO(-2,"请先关注翼支付关众号",null);
        }
        TyUser tyUser=this.commonObjectBySingleParam("ty_user","tuOpenId",openid, TyUser.class);
        if(tyUser==null || StringUtils.isBlank(tyUser.getTuTelphone())){
            return new ResponseVO(-2,"抱歉，请绑定手机号",null);
        }
        Map condition=new HashMap();
        condition.put("tUid",tyUser.getId());
        condition.put("trIsOpen",false);
        List<Map> tyRedPackList=this.commonList("ty_red_packet",null,null,null,condition);
        if(tyRedPackList!=null && !tyRedPackList.isEmpty()){
            JSONObject param=null;
            Map updateMap=null;
            List amount=new ArrayList();
            for(Map map:tyRedPackList){
                updateMap=new HashMap();
                updateMap.put("trIsOpen",true);
                updateMap.put("updateTime",new Date());
                this.commonUpdateBySingleSearchParam("ty_red_packet",updateMap,"tUid",tyUser.getId());
                param=new JSONObject();
                param.put("pay_user",tyUser.getTuTelphone());
                param.put("act_code", ActEnum.act7.getCode());
                param.put("efCampaignId", map.get("trActivityId"));
                param.put("packetId", map.get("trFromId"));
                param.put("packetValue", map.get("trAmount").toString());
                amount.add(Tools.getRealAmount(map.get("trAmount").toString()));
                String callBackStr=HttpUtil.doPost(globals.getOpenRedPacketUrl(),param.toJSONString());
                if(StringUtils.isBlank(callBackStr)){
                    throw new GenException("openRedPacket->红包充值返回参数异常");
                }

                JSONObject callBackJson=JSONObject.parseObject(callBackStr);
                if(callBackJson.containsKey("status") && !callBackJson.getString("status").equals("0")){
                    throw new GenException("openRedPacket->红包充值失败");
                }

            }
            return new ResponseVO(1,"红包充值成功",amount);
        }
        return new ResponseVO(-2,"红包充值失败",null);
    }
}
