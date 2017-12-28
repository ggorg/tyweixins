package com.ty.dao;

import com.gen.framework.common.util.Page;
import com.ty.entity.Tags;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户标签管理
 * Created by Jacky on 2017/12/27.
 */
@Mapper
public interface TagsMapper {
    List<Tags> findList(@Param("page")Page page,@Param("tags")Tags tags);
    int findListCount(@Param("tags")Tags tags);
    int insert(@Param("entity")Tags entity);
    int delete(@Param("id")Integer id);
    int update(Tags entity);
    Tags selectById(Tags entity);
}
