package cn.stylefeng.guns.modular.system.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.entity.ExchangeSetting;
import cn.stylefeng.guns.modular.system.service.ExchangeFeeSettingService;
import cn.stylefeng.guns.modular.system.warpper.ExchangeFeeSettingWrapper;
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

import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/exchangeFeeSettingMgr")
public class ExchangeFeeSettingController extends BaseController {

	   private String PREFIX = "/modular/system/exchangeFeeSetting/";
	   
	   @Autowired
	   private ExchangeFeeSettingService exchangeFeeSettingService;
	   
	   /**
	    * 会员
	    * @return
	    */
	   @RequestMapping("/sellerExchangeFeeSetting")
	    public String sellerExchangeFeeSetting() {
	        return PREFIX + "sellerExchangeFeeSetting.html";
	    }
	   /**
	    * 商户
	    * @return
	    */
	   @RequestMapping("/userExchangeFeeSetting")
	    public String userExchangeFeeSetting() {
	        return PREFIX + "userExchangeFeeSetting.html";
	    }
	   
	   /**
	    * 代理商
	    */
	   @RequestMapping("/agentExchangeFeeSetting")
	    public String agentExchangeFeeSetting() {
	        return PREFIX + "agentExchangeFeeSetting.html";
	    }
	   
	   @RequestMapping(value = "/list//{roleId}")
	   @ResponseBody
	    public Object list(@PathVariable Integer roleId) {
	        Page<Map<String, Object>> list = this.exchangeFeeSettingService.list(roleId);
	        Page<Map<String, Object>> wrap = new ExchangeFeeSettingWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   @RequestMapping("/exchangeFeeSetting_update/{id}")
	    public String update(@PathVariable Long id, Model model) {
		   ExchangeSetting setting = this.exchangeFeeSettingService.getById(id);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "exchangeFeeSetting_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(ExchangeSetting setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }     
	        ExchangeSetting old = this.exchangeFeeSettingService.getById(setting.getId());
	        old.setExchangeValue(setting.getExchangeValue());
	        old.setUpdateTime(new Date());
	        this.exchangeFeeSettingService.updateById(old);
	        return SUCCESS_TIP;
	    }
	   	
}
