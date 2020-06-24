package cn.stylefeng.guns.modular.system.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.entity.UserWallter;
import cn.stylefeng.guns.modular.system.entity.UserWithdrawFeeSetting;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.guns.modular.system.service.UserWithdrawCoinAppealService;
import cn.stylefeng.guns.modular.system.service.UserWithdrawFeeSettingService;
import cn.stylefeng.guns.modular.system.warpper.UserWithdrawCoinAppealOrderWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/userWithdrawCoinAppealMgr")
public class UserWithdrawCoinAppealController extends BaseController {

	   private String PREFIX = "/modular/system/user/";
	   
	   @Autowired
	   private UserWithdrawCoinAppealService appealService;
	   
	   @Autowired
	   private UserWithdrawFeeSettingService feeSettingService;
	   
	   @Autowired
	    private UserService userService;
	  
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "userWithdrawCoinAppealOrder.html";
	    }
	   
	   @RequestMapping(value="/agentIndex")
	    public String agentIndex() {
	        return PREFIX + "agentWithdrawCoinAppealOrder.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(
	    		@RequestParam(required = false) String condition,
	    		@RequestParam(required = false) String phone,
				@RequestParam(required = false) String address,
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
			   Page<Map<String, Object>> list = this.appealService.list(condition,phone,null,address,status,beginTime,endTime);
		        Page<Map<String, Object>> wrap = new UserWithdrawCoinAppealOrderWrapper(list).wrap();
		        return LayuiPageFactory.createPageInfo(wrap); 
		   }
	        Page<Map<String, Object>> list = this.appealService.list(condition,phone,ShiroKit.getUserNotNull().getId(),address,status,beginTime,endTime);
	        Page<Map<String, Object>> wrap = new UserWithdrawCoinAppealOrderWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   @RequestMapping(value = "/updateStatus")
	    @ResponseBody
	    public ResponseData updateStatus(@RequestParam Long appealId,@RequestParam Integer status,String password) {
	 		return this.appealService.updateStatus(appealId,status,password);
	    }
	   
	   @RequestMapping("/withdrawCoinAppeal_add")
	    public String withdrawCoinAppealAdd(Model model) {
		   Long userId = ShiroKit.getUserNotNull().getId();
		   User user = this.userService.getById(userId);
		   List<UserWallter> wallter = userService.selectUserWallter(user.getUserId());
		   for (UserWallter userWallter : wallter) {
				if(userWallter.getType() ==1) {
					   model.addAttribute("money",  userWallter.getAvailableBalance());
					   break;
				}
		   }
		   String roleId = null;
		   if("2".equals(user.getRoleId())) {
			   roleId ="2";
		   }else {
			   roleId ="4";
		   }
		   UserWithdrawFeeSetting feeSetting =  feeSettingService.getFeeSettingByRoleId(roleId);
		   model.addAllAttributes(BeanUtil.beanToMap(feeSetting));
	        return PREFIX + "withdrawCoinAppeal_add.html";
	    }
	   
	   
	   @RequestMapping(value = "/add")
	    @ResponseBody
	    @Role(value =2)
	    public Object add(@RequestParam(required=false,value="tradePwd") String tradePwd, @RequestParam Double number, @RequestParam String address, HttpServletRequest request) {
		   return this.appealService.sumbitWithdrawAppeal(tradePwd,number,address,request);
		  
	    }
	   
	   
	
}
