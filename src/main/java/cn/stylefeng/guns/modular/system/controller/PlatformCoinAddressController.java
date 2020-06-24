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
import cn.stylefeng.guns.modular.system.entity.PlatformCoinAddress;
import cn.stylefeng.guns.modular.system.service.PlatformCoinAddressService;
import cn.stylefeng.guns.modular.system.warpper.SellerChargerCoinAppealWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/platformCoinAddressMgr")
public class PlatformCoinAddressController extends BaseController {

	   private String PREFIX = "/modular/system/platformCoinAddress/";
	   
	   @Autowired
	   private PlatformCoinAddressService platformCoinAddressService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "platformCoinAddress.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.platformCoinAddressService.list(condition);
	       // Page<Map<String, Object>> wrap = new SellerChargerCoinAppealWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(list);
	    }
	   
	   @RequestMapping("/platformCoinAddress_update/{coinAddressId}")
	    public String noticeUpdate(@PathVariable Long coinAddressId, Model model) {
	        PlatformCoinAddress address = this.platformCoinAddressService.getById(coinAddressId);
	        model.addAllAttributes(BeanUtil.beanToMap(address));
	        LogObjectHolder.me().set(address);
	        return PREFIX + "platformCoinAddress_edit.html";
	    }
	   
	   
	   	@RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(PlatformCoinAddress address) {
	        if (ToolUtil.isOneEmpty(address, address.getCoinAddressId())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        if(StringUtils.isBlank(address.getAddress())) {
	        	return ResponseData.error("USDT充币地址不能为空");
	        }
	        PlatformCoinAddress old = this.platformCoinAddressService.getById(address.getCoinAddressId());
	        old.setAddress(address.getAddress());
	        this.platformCoinAddressService.updateById(old);
	        return SUCCESS_TIP;
	    }

	
}
