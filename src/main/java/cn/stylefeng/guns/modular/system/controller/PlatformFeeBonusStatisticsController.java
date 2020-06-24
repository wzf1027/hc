package cn.stylefeng.guns.modular.system.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.util.NumberUtil;
import cn.stylefeng.guns.modular.system.service.BuyCoinOrderService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;

@Controller
@RequestMapping("/platformFeeBonusStatisticsMgr")
public class PlatformFeeBonusStatisticsController  extends BaseController {
	
	   private String PREFIX = "/modular/system/platformFeeBonusStatistics/";
	   
	   @Autowired
		  private BuyCoinOrderService buyCoinOrderService;
		  
		  @RequestMapping("")
		  public String index(Model model) {
			  Map<String, Object> param = buyCoinOrderService.getPlatformFeeBonusStatisticsTotal();
			  if(param != null) {
				  if(param.get("totalNumber") != null) {
						 param.put("totalNumber", Double.valueOf(param.get("totalNumber").toString()));
						 Double totalNumber =  (Double) param.get("totalNumber");
						 param.put("totalNumber", NumberUtil.changeDoulePrecision(totalNumber, 2));
					 }
			  }else {
				  param = new HashMap<String, Object>();
				  param.put("totalNumber", 0);
				  param.put("totalSellerBonusNumber", 0);
				  param.put("totalMerchantFeeRatio", 0);
				  param.put("totalAgentBounsNumber", 0);
				  param.put("totalTeamBonusNumber", 0);
				  param.put("totalSupReturnNumber", 0);
				  param.put("totalSellerExchangeFee", 0);
				  param.put("totalMerchantExchangeFee", 0);
				  param.put("totalAgentExchangeFee", 0);
				  param.put("totalAccepterBonusNumber", 0);
				  param.put("totalOtcFeePriceSeller", 0);
				  param.put("totalOtcFeePriceAgent", 0);
				  param.put("totalOtcFeePriceMerchant", 0);
				  param.put("totalProfit", 0);
				  param.put("totalAgentWithdrawFee", 0);
				  param.put("totalMerchantWithdrawFee", 0);
				  param.put("totalSellerWithdrawFee", 0);
			  }
			  model.addAllAttributes(param);
			  return PREFIX+ "platformFeeBonusStatistics.html";  
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
		        Page<Map<String, Object>> list = this.buyCoinOrderService.findPlatformFeeBonusStatistics(beginTime,endTime);
		        return LayuiPageFactory.createPageInfo(list);
		    }
		
	   
	   

}
