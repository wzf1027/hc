package cn.stylefeng.guns.modular.system.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.entity.UserWallter;
import cn.stylefeng.guns.modular.system.service.BuyCoinOrderService;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.roses.core.base.controller.BaseController;

@Controller
@RequestMapping("/merchantChannelMgr")
public class MerchantChannelController extends BaseController {
	
	 private static String PREFIX = "/modular/system/merchant/";
	  
	  @Autowired
	  private UserService userService;
	  
	  @Autowired
	  private BuyCoinOrderService buyCoinOrderService;
	  
	  @RequestMapping("/{userId}")
	  public String index(@PathVariable Long userId,Model model) {
		  Long id = null;
		  if(ShiroKit.isAdmin()) {
			  id = userId;
		  }else {
			  id  = ShiroKit.getUserNotNull().getId();
		  }
	      User user = this.userService.getById(id);
	      List<UserWallter> wallter = userService.selectUserWallter(user.getUserId());
	      Map<String, Object> map = new HashMap<String, Object>();
	      if(wallter != null) {
	    	  for (UserWallter userWallter : wallter) {
				if(userWallter.getType() ==1) {
					   map.put("usdtMoney", userWallter.getAvailableBalance()== null?0: userWallter.getAvailableBalance());
			    }
				if(userWallter.getType() ==2) {
					   map.put("money", userWallter.getAvailableBalance() == null?0:userWallter.getAvailableBalance() );
			    	   map.put("frozenMoney", userWallter.getFrozenBalance() == null?0:userWallter.getFrozenBalance());
				}
			}   	
	      }
	      List<Map<String,Object>> returnParam = buyCoinOrderService.findSellerBuyCoinChannelInfo(user.getUserId());
	      for (Map<String, Object> map2 : returnParam) {
				Double successRatio = new BigDecimal(map2.get("successTotalNumber").toString())
								.divide(new BigDecimal(map2.get("orderTotalNumber").toString()),2,BigDecimal.ROUND_HALF_UP)
								.multiply(new BigDecimal(100))
								.doubleValue();
				map2.put("successRatio", successRatio+"%");
	     }
	      map.put("list", JSONObject.toJSON(returnParam));
	      model.addAllAttributes(map);
	      LogObjectHolder.me().set(map);
		return PREFIX+ "merchantChannel.html";  
	  }

}
