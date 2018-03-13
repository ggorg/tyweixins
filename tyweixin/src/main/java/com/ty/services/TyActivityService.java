package com.ty.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.config.MainGlobals;
import com.gen.framework.common.services.CacheService;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.util.*;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.config.Globals;
import com.ty.dao.TyActivityMapper;
import com.ty.entity.PropagateVO;
import com.ty.entity.TyActivity;
import com.ty.entity.TyActivityPropagate;
import com.ty.enums.ActEnum;
import com.ty.util.CommonUtil;
import com.ty.util.HttpUtil;
import com.ty.util.TydicDES;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

@Service
public class TyActivityService extends CommonService {
    private final Logger logger = LoggerFactory.getLogger(TyActivityService.class);
    @Autowired
    private Globals globals;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private MainGlobals mainGlobals;

    @Autowired
    private TyActivityMapper tyActivityMapper;


    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO pullActivity()throws Exception{
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("act_code", ActEnum.act3.getCode());
        String callBackStr=null;
        if(globals.getPullActivityUrl().startsWith("http")){
            logger.info("TyActivityService->pullActivity->请求获取活动接口->requestData:{}",jsonObject.toJSONString());
            callBackStr=HttpUtil.sendHtpps(globals.getPullActivityUrl(),TydicDES.encodeValue(jsonObject.toJSONString()));
        }else{
            callBackStr= FileUtils.readFileToString(new File(globals.getPullActivityUrl()));
        }

        if(StringUtils.isNotBlank(callBackStr)){
            JSONObject callBackJson=null;
            if(globals.getPullActivityUrl().startsWith("http")){

                callBackJson= JSONObject.parseObject(TydicDES.decodedecodeValue(callBackStr));
                logger.info("TyActivityService->pullActivity->响应获取活动接口->requestData:{}",callBackJson.toJSONString());
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
                            map.put("taUsedNumber",0);
                            this.commonInsertMap("ty_activity",map);
                        }

                    }
                    return new ResponseVO(1,"获取最新活动成功",null);
                }
            }
        }
        return new ResponseVO(-2,"获取最新活动失败",null);
    }
    public ResponseVO getActivity(Integer actid)throws Exception{
        if(actid==null || actid<=0){
            return new ResponseVO(-2,"获取失败",null);
        }
        TyActivity ta=this.commonObjectBySingleParam("ty_activity","id",actid, TyActivity.class);
        return new ResponseVO(1,"获取成功",ta);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO updateActivity(String idStr,Integer taMaxCost,Integer taMinCost,Integer taNumber,Boolean taDisabled)throws Exception{
        String id=MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(id)){
            return new ResponseVO(-2,"修改失败",null);
        }
        TyActivity ta= this.commonObjectBySingleParam("ty_activity","id",id,TyActivity.class);
        if(ta==null){
            return new ResponseVO(-2,"修改失败，记录不存在",null);
        }

        if(taNumber==null || taNumber==0){
            return new ResponseVO(-2,"修改失败,请填写数量",null);
        }

        if(taMaxCost==null || taMinCost==null){
            return new ResponseVO(-2,"修改失败,请填写区间值",null);
        }
        Integer realMaxMaxCost=ta.getTaAmount()%taNumber;
        if(realMaxMaxCost==0){
            realMaxMaxCost= ta.getTaAmount()/taNumber;
        }
        if(taMaxCost<taMinCost){
            return new ResponseVO(-2,"修改失败,区间最大值必须大于最小值",null);
        }
        Map map=new HashMap();
        map.put("taMaxCost",taMaxCost);
        map.put("taMinCost",taMinCost);
        map.put("taDisabled",taDisabled);
        map.put("taNumber",taNumber);
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

    /**
     * 活动宣传页分页
     * @param pageNum
     * @return
     * @throws Exception
     */
    public Page getPropagatePage(Integer pageNum)throws Exception{
        return this.commonPage("ty_activity_propagete","updateTime desc",pageNum,10,new HashMap<>());
    }
    public List getAllActivity(){
        return this.tyActivityMapper.getAllActivity();
    }

    /**
     * 获取最新宣传
     * @return
     */
    public Map getNewPropagate(){
        List list=this.commonList("ty_activity_propagete","updateTime desc",null,null,null);
        if(list!=null && list.size()>0){
           Map map= (Map)list.get(0);
           return  actJsonStrToJsoObject(map);
        }
        return null;
    }

    /**
     * 根据活动id获取宣传
     * @param actId
     * @return
     * @throws Exception
     */
    public Map getPropagateByActId(Integer actId)throws Exception{
        Map map=this.commonObjectBySingleParam("ty_activity_propagete","actId",actId);
        if(map!=null){
            return  actJsonStrToJsoObject(map);

        }
        return null;
    }
    /**
     * 根据id获取宣传
     * @param id
     * @return
     * @throws Exception
     */
    public Map getPropagateById(Integer id)throws Exception{
        Map map=this.commonObjectBySingleParam("ty_activity_propagete","id",id);
        if(map!=null){
           return  actJsonStrToJsoObject(map);

        }
        return null;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updatePropagateAct(Integer proid,Integer actid)throws Exception{
        if(proid==null || proid<1){
            return new ResponseVO(-2,"id是空",null);
        }
        Map map=this.commonObjectBySingleParam("ty_activity_propagete","id",proid);
        if(map==null || map.isEmpty()){
            return new ResponseVO(-2,"更新失败",null);
        }
        map.clear();
        map.put("actId",actid);
        map.put("updateTime",new Date());
        return this.commonUpdateBySingleSearchParam("ty_activity_propagete",map,"id",proid);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO editPropagate(PropagateVO propagateVO)throws Exception{
        if(StringUtils.isBlank(propagateVO.getTopTitle())){
            return new ResponseVO(-2,"请填写标题",null);
        }
        if(StringUtils.isBlank(propagateVO.getMainDesc())){
            return new ResponseVO(-2,"请填写描述",null);
        }
        ArrayList isUpdateImg=propagateVO.getIsUpdateImg();
        TyActivityPropagate propagate =new TyActivityPropagate();
        Map proMap=null;
        JSONObject actJsonObj=null;
        if(propagateVO.getId()!=null){
            proMap=this.getPropagateById(propagateVO.getId());
            actJsonObj=(JSONObject) proMap.get("jsonObj");
        }
        String topImg=null;
        if(!propagateVO.getTopImg().isEmpty()){
            String topImgName=DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+FilenameUtils.getExtension(propagateVO.getTopImg().getOriginalFilename());
            UploadFileMoveUtil.move(propagateVO.getTopImg(),mainGlobals.getRsDir(), topImgName);
            propagate.setTopImgSrc(topImgName);
        }else if(isUpdateImg!=null && isUpdateImg.contains("topimg")){
            propagate.setTopImgSrc((String)proMap.get("topImgSrc"));
        }
        JSONObject json=new JSONObject();
        if(StringUtils.isNotBlank(propagateVO.getTitle1()) && StringUtils.isNotBlank(propagateVO.getDesc1())) {
            json.put("title1", propagateVO.getTitle1());
            json.put("desc1", StringEscapeUtils.escapeHtml4(propagateVO.getDesc1()));
            if (!propagateVO.getImg1().isEmpty()) {
                String img1Name = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + "." + FilenameUtils.getExtension(propagateVO.getImg1().getOriginalFilename());
                UploadFileMoveUtil.move(propagateVO.getImg1(), mainGlobals.getRsDir(), img1Name);
                json.put("img1", img1Name);
            }else if(isUpdateImg!=null && isUpdateImg.contains("img1")){
                json.put("img1", actJsonObj.getString("img1"));
            }
            json.put("updateTime1", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
        }
        if(StringUtils.isNotBlank(propagateVO.getTitle2()) && StringUtils.isNotBlank(propagateVO.getDesc2())) {
            json.put("title2", propagateVO.getTitle2());
            json.put("desc2", StringEscapeUtils.escapeHtml4(propagateVO.getDesc2()));
            if (!propagateVO.getImg2().isEmpty()) {
                String img2Name = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + "." + FilenameUtils.getExtension(propagateVO.getImg2().getOriginalFilename());
                UploadFileMoveUtil.move(propagateVO.getImg2(), mainGlobals.getRsDir(), img2Name);
                json.put("img2", img2Name);
            }else if(isUpdateImg!=null && isUpdateImg.contains("img2")){

                json.put("img2", actJsonObj.getString("img2"));
            }
            json.put("updateTime2", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
        }

        PropertyUtils.copyProperties(propagate,propagateVO);

        propagate.setMainDesc(StringEscapeUtils.escapeHtml4(propagate.getMainDesc()));
        if(proMap!=null)propagate.setActId((Integer) proMap.get("actId"));
        propagate.setActJsonStr(json.toJSONString());
        Map map=BeanToMapUtil.beanToMap(propagate);
        map.put("updateTime",new Date());
        if(propagate.getId()==null){
            map.put("createTime",new Date());
            return this.commonInsertMap("ty_activity_propagete",map);
        }else{
            map.remove("id");
            return this.commonUpdateBySingleSearchParam("ty_activity_propagete",map,"id",propagate.getId());
        }
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO deletePropagate(Integer id){
        int n=this.commonDelete("ty_activity_propagete","id",id);
        if(n>0){
            return new ResponseVO(1,"删除成功",null);
        }
        return new ResponseVO(-2,"删除失败",null);
    }
    private Map actJsonStrToJsoObject(Map map){
        String actJsonStr= (String) map.get("actJsonStr");
        if(StringUtils.isNotBlank(actJsonStr)){
            map.put("jsonObj",JSONObject.parseObject(actJsonStr));

        }
        return map;
    }
    public static void main(String[] args)throws Exception {
        System.out.println(new Random().nextInt(10 - 3 + 1) + 3);
        System.out.println(MyEncryptUtil.encry("1"));

        System.out.println(MyEncryptUtil.getRealValue("yUDM4MTOzYDN==QM"));
        JSONObject jsonObject=new JSONObject();
       // jsonObject.put("act_code", ActEnum.act3.getCode());
        jsonObject.put("phone_no","18038388686");
        String ranInt=RandomStringUtils.randomNumeric(5);
        jsonObject.put("act_code", ActEnum.act6.getCode());
        jsonObject.put("message",ranInt);

        String str=HttpUtil.sendHtpps("http://222.221.16.170/coupon/webserver/get",TydicDES.encodeValue(jsonObject.toJSONString()));
        System.out.println(TydicDES.decodedecodeValue(str));
    }
}
