package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.service.SellerAccpterAppealService;
import cn.stylefeng.guns.modular.system.warpper.SellerAccpterAppealWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;

@Controller
@RequestMapping("/sellerAccpterAppealMgr")
public class SellerAccpterAppealController extends BaseController {

	   private String PREFIX = "/modular/system/seller/";
	   
	   @Autowired
	   private SellerAccpterAppealService appealService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "sellerAccepterAppeal.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(
	    		@RequestParam(required = false) String phone,
				@RequestParam(required = false) String idcardNo,
				@RequestParam(required = false) String name,
				@RequestParam(required = false) Integer status,
	            @RequestParam(required = false) String timeLimit) {
		   //拼接查询条件
	        String beginTime = "";
	        String endTime = "";

	        if (ToolUtil.isNotEmpty(timeLimit)) {
	            String[] split = timeLimit.split(" - ");
	            beginTime = split[0];
	            endTime = split[1];
	        }
	        Page<Map<String, Object>> list = this.appealService.list(phone,idcardNo,name,status,beginTime,endTime);
	        Page<Map<String, Object>> wrap = new SellerAccpterAppealWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   @RequestMapping(value = "/updateStatus")
	    @ResponseBody
	    public ResponseData updateStatus(@RequestParam Long appealId,@RequestParam Integer status) {
	 		return this.appealService.updateStatus(appealId,status);
	    }
	   
	   
	
}
