package com.ty.services;

import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.services.CacheService;
import com.gen.framework.common.util.HttpUtil;
import com.gen.framework.common.vo.ResponseVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChannelApiService {

    private String vaildCodeUrl;
    @Value("${ty.telphone.prefix}")
    private String tyTelPrefix;

    @Autowired
    private CacheService cacheService;



    public ResponseVO sendVaildCode(String telphone)throws Exception{

        if(StringUtils.isBlank(telphone)){
            return new ResponseVO(-2,"手机号为空",null);
        }
        if(!NumberUtils.isNumber(telphone)){
            return new ResponseVO(-2,"请填写正确的手机号",null);
        }
        if(!telphone.matches("^("+tyTelPrefix+").*$")){
            return new ResponseVO(-2,"抱歉，非电信手机号码不能绑定",null);
        }
        JSONObject obj=new JSONObject();

        obj.put("phone_no",telphone);
        String ranInt=RandomStringUtils.randomNumeric(5);
        obj.put("act_code","5");
        obj.put("message",ranInt);
        String callBackStr=HttpUtil.jsonPost(vaildCodeUrl,obj.toJSONString());
        if(StringUtils.isNotBlank(callBackStr)){
            JSONObject json=JSONObject.parseObject(callBackStr);
            if(json.containsKey("status") && json.getString("status").equals("0")){
                cacheService.setValidCode(telphone,ranInt);
                return new ResponseVO(1,"验证码发送成功",null);
            }
        }
        return new ResponseVO(-2,"验证码发送失败",null);
    }
    public ResponseVO vaildeCode(String telphone,String code)throws Exception{
        if(StringUtils.isBlank(telphone)){
            return new ResponseVO(-2,"手机号为空",null);
        }
        if(StringUtils.isBlank(code)){
            return new ResponseVO(-2,"验证码为空",null);
        }
        if(!NumberUtils.isNumber(telphone)){
            return new ResponseVO(-2,"请填写正确的手机号",null);
        }
        String cacheCode=this.cacheService.getValidCode(telphone);
        if(StringUtils.isBlank(cacheCode)){
            return new ResponseVO(-2,"验证码已经超时请重新发送一次",null);
        }
        if(cacheCode.equals(code)){
            this.cacheService.deleteValidCode(telphone);
            return new ResponseVO(1,"验证成功",null);
        }else{
            return new ResponseVO(-2,"验证失败",null);
        }
    }

}
