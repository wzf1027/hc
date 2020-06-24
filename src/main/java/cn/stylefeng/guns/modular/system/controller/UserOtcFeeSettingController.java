package cn.stylefeng.guns.modular.system.controller;

import java.util.Date;
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
import cn.stylefeng.guns.modular.system.entity.UserOtcFeeSetting;
import cn.stylefeng.guns.modular.system.service.UserOtcFeeSettingService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/userOtcFeeSettingMgr")
public class UserOtcFeeSettingController extends BaseController {

	   private String PREFIX = "/modular/system/user/";
	   
	   @Autowired
	   private UserOtcFeeSettingService userOtcFeeSettingService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "otcFeeSetting.html";
	    }
	   
	   @RequestMapping("/agentIndex")
	    public String agentIndex() {
	        return PREFIX + "agentOtcFeeSetting.html";
	    }
	   
	   @RequestMapping(value = "/list/{type}")
	   @ResponseBody
	    public Object list(@PathVariable Integer type,String condition) {
	        Page<Map<String, Object>> list = this.userOtcFeeSettingService.list(condition,type);
	        return LayuiPageFactory.createPageInfo(list);
	    }
	   
	   @RequestMapping("/fee_update/{settingId}")
	    public String feeUpdate(@PathVariable Long settingId, Model model) {
	        UserOtcFeeSetting setting = this.userOtcFeeSettingService.getById(settingId);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "fee_edit.html";
	    }
	   
	   @RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(UserOtcFeeSetting setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getSettingId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        if(setting.getRatio() == null || setting.getRatio()<0) {
	        	return ResponseData.error("比例不能小于0");
	        }
	        UserOtcFeeSetting old = this.userOtcFeeSettingService.getById(setting.getSettingId());
	        old.setUpdateTime(new Date());
	        old.setRatio(setting.getRatio());
	        this.userOtcFeeSettingService.updateById(old);
	        return SUCCESS_TIP;
	    }
	   
	 
	
}
