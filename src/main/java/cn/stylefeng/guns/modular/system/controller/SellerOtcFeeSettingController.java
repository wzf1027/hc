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
import cn.stylefeng.guns.modular.system.entity.SellerOtcFeeSetting;
import cn.stylefeng.guns.modular.system.service.SellerOtcFeeSettingService;
import cn.stylefeng.guns.modular.system.warpper.AccepterRateSettingWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/sellerOtcFeeSettingMgr")
public class SellerOtcFeeSettingController extends BaseController {

	   private String PREFIX = "/modular/system/sellerOtcFeeSetting/";
	   
	   @Autowired
	   private SellerOtcFeeSettingService sellerOtcFeeSettingService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "sellerOtcFeeSetting.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.sellerOtcFeeSettingService.list(condition);
	        Page<Map<String, Object>> wrap = new AccepterRateSettingWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   @RequestMapping("/sellerOtcFeeSetting_update/{settingId}")
	    public String update(@PathVariable Long settingId, Model model) {
		   SellerOtcFeeSetting setting = this.sellerOtcFeeSettingService.getById(settingId);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "sellerOtcFeeSetting_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(SellerOtcFeeSetting setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getSettingId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }     
	        SellerOtcFeeSetting old = this.sellerOtcFeeSettingService.getById(setting.getSettingId());
	        old.setRatio(setting.getRatio());
	        old.setUpdateTime(new Date());
	        this.sellerOtcFeeSettingService.updateById(old);
	        return SUCCESS_TIP;
	    }
	   	
}
