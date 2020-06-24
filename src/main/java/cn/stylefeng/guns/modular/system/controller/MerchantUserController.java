package cn.stylefeng.guns.modular.system.controller;

import java.util.Date;
import java.util.Map;

import cn.stylefeng.guns.core.util.Md5Utils;
import cn.stylefeng.guns.modular.system.entity.*;
import cn.stylefeng.guns.modular.system.service.MoneyPasswordSettingMgrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.constant.state.ManagerStatus;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.guns.modular.system.warpper.UserWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/merchantMgr")
public class MerchantUserController extends BaseController {

    private static String PREFIX = "/modular/system/merchant/";
    
   // private Logger logger = LoggerFactory.getLogger(MerchantUserController.class);
	
    @Autowired
    private UserService userService;

	@Autowired
	private MoneyPasswordSettingMgrService settingService;
    
	 @RequestMapping("")
	 public String index() {
        return PREFIX + "merchant.html";
    }
	 
	 @RequestMapping("/payMethodFee_update/{userId}")
	    public String payMethodFeeUpdate(@PathVariable Long userId, Model model) {
	        User user = this.userService.getById(userId);
	       UserPayMethodFeeSetting setting =   userService.findPayMethodFeeByUserId(user.getUserId());
	       if(setting == null) {
	    	   setting = new UserPayMethodFeeSetting();
	    	   setting.setUserId(userId);
	       }
	       model.addAllAttributes(BeanUtil.beanToMap(setting));
	       LogObjectHolder.me().set(setting);
	        return PREFIX + "payMethodFee.html";
	    }
	 
	 @RequestMapping("/gentBonus_update/{userId}")
	    public String gentBonus(@PathVariable Long userId, Model model) {
	       User user = this.userService.getById(userId);
	       UserRecommendRelation relation = this.userService.selectRecommendRelation(user.getUserId());
	       UserBonusSetting setting =   userService.findUserBonusSettingByUserId(user.getUserId(),relation.getRecommendId());
	       if(setting == null) {
	    	   setting = new UserBonusSetting();
	    	   setting.setUserId(userId);
	    	   setting.setAgentId(relation.getRecommendId());
	       }
	       model.addAllAttributes(BeanUtil.beanToMap(setting));
	       LogObjectHolder.me().set(setting);
	        return PREFIX + "bonus.html";
	    }
	 
	 
	 @RequestMapping("/payMethodFee/{userId}")
	    public String payMethodFee(@PathVariable Long userId, Model model) {
	        User user = this.userService.getById(userId);
	       UserPayMethodFeeSetting setting =   userService.findPayMethodFeeByUserId(user.getUserId());
	       if(setting == null) {
	    	   setting = new UserPayMethodFeeSetting();
	    	   setting.setUserId(userId);
	       }
	       model.addAllAttributes(BeanUtil.beanToMap(setting));
	       LogObjectHolder.me().set(setting);
	        return PREFIX + "merchantPayMethodFee.html";
	    }
	 
 	@RequestMapping("/agentBonus/{userId}")
    public String agentBonus(@PathVariable Long userId, Model model) {
       User user = this.userService.getById(userId);
       UserRecommendRelation relation = this.userService.selectRecommendRelation(user.getUserId());
       UserBonusSetting setting =   userService.findUserBonusSettingByUserId(user.getUserId(),relation.getRecommendId());
       if(setting == null) {
    	   setting = new UserBonusSetting();
    	   setting.setUserId(userId);
    	   setting.setAgentId(relation.getRecommendId());
       }
       model.addAllAttributes(BeanUtil.beanToMap(setting));
       LogObjectHolder.me().set(setting);
       return PREFIX + "agentBonus.html";
    }
	 
	 @RequestMapping(value = "/saveUpdateBonus")
	 @ResponseBody
    public Object gentBonusUpdate(UserBonusSetting setting) {
        if (ToolUtil.isOneEmpty(setting,setting.getUserId(),setting.getAgentId())) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        if(setting.getAlipayRatio() <0) {
        	return ResponseData.error("支付宝费用不能小于0");
        }
        if(setting.getWxRatio() <0) {
        	return ResponseData.error("微信费用不能小于0");
        }
        if(setting.getCardRatio() <0) {
        	return ResponseData.error("银行卡费用不能小于0");
        }
		 if(setting.getCloudPayRatio() <0) {
			 return ResponseData.error("云闪付费用不能小于0");
		 }

        UserBonusSetting old = this.userService.getUserBonusById(setting.getBonusId());
        if(old == null) {
        	old  = new UserBonusSetting();
        	old.setUserId(setting.getUserId());
        	old.setAgentId(setting.getAgentId());
        	old.setAlipayRatio(setting.getAlipayRatio());
        	old.setWxRatio(setting.getWxRatio());
        	old.setCardRatio(setting.getCardRatio());
        	old.setCloudPayRatio(setting.getCloudPayRatio());
        	old.setCreateTime(new Date());
        	this.userService.addUserBonusSetting(old);
        }else {
        	old.setAlipayRatio(setting.getAlipayRatio());
         	old.setWxRatio(setting.getWxRatio());
         	old.setCardRatio(setting.getCardRatio());
			old.setCloudPayRatio(setting.getCloudPayRatio());
         	old.setUpdateTime(new Date());
             this.userService.updateUseBonusSetting(old);
        }  
        return SUCCESS_TIP;
    }
	 
	 @RequestMapping(value = "/saveUpdatePayMethod")
	 @ResponseBody
    public Object update(UserPayMethodFeeSetting setting) {
        if (ToolUtil.isOneEmpty(setting,setting.getUserId())) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        if(setting.getAlipayRatio() <0) {
        	return ResponseData.error("支付宝费用不能小于0");
        }
        if(setting.getWxRatio() <0) {
        	return ResponseData.error("微信费用不能小于0");
        }
        if(setting.getCardRatio() <0) {
        	return ResponseData.error("银行卡费用不能小于0");
        }
        if (setting.getCloudPayRatio() <0){
			return ResponseData.error("云闪付费用不能小于0");
		}
		 if (StringUtils.isBlank(setting.getPassword())){
			 return  ResponseData.error("请输入二级密码");
		 }
		 MoneyPasswordSetting passwordSetting = settingService.getOne(null);
		 if(!passwordSetting.getPassword().equals(Md5Utils.GetMD5Code(setting.getPassword()))) {
			 return ResponseData.error("输入二级密码有误");
		 }
        UserPayMethodFeeSetting old = this.userService.getPayMethodFeeById(setting.getSettingId());
        if(old == null) {
        	old  = new UserPayMethodFeeSetting();
        	old.setUserId(setting.getUserId());
        	old.setAlipayRatio(setting.getAlipayRatio());
        	old.setWxRatio(setting.getWxRatio());
        	old.setCardRatio(setting.getCardRatio());
        	old.setCloudPayRatio(setting.getCloudPayRatio());
        	old.setCreateTime(new Date());
        	old.setMinAlipayValue(setting.getMinAlipayValue());
        	old.setMaxAlipayValue(setting.getMaxAlipayValue());
        	old.setMinWxValue(setting.getMinWxValue());
        	old.setMaxWxValue(setting.getMaxWxValue());
        	old.setMinCardValue(setting.getMinCardValue());
        	old.setMaxCardValue(setting.getMaxCardValue());
        	old.setMinCloudPayValue(setting.getMinCloudPayValue());
        	old.setMaxCloudPayValue(setting.getMaxCloudPayValue());
        	this.userService.addUserPayMethodFeeSetting(old);
        }else {
        	old.setAlipayRatio(setting.getAlipayRatio());
         	old.setWxRatio(setting.getWxRatio());
         	old.setCardRatio(setting.getCardRatio());
			old.setCloudPayRatio(setting.getCloudPayRatio());
         	old.setUpdateTime(new Date());
			old.setMinAlipayValue(setting.getMinAlipayValue());
			old.setMaxAlipayValue(setting.getMaxAlipayValue());
			old.setMinWxValue(setting.getMinWxValue());
			old.setMaxWxValue(setting.getMaxWxValue());
			old.setMinCardValue(setting.getMinCardValue());
			old.setMaxCardValue(setting.getMaxCardValue());
			old.setMinCloudPayValue(setting.getMinCloudPayValue());
			old.setMaxCloudPayValue(setting.getMaxCloudPayValue());
             this.userService.updateUserPayMethodFeeSetting(old);
        }  
        return SUCCESS_TIP;
    }
	 
	 @RequestMapping("/traderPwdPage")
	 public String traderPwdPage() {
        return PREFIX + "tradePassword.html";
    }
	 
	 @RequestMapping(value = "/updateTradepwd")
    @ResponseBody
    public ResponseData updateTradepwd(@RequestParam String tradePwd,@RequestParam String code) {
 		return this.userService.updateTradePwd(tradePwd,code);
	    }
	 
	 
	@SuppressWarnings("rawtypes")
	@RequestMapping("/list")
    @ResponseBody
    public Object list(
       	    		@RequestParam(required = false) String phone,
       	            @RequestParam(required = false) String timeLimit,
	       	        @RequestParam(required = false) String recommend,
	 				@RequestParam(required = false) Integer isAuth,
	 				@RequestParam(required = false) Integer enabled) {
        //拼接查询条件
        String beginTime = "";
        String endTime = "";
        if (ToolUtil.isNotEmpty(timeLimit)) {
            String[] split = timeLimit.split(" - ");
            beginTime = split[0];
            endTime = split[1];
        }
	    Page<Map<String, Object>> users = userService.selectMerchantUsers(phone, beginTime, endTime,recommend,isAuth,enabled);
	    Page wrapped = new UserWrapper(users).wrap();
//	    logger.info(JSONObject.toJSONString(wrapped.getRecords()));
	    return LayuiPageFactory.createPageInfo(wrapped);
    }

	/**
	 * 跳转用户的认证页面
	 * @param userId 用户Id
	 * @param model model
	 * @return String
	 */
	@RequestMapping("/authPage/{userId}")
    public String authPage(@PathVariable Long userId, Model model) {
        User user = this.userService.getById(userId);
        model.addAllAttributes(BeanUtil.beanToMap(user));
        LogObjectHolder.me().set(user);
        return PREFIX + "authPage.html";
    }

	/**
	 * 更新认证状态
	 * @param userId 用户id
	 * @param status 状态：1表示审核通过，2表示审核不通过
	 * @return ResponseData
	 */
	@RequestMapping(value = "/updateAuth")
    @ResponseBody
    public ResponseData updateAuth(@RequestParam Long userId,@RequestParam Integer status) {
 		return this.userService.updateAuth(userId,status);
    }
	
	@RequestMapping("/freeze")
    @ResponseBody
    public ResponseData freeze(@RequestParam Long userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        User user =  this.userService.getById(userId);
        this.userService.assertAuth(userId);
        if(ManagerStatus.FREEZED.getCode().equals(user.getStatus())) {
        	  this.userService.setStatus(userId, ManagerStatus.OK.getCode());
        }else {
        	 this.userService.setStatus(userId, ManagerStatus.FREEZED.getCode());
        }
        return SUCCESS_TIP;
    }
	 
	 
	
}
