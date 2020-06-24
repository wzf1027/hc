package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.service.BuyCoinOrderService;
import cn.stylefeng.roses.core.base.controller.BaseController;

@Controller
@RequestMapping("/platformAgentStatisticsMgr")
public class PlatformAgentStatisticsController  extends BaseController {
	
	   private String PREFIX = "/modular/system/platformAgentStatistics/";
	   
	   @Autowired
		  private BuyCoinOrderService buyCoinOrderService;
		  
		  @RequestMapping("")
		  public String index() {
			  return PREFIX+ "platformAgentStatistics.html";  
		  }
		  
		  @RequestMapping(value = "/list")
		  @ResponseBody
		  public Object list(@RequestParam(required = false) String account) {
		        Page<Map<String, Object>> list = this.buyCoinOrderService.findPlatformAgentStatistics(account);
		        return LayuiPageFactory.createPageInfo(list);
		    }
		
	   
	   

}
