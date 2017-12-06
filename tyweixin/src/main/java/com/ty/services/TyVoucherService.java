package com.ty.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.ActEnum;
import com.ty.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@EnableAsync
@Service
public class TyVoucherService extends CommonService {
    private String getVoucherUrl;

    @Async
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveVoucheies(String telphone)throws Exception{
        JSONObject param=new JSONObject();
        param.put("pay_user",telphone);
        param.put("act_code", ActEnum.act5.getCode());
        String callBackStr=HttpUtil.doPost(getVoucherUrl,param.toString());
        if(StringUtils.isNotBlank(callBackStr)){
            JSONObject callBackJson= JSONObject.parseObject(callBackStr);
            if(callBackJson.getString("status").equals("0")){
                if(callBackJson.containsKey("data") && !callBackJson.getJSONArray("data").isEmpty()){
                    JSONArray jsonArray=callBackJson.getJSONArray("data");
                    JSONObject json=null;
                    Map obj=null;
                    for(int i=0;i<jsonArray.size();i++){
                        obj=new HashMap();
                        json=jsonArray.getJSONObject(i);
                        obj.put("tvCode",json.getString("code"));
                        obj.put("tvSendDate",DateUtils.parseDate(json.getString("sendDate"),"yyyy-MM-dd HH:mm:ss"));
                        obj.put("tvBusinessType",json.getString("businessType"));
                        obj.put("tvRemark",json.getString("remark"));
                        obj.put("tvBeginTime",DateUtils.parseDate(json.getString("beginTime"),"yyyy-MM-dd"));
                        obj.put("tvEndTime",DateUtils.parseDate(json.getString("endTime"),"yyyy-MM-dd"));
                        obj.put("createTime",new Date());
                        this.commonInsertMap("ty_voucher",obj);
                    }
                }
            }
        }

    }
}
