package com.ty.dao;

import com.ty.entity.RedPack;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 微信红包配置
 *
 * @author Jacky
 */
@Repository
@Transactional
public interface RedPackMapper {
    int insert(@Param("userInfo") RedPack redPack);
    
    int update(RedPack redPack);
    
    RedPack selectByappid(@Param("appid") String appid);
    
}
