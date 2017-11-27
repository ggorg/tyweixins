package com.ty.dao;

import com.ty.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 微信用户资料
 *
 * @author Jacky
 */
@Mapper
public interface UserInfoMapper {
    int insert(@Param("userInfo") UserInfo userInfo);
    
    int update(UserInfo userInfo);

    UserInfo selectByopenid(@Param("openid") String openid);

    /**
     * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setPage(new Page<T>());
     *
     * @param entity
     * @return
     */
    List<UserInfo> findList(UserInfo entity);
    
    /**
     * 获取用户openid列表
     * @param appid 应用ID
     * @return 用户openid
     */
    List<Map<String,String>> findOpenidList(@Param("appid") String appid);
}
