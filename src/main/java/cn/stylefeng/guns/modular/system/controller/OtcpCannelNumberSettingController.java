package cn.stylefeng.guns.modular.system.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.entity.AccepterRebateSetting;
import cn.stylefeng.guns.modular.system.entity.OtcpCannelNumberSetting;
import cn.stylefeng.guns.modular.system.service.AccepterRateSettingService;
import cn.stylefeng.guns.modular.system.service.OtcpCannelNumberSettingService;
import cn.stylefeng.guns.modular.system.warpper.AccepterRateSettingWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/otcpCannelNumberSettingMgr")
public class OtcpCannelNumberSettingController extends BaseController {

	   private String PREFIX = "/modular/system/otcpCannelNumberSetting/";
	   
	   @Autowired
	   private OtcpCannelNumberSettingService cannelNumberSettingService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "otcpCannelNumberSetting.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.cannelNumberSettingService.list(condition);
	        Page<Map<String, Object>> wrap = new AccepterRateSettingWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   @RequestMapping("/otcpCannelNumberSetting_update/{settingId}")
	    public String noticeUpdate(@PathVariable Long settingId, Model model) {
		   	OtcpCannelNumberSetting setting = this.cannelNumberSettingService.getById(settingId);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "otcpCannelNumberSetting_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(OtcpCannelNumberSetting setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getSettingId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        if(setting.getNumber()== null || setting.getNumber() <=0) {
	        	return ResponseData.error("取消次数不能小于0");
	        }
			if(setting.getTime()== null || setting.getTime() <=0) {
				return ResponseData.error("可购买时间不能小于0");
			}
			if(setting.getMinTime()== null || setting.getMinTime() <=0) {
				return ResponseData.error("确认时间不能小于0");
			}
	        OtcpCannelNumberSetting old = this.cannelNumberSettingService.getById(setting.getSettingId());
			old.setNumber(setting.getNumber());
			old.setTime(setting.getTime());
			old.setMinTime(setting.getMinTime());
	        this.cannelNumberSettingService.updateById(old);
	        return SUCCESS_TIP;
	    }
	   	
}
