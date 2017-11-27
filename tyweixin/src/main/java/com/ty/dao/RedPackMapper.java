package com.ty.dao;

import com.ty.entity.RedPack;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 微信红包配置
 *
 * @author Jacky
 */
@Mapper
public interface RedPackMapper {
    int insert(@Param("userInfo") RedPack redPack);
    
    int update(RedPack redPack);
    
    RedPack selectByappid(@Param("appid") String appid);
    
}
