package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.service.BuyCoinOrderService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;

@Controller
@RequestMapping("/merchantBillMgr")
public class MerchantBillController extends BaseController {

	private static String PREFIX = "/modular/system/merchant/"; 
  
	  @Autowired
	  private BuyCoinOrderService buyCoinOrderService;
	  
	  @RequestMapping("/{userId}")
	  public String index(@PathVariable Long userId,Model model) {
		 model.addAttribute("userId", userId);
		return PREFIX+ "merchantBill.html";  
	  }
	  
	  @RequestMapping(value = "/list/{userId}")
	   @ResponseBody
	    public Object list(@PathVariable Long userId,String condition,
	            @RequestParam(required = false) String timeLimit) {
		   //拼接查询条件
	        String beginTime = "";
	        String endTime = "";
	        if (ToolUtil.isNotEmpty(timeLimit)) {
	            //String[] split = timeLimit.split(" - ");
	            beginTime = timeLimit;
	            endTime = timeLimit;
	        }
	        Long id =null;
	        if(ShiroKit.isAdmin()) {
	        	id = userId;
	        }else {
	        	id = ShiroKit.getUser().getId();
	        }
	        Page<Map<String, Object>> list = this.buyCoinOrderService.findSellerBuyCoinBill(condition,beginTime,endTime,id);
	        return LayuiPageFactory.createPageInfo(list);
	    }
	
}
