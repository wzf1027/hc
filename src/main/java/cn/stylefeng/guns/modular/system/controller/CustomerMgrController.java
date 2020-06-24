package cn.stylefeng.guns.modular.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.modular.system.entity.Customer;
import cn.stylefeng.guns.modular.system.service.CustomerMgrService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;


@Controller
@RequestMapping("/customerMgr")
public class CustomerMgrController extends BaseController {

	 private String PREFIX = "/modular/system/customer/";

	    @Autowired
	    private CustomerMgrService customerMgrService;


		@RequestMapping("")
		public String index() {
		    return PREFIX + "customer.html";
		}
	  

		
	    @RequestMapping("/getCustomerDetail")
	    @ResponseBody
	    public ResponseData getCustomerDetail() {
	        Customer customer = customerMgrService.getCustomerDetail();
	        return ResponseData.success(customer);
	    }



	    @RequestMapping(value = "/saveOrUpdate")
	    @ResponseBody
	    public Object update(Customer customer) {
	        if (ToolUtil.isOneEmpty(customer, customer.getQqNo())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        if(customer.getCustomerId() != null && customer.getCustomerId() >0) {
	        	Customer  old = this.customerMgrService.getById(customer.getCustomerId());
	        	old.setEmail(customer.getEmail());
	        	old.setQqNo(customer.getQqNo());
	 	        old.setWeixinAccount(customer.getWeixinAccount());
	 	        old.setWxQrCode(customer.getWxQrCode());
	 	        this.customerMgrService.updateById(old);
	        }else {
	        	this.customerMgrService.save(customer);
	        }
	        return SUCCESS_TIP;
	    }
	
}
