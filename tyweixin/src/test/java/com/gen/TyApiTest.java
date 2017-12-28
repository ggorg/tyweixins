package com.gen;

import com.alibaba.fastjson.JSONObject;
import com.ty.enums.ActEnum;
import com.ty.util.HttpUtil;
import com.ty.util.TydicDES;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)

public class TyApiTest {
   // private String url="http://222.221.16.170/coupon/webserver/get";
    private String url="http://222.221.16.170/coupon/webserver/get";
    private String telphone="15393944645";

    @Test
    public void testSendMsg()throws Exception{
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("phone_no",telphone);
        String ranInt= RandomStringUtils.randomNumeric(5);
        jsonObject.put("act_code", ActEnum.act6.getCode());
        jsonObject.put("message",ranInt);

        String str= HttpUtil.doPost(url, TydicDES.encodeValue(jsonObject.toJSONString()));
        System.out.println(TydicDES.decodedecodeValue(str));
    }
    @Test
    public void testPullActivity()throws Exception{
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("act_code", ActEnum.act3.getCode());
        String callBackStr=HttpUtil.doPost(url,TydicDES.encodeValue(jsonObject.toJSONString()));
        System.out.println(TydicDES.decodedecodeValue(callBackStr));
    }
    @Test
    public void testOpenRedPacket()throws Exception{
        JSONObject param=new JSONObject();
        param.put("pay_user",telphone);
        param.put("act_code", ActEnum.act4.getCode());
       // param.put("efCampaignId", map.get("id"));
       // param.put("packetId", reIsert.getData().toString());
       // param.put("packetValue", insertMap.get("trAmount").toString());
       // param.put("seqCode",insertMap.get("trSeqCode").toString());


        String callBackStr=HttpUtil.doPost(url,TydicDES.encodeValue(param.toJSONString()));
        System.out.println(TydicDES.decodedecodeValue(callBackStr));
    }
    @Test
    public void testGetBlance()throws Exception{
        JSONObject param=new JSONObject();
        param.put("pay_user",telphone);
        param.put("act_code", ActEnum.act2.getCode());
        String callBackStr=HttpUtil.doPost(url,TydicDES.encodeValue(param.toJSONString()));

        System.out.println(TydicDES.decodedecodeValue(callBackStr)+"----------------"+ callBackStr);
    }
    @Test
    public void testGetBalanceDetail()throws Exception{
        JSONObject param=new JSONObject();
        param.put("pay_user",telphone);
        param.put("act_code", ActEnum.act1.getCode());
        String callBackStr=HttpUtil.doPost(url,TydicDES.encodeValue(param.toJSONString()));
        System.out.println(TydicDES.decodedecodeValue(callBackStr));
    }
    @Test
    public void testGetVoucheies()throws Exception{
        JSONObject param=new JSONObject();
        param.put("pay_user",telphone);
        param.put("act_code", ActEnum.act5.getCode());
        System.out.println(TydicDES.encodeValue(param.toJSONString()));
        String callBackStr=HttpUtil.doPost(url,TydicDES.encodeValue(param.toJSONString()));
        System.out.println(TydicDES.decodedecodeValue(callBackStr));
    }
}
