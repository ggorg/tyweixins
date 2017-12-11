package com.ty.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.thymeleaf.Tools;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.config.Globals;
import com.ty.enums.ActEnum;
import com.ty.entity.TyBalanceTradeDetail;
import com.ty.entity.TyUser;
import com.ty.util.HttpUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * 查看余额
 */
@Service
public class TyBalanceService extends CommonService {

    @Autowired
    private Globals globals;

    public ResponseVO getBalance(String openid)throws Exception{
        if(StringUtils.isBlank(openid)){
            return new ResponseVO(-2,"参数异常",null);
        }
        TyUser tyuser= this.commonObjectBySingleParam("ty_user","tuOpenId",openid,TyUser.class);
        if(tyuser==null){
            return new ResponseVO(-2,"获取用户信息失败",null);
        }
        JSONObject param=new JSONObject();
        param.put("pay_user",tyuser.getTuTelphone());
        param.put("act_code", ActEnum.act2.getCode());
        String callBackStr=null;
        if(globals.getSearchBalanceUrl().startsWith("http")){
            callBackStr=HttpUtil.doPost(globals.getSearchBalanceUrl(),param.toJSONString());
        }else{
            callBackStr=FileUtils.readFileToString(new File(globals.getSearchBalanceUrl()));
        }
        if(StringUtils.isNotBlank(callBackStr)){
            JSONObject callBackJson=JSONObject.parseObject(callBackStr);
            if(callBackJson.getString("status").equals("0")){
                if(callBackJson.containsKey("data")){
                    JSONObject data=callBackJson.getJSONObject("data");
                    if(data!=null){

                        String balanceVal= Tools.getRealAmount(data.containsKey("balanceVal")?data.getString("balanceVal"):"0");
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
    public ResponseVO getBalanceDetail(String openid)throws Exception{
        if(StringUtils.isBlank(openid)){
            return new ResponseVO(-2,"参数异常",null);
        }
        TyUser tyuser= this.commonObjectBySingleParam("ty_user","tuOpenId",openid,TyUser.class);
        if(tyuser==null){
            return new ResponseVO(-2,"获取用户信息失败",null);
        }
        JSONObject param=new JSONObject();
        param.put("pay_user",tyuser.getTuTelphone());
        param.put("act_code", ActEnum.act2.getCode());
        String callBackStr=null;
        if(globals.getSearchBalanceDetailUrl().startsWith("http")){
            callBackStr=HttpUtil.doPost(globals.getSearchBalanceDetailUrl(),param.toJSONString());
        }else{
            callBackStr=FileUtils.readFileToString(new File(globals.getSearchBalanceDetailUrl()));
        }
        if(StringUtils.isNotBlank(callBackStr)){
            JSONObject callBackJson=JSONObject.parseObject(callBackStr);
            if(callBackJson.getString("status").equals("0")){
                if(callBackJson.containsKey("data")){
                    JSONObject data=callBackJson.getJSONObject("data");

                    Date currentDate=new Date();
                    String currentMonthDate= DateFormatUtils.format(currentDate,"yyyy-MM");
                    String lastMonthDate=DateFormatUtils.format( DateUtils.addMonths(currentDate,-1),"yyyy-MM");
                    String beforeLastMonthDate=DateFormatUtils.format( DateUtils.addMonths(currentDate,-2),"yyyy-MM");
                    if(data!=null && data.containsKey("tradeDetail")){
                        JSONArray array=data.getJSONArray("tradeDetail");
                        if(array!=null){
                            JSONObject jo=null;
                            String orderDate=null;
                            Map currentMonthMap=new HashMap();
                            Map lastMonthMap=new HashMap();
                            Map beforeLastMonthMap=new HashMap();

                            List currentMonthList=new ArrayList();
                            List lastMonthList=new ArrayList();
                            List beforeLastMonthList=new ArrayList();

                            currentMonthMap.put("list",currentMonthList);
                            lastMonthMap.put("list",lastMonthList);
                            beforeLastMonthMap.put("list",beforeLastMonthList);

                            Map mainMap=new HashMap();
                            mainMap.put("currentMonth",currentMonthMap);
                            mainMap.put("lastMonth",lastMonthMap);
                            mainMap.put("beforeLastMonth",beforeLastMonthMap);
                            TyBalanceTradeDetail td=null;
                            Integer transSumAmt=0;
                            Integer transAmt=0;
                            for(int i=0;i<array.size();i++){
                                jo=array.getJSONObject(i);
                                td=jo.toJavaObject(TyBalanceTradeDetail.class);
                                orderDate=StringUtils.isNotBlank(td.getOrderDate())?td.getOrderDate():"";
                                transAmt=StringUtils.isBlank(td.getTrnanAmt())?0:Integer.parseInt(td.getTrnanAmt());
                                if(orderDate.startsWith(currentMonthDate)){
                                    currentMonthList.add(td);

                                    transSumAmt=currentMonthMap.containsKey("transSumAmt")?(Integer)currentMonthMap.get("transSumAmt"):0;
                                    currentMonthMap.put("transSumAmt",transSumAmt+transAmt);
                                    currentMonthMap.put("transCount",currentMonthList.size());
                                }else if(orderDate.startsWith(lastMonthDate)){
                                    lastMonthList.add(td);
                                    transSumAmt=lastMonthMap.containsKey("transSumAmt")?(Integer)lastMonthMap.get("transSumAmt"):0;
                                    lastMonthMap.put("transSumAmt",transSumAmt+transAmt);
                                    lastMonthMap.put("transCount",lastMonthList.size());
                                }else if(orderDate.startsWith(beforeLastMonthDate)){
                                    beforeLastMonthList.add(td);
                                    transSumAmt=beforeLastMonthMap.containsKey("transSumAmt")?(Integer)beforeLastMonthMap.get("transSumAmt"):0;
                                    beforeLastMonthMap.put("transSumAmt",transSumAmt+transAmt);
                                    beforeLastMonthMap.put("transCount",beforeLastMonthList.size());
                                }
                            }
                            return new ResponseVO(1,"成功",mainMap);
                        }
                    }
                }
            }
        }
        return new ResponseVO(-1,"",null);
    }

}
