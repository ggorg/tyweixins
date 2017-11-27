package com.ty.dao;

import com.ty.entity.ShakeRecord;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 微信摇一摇
 * @author Jacky
 *
 */
@Repository
@Transactional
public interface ShakeRecordMapper {
	
	int insert(ShakeRecord shakeRecord);
}
