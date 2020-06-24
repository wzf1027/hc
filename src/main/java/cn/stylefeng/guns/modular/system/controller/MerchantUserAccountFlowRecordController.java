package cn.stylefeng.guns.modular.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.entity.UserWallter;
import cn.stylefeng.guns.modular.system.service.MerchantUserMoneyService;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.roses.core.base.controller.BaseController;

@Controller
@RequestMapping("/merchantFlowRecordMgr")
public class MerchantUserAccountFlowRecordController extends BaseController {

    private static String PREFIX = "/modular/system/merchant/";
	
    @Autowired
    private MerchantUserMoneyService merchantUserMoneyService;
    
    @Autowired
    private UserService userService;
    
	 @RequestMapping("")
	 public String index(Model model) {
		  Long userId = ShiroKit.getUserNotNull().getId();
	      User user = this.userService.getById(userId);
	      List<UserWallter> wallter = userService.selectUserWallter(user.getUserId());
	      Map<String, Object> map = new HashMap<String, Object>();
	      map.put("name", user.getAccount());
	      map.put("phone", user.getPhone());
	      map.put("uId", user.getAccountCode());
	      map.put("secret", user.getAppSecret());
	      if(wallter != null) {
	    	  for (UserWallter userWallter : wallter) {
				if(userWallter.getType() ==1) {
					   map.put("usdtMoney", userWallter.getAvailableBalance());
			    	   map.put("usdtFrozenMoney", userWallter.getFrozenBalance());
				}
				if(userWallter.getType() ==2) {
					   map.put("money", userWallter.getAvailableBalance());
			    	   map.put("frozenMoney", userWallter.getFrozenBalance());
				}
			}
	    	
	      }
	      model.addAllAttributes(map);
	      LogObjectHolder.me().set(map);
        return PREFIX + "merchantMoney.html";
    }
	 
	 
	@RequestMapping("/list")
    @ResponseBody
    public Object list(@RequestParam(required = false) String condition) {
		Long userId = ShiroKit.getUserNotNull().getId();
        User user = this.userService.getById(userId);
	    Page<Map<String, Object>> list = merchantUserMoneyService.list(condition,user.getUserId());
	  //  Page wrapped = new UserWrapper(users).wrap();
	    return LayuiPageFactory.createPageInfo(list);
    }
	
}
