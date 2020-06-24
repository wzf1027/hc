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

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.system.entity.SellerPayMethod;
import cn.stylefeng.guns.modular.system.entity.UserPayMethod;
import cn.stylefeng.guns.modular.system.model.SellerPayMethodDto;
import cn.stylefeng.guns.modular.system.model.UserPayMethodDto;
import cn.stylefeng.guns.modular.system.service.OtcpOrderService;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.guns.modular.system.warpper.OtcpOrderWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;

@Controller
@RequestMapping("/otcpOrderMgr")
public class OtcpOrderController extends BaseController {

	   private String PREFIX = "/modular/system/otcpOrder/";
	   
	   @Autowired
	   private OtcpOrderService otcpOrderService;
	   
	   @Autowired
	   private SellerMapper sellerMapper;
	   
	   @Autowired
	   private UserService UserService;
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "otcpOrder.html";
	    }
	   
	   @RequestMapping("/appeal_add/{orderId}")
	    public String appealAdd(@PathVariable Long orderId,Model model) {
		   	model.addAttribute("orderId", orderId);
	        return PREFIX + "appeal_add.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(String condition,
				@RequestParam(required = false) String buyerPhone,
				@RequestParam(required = false) String sellerPhone,
				@RequestParam(required = false) String serialno,
	            @RequestParam(required = false) String timeLimit,
	            @RequestParam(required = false) String remark,
	            @RequestParam(required = false) Integer status,
	            @RequestParam(required = false) Integer isAppeal,
	            @RequestParam(required = false) Integer payMethodType
	    		) {
		   //拼接查询条件
	        String beginTime = "";
	        String endTime = "";

	        if (ToolUtil.isNotEmpty(timeLimit)) {
	            String[] split = timeLimit.split(" - ");
	            beginTime = split[0];
	            endTime = split[1];
	        }
	        if (ShiroKit.isAdmin()) {
	        	 Page<Map<String, Object>> list = this.otcpOrderService.list(condition,buyerPhone,sellerPhone,serialno,beginTime,endTime,null,remark,status,isAppeal,payMethodType);
	 	        Page<Map<String, Object>> wrap = new OtcpOrderWrapper(list).wrap();
	 	        return LayuiPageFactory.createPageInfo(wrap);
			}
	        Page<Map<String, Object>> list = this.otcpOrderService.list(condition,buyerPhone,sellerPhone,serialno,beginTime,endTime,ShiroKit.getUser().getId(),remark,status,isAppeal,payMethodType);
	        Page<Map<String, Object>> wrap = new OtcpOrderWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   /**
	    * 买家获胜
	    * @param id
	    * @return
	    */
	   @RequestMapping(value = "/buyerWarn")
	    @ResponseBody
	    public ResponseData buyerWarn(@RequestParam Long id) {
	 		return this.otcpOrderService.buyerWarn(id);
	    }
	   
	   /**
	    * 卖家获胜
	    * @param id
	    * @return
	    */
	   @RequestMapping(value = "/sellerWarn")
	    @ResponseBody
	    public ResponseData sellerWarn(@RequestParam Long id) {
	 		return this.otcpOrderService.sellerWarn(id);
	    }
	   
	   
	   /**
	    * 取消交易
	    * @param orderId
	    * @return
	    */
	   @RequestMapping(value = "/cannelTrade")
	    @ResponseBody
	    public ResponseData cannelTrade(@RequestParam Long orderId) {
	 		return this.otcpOrderService.cannelTrade(orderId);
	    }
	   
	   /**
	    * 确认收款
	    * @param orderId
	    * @return
	    */
	   @RequestMapping(value = "/getMoney")
	    @ResponseBody
	    public ResponseData getMoney(@RequestParam Long orderId) {
	 		return this.otcpOrderService.getMoney(orderId);
	    }
	   
	   /**
	    * 申诉冻结
	    * @param orderId
	    * @param content
	    * @param image
	    * @return
	    */
	   @RequestMapping(value = "/sumbtiAppeal")
	    @ResponseBody
	    public ResponseData sumbtiAppeal(@RequestParam Long orderId,@RequestParam String content,@RequestParam(required = false,value = "image") String image) {
	 		return this.otcpOrderService.sumbtiAppeal(orderId,content,image);
	    }
	   
	   
	   @RequestMapping("/payMethod")
	    public String authPage(@RequestParam Integer type,@RequestParam Long id, Model model) {
		   if(type != null && (type ==1  || type ==2)) {
			   SellerPayMethod sellerPayMethod = sellerMapper.findSellerPayMethodById(id);
			   if(sellerPayMethod != null) {
				  SellerPayMethodDto payMethodDto = new SellerPayMethodDto();
				  BeanUtil.copyProperties(sellerPayMethod,payMethodDto);
				  payMethodDto.setAccountName(sellerPayMethod.getName());
				   model.addAllAttributes(BeanUtil.beanToMap(payMethodDto));
				   LogObjectHolder.me().set(payMethodDto);
				   if(sellerPayMethod.getType() == 1 || sellerPayMethod.getType() ==2) {
					   return PREFIX + "wxOrAlipay.html"; 
				   }else if(sellerPayMethod.getType() ==3) {
					   return PREFIX + "cardBank.html";
				   }
			   }      
		   }else if(type != null && (type.equals(3) || type.equals(4))) {
			   UserPayMethod userPayMethod = UserService.findPayMethodById(id);
			   if(userPayMethod != null) {
				   UserPayMethodDto payMethodDto = new UserPayMethodDto();
				   BeanUtil.copyProperties(userPayMethod,payMethodDto);
				   payMethodDto.setAccountName(userPayMethod.getName());
				   model.addAllAttributes(BeanUtil.beanToMap(payMethodDto));
			        LogObjectHolder.me().set(payMethodDto);
			        return PREFIX + "cardBank.html";
			   }
		   }   
		   return PREFIX+"noEixtPayMethod.html";
	    }
		
	
	  
}
