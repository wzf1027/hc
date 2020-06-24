package cn.stylefeng.guns.modular.system.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.entity.SellerOrderTimeSetting;
import cn.stylefeng.guns.modular.system.service.SellerOrderTimeSettingService;
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

import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/sellerOrderTimeMgr")
public class SellerOrderTimeSettingController extends BaseController {

	   private String PREFIX = "/modular/system/sellerOrderTimeSetting/";
	   
	   @Autowired
	   private SellerOrderTimeSettingService orderTimeSettingService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "sellerOrderTimeSetting.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.orderTimeSettingService.list(condition);
	        return LayuiPageFactory.createPageInfo(list);
	    }
	   
	   @RequestMapping("/sellerOrderTimeSetting_update/{id}")
	    public String noticeUpdate(@PathVariable Long id, Model model) {
		   SellerOrderTimeSetting setting = this.orderTimeSettingService.getById(id);
	        model.addAllAttributes(BeanUtil.beanToMap(setting));
	        LogObjectHolder.me().set(setting);
	        return PREFIX + "sellerOrderTimeSetting_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(SellerOrderTimeSetting setting) {
	        if (ToolUtil.isOneEmpty(setting, setting.getId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        if (setting.getStarTime() == null || setting.getStarTime() <=0){
	        	return  ResponseData.error("待解冻时间不能小于0");
			}
			if (setting.getEndTime() == null || setting.getEndTime() <=0){
				return  ResponseData.error("取消时间不能小于0");
			}

			SellerOrderTimeSetting old = this.orderTimeSettingService.getById(setting.getId());
	    	old.setStarTime(setting.getStarTime());
	    	old.setEndTime(setting.getEndTime());
	    	old.setUpdateTime(new Date());
	        this.orderTimeSettingService.updateById(old);
	        return SUCCESS_TIP;
	    }
	   	
}
