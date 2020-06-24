package cn.stylefeng.guns.modular.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.stylefeng.guns.core.util.ResponseData;
import cn.stylefeng.guns.modular.app.dto.BuyCoinDto;
import cn.stylefeng.guns.modular.app.dto.NoticeBuyCoinDto;
import cn.stylefeng.guns.modular.app.service.BuyCoinService;

import javax.servlet.http.HttpServletRequest;

/**
 * 商户充值控制器
 * @author zf
 *
 */
@Controller
@RequestMapping("/app/buyCoin/")
public class BuyCoinController {
	

	@Autowired
	private BuyCoinService buyCoinService;
	
	/**
	 * 玩家充值，方式二：返回数据形式
	 * @param buyConiDto
	 * @return
	 */
	@RequestMapping(value = "/sumbitNumber")
	@ResponseBody
	public ResponseData sumbitNumber(BuyCoinDto buyConiDto, HttpServletRequest request) {
		return buyCoinService.sumbitNumber(buyConiDto,request);
	}
	
	/**
	 * 模拟异步通知成功
	 * @param buyConiDto
	 * @return
	 */
	@RequestMapping(value = "/notifyNotice")
	@ResponseBody
	public String notifyNotice(NoticeBuyCoinDto buyConiDto) {
		return "success";
	}
	
	/**
	 * 玩家充值，方式一，跳转H5页面
	 * @param buyConiDto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/sumbitPay")
	public String sumbitPay(BuyCoinDto buyConiDto,Model model, HttpServletRequest request) {
		ResponseData responseData =  buyCoinService.sumbitPay(buyConiDto,request);
		if(responseData.getSuccess()) {
			Map<String,Object> map = (Map<String, Object>) responseData.getData();
			model.addAllAttributes(map);
			return "/modular/frame/pay.html"; 
		}
		model.addAttribute("message",responseData.getMessage());
		return "/modular/frame/error.html"; 
	}
	
	/**
	 * 模拟测试充值
	 * @param buyConiDto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/testPay")
	public String testPay(BuyCoinDto buyConiDto,Model model, HttpServletRequest request) {
		ResponseData responseData =  buyCoinService.testPay(buyConiDto,request);
		if(responseData.getSuccess()) {
			Map<String,Object> map = (Map<String, Object>) responseData.getData();
			model.addAllAttributes(map);
			return "/modular/frame/pay.html"; 
		}
		model.addAttribute("message",responseData.getMessage());
		return "/modular/frame/error.html"; 
	}
	
	/**
	 * 充值成功回调
	 * @param buyConiDto
	 * @return
	 */
	@RequestMapping(value = "/successPay")
	public String successPay() {
		return "/modular/frame/success.html"; 
	}
	
	/**
	 * 确认支付
	 * @param serialno
	 * @return
	 */
	@RequestMapping(value = "/confirmOrder")
	@ResponseBody
	public ResponseData confirmOrder(String serialno) {
		return buyCoinService.confirmOrder(serialno);
	}
	
}
