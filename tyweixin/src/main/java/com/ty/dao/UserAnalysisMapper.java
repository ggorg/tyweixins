package com.ty.dao;


import com.ty.entity.UserAnalysis;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户分析
 * @author Jacky
 *
 */
@Repository
@Transactional
public interface UserAnalysisMapper {
	
	int insert(UserAnalysis entity);
	
	List<UserAnalysis> findList(UserAnalysis entity);
}
