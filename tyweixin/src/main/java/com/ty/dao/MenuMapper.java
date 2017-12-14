package com.ty.dao;

import com.gen.framework.common.util.Page;
import com.ty.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2017/12/6.
 */
@Mapper
public interface MenuMapper {
    List<Menu> findList(@Param("page")Page page, @Param("appid")String appid);
    /** 查询总数*/
    public int findListCount(@Param("appid")String appid);
    /*根据父id查询子菜单*/
    List<Menu> findListById(@Param("id")Integer id);
    int insert(Menu menu);
    int delete(@Param("id")Integer id);
    int update(Menu menu);
    Menu selectById(@Param("id")int id);
}
