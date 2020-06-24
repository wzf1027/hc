package cn.stylefeng.guns.modular.system.controller;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.Const;
import cn.stylefeng.guns.core.util.Md5Utils;
import cn.stylefeng.guns.modular.system.entity.MoneyPasswordSetting;
import cn.stylefeng.guns.modular.system.service.MoneyPasswordSettingMgrService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;


@Controller
@RequestMapping("/moneyPasswordSettingMgr")
public class MoneyPasswordSettingMgrController extends BaseController {
	
	@Value("${platform.DOMAIN}")
	private String domain;

	 private String PREFIX = "/modular/system/moneyPasswordSetting/";

	    @Autowired
	    private MoneyPasswordSettingMgrService settingService;

	
	    @RequestMapping("")
	    public String index() {
	        return PREFIX + "moneyPasswordSetting.html";
	    }



	    @RequestMapping(value = "/update")
	    @ResponseBody
	    @Permission(Const.ADMIN_NAME)
	    public ResponseData update(String oldPassword,String newPassword,String againPassword) {
	    	if(StringUtils.isBlank(oldPassword)) {
	    		return ResponseData.error("请输入原密码");
	    	}
	    	if(StringUtils.isBlank(newPassword)) {
	    		return ResponseData.error("请输入新密码");
	    	}
	    	if(StringUtils.isBlank(againPassword)) {
	    		return ResponseData.error("请输入确认新密码");
	    	}
	    	if(!newPassword.equals(againPassword)) {
	    		return ResponseData.error("新密码与确认密码不一致");
	    	}
	    	MoneyPasswordSetting old = settingService.getOne(null);
	    	if(!old.getPassword().equals(Md5Utils.GetMD5Code(oldPassword))) {
	    		return ResponseData.error("输入的原密码有误");
	    	}
	    	old.setPassword(Md5Utils.GetMD5Code(newPassword));
	    	old.setUpdateTime(new Date());
	    	this.settingService.updateById(old);
	        return SUCCESS_TIP;
	    }
	
}
