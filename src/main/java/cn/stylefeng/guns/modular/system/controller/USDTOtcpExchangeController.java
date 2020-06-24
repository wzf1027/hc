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
import cn.stylefeng.guns.modular.system.entity.USDTOtcpExchange;
import cn.stylefeng.guns.modular.system.service.USDTOtcpExchangeService;
import cn.stylefeng.guns.modular.system.warpper.USDTOtcpExchangeWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/uSDTOtcpExchangeMgr")
public class USDTOtcpExchangeController extends BaseController {

	   private String PREFIX = "/modular/system/usdtOtcpExchange/";
	   
	   @Autowired
	   private USDTOtcpExchangeService uSDTOtcpExchangeService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "usdtOtcpExchange.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.uSDTOtcpExchangeService.list(condition);
	        Page<Map<String, Object>> wrap = new USDTOtcpExchangeWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   @RequestMapping("/usdtOtcpExchange_update/{exchangeId}")
	    public String noticeUpdate(@PathVariable Long exchangeId, Model model) {
	        USDTOtcpExchange setting = this.uSDTOtcpExchangeService.getById(exchangeId);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "usdtOtcpExchange_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(USDTOtcpExchange setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getExchangeId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        if(setting.getValue()== null || setting.getValue() <0) {
	        	return ResponseData.error("价格不能小于0");
	        }
	        
	        USDTOtcpExchange old = this.uSDTOtcpExchangeService.getById(setting.getExchangeId());
	        old.setValue(setting.getValue());
	        this.uSDTOtcpExchangeService.updateById(old);
	        return SUCCESS_TIP;
	    }
	   	
}
