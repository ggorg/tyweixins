package com.ty.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.services.CacheService;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.util.MyEncryptUtil;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.config.Globals;
import com.ty.entity.TyActivity;
import com.ty.enums.ActEnum;
import com.ty.util.HttpUtil;
import com.ty.util.TydicDES;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TyActivityService extends CommonService {

    @Autowired
    private Globals globals;

    @Autowired
    private CacheService cacheService;

    public ResponseVO pullActivity()throws Exception{
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("act_code", ActEnum.act3.getCode());
        String callBackStr=null;
        if(globals.getPullActivityUrl().startsWith("http")){
            callBackStr=HttpUtil.doPost(globals.getPullActivityUrl(),TydicDES.encodeValue(jsonObject.toJSONString()));
        }else{
            callBackStr= FileUtils.readFileToString(new File(globals.getPullActivityUrl()));
        }

        if(StringUtils.isNotBlank(callBackStr)){
            JSONObject callBackJson=JSONObject.parseObject(TydicDES.decodedecodeValue(callBackStr));
            if(callBackJson.getString("status").equals("0")){
                if(callBackJson.containsKey("data") && !callBackJson.getJSONArray("data").isEmpty()){
                    JSONArray data=callBackJson.getJSONArray("data");
                    JSONObject obj=null;
                    Map map=null;
                    for(int d=0;d<data.size();d++){
                        obj=data.getJSONObject(d);
                        map=new HashMap();
                        map.put("id",Integer.parseInt(obj.getString("efCampaignId")));
                        map.put("taName",obj.getString("efCampaignName"));
                        map.put("taAmount",Integer.parseInt(obj.getString("cost")));
                        map.put("taRemark",obj.getString("remark"));
                        map.put("taBeginDate", DateUtils.parseDate(obj.getString("beginDate"),"yyyy-MM-dd HH:mm:ss"));
                        map.put("taEndDate", DateUtils.parseDate(obj.getString("endDate"),"yyyy-MM-dd HH:mm:ss"));


                        map.put("updateTime",new Date());
                        long o=this.commonCountBySingleParam("ty_activity","id",map.get("id"));
                        if(o>0){
                            this.commonUpdateBySingleSearchParam("ty_activity",map,"id",map.get("id"));
                        }else{
                            map.put("createTime",new Date());
                            map.put("taDisabled",false);
                            this.commonInsertMap("ty_activity",map);
                        }

                    }
                    return new ResponseVO(1,"获取最新活动成功",null);
                }
            }
        }
        return new ResponseVO(-2,"获取最新活动失败",null);
    }
    public ResponseVO getActivity(String id)throws Exception{
        String idStr=MyEncryptUtil.encry(id);
        if(StringUtils.isBlank(idStr)){
            return new ResponseVO(-2,"获取失败",null);
        }
        TyActivity ta=this.commonObjectBySingleParam("ty_activity","id",Integer.parseInt(idStr), TyActivity.class);
        return new ResponseVO(1,"获取成功",ta);
    }
    public ResponseVO updateActivity(String id,Integer taMaxCost,Integer taMinCost,Boolean taDisabled){
        String idStr=MyEncryptUtil.encry(id);
        if(StringUtils.isBlank(idStr)){
            return new ResponseVO(-2,"修改失败",null);
        }
        if(taMaxCost==null || taMinCost==null){
            return new ResponseVO(-2,"修改失败,请填写区间值",null);
        }
        if(taMaxCost<taMinCost){
            return new ResponseVO(-2,"修改失败,区间最大值必须大于最小值",null);
        }
        Map map=new HashMap();
        map.put("taMaxCost",taMaxCost);
        map.put("taMinCost",taMinCost);
        map.put("taDisabled",taDisabled);
        map.put("updateTime",new Date());
        return this.commonUpdateBySingleSearchParam("ty_activity",map,"id",Integer.parseInt(idStr));

    }
    public static void main(String[] args)throws Exception {
        System.out.println(new Random().nextInt(10 - 3 + 1) + 3);
        System.out.println(MyEncryptUtil.encry("1"));

        System.out.println(MyEncryptUtil.getRealValue("yUDM4MTOzYDN==QM"));
    }
}
