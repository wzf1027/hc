package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.service.BuyCoinOrderService;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.guns.modular.system.warpper.BuyCoinOrderWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;

@Controller
@RequestMapping("/merchantBuyCoinnMgr")
public class MerchantBuyConinController extends BaseController {
	
	 private static String PREFIX = "/modular/system/merchant/";
	  
	  @Autowired
	  private UserService userService;
	  
	  @Autowired
	  private BuyCoinOrderService buyCoinOrderService;
	  
	  @RequestMapping("")
	  public String index() {
		return PREFIX+ "merchantBuyCoinOrder.html";  
	  }
	  
	  @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition,
	    		@RequestParam(required = false) String account,
	    		@RequestParam(required = false) String serialno,
	    		@RequestParam(required = false) String timeLimit,
	            @RequestParam(required = false) String payMethodType) {
		   //拼接查询条件
	        String beginTime = "";
	        String endTime = "";
	        if (ToolUtil.isNotEmpty(timeLimit)) {
				String[] split = timeLimit.split(" - ");
				beginTime = split[0];
				endTime = split[1];
	        }
	        Long userId = ShiroKit.getUserNotNull().getId();
		    User user = this.userService.getById(userId);
	        Page<Map<String, Object>> list = this.buyCoinOrderService.findSellerBuyCoinMerchant(condition,beginTime,endTime,user.getUserId(),account,serialno,payMethodType);
	        Page<Map<String, Object>> wrap = new BuyCoinOrderWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }

}
