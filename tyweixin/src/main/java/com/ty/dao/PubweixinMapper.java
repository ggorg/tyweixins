package com.ty.dao;

import com.ty.entity.Pubweixin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 微信公众号
 * @author Jacky
 *
 */
@Mapper
public interface PubweixinMapper {
	
	Pubweixin selectByAppid(@Param("appid") String appid);
	Pubweixin selectByOpenid(@Param("openid") String openid);
	/**查询出所有公众号信息  获取accesstoken用 */
	List<Pubweixin> select();
	/**查询出所有删除公众号信息  移除accesstoken用 */
	List<Pubweixin> selectdel();
	
	int insert(Pubweixin pubweixin);
	
	int update(Pubweixin pubweixin);
	
	/**
     * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setPage(new Page<T>());
     *
     * @param entity
     * @return
     */
    public List<Pubweixin> findList(Pubweixin entity);
}
