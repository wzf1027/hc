package cn.stylefeng.guns.modular.app.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.stylefeng.guns.modular.app.service.OtcOrderService;

@Component
public class JobTasks {
	
	
	@Autowired
	private OtcOrderService otcOrderService;
	
	/**
	 * 检查购买otc的订单是否已超时
	 */
	@Scheduled(cron = "0/1 * * * * ? ")
 	public void checkSellOtcOrderNoPay() {
		otcOrderService.checkSellOtcOrderNoPay();
	}
	
	/**
	 * 检查交易订单是否超时
	 */
	@Scheduled(cron = "0/1 * * * * ? ")
 	public void checkBuyCoinOrderNoPay() {
		otcOrderService.checkBuyCoinOrderNoPay();
	}

	/**
	 * 检查码商勾选的收款码的失败次数是否超过设定的，若超过自动下架
	 */
	@Scheduled(cron = "0/1 * * * * ? ")
	public void checkSellerPayMethodFailNumber() {
		otcOrderService.checkSellerPayMethodFailNumber();
	}

	/**
	 * 检查是否在线，未在线的话就查询是否挂单，若挂单进行关闭
	 */
	@Scheduled(cron = "0/1 * * * * ? ")
 	public void checkSellerOnLine() {
		otcOrderService.checkSellerOnLine();
	}

	/**
	 * 每日凌晨团队分红
	 */
	@Scheduled(cron = "0 0 0 * * ? ")
 	public void sellerBuyCoinBonus() {
		otcOrderService.sellerBuyCoinBonus();
	}

	/**
	 * 订单确认回调通知商户
	 */
//	@Scheduled(cron = "0/1 * * * * ? ")
	public void checkSellerBuyerCoinOrder() {
		otcOrderService.checkSellerBuyerCoinOrder();
	}


	@Scheduled(cron = "0/1 * * * * ? ")
	public void checkSellerPayMethodIsCheck() {
		otcOrderService.checkSellerPayMethodIsCheck();
	}

}
