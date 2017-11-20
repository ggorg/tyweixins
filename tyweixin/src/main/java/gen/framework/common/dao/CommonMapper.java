package gen.framework.common.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import gen.framework.common.beans.CommonCountBean;
import gen.framework.common.beans.CommonInsertBean;
import gen.framework.common.beans.CommonSearchBean;
import gen.framework.common.beans.CommonUpdateBean;
@SuppressWarnings("rawtypes")
@Mapper
public interface CommonMapper {
	long selectCount(CommonCountBean commonCountBean);
	List selectObjects(CommonSearchBean commonSearchBean);
	int insertObject(CommonInsertBean commonInsertBean);
	int updateObject(CommonUpdateBean commonUpdateBean);
}
