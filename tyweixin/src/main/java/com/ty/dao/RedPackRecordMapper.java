package com.ty.dao;


import com.ty.entity.RedPackRecord;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 红包发送记录
 * @author Jacky
 *
 */
@Repository
@Transactional
public interface RedPackRecordMapper {
	int insert(RedPackRecord redPackRecord);
}
