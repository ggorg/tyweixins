package com.gen.framework.common.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuPowerMapper {
    List queryById(@Param("uid") Integer uid);
}
