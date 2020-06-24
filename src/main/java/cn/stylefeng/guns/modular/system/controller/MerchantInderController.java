package cn.stylefeng.guns.modular.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.entity.UserWallter;
import cn.stylefeng.guns.modular.system.service.BuyCoinOrderService;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.roses.core.base.controller.BaseController;

@Controller
@RequestMapping("/merchantIndexMgr")
public class MerchantInderController extends BaseController {

	  private static String PREFIX = "/modular/system/merchant/";
	  
	  @Autowired
	  private UserService userService;
	  
	  @Autowired
	  private BuyCoinOrderService buyCoinOrderService;
	  
	  @RequestMapping("")
	  public String index(Model model) {
		  Long userId = ShiroKit.getUserNotNull().getId();
	      User user = this.userService.getById(userId);
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
	      //获取今日全部订单数量
	      int number = buyCoinOrderService.findNumberByStatus(user.getUserId(),0);
	      map.put("todayOrderNumber", number);
	      number = buyCoinOrderService.findNumberByStatus(user.getUserId(), 4);
	      map.put("todaySuccessNumber", number);
	      number = buyCoinOrderService.findNumberByStatus(user.getUserId(), 1);
	      map.put("todayNoPayNumber", number);
	      Double todayPrice = buyCoinOrderService.findTotalPrice(user.getUserId());
	      Double todayUserTotalPrice = buyCoinOrderService.findTotalPayPrice(user.getUserId());
	      map.put("todayPrice", todayPrice);
	      map.put("todayUserTotalPrice",todayUserTotalPrice);
	      number = buyCoinOrderService.findNumberByStatus(user.getUserId(), 7);
	      map.put("todayUserCannelNumber",number);
	      model.addAllAttributes(map);
	      LogObjectHolder.me().set(map);
		return PREFIX+ "merchantIndex.html";  
	  }
	
}
