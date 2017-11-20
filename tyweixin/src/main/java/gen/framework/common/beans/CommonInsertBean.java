package gen.framework.common.beans;

import java.util.Map;

import gen.framework.common.util.BeanToMapUtil;

public class CommonInsertBean {
	private String tablename;
	private Object params;
	public String getTablename() {
		return tablename;
	}
	public Object getParams() {
		return params;
	}
	public void setParams(Object params) {
		if(!(params instanceof Map)){
			this.params=BeanToMapUtil.beanToMap(params);
		}else{
			this.params = params;
		}
	
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public CommonInsertBean() {
		// TODO Auto-generated constructor stub
	}
	public CommonInsertBean(String tablename,Object params){
		this.tablename=tablename;
		this.setParams(params);
	}
}
