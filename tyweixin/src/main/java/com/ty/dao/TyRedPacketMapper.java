package com.ty.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TyRedPacketMapper {
    List getCount(@Param("trIsOpen") Boolean trIsOpen);
}
