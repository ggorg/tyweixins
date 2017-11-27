package com.ty.dao;

import com.ty.entity.Msg;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息管理
 * 
 * @author Jacky
 * 
 */
@Repository
@Transactional
public interface MsgMapper {
    /** 消息入库 */
    public int save(Msg msg);

    /** 修改 */
    public int update(Msg msg);

    /** 删除五天前状态为未收藏消息 */
    public int deleteUnCollect();

    /** 根据id查询消息对象 */
    public Msg selectById(Msg msg);

    /**
     * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setPage(new Page<T>());
     */
    public List<Msg> findList(Msg msg);

    /** 回复消息列表 */
    public List<Msg> replyList(Msg msg);
    
    /** 更新历史消息回复状态 */
    public int markMsg(Msg msg);
}