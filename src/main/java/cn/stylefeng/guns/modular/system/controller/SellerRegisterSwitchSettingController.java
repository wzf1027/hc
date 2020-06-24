package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.entity.SellerRegisterSwitchSetting;
import cn.stylefeng.guns.modular.system.service.SellerRegisterSwitchSettingService;
import cn.stylefeng.guns.modular.system.warpper.SellerRegisterSwitchWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/sellerRegisterSwitchSettingMgr")
public class SellerRegisterSwitchSettingController extends BaseController {

	   private String PREFIX = "/modular/system/seller/";
	   
	   @Autowired
	   private SellerRegisterSwitchSettingService settingService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "sellerRegisterSwitchSetting.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.settingService.list(condition);
	        Page<Map<String, Object>> wrap = new SellerRegisterSwitchWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   	@RequestMapping("/freeze")
	    @ResponseBody
	    public ResponseData freeze(@RequestParam Long switchSettingId) {
	        if (ToolUtil.isEmpty(switchSettingId)) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        SellerRegisterSwitchSetting setting =  this.settingService.getById(switchSettingId);
	        if(setting.getIsSwitch() ==0) {
	        	setting.setIsSwitch(1);
	        }else {
	        	setting.setIsSwitch(0);
	        }
	        this.settingService.updateById(setting);
	        return SUCCESS_TIP;
	    }


	
}
