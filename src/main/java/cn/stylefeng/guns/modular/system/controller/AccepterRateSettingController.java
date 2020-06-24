package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.entity.AccepterRebateSetting;
import cn.stylefeng.guns.modular.system.service.AccepterRateSettingService;
import cn.stylefeng.guns.modular.system.warpper.AccepterRateSettingWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/accepterRateSettingMgr")
public class AccepterRateSettingController extends BaseController {

	   private String PREFIX = "/modular/system/accepterRateSetting/";
	   
	   @Autowired
	   private AccepterRateSettingService accepterRateSettingService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "accepterRateSetting.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.accepterRateSettingService.list(condition);
	        Page<Map<String, Object>> wrap = new AccepterRateSettingWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   @RequestMapping("/accepterReteSetting_update/{rebateId}")
	    public String noticeUpdate(@PathVariable Long rebateId, Model model) {
		   AccepterRebateSetting setting = this.accepterRateSettingService.getById(rebateId);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "accepterReteSetting_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(AccepterRebateSetting setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getRebateId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        if(setting.getValue()== null || setting.getValue() <0) {
	        	return ResponseData.error("返利比例不能小于0");
	        }
	        
	        AccepterRebateSetting old = this.accepterRateSettingService.getById(setting.getRebateId());
	        old.setValue(setting.getValue());
	        this.accepterRateSettingService.updateById(old);
	        return SUCCESS_TIP;
	    }
	   	
}
