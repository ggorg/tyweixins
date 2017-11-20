package gen.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gen.framework.common.beans.CommonSearchBean;
import gen.framework.common.dao.CommonMapper;
@Service
public class DirectoryDataService {
	@Autowired
	private CommonMapper commonMapper;
	public List get(String type){
		String tablename=null;
		switch (type) {
		case "dept":tablename="em_department";break;
		case "projecttype":tablename="em_projecttype";break;
		case "useTypeCode":tablename="em_usetype";break;

		default:
			break;
		}
		return this.commonMapper.selectObjects(new CommonSearchBean(tablename, null, null, null,null,null));
	}
}
