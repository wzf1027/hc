package cn.stylefeng.guns.modular.system.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.roses.core.base.controller.BaseController;

/**
 * 平台财务统计控制器
 * @author zf
 *
 */
@Controller
@RequestMapping("/systemStatisticsMgr")
public class SystemStatisticsController extends BaseController {
	
    private static String PREFIX = "/modular/system/statistics/";
    
    @Autowired
    private UserService userService;
    
    @RequestMapping("")
	 public String index(Model model) {
    	 Double totalIntoMoney  = userService.getTotalIntoMoneyByIsToday(false); //平台总入金
    	 Double totalTodayIntoMoney  = userService.getTotalIntoMoneyByIsToday(true); //今日平台总入金
    	 Double sellerTotalOutMoney = userService.getSellerTotalOutMoneyByIsToday(false);//会员总提现
    	 Double sellerTotalTodayOutMoney = userService.getSellerTotalOutMoneyByIsToday(true);//今日会员总提现
    	 Double merchantTotalOutMoney = userService.getMerchantTotalOutMoneyByIsToday(false);//商户总提现
    	 Double merchantTotalTodayOutMoney = userService.getMerchantTotalOutMoneyByIsToday(true);//商户总提现
    	 Double agentTotalOutMoney = userService.getAgentTotalOutMoneyByIsToday(false);//代理商总提现
    	 Double agentTotalTodayOutMoney = userService.getAgentTotalOutMoneyByIsToday(true);//代理商总提现	 
	     Double totalFeePrice = userService.getTotalFeePriceByIsToday(false);//平台手续费分成
	     Double totalTodayFeePrice = userService.getTotalFeePriceByIsToday(true);
    	 Map<String, Object> map = new HashMap<String, Object>();
	      map.put("totalIntoMoney", totalIntoMoney);
	      map.put("totalTodayIntoMoney", totalTodayIntoMoney);
	      map.put("sellerTotalOutMoney", sellerTotalOutMoney);	
	      map.put("sellerTotalTodayOutMoney", sellerTotalTodayOutMoney);
	      map.put("merchantTotalOutMoney", merchantTotalOutMoney);
	      map.put("merchantTotalTodayOutMoney", merchantTotalTodayOutMoney);
	      map.put("agentTotalOutMoney", agentTotalOutMoney);
	      map.put("agentTotalTodayOutMoney", agentTotalTodayOutMoney);
	      map.put("totalFeePrice", totalFeePrice);
	      map.put("totalTodayFeePrice", totalTodayFeePrice);
	      model.addAllAttributes(map);
	      LogObjectHolder.me().set(map);
       return PREFIX + "statistics.html";
   }

}
