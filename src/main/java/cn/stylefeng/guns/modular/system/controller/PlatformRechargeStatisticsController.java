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
import cn.stylefeng.roses.core.util.ToolUtil;

@Controller
@RequestMapping("/platformRechargeStatisticsMgr")
public class PlatformRechargeStatisticsController  extends BaseController {
	
	   private String PREFIX = "/modular/system/platformRechargeStatistics/";
	   
	   @Autowired
		  private BuyCoinOrderService buyCoinOrderService;
		  
		  @RequestMapping("")
		  public String index(Model model) {
			  Map<String, Object> map = buyCoinOrderService.getPlatformRechargeStatisticsTotalByToday();
			  Map<String, Object> param = buyCoinOrderService.getPlatformRechargeStatisticsTotal();
			  model.addAllAttributes(map);
			  model.addAllAttributes(param);
			  return PREFIX+ "platformRechargeStatistics.html";  
		  }
		  
		  @RequestMapping(value = "/list")
		  @ResponseBody
		  public Object list(@RequestParam(required = false) String timeLimit) {
			   //拼接查询条件
		        String beginTime = "";
		        String endTime = "";
		        if (ToolUtil.isNotEmpty(timeLimit)) {
		            String[] split = timeLimit.split(" - ");
		            beginTime = split[0];
		            endTime = split[1];
		        }
		        Page<Map<String, Object>> list = this.buyCoinOrderService.findPlatformRechargeStatistics(beginTime,endTime);
		        return LayuiPageFactory.createPageInfo(list);
		    }
		
	   
	   

}