package com.ty.dao;

import com.ty.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2017/12/6.
 */
@Mapper
public interface MenuMapper {
    List<Menu> findList(@Param("appid")String appid);
    List<Menu> findListById(@Param("id")Integer id);
    int insert(Menu menu);
    int delete(@Param("id")Integer id);
    int update(Menu menu);
    Menu selectById(@Param("id")int id);
}
