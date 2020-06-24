package cn.stylefeng.guns.modular.system.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.entity.SellerBuySoldOutSetting;
import cn.stylefeng.guns.modular.system.service.SellerBuySoldOutSettingService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
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
@RequestMapping("/sellerBuySoldOutMgr")
public class SellerBuySoldOutSettingController extends BaseController {

	   private String PREFIX = "/modular/system/sellerBuySoldOutSetting/";
	   
	   @Autowired
	   private SellerBuySoldOutSettingService sellerBuySoldOutSettingService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "sellerBuySoldOutSetting.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.sellerBuySoldOutSettingService.list(condition);
	        return LayuiPageFactory.createPageInfo(list);
	    }
	   
	   @RequestMapping("/sellerBuySoldOutSetting_update/{id}")
	    public String noticeUpdate(@PathVariable Long id, Model model) {
		   SellerBuySoldOutSetting setting = this.sellerBuySoldOutSettingService.getById(id);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "sellerBuySoldOutSetting_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(SellerBuySoldOutSetting setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        if (setting.getNumber() == null || setting.getNumber() <=0){
	        	return ResponseData.error("数量不能小于0");
			}
	        if (setting.getTime() == null || setting.getTime() <=0){
	        	return ResponseData.error("时间不能小于0");
			}
			SellerBuySoldOutSetting old = this.sellerBuySoldOutSettingService.getById(setting.getId());
	   		old.setNumber(setting.getNumber());
	   		old.setTime(setting.getTime());
	        this.sellerBuySoldOutSettingService.updateById(old);
	        return SUCCESS_TIP;
	    }
	   	
}
