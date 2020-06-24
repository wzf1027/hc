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
import cn.stylefeng.guns.modular.system.entity.SellerAwardSetting;
import cn.stylefeng.guns.modular.system.service.SellerAwardSettingService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/sellerAwardSettingMgr")
public class SellerAwardSettingController extends BaseController {

	   private String PREFIX = "/modular/system/sellerAwardSetting/";
	   
	   @Autowired
	   private SellerAwardSettingService sellerAwardSettingService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "sellerAwardSetting.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.sellerAwardSettingService.list(condition);
	        return LayuiPageFactory.createPageInfo(list);
	    }
	   
	   
	   @RequestMapping("/sellerAwardSetting_update/{settingId}")
	    public String update(@PathVariable Long settingId, Model model) {
		   SellerAwardSetting setting = this.sellerAwardSettingService.getById(settingId);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "sellerAwardSetting_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(SellerAwardSetting setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getSettingId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        SellerAwardSetting old = this.sellerAwardSettingService.getById(setting.getSettingId());
	        old.setValue(setting.getValue());
	        this.sellerAwardSettingService.updateById(old);
	        return SUCCESS_TIP;
	    }
	   	
	   
	   
	   
	
}
