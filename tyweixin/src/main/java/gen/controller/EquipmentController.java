package gen.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gen.services.EquipmentService;

@Controller
@RequestMapping("/equipment")
public class EquipmentController {
	private static final Logger logger = LoggerFactory.getLogger(EquipmentController.class);
	@Autowired
	private EquipmentService equipmentService;
	

	@RequestMapping(value="/ajaxList")
	@ResponseBody
	
	public String list(String typeName,
			String subjectName,
			String deptid,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize){
		try {
			return this.equipmentService.equipmentList(typeName, subjectName, deptid, pageNum, pageSize);
		} catch (Exception e) {
			logger.error("EquipmentController.list",e);
			e.printStackTrace();
			return "{\"retCode\":-1,\"retMsg\":\"系统出现异常\"}";
		}

	}
	@RequestMapping(value="/ajaxInfo")
	@ResponseBody
	public String info(String id){
		try {
			return this.equipmentService.equipmentInfo(id);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("EquipmentController.info",e);
			e.printStackTrace();
			return "{\"retCode\":-1,\"retMsg\":\"系统出现异常\"}";
		}
	}
}
