package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.service.BuyCoinOrderService;
import cn.stylefeng.roses.core.base.controller.BaseController;

@Controller
@RequestMapping("/platformOtcpMoneyStatisticsMgr")
public class PlatformOtcpMoneyStatisticsController  extends BaseController {
	
	   private String PREFIX = "/modular/system/platformOtcpMoneyStatistics/";
	   
	   @Autowired
		  private BuyCoinOrderService buyCoinOrderService;
		  
		  @RequestMapping("")
		  public String index(Model model) {
			  Map<String, Object> map = buyCoinOrderService.getPlatformRechargeStatisticsTotalByToday();
			  model.addAllAttributes(map);
			  return PREFIX+ "platformOtcpMoneyStatistics.html";  
		  }
		  
		  @RequestMapping(value = "/list")
		  @ResponseBody
		  public Object list(@RequestParam(required = false) String timeLimit) {
		        Page<Map<String, Object>> list = this.buyCoinOrderService.findPlatformOtcpMoneyStatistics();
		        return LayuiPageFactory.createPageInfo(list);
		    }
		
	   
	   

}
