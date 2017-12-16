package com.ty.dao;

import com.gen.framework.common.util.Page;
import com.ty.entity.Msg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息管理
 * 
 * @author Jacky
 * 
 */
@Mapper
public interface MsgMapper {
    /** 消息入库 */
    public int save(Msg msg);

    /** 修改 */
    public int update(Msg msg);

    /** 删除五天前状态为未收藏消息 */
    public int deleteUnCollect();

    /** 根据id查询消息对象 */
    public Msg selectById(@Param("id")Integer id);

    List<Msg> findList(@Param("page")Page page, @Param("msg")Msg msg);
    /** 查询总数*/
    int findListCount(@Param("msg")Msg msg);

    /** 回复消息列表 */
    public List<Msg> replyList(Msg msg);
    
    /** 更新历史消息回复状态 */
    public int markMsg(Msg msg);
}