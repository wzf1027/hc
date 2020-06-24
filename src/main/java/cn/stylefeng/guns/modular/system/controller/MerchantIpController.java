package cn.stylefeng.guns.modular.system.controller;

import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.Const;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.entity.Carousel;
import cn.stylefeng.guns.modular.system.entity.MerchantIp;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.service.BuyCoinOrderService;
import cn.stylefeng.guns.modular.system.service.MerchantIpService;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.guns.modular.system.warpper.AccepterRateSettingWrapper;
import cn.stylefeng.guns.modular.system.warpper.BuyCoinOrderWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/merchantIpMgr")
public class MerchantIpController extends BaseController {

	   private String PREFIX = "/modular/system/merchantIp/";
	   
	   @Autowired
	   private MerchantIpService merchantIpService;

	   @Autowired
	   private UserService userService;
	   
	   
	   @RequestMapping("/{type}")
	    public String index(@PathVariable Integer type) {
	   		if (type.equals(1)){
				return PREFIX + "merchantIp_white.html";
			}
	   		return PREFIX + "merchantIp.html";
	    }



	   @RequestMapping(value = "/list/{type}")
	   @ResponseBody
	   public Object list(@PathVariable Integer type ,
						  @RequestParam(required = false) String ipAddress,
						  @RequestParam(required = false) String account) {
		   Page<Map<String, Object>> list = this.merchantIpService.list(type,ipAddress,account);
		   return LayuiPageFactory.createPageInfo(list);
	   }

	@RequestMapping("/merchantIp_add/{type}")
	public String noticeAdd(@PathVariable Integer type,Model model) {
	   	model.addAttribute("type",type);
		return PREFIX + "merchantIp_add.html";
	}

	@RequestMapping(value = "/add")
	@ResponseBody
	public Object add(String account,String ipAddress,Integer type) {
		if (StringUtils.isBlank(account)){
			return ResponseData.error("请输入商户ID");
		}
		if (StringUtils.isBlank(ipAddress)){
			return  ResponseData.error("ip地址不能为空");
		}
		if (type == null || type <=0){
			return ResponseData.error("添加失败");
		}
		User user = new User();
		user.setAccountCode(account);
		user.setStatus("ENABLE");
		user = userService.getOne(new QueryWrapper<>(user));
		if (user == null){
			return  ResponseData.error("该商户ID不存在");
		}
		MerchantIp merchantIp = new MerchantIp();
		merchantIp.setUserId(user.getUserId());
		merchantIp.setType(type);
		merchantIp.setIpAddress(ipAddress);
		int count = merchantIpService.count(new QueryWrapper<>(merchantIp));
		if (count >0){
			return  ResponseData.error("该IP地址已存在");
		}
		merchantIp = new MerchantIp();
		merchantIp.setUserId(user.getUserId());
		merchantIp.setType(type);
		merchantIp.setIpAddress(ipAddress);
		this.merchantIpService.save(merchantIp);
		return SUCCESS_TIP;
	}


	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(@RequestParam Long id) {
		this.merchantIpService.removeById(id);
		return SUCCESS_TIP;
	}

	  
}
