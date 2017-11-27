package com.ty.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.util.Page;
import com.ty.dao.UserInfoMapper;
import com.ty.entity.UserInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 微信用户相关操作服务
 */
@Service
public class WeixinUserService {

    private static final Logger logger = Logger.getLogger(WeixinUserService.class);
    /** 头像保存路径 */
    private static String HEADIMG_PATH = "";
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private UserInfoMapper userInfoMapper;


    /**
     * 分页查询微信列表
     * 
     * @param page 分页信息
     * @param userInfo 查询条件
     * @return 微信用户列表
     */
    public Page<UserInfo> findUser(Page<UserInfo> page, UserInfo userInfo) {
        // 设置分页参数
        List<UserInfo> UserInfoList = userInfoMapper.findList(userInfo);
        /*
        for(UserInfo user : UserInfoList){
            //转换昵称中的Emoji表情
            user.setNickname(EmojiUtil.resolveToEmojiFromByte(user.getNickname()));
        }
        */
        // 执行分页查询
        page.setResult(UserInfoList);
        return page;
    }

    /**
     * 无分页查询微信列表
     * 
     * @param userInfo 查询条件
     * @return 微信用户列表
     */
    public List<UserInfo> findUser(UserInfo userInfo) {
        return userInfoMapper.findList(userInfo);
    }
    
    /**
     * 添加或更新微信用户资料
     * 
     * @param appid 应用id
     * @param openid 用户openid
     * @return 更新状态
     */
    @Transactional(readOnly = false)
    public UserInfo saveOrUpdate(String appid, String openid) {
    	UserInfo userinfo = weixinInterfaceService.getUserInfo(appid, openid.toString());
    	UserInfo ui = userInfoMapper.selectByopenid(userinfo.getOpenid());
    	if (ui != null) {
    		ui.setOpenid(ui.getOpenid());
    		userInfoMapper.update(userinfo);
    	} else {
    		userInfoMapper.insert(userinfo);
    	}
    	return userinfo;
    }
    
    /**
     * 添加或更新微信用户资料
     * 
     * @param userInfo 查询条件
     * @return 更新状态
     */
    @Transactional(readOnly = false)
    public int saveOrUpdate(UserInfo userInfo) {
        int ret = 0;
        UserInfo ui = userInfoMapper.selectByopenid(userInfo.getOpenid());
        if (ui != null) {
            ui.setOpenid(ui.getOpenid());
            ret = userInfoMapper.update(userInfo);
        } else {
            ret = userInfoMapper.insert(userInfo);
        }
        return ret;
    }

    /**
     * 保存微信用户资料入库以及头像保存本地
     * 
     * @param userInfo 微信用户资料实体bean
     */
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    /**
     * 根据openid查询微信用户表获取微信用户资料
     * 
     * @param openid
     * @return
     */
    public UserInfo selectByopenid(String openid) {
        return userInfoMapper.selectByopenid(openid);
    }

    /**
     * 获取关注者列表并入库
     * 
     * @param appid 应用ID
     * @return
     */
    // public JSONObject getAllUser(String appid){
    // //获取关注者列表
    // JSONObject jsonObject = weixinInterfaceService.getAllUser(appid,"");
    // //拉取列表的后一个用户的OPENID
    // String next_openid = "";
    // if(jsonObject.containsKey("data")){
    // JSONObject json = jsonObject.getJSONObject("data");
    // JSONArray jsarray = json.getJSONArray("openid");
    // next_openid = jsonObject.getString("next_openid");
    // try {
    // //从数据库中用户信息
    // List<Map<String,String>>list = userInfoMapper.findOpenidList(appid);
    // //移除已经存在的用户信息
    // for(Map<String,String> m :list){
    // jsarray.remove(m.get("openid"));
    // }
    // //请求单个用户资料并入库
    // for(Object openid : jsarray.toArray()){
    // UserInfo userinfo = weixinInterfaceService.getUserInfo(appid, openid.toString());
    // this.save(userinfo);
    // }
    // //关注该公众账号的总用户数超过10000的处理
    // while(!next_openid.equals("")){
    // //获取关注者列表
    // jsonObject = weixinInterfaceService.getAllUser(appid,next_openid);
    // //拉取列表的后一个用户的OPENID
    // if(jsonObject.containsKey("data")){
    // json = jsonObject.getJSONObject("data");
    // jsarray = json.getJSONArray("openid");
    // }
    // next_openid = jsonObject.getString("next_openid");
    // //移除已经存在的用户信息
    // for(Map<String,String> m :list){
    // jsarray.remove(m.get("openid"));
    // }
    // //请求单个用户资料并入库
    // for(Object openid : jsarray.toArray()){
    // UserInfo userinfo = weixinInterfaceService.getUserInfo(appid, openid.toString());
    // this.save(userinfo);
    // }
    // }
    // } catch (Exception e) {
    // logger.error(e.getMessage(), e);
    // }
    // }
    // return jsonObject;
    // }

    /**
     * 调用微信接口取回所有关注微信号，数据库存在则判断差异更新，不存在则新增
     * 
     * @param appid 应用id
     * @return
     */
    public JSONObject updateOrSaveUser(String appid) {
        JSONObject jsonObject = new JSONObject();
        try {
            // 获取关注者列表
            jsonObject = weixinInterfaceService.getAllUser(appid, "");
            // 从数据库中用户信息
            List<Map<String, String>> dblist = userInfoMapper.findOpenidList(appid);
            if (jsonObject.containsKey("data")) {
                // 拉取列表的后一个用户的OPENID
                String next_openid = this.updateOrSave(appid, jsonObject, dblist);
                // 关注该公众账号的总用户数超过10000的处理
                while (!next_openid.equals("")) {
                    // 获取关注者列表
                    jsonObject = weixinInterfaceService.getAllUser(appid, next_openid);
                    next_openid = this.updateOrSave(appid, jsonObject, dblist);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return jsonObject;
    }

    /**
     * updateOrSaveUser的封装类
     * 
     * @param appid 应用id
     * @param jsonObject 调用接口获取到的关注公众号账号
     * @return
     */
    public String updateOrSave(String appid, JSONObject jsonObject, List<Map<String, String>> dblist) {
        // 拉取列表的后一个用户的OPENID
        String next_openid = "";
        if (jsonObject.containsKey("data")) {
            JSONObject json = jsonObject.getJSONObject("data");
            JSONArray jsarray = json.getJSONArray("openid");
            next_openid = jsonObject.getString("next_openid");
            try {
                // 数据库微信列表
                List<UserInfo> userlist = userInfoMapper.findList(new UserInfo());
                // 从接口取回来的openid List移除数据库已经存在的用户信息，剩下新增的微信用户
                List<Map<String, String>> removeList = new ArrayList<Map<String, String>>();
                for (Map<String, String> m : dblist) {
                    // 判断已经存在的微信用户
                    if (jsarray.contains(m.get("openid"))) {
                        // 添加进重复列表来进行比对数据库的数据进行更新
                        UserInfo userinfo_interface = weixinInterfaceService.getUserInfo(appid, m.get("openid"));
                        for (UserInfo userinfo_db : userlist) {
                            if (userinfo_interface.getOpenid().equals(userinfo_db.getOpenid())) {
                                // 判断头像是否更新
                                if (userinfo_interface.getHeadimgurl() != null && !userinfo_interface.getHeadimgurl().equals(userinfo_db.getHeadimgurl())
                                        ||(userinfo_interface.getNickname() != null
                                        && !userinfo_interface.getNickname().equals(userinfo_db.getNickname()))
                                        || (userinfo_interface.getSex() != null && !userinfo_interface.getSex().equals(userinfo_db.getSex()))
                                        || (userinfo_interface.getCountry() != null && !userinfo_interface.getCountry().equals(userinfo_db.getCountry()))
                                        || (userinfo_interface.getProvince() != null && !userinfo_interface.getProvince().equals(userinfo_db.getProvince()))
                                        || (userinfo_interface.getCity() != null && !userinfo_interface.getCity().equals(userinfo_db.getCity()))) {
                                    userInfoMapper.update(userinfo_interface);
                                }
                            }
                        }
                        // 从数据库中取出的openidList移除从接口获取的openidList，剩下已取消关注的微信用户，更新关注状态为未关注
                        removeList.add(m);
                        // 接口取回来的微信openid移除数据库有的，剩下的进行新增操作
                        jsarray.remove(m.get("openid"));
                    }
                }
                for (Map<String, String> remove : removeList) {
                    dblist.remove(remove);
                }
                // 如果list不为空，则更新list里的微信用户为未关注
                UserInfo user = new UserInfo();
                for (Map<String, String> m : dblist) {
                    user.setOpenid(m.get("openid"));
                    user.setSubscribe("0");
                    userInfoMapper.update(user);
                }
                // 请求单个用户资料并入库
                for (Object openid : jsarray.toArray()) {
                    UserInfo userinfo = weixinInterfaceService.getUserInfo(appid, openid.toString());
                    this.save(userinfo);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return next_openid;
    }

    /**
     * 单个更新微信用户资料
     * 
     * @param appid 应用id
     * @param openid 用户openid
     * @return
     */
    public UserInfo updateUser(String appid, String openid) {
        UserInfo userinfo_interface = weixinInterfaceService.getUserInfo(appid, openid);
        UserInfo userinfo_db = userInfoMapper.selectByopenid(openid);
        // 已取消关注，从公众号接口获取不到用户信息
        if (userinfo_interface.getSubscribe().equals("0")) {
            userInfoMapper.update(userinfo_interface);
        } else {
            // 这里添加判断差异的字段
            if (!userinfo_interface.getNickname().equals(userinfo_db.getNickname()) ||
                    !userinfo_interface.getSex().equals(userinfo_db.getSex()) ||
                    !userinfo_interface.getCountry().equals(userinfo_db.getCountry()) ||
                    !userinfo_interface.getProvince().equals(userinfo_db.getProvince()) ||
                    !userinfo_interface.getCity().equals(userinfo_db.getCity()) ||
                    !userinfo_interface.getRemark().equals(userinfo_db.getRemark())) {
                userInfoMapper.update(userinfo_interface);
            }
        }
        UserInfo userinfo_dbnew = userInfoMapper.selectByopenid(openid);
        return userinfo_dbnew;
    }
    
    /**
     * 更新单个微信用户备注
     * 
     * @param appid
     * @param openid
     * @param remark
     * @return
     */
    public JSONObject updateRemark(String appid, String openid, String remark) {
        JSONObject ret = new JSONObject();
        try {
            // 调用微信接口更新用户备注
            ret = weixinInterfaceService.updateRemark(appid, openid, remark);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ret.put("retCode", -1);
            ret.put("retMsg", e.getMessage());
        }
        return ret;
    }
}