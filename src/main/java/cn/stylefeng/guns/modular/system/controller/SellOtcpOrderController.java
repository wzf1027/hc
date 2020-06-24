package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import cn.stylefeng.guns.core.util.IpUtil;
import cn.stylefeng.guns.modular.system.entity.MerchantIp;
import cn.stylefeng.guns.modular.system.service.MerchantIpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.entity.UserOtcFeeSetting;
import cn.stylefeng.guns.modular.system.entity.UserWallter;
import cn.stylefeng.guns.modular.system.model.SellOtcpDto;
import cn.stylefeng.guns.modular.system.service.SellOtcpOrderService;
import cn.stylefeng.guns.modular.system.service.UserOtcFeeSettingService;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.guns.modular.system.warpper.SellOtcpOrderWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/sellOtcpOrderMgr")
public class SellOtcpOrderController extends BaseController {

	   private String PREFIX = "/modular/system/sellOtcpOrder/";
	   
	   @Autowired
	   private SellOtcpOrderService sellOtcpOrderService;

	   @Autowired
	   private UserOtcFeeSettingService feeSettingService;
	   
	   @Autowired
	   private UserService userService;

	   @Autowired
	   private MerchantIpService merchantIpService;
	   
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "sellOtcpOrder.html";
	   }
	   
	   @RequestMapping("/auto_add")
	    public String autoAdd(Model model) {
		   UserWallter userWallter = sellOtcpOrderService.findUserWallter(ShiroKit.getUser().getId());
		   model.addAttribute("money", 0);
		   if(userWallter != null) {
			   model.addAttribute("money", userWallter.getAvailableBalance());
		   }
	        return PREFIX + "auto_add.html";
	    }
	   
	   @RequestMapping("/no_auto_add")
	    public String noAutoAdd(Model model) {
		   User user =userService.getById(ShiroKit.getUser().getId()); 
		   QueryWrapper<UserOtcFeeSetting> queryWrapper = new QueryWrapper<UserOtcFeeSetting>();
		   if("2".equals(user.getRoleId())) {
			   queryWrapper.eq("type", 1);
		   }else {
			   queryWrapper.eq("type", 2);
		   }
		   UserOtcFeeSetting feeSetting = feeSettingService.getOne(queryWrapper);
		   model.addAttribute("ratio", feeSetting.getRatio());
	        return PREFIX + "no_auto_add.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition,
				@RequestParam(required = false) String phone,
				@RequestParam(required = false) String roleType,
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
	        	Page<Map<String, Object>> list = this.sellOtcpOrderService.list(condition,phone,beginTime,endTime,roleType,null);
	  	        Page<Map<String, Object>> wrap = new SellOtcpOrderWrapper(list).wrap();
	  	        return LayuiPageFactory.createPageInfo(wrap);
	        }
	        Page<Map<String, Object>> list = this.sellOtcpOrderService.list(condition,phone,beginTime,endTime,roleType,ShiroKit.getUser().getId());
	        Page<Map<String, Object>> wrap = new SellOtcpOrderWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   
	   @RequestMapping(value = "/autoAdd")
	   @ResponseBody
	   public ResponseData autoAdd(SellOtcpDto sellOtcpDto, HttpServletRequest request) {
		   User user =userService.getById(ShiroKit.getUser().getId());
		   if("2".equals(user.getRoleId())) {
			   String ipAddress = IpUtil.getIpAddress(request);
			   MerchantIp merchantIp = new MerchantIp();
			   merchantIp.setIpAddress(ipAddress);
			   merchantIp.setUserId(ShiroKit.getUser().getId());
			   merchantIp.setType(1);
			   int ipCount = merchantIpService.count(new QueryWrapper<>(merchantIp));
			   if (ipCount <=0){
				   return ResponseData.error("暂时无法提现");
			   }
		   }
	       return sellOtcpOrderService.autoAdd(sellOtcpDto);
	    }	
	   
	   @RequestMapping(value = "/noAutoAdd")
	   @ResponseBody
	   public ResponseData noAutoAdd(SellOtcpDto sellOtcpDto, HttpServletRequest request) {
		   User user =userService.getById(ShiroKit.getUser().getId()); 
		   QueryWrapper<UserOtcFeeSetting> queryWrapper = new QueryWrapper<UserOtcFeeSetting>();
		   if("2".equals(user.getRoleId())) {
			   queryWrapper.eq("TYPE", 1);
			   String ipAddress = IpUtil.getIpAddress(request);
			   MerchantIp merchantIp = new MerchantIp();
			   merchantIp.setIpAddress(ipAddress);
			   merchantIp.setUserId(ShiroKit.getUser().getId());
			   merchantIp.setType(1);
			   int ipCount = merchantIpService.count(new QueryWrapper<>(merchantIp));
			   if (ipCount <=0){
				   return ResponseData.error("暂时无法提现");
			   }
		   }else {
			   queryWrapper.eq("TYPE", 2);
		   }
		   UserOtcFeeSetting feeSetting = feeSettingService.getOne(queryWrapper);
	       return sellOtcpOrderService.noAutoAdd(sellOtcpDto,feeSetting.getRatio());
	    }
	
	   
	   @RequestMapping("/revocation")
	   @ResponseBody
		public ResponseData revocation(@RequestParam("orderId")Long orderId) {
			  return sellOtcpOrderService.revocation(orderId);
		}
	  
}
