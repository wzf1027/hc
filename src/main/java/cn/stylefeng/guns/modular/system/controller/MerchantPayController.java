package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
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
import cn.stylefeng.guns.modular.system.entity.MerchantPay;
import cn.stylefeng.guns.modular.system.service.MerchantPayService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/merchantPayMgr")
public class MerchantPayController extends BaseController {

	   private String PREFIX = "/modular/system/merchant/";
	   
	   @Autowired
	   private MerchantPayService merchantPayService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "merchantPay.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.merchantPayService.list(condition);
	        return LayuiPageFactory.createPageInfo(list);
	    }
	   
	   @RequestMapping("/merchantPay_update/{id}")
	    public String noticeUpdate(@PathVariable Long id, Model model) {
		   MerchantPay setting = this.merchantPayService.getById(id);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "merchantPay_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(MerchantPay setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        if(StringUtils.isBlank(setting.getValue())) {
	        	return ResponseData.error("返利比例不能小于0");
	        }
	        
	        MerchantPay old = this.merchantPayService.getById(setting.getId());
	        old.setValue(setting.getValue());
	        this.merchantPayService.updateById(old);
	        return SUCCESS_TIP;
	    }
	   	
}
