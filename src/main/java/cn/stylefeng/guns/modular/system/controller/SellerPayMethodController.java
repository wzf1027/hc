package cn.stylefeng.guns.modular.system.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.entity.*;
import cn.stylefeng.guns.modular.system.service.AccepterRateSettingService;
import cn.stylefeng.guns.modular.system.service.BuyCoinOrderService;
import cn.stylefeng.guns.modular.system.service.SellerPayMethodService;
import cn.stylefeng.guns.modular.system.warpper.AccepterRateSettingWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sellerPayMethodMgr")
public class SellerPayMethodController extends BaseController {

	   private String PREFIX = "/modular/system/sellerPayMethod/";
	   
	   @Autowired
	   private SellerPayMethodService sellerPayMethodService;

	   @Autowired
	   private BuyCoinOrderService buyCoinOrderService;
	   
	   @RequestMapping("")
	    public String index(Model model) {
			Integer number  =sellerPayMethodService.onLineNumberByType(1);
			model.addAttribute("alipayNumber",number);
			number  =sellerPayMethodService.onLineNumberByType(2);
		   model.addAttribute("wxNumber",number);
		   number  =sellerPayMethodService.onLineNumberByType(3);
		   model.addAttribute("cardNumber",number);
		   number  =sellerPayMethodService.onLineNumberByType(4);
		   model.addAttribute("cloudNumber",number);
		   number  =sellerPayMethodService.onLineNumberByType(5);
		   model.addAttribute("alipayAccountNumber",number);
		   number  =sellerPayMethodService.onLineNumberByType(7);
		   model.addAttribute("alipayCardNumber",number);
		   number  =sellerPayMethodService.onLineNumberByType(6);
		   model.addAttribute("wxAccountNumber",number);
		   number  =sellerPayMethodService.onLineNumberByType(8);
		   model.addAttribute("wxCardNumber",number);
		   number  =sellerPayMethodService.onLineNumberByType(9);
		   model.addAttribute("wxZanNumber",number);

	        return PREFIX + "sellerPayMethod.html";
	    }
	   
	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(@RequestParam(required = false)String account,
						   @RequestParam(required = false)String phone,
						   @RequestParam(required = false)String payMethodName,
						   @RequestParam(required = false)Integer payMethodType,
						   @RequestParam(required = false)String nickName,
	  					 @RequestParam(required = false)Integer isCheck) {
	        Page<Map<String, Object>> list = this.sellerPayMethodService.list(account,phone,payMethodName,payMethodType,nickName,isCheck);
	        return LayuiPageFactory.createPageInfo(list);
	    }

	/**
	 * 封禁/解禁
	 * @return
	 */
	@RequestMapping("/isSoldOut")
	@ResponseBody
	public ResponseData isSoldOut(@RequestParam Long payMethodId,@RequestParam Integer status) {
		if (status == null || status <= 0) {
			return ResponseData.error("参数有误");
		}
		SellerPayMethod payMethod = sellerPayMethodService.getById(payMethodId);
		if (payMethod != null){
			if (status.equals(1) && payMethod.getIsSoldOut().equals(0)){//下架
				payMethod.setSoldOutTime(new Date());
				payMethod.setIsSoldOut(1);
				payMethod.setIsCheck(0);
				payMethod.setUpdateTime(new Date());
				this.sellerPayMethodService.updateById(payMethod);
				return ResponseData.success();
			}else if (status.equals(2) && payMethod.getIsSoldOut().equals(1)){
				payMethod.setSoldOutTime(null);
				payMethod.setIsSoldOut(0);
				payMethod.setUpdateTime(new Date());
				payMethod.setFailNumber(0);
				payMethod.setFailNotice(0);
				this.sellerPayMethodService.updateById(payMethod);
				return ResponseData.success();
			}
		}
		return ResponseData.error("操作失败");
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(@RequestParam Long payMethodId) {
		List<SellerBuyerCoinOrder> list =buyCoinOrderService.findNoFinishSellerBuyerCoinListByPayMethodId(payMethodId);
		if (list != null && list.size()>0){
			return ResponseData.error("该收款方式暂时还有订单未处理，无法删除");
		}
		this.sellerPayMethodService.removeById(payMethodId);
		return SUCCESS_TIP;
	}

}
