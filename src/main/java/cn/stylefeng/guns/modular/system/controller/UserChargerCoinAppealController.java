package cn.stylefeng.guns.modular.system.controller;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.util.Md5Utils;
import cn.stylefeng.guns.modular.app.service.OtcOrderService;
import cn.stylefeng.guns.modular.system.entity.PlatformCoinAddress;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.entity.UserChargerCoinAppealOrder;
import cn.stylefeng.guns.modular.system.service.PlatformCoinAddressService;
import cn.stylefeng.guns.modular.system.service.UserChargerCoinAppealService;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.guns.modular.system.warpper.UserChargerCoinAppealWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/userChargerCoinAppealMgr")
public class UserChargerCoinAppealController extends BaseController {

	   private String PREFIX = "/modular/system/user/";
	   
	   @Autowired
	   private UserChargerCoinAppealService appealService;
	   
	   @Autowired
	   private UserService userService;
	   
	   @Autowired
	   private OtcOrderService  otcOrderService;
	   
	   @Autowired
	   private PlatformCoinAddressService platformCoinAddressService;
	   
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "userChargerCoinAppealOrder.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition,
	    		@RequestParam(required = false) String phone,
				@RequestParam(required = false) String serialno,
				@RequestParam(required = false) String hashValue,
				@RequestParam(required = false) Integer status,
	            @RequestParam(required = false) String timeLimit) {
		 //拼接查询条件
	        String beginTime = "";
	        String endTime = "";

	        if (ToolUtil.isNotEmpty(timeLimit)) {
	            String[] split = timeLimit.split(" - ");
	            beginTime = split[0];
	            endTime = split[1];
	        }
		   if(ShiroKit.isAdmin()) {
			   Page<Map<String, Object>> list = this.appealService.list(condition,null,phone,serialno,hashValue,status,beginTime,endTime);
		        Page<Map<String, Object>> wrap = new UserChargerCoinAppealWrapper(list).wrap();
		        return LayuiPageFactory.createPageInfo(wrap); 
		   }
		   Page<Map<String, Object>> list = this.appealService.list(condition,ShiroKit.getUserNotNull().getId(),phone,serialno,hashValue,status,beginTime,endTime);
	        Page<Map<String, Object>> wrap = new UserChargerCoinAppealWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	       
	    }
	   
	   @RequestMapping("/chargerCoinAppeal_add")
	    public String chargerCoinAppealAdd(Model model) {
		   PlatformCoinAddress coinAddress = platformCoinAddressService.getOne(null);
		   if(coinAddress != null) {
			   model.addAttribute("address", coinAddress.getAddress());
		   }
	        return PREFIX + "chargerCoinAppeal_add.html";
	    }
	   
	   
	   @RequestMapping(value = "/add")
	    @ResponseBody
	    @Role(value =2)
	    public Object add(@RequestParam String tradePwd,@RequestParam Double number,@RequestParam String hashValue) {
		   if(StringUtils.isBlank(tradePwd)) {
			   return ResponseData.error("交易密码不能为空");
		   }
		   if(StringUtils.isBlank(hashValue)) {
			   return ResponseData.error("hash值不能为空");
		   }
		   if(number == null || number <=0) {
			   return ResponseData.error("数量不能小于0");
		   }
		   Long userId =  ShiroKit.getUser().getId();
		   User user =  userService.getById(userId);
		   if(user == null) {
			   throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
		   }
		   if(StringUtils.isBlank(user.getTradePassword())) {
			   return ResponseData.error("未设置交易密码");
		   }
		   if(!user.getTradePassword().equals(Md5Utils.GetMD5Code(tradePwd))) {
			   return ResponseData.error("输入的交易密码有误");
		   }
		   UserChargerCoinAppealOrder order = new UserChargerCoinAppealOrder();
		   PlatformCoinAddress coinAddress = platformCoinAddressService.getOne(null);
		   if(coinAddress != null) {
			   order.setAddress(coinAddress.getAddress());
		   }
		   order.setCreateTime(new Date());
		   order.setHashValue(hashValue);
		   order.setNumber(number);
		   order.setStatus(1);
		   order.setUserId(user.getUserId());
		   order.setSerialno(otcOrderService.generateSimpleSerialno(user.getUserId(), 6));
		   this.appealService.save(order);
	        return SUCCESS_TIP;
	    }
	   
	   @RequestMapping(value = "/updateStatus")
	    @ResponseBody
	    public ResponseData updateStatus(@RequestParam Long appealId,@RequestParam Integer status) {
	 		return this.appealService.updateStatus(appealId,status);
	    }
	
}
