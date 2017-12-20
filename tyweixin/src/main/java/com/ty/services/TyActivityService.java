package com.ty.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.services.CacheService;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.util.MyEncryptUtil;
import com.gen.framework.common.util.Page;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class TyActivityService extends CommonService {

    @Autowired
    private Globals globals;

    @Autowired
    private CacheService cacheService;

    @Transactional(propagation = Propagation.REQUIRED)
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
            JSONObject callBackJson=null;
            if(globals.getPullActivityUrl().startsWith("http")){
                callBackJson= JSONObject.parseObject(TydicDES.decodedecodeValue(callBackStr));

            }else{
                callBackJson= JSONObject.parseObject(callBackStr);
            }

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
                        map.put("taRule", obj.getString("rule"));


                        map.put("updateTime",new Date());
                        long o=this.commonCountBySingleParam("ty_activity","id",map.get("id"));
                        if(o>0){
                            this.commonUpdateBySingleSearchParam("ty_activity",map,"id",map.get("id"));
                        }else{
                            map.put("createTime",new Date());
                            map.put("taDisabled",false);
                            map.put("taUsed",0);
                            this.commonInsertMap("ty_activity",map);
                        }

                    }
                    return new ResponseVO(1,"获取最新活动成功",null);
                }
            }
        }
        return new ResponseVO(-2,"获取最新活动失败",null);
    }
    public ResponseVO getActivity(String idStr)throws Exception{
        String id=MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(id)){
            return new ResponseVO(-2,"获取失败",null);
        }
        TyActivity ta=this.commonObjectBySingleParam("ty_activity","id",Integer.parseInt(id), TyActivity.class);
        return new ResponseVO(1,"获取成功",ta);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO updateActivity(String idStr,Integer taMaxCost,Integer taMinCost,Boolean taDisabled){
        String id=MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(id)){
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
        ResponseVO re=this.commonUpdateBySingleSearchParam("ty_activity",map,"id",Integer.parseInt(id));
        if(re.getReCode()==1){
            re.setReMsg(taDisabled?"保存并发布成功":"保存成功");
        }else{
            re.setReMsg("保存失败");
        }

        return re;

    }
    public Page getActivityPage(Integer pageNum)throws Exception{
        return this.commonPage("ty_activity","updateTime desc",pageNum,10,new HashMap<>());
    }
    public static void main(String[] args)throws Exception {
        System.out.println(new Random().nextInt(10 - 3 + 1) + 3);
        System.out.println(MyEncryptUtil.encry("1"));

        System.out.println(MyEncryptUtil.getRealValue("yUDM4MTOzYDN==QM"));
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("act_code", ActEnum.act3.getCode());

        String str=HttpUtil.doPost("http://106.59.229.40:7009/webserver/get",jsonObject.toJSONString());
        System.out.println(TydicDES.decodedecodeValue(str));
    }
}
