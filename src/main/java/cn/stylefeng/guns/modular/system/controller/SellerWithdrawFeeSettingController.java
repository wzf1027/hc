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
import cn.stylefeng.guns.modular.system.entity.SellerWithdrawFeeSetting;
import cn.stylefeng.guns.modular.system.service.SellerWithdrawFeeSettingService;
import cn.stylefeng.guns.modular.system.warpper.SellerWithdrawCoinAppealOrderWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/sellerWithdrawFeeSettingMgr")
public class SellerWithdrawFeeSettingController extends BaseController {

	   private String PREFIX = "/modular/system/sellerWithdrawFeeSetting/";
	   
	   @Autowired
	   private SellerWithdrawFeeSettingService sellerWithdrawFeeSettingService;
	   
	   @RequestMapping("")
	   public String index() {
	        return PREFIX + "sellerWithdrawFeeSetting.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.sellerWithdrawFeeSettingService.list(condition);
	        //Page<Map<String, Object>> wrap = new SellerWithdrawCoinAppealOrderWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(list);
	    }
	   
	   @RequestMapping("/sellerWithdrawFeeSetting_update/{settingId}")
	    public String noticeUpdate(@PathVariable Long settingId, Model model) {
	        SellerWithdrawFeeSetting setting = this.sellerWithdrawFeeSettingService.getById(settingId);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "sellerWithdrawFeeSetting_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(SellerWithdrawFeeSetting setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getSettingId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        if(setting.getMinNumber() == null || setting.getMinNumber() <0) {
	        	return ResponseData.error("最小提现额度不能小于0");
	        }
	        if(setting.getMaxNumber() == null || setting.getMaxNumber() <0) {
	        	return ResponseData.error("最大提现额度不能小于0");
	        }
	        if(setting.getMinFeeNumber() == null || setting.getMinFeeNumber() <0) {
	        	return ResponseData.error("最小提现手续费数量不能小于0");
	        }
	        if(setting.getStartRatioNumber() == null || setting.getStartRatioNumber() <0) {
	        	return ResponseData.error("起始提现手续费比例算起比例不能小于0");
	        }
	        if(setting.getFeeRatio() == null || setting.getFeeRatio() <0) {
	        	return ResponseData.error("起始提现比例不能小于0");
	        }
	        
	        SellerWithdrawFeeSetting old = this.sellerWithdrawFeeSettingService.getById(setting.getSettingId());
	        old.setMinFeeNumber(setting.getMinFeeNumber());
	        old.setMaxNumber(setting.getMaxNumber());
	        old.setMinNumber(setting.getMinNumber());
	        old.setStartRatioNumber(setting.getStartRatioNumber());
	        old.setFeeRatio(setting.getFeeRatio());
	        this.sellerWithdrawFeeSettingService.updateById(old);
	        return SUCCESS_TIP;
	    }
	   
	
}
