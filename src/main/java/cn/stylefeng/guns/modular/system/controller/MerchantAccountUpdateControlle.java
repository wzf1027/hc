package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import cn.stylefeng.guns.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.dto.SellerAccountUpdate;
import cn.stylefeng.guns.modular.system.service.AccountUpdateMgrService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;

@Controller
@RequestMapping("/merchantAccountUpdateMgr")
public class MerchantAccountUpdateControlle extends BaseController {

	private static String PREFIX = "/modular/system/merchant/"; 
  
	@Autowired
	private AccountUpdateMgrService accountUpdateService;
	
	  @RequestMapping("")
	  public String index() {	
		return PREFIX+ "merchantAccountUpdate.html";  
	  }
	  
	  @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(SellerAccountUpdate sellerAccountUpdate) {
		//拼接查询条件
	        String beginTime = "";
	        String endTime = "";
	        if (ToolUtil.isNotEmpty(sellerAccountUpdate.getTimeLimit())) {
	            String[] split = sellerAccountUpdate.getTimeLimit().split(" - ");
	            beginTime = split[0];
	            endTime = split[1];
	        }
		  	Long role = 2L;
		  Page<Map<String, Object>> list = null;
		  if (ShiroKit.isAdmin()){
			   list = this.accountUpdateService.findAccountUpdateByCondition(sellerAccountUpdate,role,beginTime,endTime,null);
		  }else{
			  list = this.accountUpdateService.findAccountUpdateByCondition(sellerAccountUpdate,role,beginTime,endTime,ShiroKit.getUser().getId());
		  }
	       return LayuiPageFactory.createPageInfo(list);
	    }
	
}
