package com.ty.services;

import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.ActEnum;
import com.ty.entity.TyUser;
import com.ty.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 查看余额
 */
@Service
public class TyBalanceService extends CommonService {

    private String searchBalanceUrl;

    public ResponseVO getBalance(String openid)throws Exception{
        if(StringUtils.isBlank(openid)){
            return new ResponseVO(-2,"参数异常",null);
        }
        TyUser tyuser= this.commonObjectBySingleParam("ty_user","tuOpenId",openid,TyUser.class);
        if(tyuser==null){
            return new ResponseVO(-2,"获取用户信息失败",null);
        }
        JSONObject param=new JSONObject();
        param.put("pay_user",tyuser.getTyTelphone());
        param.put("act_code", ActEnum.act2.getCode());
        String callBackStr=HttpUtil.doPost(searchBalanceUrl,param.toJSONString());
        if(StringUtils.isNotBlank(callBackStr)){
            JSONObject callBackJson=JSONObject.parseObject(callBackStr);
            if(callBackJson.getString("status").equals("0")){
                if(callBackJson.containsKey("data")){
                    JSONObject data=callBackJson.getJSONObject("data");
                    if(data!=null){
                        String balanceVal=data.containsKey("balanceVal")?data.getString("balanceVal"):"0";
                        if(balanceVal.length()==1){
                            balanceVal="0.0"+balanceVal;
                        }else if(balanceVal.length()==2){
                            balanceVal="0."+balanceVal;
                        }else{
                            balanceVal=balanceVal.replaceAll("^([0-9]+)([0-9][0-9])$","$1.$2");
                        }
                        ResponseVO re=new ResponseVO();
                        re.setReCode(1);
                        re.setReMsg("成功");
                        Map dataMap=new HashMap();
                        dataMap.put("balanceVal",balanceVal);
                        re.setData(dataMap);
                        return re;
                    }
                }
            }
        }
        return new ResponseVO(-1,"操作失败",null);
    }
    public ResponseVO getBalanceDetail(){
        return new ResponseVO(-1,"",null);
    }

}
