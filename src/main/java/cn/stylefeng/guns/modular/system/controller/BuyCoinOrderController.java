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

import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.Const;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.service.BuyCoinOrderService;
import cn.stylefeng.guns.modular.system.warpper.BuyCoinOrderWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;

@Controller
@RequestMapping("/buyCoinOrderMgr")
public class BuyCoinOrderController extends BaseController {

	   private String PREFIX = "/modular/system/buyCoinOrder/";
	   
	   @Autowired
	   private BuyCoinOrderService buyCoinOrderService;
	   
	   
	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "buyCoinOrder.html";
	    }
	   
	   
	   @RequestMapping("/merchantBuyCoinOrder")
	    public String merchantBuyCoinOrder() {
	        return PREFIX + "merchantBuyCoinOrder.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(@RequestParam(required = false)String serialno,
				@RequestParam(required = false) String account,
				@RequestParam(required = false) String userOrderNo,
				@RequestParam(required = false) String seller,
				@RequestParam(required = false) Integer isAppeal,
				@RequestParam(required = false) Integer isSuccess,
				@RequestParam(required = false) Integer payMethodType,
	            @RequestParam(required = false) String timeLimit,
	            @RequestParam(required = false) String payMethodAccount,
	            @RequestParam(required = false) String payMethodName,
	            @RequestParam(required = false) Integer status,
				 @RequestParam(required = false) Integer orderCode
						   ,@RequestParam(required = false) String remark
	            ) {
		   //拼接查询条件
	        String beginTime = "";
	        String endTime = "";

	        if (ToolUtil.isNotEmpty(timeLimit)) {
	            String[] split = timeLimit.split(" - ");
	            beginTime = split[0];
	            endTime = split[1];
	        }
	        if(ShiroKit.isAdmin()) {
	        	Page<Map<String, Object>> list = this.buyCoinOrderService.list(serialno,account,beginTime,endTime,
						null,status,userOrderNo,seller,isAppeal,isSuccess,payMethodType
						,payMethodAccount,payMethodName,orderCode,remark);
	  	        Page<Map<String, Object>> wrap = new BuyCoinOrderWrapper(list).wrap();
	  	        return LayuiPageFactory.createPageInfo(wrap);
	        }
	        Page<Map<String, Object>> list = this.buyCoinOrderService.list(serialno,account,beginTime,endTime,ShiroKit.getUser().getId(),status,userOrderNo,seller,isAppeal
					,isSuccess,payMethodType,payMethodAccount,payMethodName,orderCode,remark);
	        Page<Map<String, Object>> wrap = new BuyCoinOrderWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
	   @RequestMapping("/update_number/{orderId}")
	    public String updateNumberPage(@PathVariable Long orderId, Model model) {
		   model.addAttribute("orderId", orderId);
	        return PREFIX + "update_number.html";
	    }


	@RequestMapping("/coinOrder_add")
	public String coinOrder_add() {
		return PREFIX + "buy_coin_order_add.html";
	}


	@RequestMapping(value = "/addOrder")
	@ResponseBody
	@Permission(Const.ADMIN_NAME)
	public ResponseData addOrder(@RequestParam Integer payMethodType,
									 @RequestParam String serialno,
									 @RequestParam Double number,
									 @RequestParam String merchantAccount,
									 @RequestParam String sellerAccount,
								 @RequestParam String password) {
		return this.buyCoinOrderService.addOrder(payMethodType,serialno,number,merchantAccount,sellerAccount,password);
	}

	   /**
	    * 补单
	    * @param orderId
	    * @param number
	    * @return
	    */
	   @RequestMapping(value = "/updateNumber")
	    @ResponseBody
	    @Permission(Const.ADMIN_NAME)
	    public ResponseData updateNumber(@RequestParam Long orderId,@RequestParam Double number) {
	 		return this.buyCoinOrderService.updateNumber(orderId,number);
	    }


			@RequestMapping(value = "/returnBuyCoinOrder")
			@ResponseBody
			@Permission(Const.ADMIN_NAME)
			public ResponseData returnBuyCoinOrder(@RequestParam Long orderId,@RequestParam String password) {
				return this.buyCoinOrderService.returnBuyCoinOrder(orderId,password);
			}

		@RequestMapping(value = "/warn")
		@ResponseBody
		public ResponseData warn() {
			Boolean flag  = ShiroKit.isAdmin();
			return this.buyCoinOrderService.warn(flag);
		}



	   /**
	    * 冻结订单
	    * @param orderId
	    * @return
	    */
	   @RequestMapping(value = "/updateAppeal")
	    @ResponseBody
	    public ResponseData updateAppeal(@RequestParam Long orderId,String password) {
	 		return this.buyCoinOrderService.buyCoinOrderService(orderId,password);
	    }
	   
	   /**
	    * 确认完成
	    * @param orderId
	    * @return
	 * @throws Exception 
	    */
	   @RequestMapping(value = "/finishStatus")
	    @ResponseBody
	    @Permission(Const.ADMIN_NAME)
	    public ResponseData finishStatus(@RequestParam Long orderId,String password) throws Exception {
	 		return this.buyCoinOrderService.finishStatus(orderId,password);
	    }
	   
	   /**
	    * 取消订单重新激活
	    * @param orderId
	    * @return
	    */
	   @RequestMapping(value = "/updateOrderStatus")
	    @ResponseBody
	    @Permission(Const.ADMIN_NAME)
	    public ResponseData updateOrderStatus(@RequestParam Long orderId,String password) {
	 		return this.buyCoinOrderService.updateOrderStatus(orderId,password);
	    }
	   
	   
	   /**
	    * 订单申诉冻结订单后，确认收款
	    * @param orderId
	    * @return
	 * @throws Exception 
	    */
	   @RequestMapping(value = "/confirmUpdateStatus")
	    @ResponseBody
	    @Permission(Const.ADMIN_NAME)
	    public ResponseData confirmUpdateStatus(@RequestParam Long orderId,String password) throws Exception {
	 		return this.buyCoinOrderService.confirmUpdateStatus(orderId,password);
	    }
	   
	   /**
	    * 订单申诉冻结订单后，拒绝收款
	    * @param orderId
	    * @return
	    */
	   @RequestMapping(value = "/noConfirmUpdateStatus")
	    @ResponseBody
	    @Permission(Const.ADMIN_NAME)
	    public ResponseData noConfirmUpdateStatus(@RequestParam Long orderId,String password) {
	 		return this.buyCoinOrderService.noConfirmUpdateStatus(orderId,password);
	    }
	  
}
