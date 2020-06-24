package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.service.SellerOrderService;
import cn.stylefeng.guns.modular.system.warpper.SellerOrderWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;

@Controller
@RequestMapping("/sellerOrderMgr")
public class SellerOrderController extends BaseController {

	   private String PREFIX = "/modular/system/sellerOrder/";
	   
	   @Autowired
	   private SellerOrderService sellerOrderService;
	   
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "sellerOrder.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition,
				@RequestParam(required = false) String phone,
	            @RequestParam(required = false) String timeLimit) {
		   //拼接查询条件
	        String beginTime = "";
	        String endTime = "";

	        if (ToolUtil.isNotEmpty(timeLimit)) {
	            String[] split = timeLimit.split(" - ");
	            beginTime = split[0];
	            endTime = split[1];
	        }
	        Page<Map<String, Object>> list = this.sellerOrderService.list(condition,phone,beginTime,endTime);
	        Page<Map<String, Object>> wrap = new SellerOrderWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
		
	
	  
}
