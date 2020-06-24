package cn.stylefeng.guns.modular.system.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.entity.SellerGradPriceSetting;
import cn.stylefeng.guns.modular.system.service.SellerGradPriceSettingService;
import cn.stylefeng.roses.core.base.controller.BaseController;
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
@RequestMapping("/sellerGradPriceMgr")
public class SellerGradPriceSettingController extends BaseController {

	   private String PREFIX = "/modular/system/sellerGradPrice/";
	   
	   @Autowired
	   private SellerGradPriceSettingService gradPriceSettingService;
	   
	   @RequestMapping("")
	    public String index() {
	   		return PREFIX + "seller_grad_price.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.gradPriceSettingService.list(condition);
	        return LayuiPageFactory.createPageInfo(list);
	    }
	   
	   @RequestMapping("/gradPriceSetting_update/{id}")
	    public String noticeUpdate(@PathVariable Long id, Model model) {
		   SellerGradPriceSetting setting = this.gradPriceSettingService.getById(id);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "gradPriceSetting_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(SellerGradPriceSetting setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
			SellerGradPriceSetting old = this.gradPriceSettingService.getById(setting.getId());
	        old.setCash(setting.getCash());
	        old.setPriceRate(setting.getPriceRate());
	        this.gradPriceSettingService.updateById(old);
	        return SUCCESS_TIP;
	    }
	   	
}
