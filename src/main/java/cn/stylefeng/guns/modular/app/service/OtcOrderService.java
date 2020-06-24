package cn.stylefeng.guns.modular.app.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.stylefeng.guns.core.util.FlowRecordConstant;
import cn.stylefeng.guns.core.util.HttpUtil;
import cn.stylefeng.guns.core.util.Md5Utils;
import cn.stylefeng.guns.modular.system.entity.*;
import cn.stylefeng.guns.modular.system.service.SellerOrderTimeSettingService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.core.util.RedisUtil;
import cn.stylefeng.guns.modular.app.dto.TeamBuyCoinDto;
import cn.stylefeng.guns.modular.app.mapper.OtcOrderMapper;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.biz.service.SendSMSExtService;
import cn.stylefeng.guns.modular.system.service.UserService;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

@Service
public class OtcOrderService {
	
	private Logger logger = LoggerFactory.getLogger(OtcOrderService.class);

	@Resource
	private OtcOrderMapper orderMapper;

	@Resource
	private SellerMapper sellerMapper;
	
	@Autowired
	private SendSMSExtService sendSMSExtService;
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private UserService userService;
	
	@Value("${job.switch}")
	private String isSwitch;
	
	@Value("${job.time}")
	private String time;

	@Autowired
	private SellerOrderTimeSettingService timeSettingService;

	@SuppressWarnings("static-access")
	public String generateSimpleSerialno(Long id,Integer type) {
		StringBuilder sb = new StringBuilder();
		if(type ==1) {
			sb.append("OTC");
		}else if(type ==2) {
			sb.append("HO");
		}else if(type ==3) {
			sb.append("RC");
		}else if(type ==4) {
			sb.append("GO");
		}else if(type ==5) {
			sb.append("MB");
		}else if(type ==6) {
			sb.append("MR");
		}else if(type ==7) {
			sb.append("MW");
		}else if(type ==8) {
			sb.append("SW");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();
		sb.append(sd.format(now));
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(now);
		calendar.add(calendar.DATE, 1);
		Date nextDate = calendar.getTime();
		sb.append((int)((Math.random()*9+1)*10));
		long count =0;
		if(type ==1) {
			 count = orderMapper.getOtcOrderByToday(sdf.format(now), sdf.format(nextDate));	
		}else if(type ==2) {
			 count = orderMapper.getSellOtcpOrderByToday(sdf.format(now), sdf.format(nextDate));	
		}else if(type ==3) {
			 count = orderMapper.getSellerRechargeAppealOrderByToday(sdf.format(now), sdf.format(nextDate));	
		}else if(type ==4) {
			count = orderMapper.getSellerOrderByToday(sdf.format(now), sdf.format(nextDate));	
		}else if(type ==5) {
			count = orderMapper.getSellerBuyerCoinOrderByToday(sdf.format(now), sdf.format(nextDate));	
		}else if(type ==6) {//商户充值
			count = orderMapper.getUserRechargeCoinAppealOrderByToday(sdf.format(now), sdf.format(nextDate));	
		}else if(type ==7) {//代理商或者商户
			count = orderMapper.getUserWithDrawCoinAppealOrderByToday(sdf.format(now), sdf.format(nextDate));	
		}else if(type ==8) {//会员提现
			count = orderMapper.getSellerWithDrawCoinAppealOrderByToday(sdf.format(now), sdf.format(nextDate));	
		}
		
		count++;
		int size = 4;
		for (int i = 0; i < size - String.valueOf(count).length(); i++) {
			sb.append("0");
		}
		sb.append(count);
		return sb.toString();
	}
	
	
	public void addSellOtcpOrder(SellOtcpOrder order) {
		orderMapper.addSellOtcpOrder(order);
	}

	public SellOtcpOrder findSellerOtcpOrderById(Long id) {
		return orderMapper.findSellerOtcpOrderById(id);
	}

	public int updateSellOtcpOrder(SellOtcpOrder order) {
		return orderMapper.updateSellOtcpOrder(order);
	}

	public void addOtcpOrder(OtcpOrder otcpOrder) {
		orderMapper.addOtcpOrder(otcpOrder);
	}

	public OtcpOrder findOtcOrderById(Long id) {
		return orderMapper.findOtcOrderById(id);
	}

	public void addOtcpOrderCannelNumberRecord(OtcpOrderCannelNumberRecord record) {
		orderMapper.addOtcpOrderCannelNumberRecord(record);
	}

	public void updateOtcpOrder(OtcpOrder otcpOrder) {
		orderMapper.updateOtcpOrder(otcpOrder);
	}

	public OtcpOrderCannelNumberRecord findOtcpOrderCannelBySellerIdToday(Long sellerId) {
		return orderMapper.findOtcpOrderCannelBySellerIdToday(sellerId);
	}

	public void updateOtcpOrderCannelNumberRecord(OtcpOrderCannelNumberRecord record) {
		orderMapper.updateOtcpOrderCannelNumberRecord(record);
	}

	public void addSellerOrder(SellerOrder order) {
		orderMapper.addSellerOrder(order);
	}

	public List<SellerOrder> findSellerOrderByNoStatus() {
		return orderMapper.findSellerOrderByNoStatus();
	}

	public void addSellerBuyerCoinOrder(SellerBuyerCoinOrder coinOrder) {
		orderMapper.addSellerBuyerCoinOrder(coinOrder);
	}

	public int updateSellerOrder(SellerOrder sellerOrder) {
		return orderMapper.updateSellerOrder(sellerOrder);
	}

	public void updateSellerBuyerCoinOrder(SellerBuyerCoinOrder order) {
		orderMapper.updateSellerBuyerCoinOrder(order);
	}

	public SellerBuyerCoinOrder findSellerBuyerOrderBySerialNo(String serialno) {
		return orderMapper.findSellerBuyerOrderBySerialNo(serialno);
	}

	public List<BuyCoinUsedPayMethodRecord> findUsedPayMethodRecord(Long payMethodId, Long sellerId, Integer type) {
		return orderMapper.findUsedPayMethodRecord(payMethodId,sellerId,type);
	}

	public void addBuyCoinUsedPayMethodRecord(BuyCoinUsedPayMethodRecord record) {
		orderMapper.addBuyCoinUsedPayMethodRecord(record);
	}

	public int findSellerBuyCoinOrder(Long sellerId) {
		return orderMapper.findSellerBuyCoinOrder(sellerId);
	}

	public Map<String, Object> findSellerbuyCoinOrderByToday(Long sellerId) {
		return orderMapper.findSellerbuyCoinOrderByToday(sellerId);
	}

	@Transactional
	public void checkSellOtcOrderNoPay() {
		List<OtcpOrder> list = orderMapper.findOtcpOrderOrderByNoStatus();
		for (OtcpOrder otcpOrder : list) {
			long outTime = 8*60*1000;
			OtcpCannelNumberSetting setting = this.sellerMapper.findOtcpCannelNumberSetting();
			if (setting != null && setting.getMinTime() >0){
				outTime = setting.getMinTime()*60*1000;
			}
			if("1".equals(isSwitch)) {
				outTime = new BigDecimal(time).multiply(new BigDecimal(1000)).setScale(0).longValue();
			}
			if((new Date().getTime()-otcpOrder.getCreateTime().getTime())>outTime) {
				otcpOrder.setStatus(7);
				otcpOrder.setCloseTime(new Date());
				orderMapper.updateOtcpOrder(otcpOrder);
				
        		SellOtcpOrder sellOtcpOrder = orderMapper.findSellerOtcpOrderById(otcpOrder.getOtcOrderId());
        		if(sellOtcpOrder != null) {			
    				/**
    				 * @Date: 2019-12-16 19:24
    				 * 订单超时后，加回到可用余额里面去，订单不能开启
    				 */
        			//1表示正在进行中，2,表示已完成，3表示已取消
        			if(sellOtcpOrder.getStatus().equals(1)||sellOtcpOrder.getStatus().equals(2)){
        				
        				logger.info("购买HC未支付退接单中,订单号【"+otcpOrder.getSerialno()+"】，退回前，挂单的订单号【"
								+sellOtcpOrder.getSerialno()+"】,挂单的数量为"+sellOtcpOrder.getNumber());
        				
        				sellOtcpOrder.setSupNumber(sellOtcpOrder.getSupNumber()+otcpOrder.getNumber());
        				sellOtcpOrder.setStatus(1);
        				sellOtcpOrder.setUpdateTime(new Date());
        				int result = orderMapper.updateSellOtcpOrder(sellOtcpOrder);
        				if(result <=0) {
        					throw new WallterException("更新失败");
        				}
        			
        				logger.info("购买HC未支付退接单中,订单号【"+otcpOrder.getSerialno()+"】，退回后，挂单的订单号【"
								+sellOtcpOrder.getSerialno()+"】,挂单的数量为"+sellOtcpOrder.getNumber());
        			}
        			//加回到承兑商/商户账户里面去
        			if(sellOtcpOrder.getStatus().equals(3)){
        				/**
        				 * 已经加回过了就不再加
        				 */
        				Object isAdd=redisUtil.get("TIMEOUT:"+otcpOrder.getSerialno());
        				if(isAdd==null){
        					redisUtil.set("TIMEOUT:"+otcpOrder.getSerialno(),"Y");
        					UserWallter userWallter = new UserWallter();
    						userWallter.setType(2);
    						userWallter.setUserId(sellOtcpOrder.getUserId());
    						List<UserWallter> buyerList = sellerMapper.findUserWallterList(userWallter);
    						userWallter = buyerList.get(0);
    						if(userWallter.getFrozenBalance()<otcpOrder.getNumber())
    						{
    							throw new WallterException("订单异常");	
    						}
    						logger.info("出售HC未支付退金额,订单号【"+otcpOrder.getSerialno()+"】，退回前，用户【"
									+userWallter.getUserId()+"】,可用余额:"
									+userWallter.getAvailableBalance()
									+",冻结余额："+userWallter.getFrozenBalance());
    						AccountUpdateRecord updateRecord = new AccountUpdateRecord();
    						updateRecord.setBeforePrice(userWallter.getAvailableBalance());
    							
    						userWallter.setFrozenBalance(userWallter.getFrozenBalance()-otcpOrder.getNumber());
    						userWallter.setAvailableBalance(userWallter.getAvailableBalance() + otcpOrder.getNumber());
    						userWallter.setUpdateTime(new Date());
    						int result = sellerMapper.updateUserWallter(userWallter);
    						if (result <= 0) {
    							throw new WallterException("取消失败");
    						}
    						updateRecord.setAfterPrice(userWallter.getAvailableBalance());
    						updateRecord.setCode("HC");
    						updateRecord.setCreateTime(new Date());
    						User user = userService.getById(sellOtcpOrder.getUserId());
 							updateRecord.setAccountId(sellOtcpOrder.getUserId());
							updateRecord.setPhone(user.getAccountCode());
    						if(sellOtcpOrder.getType().equals(2)) {
    							updateRecord.setRoleId(3L);
    							updateRecord.setSource("承兑HC");
        						updateRecord.setType("出售");
        						updateRecord.setRemark("出售");
    						}else if(sellOtcpOrder.getType().equals(3)){
    							updateRecord.setRoleId(2l);
    							updateRecord.setSource("商户HC");
    							if(sellOtcpOrder.getAutoMerchant().equals(1)) {
    								updateRecord.setType("自动出售");
            						updateRecord.setRemark("自动出售");
    							}else {
    								updateRecord.setType("手动出售");
            						updateRecord.setRemark("手动出售");
    							}        						
    						}
    						updateRecord.setPrice(otcpOrder.getNumber());
    						sellerMapper.addAccountUpdateRecord(updateRecord);
    						
    						logger.info("出售HC未支付退金额,订单号【"+otcpOrder.getSerialno()+"】，退回后，用户【"
									+userWallter.getUserId()+"】,可用余额:"
									+userWallter.getAvailableBalance()
									+",冻结余额："+userWallter.getFrozenBalance());
    						
    						UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
    						flowRecord.setCode("HC");
    						flowRecord.setCreateTime(new Date());
    						flowRecord.setSellerId(sellOtcpOrder.getSellerId());
    						flowRecord.setUserId(sellOtcpOrder.getUserId());
    						flowRecord.setPrice(otcpOrder.getNumber());
    						flowRecord.setSource("支付超时，退回");
    						flowRecord.setSerialno(sellOtcpOrder.getSerialno());
							flowRecord.setType(FlowRecordConstant.SELL_HC_COIN_CANNEL);
    						sellerMapper.addUserAccountFlowRecord(flowRecord);
        				}
        			}
        		}
        		Seller seller = sellerMapper.findSellerbyId(otcpOrder.getBuyerId());
        		if(seller != null) {
        			String content ="【码力】您的订单因为超时已被系统取消，订单号"+otcpOrder.getSerialno()+"。如果您对该订单有疑问，请通过点击APP“个人中心”找到“联系客服”联系我们。HC";
            		//sendSMSExtService.sendSms(content, seller.getPhone(), null);
					SellerNotice notice = new SellerNotice();
					notice.setIsSee(0);
					notice.setContent(content);
					notice.setCreateTime(new Date());
					notice.setSellerId(seller.getSellerId());
					this.sellerMapper.addSellerNotice(notice);
        		}
        	
			}
		}
	}

	public SellerBuyerCoinOrder selectSellerBuyCoinOrder(Long sellerId) {
		return orderMapper.selectSellerBuyCoinOrder(sellerId);
	}

	public SellerOrder findSellerorderBySellerId(Long sellerId) {
		return orderMapper.findSellerorderBySellerId(sellerId);
	}

	@Transactional
	public void checkBuyCoinOrderNoPay() {
		SellerOrderTimeSetting timeSetting = timeSettingService.getOne(null);
		List<SellerBuyerCoinOrder> list = orderMapper.findSellerBuyerCoinOrderNoStatus();
		for (SellerBuyerCoinOrder order : list) {	
			long outTime = timeSetting.getEndTime()*60*1000 ;
			if("1".equals(isSwitch)) {
				outTime = new BigDecimal(time).multiply(new BigDecimal(1000)).setScale(0).longValue();
			}
			if((new Date().getTime()-order.getCreateTime().getTime())>outTime) {
				order = orderMapper.findSellerBuyerOrderBySerialNo(order.getSerialno());
				if(order.getStatus() <=2) {
					SellerPayMethod sellerPayMethod =sellerMapper.findSellerPayMethodById(order.getPayMethodId());
					if(sellerPayMethod != null) {
						sellerPayMethod.setFailNumber((sellerPayMethod.getFailNumber() == null ? 0 : sellerPayMethod.getFailNumber())+1);
						sellerMapper.updateSellerPayMethod(sellerPayMethod);	
					}
					
					SellerOrder sellerOrder =orderMapper.findSellerorderBySellerId(order.getSellerId());
					if(sellerOrder != null) {
						
						logger.info("交易订单未支付退接单中,订单号【"+order.getSerialno()+"】，退回前，会员挂单的订单号【"
								+sellerOrder.getSerialNo()+"】,挂单的数量为"+sellerOrder.getNumber());
						
						sellerOrder.setNumber(sellerOrder.getNumber()+order.getNumber());
						sellerOrder.setUpdateTime(new Date());
						int result = orderMapper.updateSellerOrder(sellerOrder);
						if(result <=0) {
							throw new WallterException("更新失败,"+sellerOrder.getSerialNo());
						}
						
						logger.info("交易订单未支付退接单中,订单号【"+order.getSerialno()+"】，退回前，会员挂单的订单号【"
								+sellerOrder.getSerialNo()+"】,挂单的数量为"+sellerOrder.getNumber());
					}else {
						//退回钱包中
						SellerWallter sellerWallter = new SellerWallter();
						sellerWallter.setSellerId(order.getSellerId());
						sellerWallter.setCode("HC");
						List<SellerWallter> wallterList = sellerMapper.findSellerWallter(sellerWallter);
						if(wallterList != null && wallterList.size() >0) {
							sellerWallter = wallterList.get(0);
							logger.info("交易订单未支付退金额,订单号【"+order.getSerialno()+"】，退回前，用户【"
										+sellerWallter.getSellerId()+"】,可用余额:"
										+sellerWallter.getAvailableBalance()
										+",冻结余额："+sellerWallter.getFrozenBalance());
							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(sellerWallter.getAvailableBalance());
							if(sellerWallter.getFrozenBalance()-order.getNumber() <0) {
								logger.info("交易订单未支付退金额,出现数量大于冻结余额,订单号【"+order.getSerialno()+"】，退回前，用户【"
										+sellerWallter.getSellerId()+"】,可用余额:"
										+sellerWallter.getAvailableBalance()
										+",冻结余额："+sellerWallter.getFrozenBalance()+",订单数量："+order.getNumber());
								continue;
							}
							sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()+order.getNumber());
							sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-order.getNumber());
							
							int reslut = sellerMapper.updateSellerWallter(sellerWallter);
							if(reslut <=0) {
								logger.info("订单流水号："+order.getSerialno()+"取消失败");
								throw new WallterException("处理失败");
							}
							Seller seller = sellerMapper.findSellerbyId(order.getSellerId());
							updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
							updateRecord.setCode("HC");
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(seller.getAccount());
							updateRecord.setSource("会员HC");
							updateRecord.setType("接单交易");
							updateRecord.setRemark("接单交易,取消订单");
							updateRecord.setPrice(order.getNumber());
							updateRecord.setRoleId(1L);
							updateRecord.setPayMethodType(order.getPayMethodType());
							updateRecord.setAccountId(seller.getSellerId());
							updateRecord.setSerialno(order.getSerialno());
							sellerMapper.addAccountUpdateRecord(updateRecord);
							
							logger.info("交易订单未支付退金额,订单号【"+order.getSerialno()+"】，退回后，用户【"
									+sellerWallter.getSellerId()+"】,可用余额:"
									+sellerWallter.getAvailableBalance()
									+",冻结余额："+sellerWallter.getFrozenBalance());
							
							SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
							flowRecord.setCode("HC");
							flowRecord.setCreateTime(new Date());
							flowRecord.setSellerId(order.getSellerId());
							flowRecord.setPrice(order.getNumber());
							flowRecord.setSource("交易未支付，退回");
							flowRecord.setSerialno(order.getSerialno());
							flowRecord.setType(FlowRecordConstant.GRAD_HC_COIN_CANNEL);
							sellerMapper.addSellerAccountFlowRecord(flowRecord);
						}
					}
					order.setStatus(7);
					order.setCloseTime(new Date());
					order.setUpdateTime(new Date());
					order.setUpdateUser(1L);
					orderMapper.updateSellerBuyerCoinOrder(order);
				}
			}
		}
	}

	public Double findSellerbuyCoinOrderByIsAppealCount(Long sellerId) {
		return orderMapper.findSellerbuyCoinOrderByIsAppealCount(sellerId);
	}

	public Double findSellerbuyCoinOrderByRunning(Long sellerId) {
		return orderMapper.findSellerbuyCoinOrderByRunning(sellerId);
	}

	public List<SellerBuyerCoinOrder> findSellerBuyCoinOrderListByOutOrderId(Long orderId) {
		return orderMapper.findSellerBuyCoinOrderListByOutOrderId(orderId);
	}

	@Transactional
	public void checkSellerOnLine() {
		synchronized (this) {
				List<SellerOrder> list = orderMapper.findSellerOrderByNoStatus();
				for (SellerOrder sellerOrder : list) {
					if(redisUtil.get("ONLINE_"+sellerOrder.getSellerId()) == null) {
					Seller seller =	sellerMapper.findSellerbyId(sellerOrder.getSellerId());
					if (seller != null) {	
				 		 SellerWallter sellerWallter = new SellerWallter();
				 		 sellerWallter.setCode("HC");
				 		 sellerWallter.setSellerId(sellerOrder.getSellerId());
				 		 List<SellerWallter> sellerWallterList =  sellerMapper.findSellerWallter(sellerWallter);
				 		 if(sellerWallterList != null && sellerWallterList.size() >0) {
				 			sellerWallter = sellerWallterList.get(0);
				 			if(sellerWallter.getFrozenBalance()-sellerOrder.getNumber() >=0) {
					 			sellerOrder.setStatus(1);
								sellerOrder.setCloseTime(new Date());
						 		int updateOrderResult =  orderMapper.updateSellerOrder(sellerOrder);
						 		if(updateOrderResult <=0) {
						 			logger.info("【=======定时器检测是否在线，更新挂单订单出现异常，异常订单号为"+sellerOrder.getSerialNo()+"====】");			
						 			throw new WallterException("更新失败");
						 		}
				 				logger.info("检查会员是否在线，退回挂单的余额,订单号【"+sellerOrder.getSerialNo()+"】，退回前，用户【"
										+sellerWallter.getSellerId()+"】,可用余额:"
										+sellerWallter.getAvailableBalance()
										+",冻结余额："+sellerWallter.getFrozenBalance()+",挂单数量："+sellerOrder.getNumber());
				 				
				 				if(sellerWallter.getFrozenBalance()-sellerOrder.getNumber() <0) {
									logger.info("检查会员是否在线，退回挂单的余额,订单号【"+sellerOrder.getSerialNo()+"】，退回前，用户【"
											+sellerWallter.getSellerId()+"】,可用余额:"
											+sellerWallter.getAvailableBalance()
											+",冻结余额："+sellerWallter.getFrozenBalance()+",订单数量："+sellerOrder.getNumber());
									continue;
								}
				 				
				 				AccountUpdateRecord updateRecord = new AccountUpdateRecord();
				 				updateRecord.setBeforePrice(sellerWallter.getAvailableBalance());

								SellerCash cash = sellerMapper.selectSellerCashBySellerId(seller.getSellerId());
								Double cashPrice = 0.0;
								if (cash != null){
									cashPrice = cash.getCash();
									sellerMapper.deleteSellerCashById(cash);
								}
					 			sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()+sellerOrder.getNumber()+cashPrice);
					 			sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-sellerOrder.getNumber());
					 			sellerWallter.setUpdateTime(new Date());
					 			int result = sellerMapper.updateSellerWallter(sellerWallter);
					 			if(result <=0) {
					 				logger.info("【=======定时器检测是否在线，更新挂单订单后的余额出现异常，异常订单号为"+sellerOrder.getSerialNo()+"====】");
					 				throw new WallterException("更新失败");
					 			}
					 			updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
								updateRecord.setCode("HC");
								updateRecord.setCreateTime(new Date());
								updateRecord.setPhone(seller.getAccount());
								updateRecord.setSource("会员HC");
								updateRecord.setType("接单交易");
								updateRecord.setRemark("接单交易，离线退回");
								updateRecord.setPrice(sellerOrder.getNumber());
								updateRecord.setRoleId(1L);
								updateRecord.setSerialno(sellerOrder.getSerialNo());
								updateRecord.setAccountId(seller.getSellerId());
								sellerMapper.addAccountUpdateRecord(updateRecord);
								
					 			logger.info("检查会员是否在线，退回挂单的余额,订单号【"+sellerOrder.getSerialNo()+"】，退回后，用户【"
										+sellerWallter.getSellerId()+"】,可用余额:"
										+sellerWallter.getAvailableBalance()
										+",冻结余额："+sellerWallter.getFrozenBalance()+",挂单数量："+sellerOrder.getNumber());
					 		
				 			}
				 		}
				     }
					}
				}
		}
		
	}

	@Transactional
	public void sellerBuyCoinBonus() {
		synchronized (this) {
			List<TeamBonusSetting> teamBonusSettingList =  sellerMapper.findTeamBonusSettingList();
			if(teamBonusSettingList != null && teamBonusSettingList.size() >0) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("recommendId", 0);
				List<TeamBuyCoinDto> noRecommendList = orderMapper.findMyselfBuyCoinInfoByNoRecommend();
				if(noRecommendList != null && noRecommendList.size()>0) {
					for (TeamBuyCoinDto teamBuyCoinDto : noRecommendList) {
						Integer level = 0;
						Double bonusRatio = 0.0;
						Long recommendId = teamBuyCoinDto.getSellerId();
						Double teamPrice = orderMapper.findTeamBuyCoinInfoByRecommendId(teamBuyCoinDto.getSellerId())+teamBuyCoinDto.getTotalNumber();
						if(teamPrice != null && teamPrice >0) {
							for(TeamBonusSetting teamBonusSetting:teamBonusSettingList) {
								if(teamBonusSetting.getMinPrice() != null) {
									if(teamPrice>=teamBonusSetting.getMinPrice()&& (teamBonusSetting.getMaxPrice() == null || teamBonusSetting.getMaxPrice()>=teamPrice)) {
										level = teamBonusSetting.getLevel();
										bonusRatio = teamBonusSetting.getBonusRatio();
										break;
									}
								}
							}
							if(level >0 && teamPrice >0) {
								if(teamBuyCoinDto.getTotalNumber()!= null && teamBuyCoinDto.getTotalNumber()>0 ) {
									Double returnMySelfBonusPrice = new BigDecimal(teamBuyCoinDto.getTotalNumber())
																	.multiply(new BigDecimal(bonusRatio))
																	.divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP).doubleValue();
									if(returnMySelfBonusPrice >0) {
										SellerProfitWallter sellerProfitWallter =new SellerProfitWallter();
										sellerProfitWallter.setCode("3");
										sellerProfitWallter.setSellerId(recommendId);
										List<SellerProfitWallter>  wallterList = sellerMapper.findSellerProfitWallterList(sellerProfitWallter);
										sellerProfitWallter = wallterList.get(0);
										
										AccountUpdateRecord updateRecord = new AccountUpdateRecord();
										updateRecord.setBeforePrice(sellerProfitWallter.getAvailableBalance());
										logger.info("团队返利，会员账号【"+sellerProfitWallter.getSellerId()+"】，可用余额："+sellerProfitWallter.getAvailableBalance()+",冻结余额："+sellerProfitWallter.getFrozenBalance()+",返利："+returnMySelfBonusPrice);
										
										sellerProfitWallter.setAvailableBalance(sellerProfitWallter.getAvailableBalance()+returnMySelfBonusPrice);
										sellerProfitWallter.setTotalBalance(sellerProfitWallter.getTotalBalance()+returnMySelfBonusPrice);
										sellerProfitWallter.setUpdateTime(new Date());
										int result = sellerMapper.updateSellerProfitWallter(sellerProfitWallter);
										if(result <=0) {
											throw new WallterException("推荐返利失败");
										}
										logger.info("处理后团队返利，会员账号【"+sellerProfitWallter.getSellerId()+"】，可用余额："+sellerProfitWallter.getAvailableBalance()+",冻结余额："+sellerProfitWallter.getFrozenBalance()+",返利："+returnMySelfBonusPrice);
										
										Seller seller = sellerMapper.findSellerbyId(sellerProfitWallter.getSellerId());
										updateRecord.setAfterPrice(sellerProfitWallter.getAvailableBalance());
										updateRecord.setCode("HC");
										updateRecord.setCreateTime(new Date());
										updateRecord.setPhone(seller.getAccount());
										updateRecord.setSource("推荐挖矿");
										updateRecord.setType("团队返利");
										updateRecord.setRemark("团队返利");
										updateRecord.setPrice(-returnMySelfBonusPrice);
										updateRecord.setRoleId(1L);
										updateRecord.setAccountId(sellerProfitWallter.getSellerId());
										sellerMapper.addAccountUpdateRecord(updateRecord);
										
										SellerProfitAccountFlowRecord profitFlowRecord = new SellerProfitAccountFlowRecord();
										profitFlowRecord.setCode(sellerProfitWallter.getCode());
										profitFlowRecord.setSellerId(sellerProfitWallter.getSellerId());
										profitFlowRecord.setCreateTime(new Date());
										profitFlowRecord.setSource("团队返利");
										profitFlowRecord.setPrice(returnMySelfBonusPrice);
										profitFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
										sellerMapper.addSellerProfitFlowRecord(profitFlowRecord);
									}
								}
								updateBonusNumber(recommendId, level, bonusRatio, teamBonusSettingList);
							}
						}
					}
				}
			}
		}
	}
	
	@Transactional
	void  updateBonusNumber(Long recommendId, Integer level, Double bonusRatio, List<TeamBonusSetting> teamBonusSettingList) {
		List<TeamBuyCoinDto> teamList = orderMapper.findSellerBuyCoinTeamByRecommdId(recommendId);
		for (TeamBuyCoinDto teamBuyCoinDto : teamList) {
			Integer level2 = 0;
			Double bonusRatio2 = 0.0;
			Long recommendId2 = teamBuyCoinDto.getSellerId();
			Double teamPrice2 =orderMapper.findTeamBuyCoinInfoByRecommendId(recommendId2)+teamBuyCoinDto.getTotalNumber();
			if(teamPrice2 != null && teamPrice2>0) {
				for(TeamBonusSetting teamBonusSetting:teamBonusSettingList) {
					if(teamBonusSetting.getMinPrice() != null) {
						if(teamPrice2>=teamBonusSetting.getMinPrice() && (teamBonusSetting.getMaxPrice() == null || teamBonusSetting.getMaxPrice()>=teamPrice2)) {
							level2 = teamBonusSetting.getLevel();
							bonusRatio2 = teamBonusSetting.getBonusRatio();
							break;
						}
					}
				}
				if(level2 != null && level2 >0) {
					if(teamBuyCoinDto.getTotalNumber() != null && teamBuyCoinDto.getTotalNumber() >0 ) {
						Double returnMySelfBonusPrice =new BigDecimal(teamBuyCoinDto.getTotalNumber())
														.multiply(new BigDecimal(bonusRatio2))
														.divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP).doubleValue();
						if(returnMySelfBonusPrice >0) {
							SellerProfitWallter sellerProfitWallter =new SellerProfitWallter();
							sellerProfitWallter.setCode("3");
							sellerProfitWallter.setSellerId(recommendId2);
							List<SellerProfitWallter>  wallterList = sellerMapper.findSellerProfitWallterList(sellerProfitWallter);
							sellerProfitWallter = wallterList.get(0);
							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(sellerProfitWallter.getAvailableBalance());
						
							logger.info("团队返利，会员账号【"+sellerProfitWallter.getSellerId()+"】，可用余额："+sellerProfitWallter.getAvailableBalance()+",冻结余额："+sellerProfitWallter.getFrozenBalance()+",返利："+returnMySelfBonusPrice);
							
							sellerProfitWallter.setAvailableBalance(sellerProfitWallter.getAvailableBalance()+returnMySelfBonusPrice);
							sellerProfitWallter.setTotalBalance(sellerProfitWallter.getTotalBalance()+returnMySelfBonusPrice);
							sellerProfitWallter.setUpdateTime(new Date());
							int result = sellerMapper.updateSellerProfitWallter(sellerProfitWallter);
							if(result <=0) {
								throw new WallterException("推荐返利失败");
							}
							
							logger.info("处理后团队返利，会员账号【"+sellerProfitWallter.getSellerId()+"】，可用余额："+sellerProfitWallter.getAvailableBalance()+",冻结余额："+sellerProfitWallter.getFrozenBalance()+",返利："+returnMySelfBonusPrice);
							
							Seller seller = sellerMapper.findSellerbyId(sellerProfitWallter.getSellerId());
							updateRecord.setAfterPrice(sellerProfitWallter.getAvailableBalance());
							updateRecord.setCode("HC");
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(seller.getAccount());
							updateRecord.setSource("推荐挖矿");
							updateRecord.setType("团队返利");
							updateRecord.setRemark("团队返利");
							updateRecord.setPrice(-returnMySelfBonusPrice);
							updateRecord.setRoleId(1L);
							updateRecord.setAccountId(sellerProfitWallter.getSellerId());
							sellerMapper.addAccountUpdateRecord(updateRecord);
							SellerProfitAccountFlowRecord profitFlowRecord = new SellerProfitAccountFlowRecord();
							profitFlowRecord.setCode(sellerProfitWallter.getCode());
							profitFlowRecord.setSellerId(sellerProfitWallter.getSellerId());
							profitFlowRecord.setCreateTime(new Date());
							profitFlowRecord.setSource("团队返利");
							profitFlowRecord.setPrice(returnMySelfBonusPrice);
							profitFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
							sellerMapper.addSellerProfitFlowRecord(profitFlowRecord);
						}
					}
				}
				
				Double returnBonusPrice = new BigDecimal(teamPrice2)
										.multiply(new BigDecimal(bonusRatio).subtract(new BigDecimal(bonusRatio2)))
										.divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP).doubleValue();;
				if(returnBonusPrice >0) {
					SellerProfitWallter sellerProfitWallter =new SellerProfitWallter();
					sellerProfitWallter.setCode("3");
					sellerProfitWallter.setSellerId(recommendId);
					List<SellerProfitWallter>  wallterList = sellerMapper.findSellerProfitWallterList(sellerProfitWallter);
					sellerProfitWallter = wallterList.get(0);
					logger.info("团队返利，会员账号【"+sellerProfitWallter.getSellerId()+"】，可用余额："+sellerProfitWallter.getAvailableBalance()+",冻结余额："+sellerProfitWallter.getFrozenBalance()+",返利："+returnBonusPrice);
					
					sellerProfitWallter.setAvailableBalance(sellerProfitWallter.getAvailableBalance()+returnBonusPrice);
					sellerProfitWallter.setTotalBalance(sellerProfitWallter.getTotalBalance()+returnBonusPrice);
					sellerProfitWallter.setUpdateTime(new Date());
					int result = sellerMapper.updateSellerProfitWallter(sellerProfitWallter);
					if(result <=0) {
						throw new WallterException("推荐返利失败");
					}
					logger.info("处理后团队返利，会员账号【"+sellerProfitWallter.getSellerId()+"】，可用余额："+sellerProfitWallter.getAvailableBalance()+",冻结余额："+sellerProfitWallter.getFrozenBalance()+",返利："+returnBonusPrice);
					
					SellerProfitAccountFlowRecord profitFlowRecord = new SellerProfitAccountFlowRecord();
					profitFlowRecord.setCode(sellerProfitWallter.getCode());
					profitFlowRecord.setSellerId(sellerProfitWallter.getSellerId());
					profitFlowRecord.setCreateTime(new Date());
					profitFlowRecord.setSource("团队返利");
					profitFlowRecord.setPrice(returnBonusPrice);
					profitFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
					sellerMapper.addSellerProfitFlowRecord(profitFlowRecord);
				}
				updateBonusNumber(recommendId2,level2,bonusRatio2,teamBonusSettingList);
			}
		}
	}

	public int findSellerBuyCoinOrderByOutOrder(String userOrderNo) {
		return orderMapper.findSellerBuyCoinOrderByOutOrder(userOrderNo);
	}

	public SellOtcpOrder findSellerOtcpOrderByAutoMerchantAndType(Integer autoMerchant, Integer type,Long userId) {
		return orderMapper.findSellerOtcpOrderByAutoMerchantAndType(autoMerchant,type,userId);
	}

	public List<SellerBuyerCoinOrder> findSellerOrderByNoStatusAndPrice(Double price, Long sellerId) {
		return orderMapper.findSellerOrderByNoStatusAndPrice(price,sellerId);
	}
	
	public static void main(String[] args) {
		String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String seconds = new SimpleDateFormat("HHmmss").format(new Date());
		for (int i = 0; i < 10000; i++) {
			System.out.println(date+"00001000"+getTwo()+"00"+seconds+getTwo());
		}
	}
	
	public static String getTwo() {
		Random rad = new Random();
		String result = rad.nextInt(100)+"";
		if(result.length() ==1) {
			return "0"+result;
		}
		return result;
	}


	/**
	 * 查询是否存在未完成的同码金额订单
	 * @param sellerId 卖家id
	 * @param price 价格
	 * @param  ：支付类型
	 * @return
	 */
	public SellerBuyerCoinOrder findSellerBuyCoinByPriceAndType(Long sellerId, Double price, Long payMethodId) {
		return orderMapper.findSellerBuyCoinByPriceAndType(sellerId,price,payMethodId);
	}

	public void checkSellerPayMethodFailNumber() {
		synchronized (this){
			SellerBuySoldOutSetting sellerBuySoldOutSetting = sellerMapper.getSellerBuySoldOutSetting();
			List<SellerPayMethod> list = sellerMapper.findSellerPayMethodByIsCheck(null,null);
			for (SellerPayMethod payMethod : list){
				if (sellerBuySoldOutSetting != null && sellerBuySoldOutSetting.getNumber()>0
						&& sellerBuySoldOutSetting.getNumber() <= payMethod.getFailNumber()){
					payMethod.setFailNotice(1);
					payMethod.setIsSoldOut(1);
					payMethod.setSoldOutTime(new Date());
					payMethod.setIsCheck(0);
					this.sellerMapper.updateSellerPayMethod(payMethod);
				}
			}
		}

	}

    public void deleteBuyCoinUsedPayMethodRecordNoPayMethodId(SellerPayMethod payMethod) {
		this.orderMapper.deleteBuyCoinUsedPayMethodRecordNoPayMethodId(payMethod);
    }

    @Transactional
	public synchronized  void checkSellerBuyerCoinOrder() {
		List<SellerBuyerCoinOrder> list = this.orderMapper.findsellerBuyerOrderByStatusAndNoSuccess(4,0);
		for (SellerBuyerCoinOrder order :list){
			// 传递给商家那边
			Map<String, String> params = new HashMap<String, String>();
			params.put("cuid", order.getCuid());
			User user = new User();
			user.setUserId(order.getBuyerId());
			user = this.sellerMapper.findUserOne(user);
			params.put("uid", user.getAccountCode()+ "");
			params.put("price", order.getNumber() + "");
			params.put("cuid", order.getCuid());
			params.put("user_order_no", order.getUserOrderNo());
			params.put("paytype", order.getPayMethodType() + "");
			params.put("orderno", order.getSerialno());
			params.put("realprice", order.getNumber() + "");
			String sbf = Md5Utils.getSign(params);
			sbf = sbf + "key=" + user.getAppSecret();
			logger.info("sign之前的：" + sbf);
			String sign = DigestUtils.md5DigestAsHex(sbf.getBytes()).toLowerCase();
			params.put("sign", sign);
			logger.info("sign之后：" + sign);
			logger.info(JSONObject.toJSONString(params));
			String result = null;
			for (int i = 0; i < 3; i++) {
				try {
					result = HttpUtil.postForm(order.getNotifyUrl(), params);
				} catch (IOException e) {
					e.printStackTrace();
				}
				logger.info("result:" + result);
				if ("success".equals(result)) {
					break;
				}
			}
			logger.info("最后返回的结果为"+result);
			if ("success".equals(result)){
				order.setIsSuccess(1);
				order.setUpdateUser(1l);
				this.orderMapper.updateSellerBuyerCoinOrder(order);
			}
		}
	}

	public synchronized void checkSellerPayMethodIsCheck() {
		List<SellerOrder> list = orderMapper.findSellerOrderByNoStatus();
		for (SellerOrder sellerOrder : list) {
			 List<SellerPayMethod> payMethodList = 	sellerMapper.findSellerPayMethodByIsCheck(sellerOrder.getSellerId(),null);
			if (payMethodList == null || payMethodList.size() <=0){
				Seller seller =	sellerMapper.findSellerbyId(sellerOrder.getSellerId());
				if (seller != null) {
					SellerWallter sellerWallter = new SellerWallter();
					sellerWallter.setCode("HC");
					sellerWallter.setSellerId(sellerOrder.getSellerId());
					List<SellerWallter> sellerWallterList =  sellerMapper.findSellerWallter(sellerWallter);
					if(sellerWallterList != null && sellerWallterList.size() >0) {
						sellerWallter = sellerWallterList.get(0);
						if(sellerWallter.getFrozenBalance()-sellerOrder.getNumber() >=0) {
							sellerOrder.setStatus(1);
							sellerOrder.setCloseTime(new Date());
							int updateOrderResult =  orderMapper.updateSellerOrder(sellerOrder);
							if(updateOrderResult <=0) {
								throw new WallterException("更新失败");
							}
							logger.info("检查会员是否在线，退回挂单的余额,订单号【"+sellerOrder.getSerialNo()+"】，退回前，用户【"
									+sellerWallter.getSellerId()+"】,可用余额:"
									+sellerWallter.getAvailableBalance()
									+",冻结余额："+sellerWallter.getFrozenBalance()+",挂单数量："+sellerOrder.getNumber());

							if(sellerWallter.getFrozenBalance()-sellerOrder.getNumber() <0) {
								continue;
							}

							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(sellerWallter.getAvailableBalance());

							SellerCash cash = sellerMapper.selectSellerCashBySellerId(seller.getSellerId());
							Double cashPrice = 0.0;
							if (cash != null){
								cashPrice = cash.getCash();
								sellerMapper.deleteSellerCashById(cash);
							}
							sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()+sellerOrder.getNumber()+cashPrice);
							sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-sellerOrder.getNumber());
							sellerWallter.setUpdateTime(new Date());
							int result = sellerMapper.updateSellerWallter(sellerWallter);
							if(result <=0) {
								logger.info("【=======定时器检测是否没有收款码，更新挂单订单后的余额出现异常，异常订单号为"+sellerOrder.getSerialNo()+"====】");
								throw new WallterException("更新失败");
							}
							updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
							updateRecord.setCode("HC");
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(seller.getAccount());
							updateRecord.setSource("会员HC");
							updateRecord.setType("接单交易");
							updateRecord.setRemark("接单交易停止，退回");
							updateRecord.setPrice(sellerOrder.getNumber());
							updateRecord.setRoleId(1L);
							updateRecord.setSerialno(sellerOrder.getSerialNo());
							updateRecord.setAccountId(seller.getSellerId());
							sellerMapper.addAccountUpdateRecord(updateRecord);

							logger.info("检查会员是否停止，退回挂单的余额,订单号【"+sellerOrder.getSerialNo()+"】，退回后，用户【"
									+sellerWallter.getSellerId()+"】,可用余额:"
									+sellerWallter.getAvailableBalance()
									+",冻结余额："+sellerWallter.getFrozenBalance()+",挂单数量："+sellerOrder.getNumber());

						}
					}
				}
			}
		}
	}

    public int findBuyerOrderByPayMethodAndStatus(Long payMethodId) {
		Map<String,Object> param = new HashMap<>();
		param.put("payMethodId",payMethodId);
		return this.orderMapper.findBuyerOrderByPayMethodAndStatus(param);
    }
}
