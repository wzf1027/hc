package cn.stylefeng.guns.modular.app.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.stylefeng.guns.core.util.*;
import cn.stylefeng.guns.modular.system.entity.*;
import cn.stylefeng.guns.modular.system.service.MerchantIpService;
import cn.stylefeng.guns.modular.system.service.SellerCitySwitchSettingService;
import cn.stylefeng.guns.modular.system.service.SellerGradPriceSettingService;
import cn.stylefeng.guns.modular.system.service.SellerOrderTimeSettingService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import com.alibaba.fastjson.JSONObject;
import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.modular.app.dto.BuyCoinDto;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.system.mapper.UserMapper;

import javax.servlet.http.HttpServletRequest;

@Service
public class BuyCoinService {
	
	private Logger logger = LoggerFactory.getLogger(BuyCoinService.class);
	
	@Value("${platform.DOMAIN}")
	private String domain;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private SellerMapper sellerMapper;
	
	@Autowired
	private OtcOrderService otcOrderService;
	
	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private MerchantIpService merchantIpService;


	@Autowired
	private SellerGradPriceSettingService sellerGradPriceSettingService;

	@Autowired
	SellerCitySwitchSettingService citySwitchSettingService;

	@Autowired
	SellerOrderTimeSettingService timeSettingService;

	@Transactional
	public ResponseData confirmOrder(String serialno) {
		if(redisUtil.get("PAY_TIME"+serialno) == null) {
			return ResponseData.error("支付时间已超时");
		}
		String time = (String) redisUtil.get("PAY_TIME"+serialno);
		if((new Date().getTime()-Long.valueOf(time))/1000>60*8) {
			return ResponseData.error("支付时间已超时"); 
		}
	   SellerBuyerCoinOrder order = otcOrderService.findSellerBuyerOrderBySerialNo(serialno);
	   if(order != null && order.getStatus() ==1) {
		   order.setStatus(2);
		   order.setUpdateTime(new Date());
		   otcOrderService.updateSellerBuyerCoinOrder(order);
		   return ResponseData.success(200, "确认收款成功", order.getReturnUrl());
	   }
	   return ResponseData.error("确认收款失败");
	}

	@Transactional
	public ResponseData sumbitNumber(BuyCoinDto buyConiDto, HttpServletRequest request) {
		
		logger.info("sumbitNumber:"+JSONObject.toJSONString(buyConiDto));
		SellerOrderTimeSetting timeSetting = timeSettingService.getOne(null);
		long outTime =timeSetting.getStarTime();
		if(buyConiDto.getPrice() == null || Double.valueOf(buyConiDto.getPrice()) <=0) {
			return ResponseData.error(201,"价格不能小于0");
		}
		if(buyConiDto.getPaytype() == null || buyConiDto.getPaytype()<=0) {
			return ResponseData.error(202,"支付渠道有误");
		}
		if(StringUtils.isBlank(buyConiDto.getUid())) {
			return ResponseData.error(203,"需传递商户账号");
		}
		if(StringUtils.isBlank(buyConiDto.getNotify_url())) {
			return ResponseData.error(204,"需传递异步通知地址");
		}
		if(StringUtils.isBlank(buyConiDto.getUser_order_no())) {
			return ResponseData.error(205,"需传递订单号");
		}
		if(StringUtils.isBlank(buyConiDto.getCuid())) {
			return ResponseData.error(206,"需传递用户id");
		}
		if(StringUtils.isBlank(buyConiDto.getSign())) {
			return ResponseData.error(207,"签名有误");
		}
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("ACCOUNT_CODE", buyConiDto.getUid());
		map.put("ROLE_ID", 2);
		List<User> users = userMapper.selectByMap(map);
		if(users == null || users.size() <=0) {
			return ResponseData.error(208,"商户号不存在");
		}
		User user = users.get(0);
		
		//判断是否存在玩家的订单号
		int count = otcOrderService.findSellerBuyCoinOrderByOutOrder(buyConiDto.getUser_order_no());
		if(count >0) {
			return ResponseData.error(209, "暂无商家出售");
		}

		//获取商户的黑白名单
		String ipAddress = IpUtil.getIpAddress(request);
		if (StringUtils.isBlank(ipAddress)){
			return ResponseData.error("暂时无法充值");
		}

		MerchantIp merchantIp = new MerchantIp();
		merchantIp.setIpAddress(ipAddress);
		merchantIp.setUserId(user.getUserId());
		merchantIp.setType(2);
		int ipCount = merchantIpService.count(new QueryWrapper<>(merchantIp));
		if (ipCount >0){
			logger.info("该商户的ip已被加入黑名单，无法进行充值，ip："+ipAddress);
			return ResponseData.error("暂时无法充值");
		}

		UserPayMethodFeeSetting feeSetting = userMapper.findPayMethodFeeByUserId(user.getUserId());
		if (feeSetting != null ){
			if (buyConiDto.getPaytype().equals(1)
					|| buyConiDto.getPaytype().equals(5)
					|| buyConiDto.getPaytype().equals(7)
					){
				if (feeSetting.getMinAlipayValue() != null && feeSetting.getMinAlipayValue() >Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能小于"+feeSetting.getMinAlipayValue());
				}
				if (feeSetting.getMaxAlipayValue() != null && feeSetting.getMaxAlipayValue() < Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能大于"+feeSetting.getMaxAlipayValue());
				}
			}else if (buyConiDto.getPaytype().equals(2)
					||buyConiDto.getPaytype().equals(6)
					||buyConiDto.getPaytype().equals(8)
					||buyConiDto.getPaytype().equals(9)){
				if (feeSetting.getMinWxValue() != null && feeSetting.getMinWxValue() >Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能小于"+feeSetting.getMinWxValue());
				}
				if (feeSetting.getMaxWxValue() != null && feeSetting.getMaxWxValue() < Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能大于"+feeSetting.getMaxWxValue());
				}
			}else if (buyConiDto.getPaytype().equals(3)){
				if (feeSetting.getMinCardValue() != null && feeSetting.getMinCardValue() >Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能小于"+feeSetting.getMinCardValue());
				}
				if (feeSetting.getMaxCardValue() != null && feeSetting.getMaxCardValue() < Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能大于"+feeSetting.getMaxCardValue());
				}
			}else{
				if (feeSetting.getMinCloudPayValue() != null && feeSetting.getMinCloudPayValue() >Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能小于"+feeSetting.getMinCloudPayValue());
				}
				if (feeSetting.getMaxCloudPayValue() != null && feeSetting.getMaxCloudPayValue() < Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能大于"+feeSetting.getMaxCloudPayValue());
				}
			}
		}

		Map<String, String> param = new HashMap<String, String>();
		param.put("uid", buyConiDto.getUid());
		param.put("price",buyConiDto.getPrice());
		param.put("paytype", buyConiDto.getPaytype().toString());
		param.put("user_order_no", buyConiDto.getUser_order_no());
		param.put("notify_url", buyConiDto.getNotify_url());
		param.put("cuid", buyConiDto.getCuid());
		param.put("return_url", buyConiDto.getReturn_url());
		String sbf = Md5Utils.getSign(param);
		sbf = sbf+"key="+user.getAppSecret();
		logger.info("签名前的数据："+sbf);
		String sign =DigestUtils.md5DigestAsHex(sbf.getBytes()).toLowerCase();
		logger.info("签名后的数据："+sign);
		if(!sign.equals(buyConiDto.getSign())) {
			return ResponseData.error(207,"签名有误");
		}
		int queueNumber = 0;
		int saveNumber = 0;
		if(redisUtil.get("QUEUE_NUMBER") != null) {
			queueNumber = Integer.parseInt(redisUtil.get("QUEUE_NUMBER").toString());
			saveNumber = queueNumber;
		}
		//查询挂单中的列表数据
		List<SellerOrder> orderList = otcOrderService.findSellerOrderByNoStatus();
		if(orderList != null && orderList.size() >0) {
			//判断是否有保存在redis中呢
			if(redisUtil.get("SELLER_ORDER_TOTAL") == null) {
				redisUtil.set("SELLER_ORDER_TOTAL", orderList.size()+"");
			}
			//获取保存到redis中的总数量
			int totalNumber = Integer.parseInt(redisUtil.get("SELLER_ORDER_TOTAL").toString());
			//获取的总数量跟查询挂单中的总数量不一致，进行更新
			if(orderList.size() !=totalNumber) {
				redisUtil.set("SELLER_ORDER_TOTAL", orderList.size()+"");
				totalNumber = orderList.size();
			}
			//判断上次轮到的位置数有没有大于总数量，若大于从第一位开始
			if(queueNumber >=totalNumber) {
				queueNumber=0;
				saveNumber = 0;
			}
		}
		
		for (int i = 0; i < orderList.size() && queueNumber<orderList.size(); i++) {
			SellerOrder sellerOrder = null;
			if(redisUtil.get("QUEUE_PAY_METHOD") == null) {
				 sellerOrder = orderList.get(i);
			}else {
				if(redisUtil.get("QUEUE_PAY_METHOD").toString().equals(buyConiDto.getPaytype().toString())) {
						sellerOrder = orderList.get(queueNumber);
				}else {
					 sellerOrder = orderList.get(i);
				}
			}
			boolean resultFlag = true;
			//判断是否出售数量是否足够
			Double price = Double.valueOf(buyConiDto.getPrice());
			SellerGradPriceSetting gradPriceSetting = sellerGradPriceSettingService.getOne(null);
			Double sellNumber = sellerOrder.getNumber();
			if (gradPriceSetting != null && gradPriceSetting.getPriceRate() >0){
				sellNumber = sellNumber*gradPriceSetting.getPriceRate()/100;
			}
			//查询该码商是否开启同城匹配
			SellerCitySwitchSetting setting = new SellerCitySwitchSetting();
			setting.setSellerId(sellerOrder.getSellerId());
			setting.setIsSwitch(1);
			setting = citySwitchSettingService.getOne(new QueryWrapper<>(setting));
			String merchantCity = null;
			String sellerCity = null;
			try {
				merchantCity = HttpUtil.get("http://whois.pconline.com.cn/ipJson.jsp?ip="+ipAddress+"&json=true","GBK");
				sellerCity = HttpUtil.get("http://whois.pconline.com.cn/ipJson.jsp?ip="+setting.getIp()+"&json=true","GBK");
			} catch (IOException e) {
				e.printStackTrace();
			}
			JSONObject merchantCityObject = JSONObject.parseObject(merchantCity);
			JSONObject sellerCitybject = JSONObject.parseObject(sellerCity);
			if (merchantCityObject == null && sellerCitybject == null ){
				return ResponseData.error("暂时无法充值");
			}
			logger.info("merchantCity:"+merchantCityObject+",sellerCity:"+sellerCity);
			merchantCity = merchantCityObject.getString("city");
			sellerCity =  sellerCitybject.getString("city");
			if (setting != null && setting.getIsSwitch().equals(1)){
				if (StringUtils.isBlank(merchantCity) || StringUtils.isBlank(sellerCity)){
					return ResponseData.error("暂时无法充值");
				}
				if (!merchantCity.equals(sellerCity)){
					logger.info("merchantCity:"+merchantCity+",sellerCity:"+sellerCity);
					resultFlag= false;
				}
			}
			if(resultFlag && price<= sellNumber) {
				logger.info("====匹配到======");
				//查询该收款类型的勾选列表数据
				List<SellerPayMethod> payMethodList = sellerMapper.findSellerPayMethodByIsCheck(sellerOrder.getSellerId(),buyConiDto.getPaytype());
				if(payMethodList != null && payMethodList.size()>0) {
					SellerPayMethod payMethod = null;
					if (payMethodList.size() ==1){
						payMethod = payMethodList.get(0);
						otcOrderService.deleteBuyCoinUsedPayMethodRecordNoPayMethodId(payMethod);
					}else {
						Boolean isDeletePayMethod = false;
						for (int j = 0; j < payMethodList.size(); j++) {
							List<BuyCoinUsedPayMethodRecord> recordeList = otcOrderService.findUsedPayMethodRecord(payMethodList.get(j).getPayMethodId(), sellerOrder.getSellerId(), buyConiDto.getPaytype());
							if (recordeList == null || recordeList.size() <= 0) {
								payMethod = payMethodList.get(j);
								break;
							}
							if (j == payMethodList.size() - 1) {
								payMethod = payMethodList.get(0);
								isDeletePayMethod = true;
							}
						}
						if (isDeletePayMethod){
							otcOrderService.deleteBuyCoinUsedPayMethodRecordNoPayMethodId(payMethod);
						}
					}
					logger.info("查询到该收款方式====="+JSONObject.toJSONString(payMethod));
					Map<String,Object> coinditionParam = new HashMap<>();
					coinditionParam.put("payMethodId",payMethod.getPayMethodId());
					coinditionParam.put("sellerId",sellerOrder.getSellerId());
					coinditionParam.put("status",1);
					coinditionParam.put("price",price);
					List<SellerBuyerCoinOrder> orders = sellerMapper.findSellerBuyCoinByCoindition(coinditionParam);
					if (orders != null && orders.size() >0){
						logger.info("存在同金额，"+price+",支付方式："+payMethod.getPayMethodId());
						queueNumber ++;
						if(i == orderList.size()-1){
							queueNumber =0;
							i=-1;
							if (redisUtil.get("CURRENT_TIME") == null){
								redisUtil.set("CURRENT_TIME",new Date().getTime()+"");
							}else{
								Long currentTime = Long.valueOf(redisUtil.get("CURRENT_TIME")+"") ;
								if(new Date().getTime()-currentTime > 10*1000){
									logger.info("====当前支付金额火爆,请换一个金额重新支付======");
									redisUtil.del("CURRENT_TIME");
									return ResponseData.error(209, "当前支付金额火爆,请换一个金额重新支付");
								}
							}
						}
						continue;
					}
									
				//保存挂单订单记录
				SellerBuyerCoinOrder coinOrder = new SellerBuyerCoinOrder();
				coinOrder.setCreateTime(new Date());
				coinOrder.setMatchingTime(sellerOrder.getCreateTime());
				coinOrder.setNumber(price);
				coinOrder.setBuyerId(user.getUserId());
				coinOrder.setSellerId(sellerOrder.getSellerId());
				coinOrder.setStatus(1);
				if(StringUtils.isBlank(buyConiDto.getNotify_url())) {
					coinOrder.setNotifyUrl(domain+"/app/buyCoin/notifyNotice");
				}else {
					coinOrder.setNotifyUrl(buyConiDto.getNotify_url());
				}
				coinOrder.setUserOrderNo(buyConiDto.getUser_order_no());
				coinOrder.setCuid(buyConiDto.getCuid());
				coinOrder.setSerialno(otcOrderService.generateSimpleSerialno(user.getUserId(), 5));
				coinOrder.setPayMethodId(payMethod.getPayMethodId());
				coinOrder.setPayMethodAccount(payMethod.getAccount());
				coinOrder.setPayMethodCardBank(payMethod.getCardBank());
				coinOrder.setPayMethodNickName(payMethod.getRemark());
				coinOrder.setPayMethodCardBankName(payMethod.getCardBankName());
				coinOrder.setPayMethodName(payMethod.getName());
				coinOrder.setPayMethodType(payMethod.getType());
				coinOrder.setPayMethodQrcode(payMethod.getQrCode());
				coinOrder.setIsAppeal(0);
				coinOrder.setIsSuccess(0);
				coinOrder.setIsNotice(0);
				coinOrder.setSellerIp(sellerOrder.getIp());
				coinOrder.setMerchantIp(ipAddress);
				coinOrder.setReturnUrl(buyConiDto.getReturn_url());
				coinOrder.setSellerOrderId(sellerOrder.getOrderId());
				coinOrder.setUpdateUser(1L);
				coinOrder.setMerchantIp(ipAddress);
				coinOrder.setOrderCode(1);
				coinOrder.setSellerCity(sellerCity);
				coinOrder.setMerchantCity(merchantCity);
				otcOrderService.addSellerBuyerCoinOrder(coinOrder);

				logger.info("商户调用sumbitNumber接口，进行订单匹配，匹配成功，卖家挂单的数量为："+sellerOrder.getNumber()+",玩家充值金额："+price);
				//更新卖家挂单数量
				Double supNumber = sellerOrder.getNumber()-price;
				if(supNumber<=0) {
					sellerOrder.setStatus(1);
				}

				sellerOrder.setNumber(supNumber);
				sellerOrder.setUpdateTime(new Date());
				int result = otcOrderService.updateSellerOrder(sellerOrder);
				if(result <=0) {
					logger.info("充值支付出现更新挂单数量异常，订单号:"+sellerOrder.getSerialNo());
					throw new WallterException("充值失败");
				}

				logger.info("商户调用sumbitNumber接口，进行订单匹配，匹配成功，卖家更新挂单的数量为："+sellerOrder.getNumber()+",玩家充值金额："+price);

				//卖家收款码方式记录
				BuyCoinUsedPayMethodRecord record2 = new BuyCoinUsedPayMethodRecord();
				record2.setCreateTime(new Date());
				record2.setSellerId(sellerOrder.getSellerId());
				record2.setPayMethodId(payMethod.getPayMethodId());
				record2.setType(payMethod.getType());
				otcOrderService.addBuyCoinUsedPayMethodRecord(record2);
				//保存匹配到的订单通知信息
				SellerBuyCoinNotice notice =sellerMapper.findSellerBuyCoinNotice(coinOrder.getSellerId());
				if(notice != null) {
					notice.setIsNotice(0);
					sellerMapper.updateSellerBuyCoinNotice(notice);
				}else {
					notice = new SellerBuyCoinNotice();
					notice.setIsNotice(0);
					notice.setSellerId(coinOrder.getSellerId());
					sellerMapper.addSellerBuyCoinNotice(notice);
				}

				//保存卖家匹配到的收款方式的数据
				SellerBuyCoinPayMethodQueue methodQueue = new SellerBuyCoinPayMethodQueue();
				methodQueue.setPayMethodId(payMethod.getPayMethodId());
				methodQueue.setPrice(price);
				methodQueue.setType(payMethod.getType());
				methodQueue.setSellerId(payMethod.getSellerId());
				methodQueue.setSellerOrderId(sellerOrder.getOrderId());
				sellerMapper.addSellerBuyCoinPayMethodQueue(methodQueue);

				//返回数据信息
				Map<String,Object> returnMap = new HashMap<String, Object>();
				returnMap.put("price", coinOrder.getNumber());
				returnMap.put("type", payMethod.getType());
				returnMap.put("qrCode", domain+payMethod.getQrCode());
				returnMap.put("account",payMethod.getAccount());
				returnMap.put("cardBank",payMethod.getCardBank());
				returnMap.put("cardBankName",payMethod.getCardBankName());
				returnMap.put("name", payMethod.getName());
				returnMap.put("serialno", coinOrder.getSerialno());
				returnMap.put("timeOut",outTime);
				returnMap.put("createTime", coinOrder.getCreateTime().getTime()/1000);
				returnMap.put("qrValue", payMethod.getQrValue());
				returnMap.put("remark", payMethod.getRemark());
				redisUtil.set("PAY_TIME"+coinOrder.getSerialno(), coinOrder.getCreateTime().getTime()+"", 300);


				if(queueNumber==orderList.size()-1) {
					redisUtil.set("QUEUE_NUMBER",0);
				}

				//保存匹配到的位置
				redisUtil.set("QUEUE_NUMBER",queueNumber+1);
				//保存匹配到的收款类型
				redisUtil.set("QUEUE_PAY_METHOD",buyConiDto.getPaytype().toString());
				if ( redisUtil.get("CURRENT_TIME") != null){
					redisUtil.del("CURRENT_TIME");
				}
				logger.info("商户调用sumbitNumber接口，进行订单匹配，匹配成功【"+JSONObject.toJSONString(returnMap)+"】");
				return ResponseData.success(200,"匹配成功",returnMap);
				}
			}
			queueNumber ++;
			if(i == orderList.size()-1){
				queueNumber =0;
				i=-1;
				if (redisUtil.get("CURRENT_TIME") == null){
					redisUtil.set("CURRENT_TIME",new Date().getTime()+"");
				}else{
					Long currentTime = Long.valueOf(redisUtil.get("CURRENT_TIME")+"") ;
					if(new Date().getTime()-currentTime > 10*1000){
						redisUtil.del("CURRENT_TIME");
						return ResponseData.error(209, "暂无商家出售");
					}
				}
			}
		}
		return ResponseData.error(209, "暂无商家出售");
	}

	@Transactional
	public ResponseData sumbitPay(BuyCoinDto buyConiDto,HttpServletRequest request) {
		logger.info("sumbitPay:"+JSONObject.toJSONString(buyConiDto));
		SellerOrderTimeSetting timeSetting = timeSettingService.getOne(null);
		long outTime =timeSetting.getStarTime();
		if(buyConiDto.getPrice() == null || Double.valueOf(buyConiDto.getPrice()) <=0) {
			return ResponseData.error(201,"价格不能小于0");
		}
		if(buyConiDto.getPaytype() == null || buyConiDto.getPaytype()<=0) {
			return ResponseData.error(202,"支付渠道有误");
		}
		if(StringUtils.isBlank(buyConiDto.getUid())) {
			return ResponseData.error(203,"需传递商户账号");
		}
		if(StringUtils.isBlank(buyConiDto.getNotify_url())) {
			return ResponseData.error(204,"需传递异步通知地址");
		}
		if(StringUtils.isBlank(buyConiDto.getUser_order_no())) {
			return ResponseData.error(205,"需传递订单号");
		}
		if(StringUtils.isBlank(buyConiDto.getCuid())) {
			return ResponseData.error(206,"需传递用户id");
		}
		if(StringUtils.isBlank(buyConiDto.getSign())) {
			return ResponseData.error(207,"签名有误");
		}
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("ACCOUNT_CODE", buyConiDto.getUid());
		map.put("ROLE_ID", 2);
		List<User> users = userMapper.selectByMap(map);
		if(users == null || users.size() <=0) {
			return ResponseData.error(208,"商户号不存在");
		}
		User user = users.get(0);
		
		//判断是否存在玩家的订单号
		int count = otcOrderService.findSellerBuyCoinOrderByOutOrder(buyConiDto.getUser_order_no());
		if(count >0) {
			return ResponseData.error(209, "暂无商家出售");
		}

		//获取商户的黑白名单
		String ipAddress = IpUtil.getIpAddress(request);
		if (StringUtils.isBlank(ipAddress)){
			return ResponseData.error("暂时无法充值");
		}

		MerchantIp merchantIp = new MerchantIp();
		merchantIp.setIpAddress(ipAddress);
		merchantIp.setType(2);
		merchantIp.setUserId(user.getUserId());
		int ipCount = merchantIpService.count(new QueryWrapper<>(merchantIp));
		if (ipCount >0){
			logger.info("该商户的ip已被加入黑名单，无法进行充值，ip："+ipAddress);
			return ResponseData.error("暂时无法充值");
		}
		UserPayMethodFeeSetting feeSetting = userMapper.findPayMethodFeeByUserId(user.getUserId());
		if (feeSetting != null ){
			if (buyConiDto.getPaytype().equals(1)
					|| buyConiDto.getPaytype().equals(5)
					|| buyConiDto.getPaytype().equals(7)
			){
				if (feeSetting.getMinAlipayValue() != null && feeSetting.getMinAlipayValue() >Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能小于"+feeSetting.getMinAlipayValue());
				}
				if (feeSetting.getMaxAlipayValue() != null && feeSetting.getMaxAlipayValue() < Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能大于"+feeSetting.getMaxAlipayValue());
				}
			}else if (buyConiDto.getPaytype().equals(2)
					||buyConiDto.getPaytype().equals(6)
					||buyConiDto.getPaytype().equals(8)
					||buyConiDto.getPaytype().equals(9)){
				if (feeSetting.getMinWxValue() != null && feeSetting.getMinWxValue() >Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能小于"+feeSetting.getMinWxValue());
				}
				if (feeSetting.getMaxWxValue() != null && feeSetting.getMaxWxValue() < Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能大于"+feeSetting.getMaxWxValue());
				}
			}else if (buyConiDto.getPaytype().equals(3)){
				if (feeSetting.getMinCardValue() != null && feeSetting.getMinCardValue() >Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能小于"+feeSetting.getMinCardValue());
				}
				if (feeSetting.getMaxCardValue() != null && feeSetting.getMaxCardValue() < Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能大于"+feeSetting.getMaxCardValue());
				}
			}else{
				if (feeSetting.getMinCloudPayValue() != null && feeSetting.getMinCloudPayValue() >Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能小于"+feeSetting.getMinCloudPayValue());
				}
				if (feeSetting.getMaxCloudPayValue() != null && feeSetting.getMaxCloudPayValue() < Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能大于"+feeSetting.getMaxCloudPayValue());
				}
			}
		}


		Map<String, String> param = new HashMap<String, String>();
		param.put("uid", buyConiDto.getUid());
		param.put("price",buyConiDto.getPrice());
		param.put("paytype", buyConiDto.getPaytype().toString());
		param.put("user_order_no", buyConiDto.getUser_order_no());
		param.put("notify_url", buyConiDto.getNotify_url());
		param.put("cuid", buyConiDto.getCuid());
		param.put("return_url", buyConiDto.getReturn_url());
		String sbf = Md5Utils.getSign(param);
		sbf = sbf+"key="+user.getAppSecret();
		logger.info("sign加密前："+sbf);
		String sign =DigestUtils.md5DigestAsHex(sbf.getBytes()).toLowerCase();
		logger.info("sign加密后："+sign);
		if(!sign.equals(buyConiDto.getSign())) {
			return ResponseData.error(207,"签名有误");
		}
		int queueNumber = 0;
		int saveNumber =0;
		if(redisUtil.get("QUEUE_NUMBER") != null) {
			queueNumber = Integer.parseInt(redisUtil.get("QUEUE_NUMBER").toString());
			saveNumber = queueNumber;
		}
		//查询挂单中的列表数据
		List<SellerOrder> orderList = otcOrderService.findSellerOrderByNoStatus();
		if(orderList != null && orderList.size() >0) {
			//判断是否有保存在redis中呢
			if(redisUtil.get("SELLER_ORDER_TOTAL") == null) {
				redisUtil.set("SELLER_ORDER_TOTAL", orderList.size()+"");
			}
			//获取保存到redis中的总数量
			int totalNumber = Integer.parseInt(redisUtil.get("SELLER_ORDER_TOTAL").toString());
			//获取的总数量跟查询挂单中的总数量不一致，进行更新
			if(orderList.size() !=totalNumber) {
				redisUtil.set("SELLER_ORDER_TOTAL", orderList.size()+"");
				totalNumber = orderList.size();
			}
			//判断上次轮到的位置数有没有大于总数量，若大于从第一位开始
			if(queueNumber >=totalNumber) {
				queueNumber=0;
				saveNumber = 0;
			}
		}
		for (int i = 0; i < orderList.size() && queueNumber<orderList.size(); i++) {
			SellerOrder sellerOrder = null;
			if(redisUtil.get("QUEUE_PAY_METHOD") == null) {
				 sellerOrder = orderList.get(i);
			}else {
				if(redisUtil.get("QUEUE_PAY_METHOD").toString().equals(buyConiDto.getPaytype().toString())) {
						sellerOrder = orderList.get(queueNumber);
				}else {
					 sellerOrder = orderList.get(i);
				}
			}
			boolean resultFlag = true;
			Double price = Double.valueOf(buyConiDto.getPrice());
			SellerGradPriceSetting gradPriceSetting = sellerGradPriceSettingService.getOne(null);
			Double sellNumber = sellerOrder.getNumber();
			if (gradPriceSetting != null && gradPriceSetting.getPriceRate() >0){
				sellNumber = sellNumber*gradPriceSetting.getPriceRate()/100;
			}
			//查询该码商是否开启同城匹配
			SellerCitySwitchSetting setting = new SellerCitySwitchSetting();
			setting.setSellerId(sellerOrder.getSellerId());
			setting.setIsSwitch(1);
			setting = citySwitchSettingService.getOne(new QueryWrapper<>(setting));
			String merchantCity = null;
			String sellerCity = null;
			try {
				merchantCity = HttpUtil.get("http://whois.pconline.com.cn/ipJson.jsp?ip="+ipAddress+"&json=true","GBK");
				sellerCity = HttpUtil.get("http://whois.pconline.com.cn/ipJson.jsp?ip="+sellerOrder.getIp()+"&json=true","GBK");
			} catch (IOException e) {
				e.printStackTrace();
			}
			//logger.info("merchantCity:"+merchantCity+",sellerCity:"+sellerCity);
			if (StringUtils.isBlank(merchantCity) || StringUtils.isBlank(sellerCity)){
				return ResponseData.error("暂时无法充值");
			}
			JSONObject merchantCityObject = JSONObject.parseObject(merchantCity);
			JSONObject sellerCitybject = JSONObject.parseObject(sellerCity);
			if (merchantCityObject == null && sellerCitybject == null ){
				return ResponseData.error("暂时无法充值");
			}
			logger.info("merchantCity:"+merchantCityObject+",sellerCity:"+sellerCity);

			merchantCity = merchantCityObject.getString("city");
			sellerCity =  sellerCitybject.getString("city");
			if (setting != null && setting.getIsSwitch().equals(1)){
				if (StringUtils.isBlank(merchantCity) || StringUtils.isBlank(sellerCity)){
					return ResponseData.error("暂时无法充值");
				}
				if (!merchantCity.equals(sellerCity)){
					resultFlag= false;
				}
			}
			if(resultFlag && price<= sellNumber) {
					//查询该收款类型的勾选列表数据
					List<SellerPayMethod> payMethodList = sellerMapper.findSellerPayMethodByIsCheck(sellerOrder.getSellerId(),buyConiDto.getPaytype());
					if(payMethodList != null && payMethodList.size()>0) {
						SellerPayMethod payMethod = null;
						if (payMethodList.size() ==1){
								payMethod = payMethodList.get(0);
								otcOrderService.deleteBuyCoinUsedPayMethodRecordNoPayMethodId(payMethod);
							}else {
								Boolean isDeletePayMethod = false;
								for (int j = 0; j < payMethodList.size(); j++) {
									List<BuyCoinUsedPayMethodRecord> recordeList = otcOrderService.findUsedPayMethodRecord(payMethodList.get(j).getPayMethodId(), sellerOrder.getSellerId(), buyConiDto.getPaytype());
									if (recordeList == null || recordeList.size() <= 0) {
										payMethod = payMethodList.get(j);
										break;
									}
									if (j == payMethodList.size() - 1) {
										payMethod = payMethodList.get(0);
										isDeletePayMethod = true;
									}
								}
								if (isDeletePayMethod){
									otcOrderService.deleteBuyCoinUsedPayMethodRecordNoPayMethodId(payMethod);
								}
							}

						Map<String,Object> coinditionParam = new HashMap<>();
						coinditionParam.put("payMethodId",payMethod.getPayMethodId());
						coinditionParam.put("sellerId",sellerOrder.getSellerId());
						coinditionParam.put("status",1);
						coinditionParam.put("price",price);
						List<SellerBuyerCoinOrder> orders = sellerMapper.findSellerBuyCoinByCoindition(coinditionParam);
						if (orders != null && orders.size() >0){
							queueNumber ++;
							if(i == orderList.size()-1){
								queueNumber =0;
								i=-1;
								if (redisUtil.get("CURRENT_TIME") == null){
									redisUtil.set("CURRENT_TIME",new Date().getTime()+"");
								}else{
									Long currentTime = Long.valueOf(redisUtil.get("CURRENT_TIME")+"") ;
									if(new Date().getTime()-currentTime > 10*1000){
										redisUtil.del("CURRENT_TIME");
										return ResponseData.error(209, "当前支付金额火爆,请换一个金额重新支付");
									}
								}
							}
							continue;
						}

								SellerBuyerCoinOrder coinOrder = new SellerBuyerCoinOrder();
								coinOrder.setCreateTime(new Date());
								coinOrder.setMatchingTime(sellerOrder.getCreateTime());
								coinOrder.setNumber(price);
								coinOrder.setBuyerId(user.getUserId());
								coinOrder.setSellerId(sellerOrder.getSellerId());
								coinOrder.setStatus(1);
								if(StringUtils.isBlank(buyConiDto.getNotify_url())) {
									coinOrder.setNotifyUrl(domain+"/app/buyCoin/notifyNotice");
								}else {
									coinOrder.setNotifyUrl(buyConiDto.getNotify_url());
								}
								coinOrder.setUserOrderNo(buyConiDto.getUser_order_no());
								coinOrder.setCuid(buyConiDto.getCuid());
								coinOrder.setSerialno(otcOrderService.generateSimpleSerialno(user.getUserId(), 5));
								coinOrder.setPayMethodId(payMethod.getPayMethodId());
								coinOrder.setPayMethodAccount(payMethod.getAccount());
								coinOrder.setPayMethodCardBank(payMethod.getCardBank());
								coinOrder.setPayMethodCardBankName(payMethod.getCardBankName());
								coinOrder.setPayMethodName(payMethod.getName());
								coinOrder.setPayMethodNickName(payMethod.getRemark());
								coinOrder.setPayMethodType(payMethod.getType());
								coinOrder.setPayMethodQrcode(payMethod.getQrCode());
								coinOrder.setIsAppeal(0);
								coinOrder.setIsSuccess(0);
								coinOrder.setIsNotice(0);
								coinOrder.setSellerIp(sellerOrder.getIp());
								coinOrder.setMerchantIp(ipAddress);
								coinOrder.setReturnUrl(buyConiDto.getReturn_url());
								coinOrder.setSellerOrderId(sellerOrder.getOrderId());
								coinOrder.setUpdateUser(1L);
								coinOrder.setMerchantIp(ipAddress);
								coinOrder.setOrderCode(1);
								coinOrder.setMerchantCity(merchantCity);
								coinOrder.setSellerCity(sellerCity);
								otcOrderService.addSellerBuyerCoinOrder(coinOrder);
								
								logger.info("商户调用sumbitPay接口，进行订单匹配，匹配成功，卖家挂单的数量为："+sellerOrder.getNumber()+",玩家充值金额："+price);
								Double supNumber = sellerOrder.getNumber()-price;
								if(supNumber<=0) {
									sellerOrder.setStatus(1);
								}
								sellerOrder.setNumber(supNumber);
								sellerOrder.setUpdateTime(new Date());
								int result = otcOrderService.updateSellerOrder(sellerOrder);
								if(result <=0) {
									logger.info("充值支付出现更新挂单数量异常，订单号:"+sellerOrder.getSerialNo());
									throw new 	WallterException("充值失败");
								}
								logger.info("商户调用sumbitPay接口，进行订单匹配，匹配成功，卖家挂单的数量为："+sellerOrder.getNumber()+",玩家充值金额："+price);
								
								
								BuyCoinUsedPayMethodRecord record2 = new BuyCoinUsedPayMethodRecord();
								record2.setCreateTime(new Date());
								record2.setSellerId(sellerOrder.getSellerId());
								record2.setPayMethodId(payMethod.getPayMethodId());
								record2.setType(payMethod.getType());
								otcOrderService.addBuyCoinUsedPayMethodRecord(record2);
					
								
								Map<String,Object> returnMap = new HashMap<String, Object>();
								returnMap.put("price", coinOrder.getNumber());
								returnMap.put("type", payMethod.getType());
								returnMap.put("qrCode", domain+payMethod.getQrCode());
								returnMap.put("account",payMethod.getAccount());
								returnMap.put("cardBank",payMethod.getCardBank());
								returnMap.put("cardBankName",payMethod.getCardBankName());
								returnMap.put("name", payMethod.getName());
								returnMap.put("serialno", coinOrder.getSerialno());
								returnMap.put("timeOut", outTime);
								returnMap.put("createTime", coinOrder.getCreateTime().getTime()/1000);
								returnMap.put("qrValue", payMethod.getQrValue());
								returnMap.put("remark", payMethod.getRemark());
								redisUtil.set("PAY_TIME"+coinOrder.getSerialno(), coinOrder.getCreateTime().getTime()+"", 300);
								
								SellerBuyCoinNotice notice =sellerMapper.findSellerBuyCoinNotice(coinOrder.getSellerId());
								if(notice != null) {
									notice.setIsNotice(0);
									sellerMapper.updateSellerBuyCoinNotice(notice);
								}else {
									notice = new SellerBuyCoinNotice();
									notice.setIsNotice(0);
									notice.setSellerId(coinOrder.getSellerId());
									sellerMapper.addSellerBuyCoinNotice(notice);
								}
								
								SellerBuyCoinPayMethodQueue methodQueue = new SellerBuyCoinPayMethodQueue();
								methodQueue.setPayMethodId(payMethod.getPayMethodId());
								methodQueue.setPrice(price);
								methodQueue.setType(payMethod.getType());
								methodQueue.setSellerId(payMethod.getSellerId());
								methodQueue.setSellerOrderId(sellerOrder.getOrderId());
								sellerMapper.addSellerBuyCoinPayMethodQueue(methodQueue);
								
								if(queueNumber==orderList.size()-1) {
									redisUtil.set("QUEUE_NUMBER",0);
								}
								
								redisUtil.set("QUEUE_NUMBER",queueNumber+1);
								redisUtil.set("QUEUE_PAY_METHOD",buyConiDto.getPaytype().toString());
								logger.info("商户调用sumbitPay接口，进行订单匹配，匹配成功【"+JSONObject.toJSONString(returnMap)+"】");
								if ( redisUtil.get("CURRENT_TIME") != null){
									redisUtil.del("CURRENT_TIME");
								}
								return ResponseData.success(200,"匹配成功",returnMap);

						}
			}
			queueNumber ++;
			if(i == orderList.size()-1){
				queueNumber =0;
				i=-1;
				if (redisUtil.get("CURRENT_TIME") == null){
					redisUtil.set("CURRENT_TIME",new Date().getTime()+"");
				}else{
					Long currentTime = Long.valueOf(redisUtil.get("CURRENT_TIME")+"") ;
					if(new Date().getTime()-currentTime > 10*1000){
						redisUtil.del("CURRENT_TIME");
						return ResponseData.error(209, "暂无商家出售");
					}
				}
			}
		}
		return ResponseData.error(209, "暂无商家出售");
	}


	@Transactional
	public ResponseData testPay(BuyCoinDto buyConiDto,HttpServletRequest request) {
		SellerOrderTimeSetting timeSetting = timeSettingService.getOne(null);
		long outTime =timeSetting.getStarTime();
		if(buyConiDto.getPrice() == null || Double.valueOf(buyConiDto.getPrice()) <=0) {
			return ResponseData.error(201,"价格不能小于0");
		}
		if(buyConiDto.getPaytype() == null || buyConiDto.getPaytype()<=0) {
			return ResponseData.error(202,"支付渠道有误");
		}
		MerchantPay pay = sellerMapper.findMerchatPayOne();
		if(pay ==null) {
			return ResponseData.error("充值失败");
		}
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("ACCOUNT_CODE", pay.getValue());
		map.put("ROLE_ID", 2);
		List<User> users = userMapper.selectByMap(map);
		if(users == null || users.size() <=0) {
			return ResponseData.error(208,"商户号不存在");
		}
		User user = users.get(0);

		//获取商户的黑白名单
		String ipAddress = IpUtil.getIpAddress(request);
		if (StringUtils.isBlank(ipAddress)){
			logger.info("ipAddress为空");
			return ResponseData.error("暂时无法充值");
		}

		MerchantIp merchantIp = new MerchantIp();
		merchantIp.setIpAddress(ipAddress);
		merchantIp.setType(2);
		merchantIp.setUserId(user.getUserId());
		int ipCount = merchantIpService.count(new QueryWrapper<>(merchantIp));
		if (ipCount >0){
			logger.info("该商户的ip已被加入黑名单，无法进行充值，ip："+ipAddress);
			return ResponseData.error("暂时无法充值");
		}

		UserPayMethodFeeSetting feeSetting = userMapper.findPayMethodFeeByUserId(user.getUserId());
		if (feeSetting != null ){
			if (buyConiDto.getPaytype().equals(1)
					|| buyConiDto.getPaytype().equals(5)
					|| buyConiDto.getPaytype().equals(7)
			){
				if (feeSetting.getMinAlipayValue() != null && feeSetting.getMinAlipayValue() >Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能小于"+feeSetting.getMinAlipayValue());
				}
				if (feeSetting.getMaxAlipayValue() != null && feeSetting.getMaxAlipayValue() < Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能大于"+feeSetting.getMaxAlipayValue());
				}
			}else if (buyConiDto.getPaytype().equals(2)
					||buyConiDto.getPaytype().equals(6)
					||buyConiDto.getPaytype().equals(8)
					||buyConiDto.getPaytype().equals(9)){
				if (feeSetting.getMinWxValue() != null && feeSetting.getMinWxValue() >Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能小于"+feeSetting.getMinWxValue());
				}
				if (feeSetting.getMaxWxValue() != null && feeSetting.getMaxWxValue() < Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能大于"+feeSetting.getMaxWxValue());
				}
			}else if (buyConiDto.getPaytype().equals(3)
					||buyConiDto.getPaytype().equals(8)){
				if (feeSetting.getMinCardValue() != null && feeSetting.getMinCardValue() >Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能小于"+feeSetting.getMinCardValue());
				}
				if (feeSetting.getMaxCardValue() != null && feeSetting.getMaxCardValue() < Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能大于"+feeSetting.getMaxCardValue());
				}
			}else{
				if (feeSetting.getMinCloudPayValue() != null && feeSetting.getMinCloudPayValue() >Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能小于"+feeSetting.getMinCloudPayValue());
				}
				if (feeSetting.getMaxCloudPayValue() != null && feeSetting.getMaxCloudPayValue() < Double.valueOf(buyConiDto.getPrice())){
					return  ResponseData.error(300,"充值金额不能大于"+feeSetting.getMaxCloudPayValue());
				}
			}
		}
		int queueNumber = 0;
		int saveNumber =0;
		if(redisUtil.get("QUEUE_NUMBER") != null) {
			queueNumber = Integer.parseInt(redisUtil.get("QUEUE_NUMBER").toString());
			saveNumber = queueNumber;
		}
		//查询挂单中的列表数据
		List<SellerOrder> orderList = otcOrderService.findSellerOrderByNoStatus();
		if(orderList != null && orderList.size() >0) {
			//判断是否有保存在redis中呢
			if(redisUtil.get("SELLER_ORDER_TOTAL") == null) {
				redisUtil.set("SELLER_ORDER_TOTAL", orderList.size()+"");
			}
			//获取保存到redis中的总数量
			int totalNumber = Integer.parseInt(redisUtil.get("SELLER_ORDER_TOTAL").toString());
			//获取的总数量跟查询挂单中的总数量不一致，进行更新
			if(orderList.size() !=totalNumber) {
				redisUtil.set("SELLER_ORDER_TOTAL", orderList.size()+"");
				totalNumber = orderList.size();
			}
			//判断上次轮到的位置数有没有大于总数量，若大于从第一位开始
			if(queueNumber >=totalNumber) {
				queueNumber=0;
				saveNumber =0;
			}
		}
		boolean isExitPrice = false;
		for (int i = 0; i < orderList.size() && queueNumber<orderList.size(); i++) {
			SellerOrder sellerOrder = null;
			if(redisUtil.get("QUEUE_PAY_METHOD") == null) {
				 sellerOrder = orderList.get(i);
			}else {
				if(redisUtil.get("QUEUE_PAY_METHOD").toString().equals(buyConiDto.getPaytype().toString())) {
						sellerOrder = orderList.get(queueNumber);
				}else {
					 sellerOrder = orderList.get(i);
				}
			}
			boolean resultFlag = true;
			Double price = Double.valueOf(buyConiDto.getPrice());
			SellerGradPriceSetting gradPriceSetting = sellerGradPriceSettingService.getOne(null);
			Double sellNumber = sellerOrder.getNumber();
			if (gradPriceSetting != null && gradPriceSetting.getPriceRate() >0){
				sellNumber = sellNumber*gradPriceSetting.getPriceRate()/100;
			}

			//查询该码商是否开启同城匹配
			SellerCitySwitchSetting setting = new SellerCitySwitchSetting();
			setting.setSellerId(sellerOrder.getSellerId());
			setting.setIsSwitch(1);
			setting = citySwitchSettingService.getOne(new QueryWrapper<>(setting));
			String merchantCity = null;
			String sellerCity = null;
			try {
				merchantCity = HttpUtil.get("http://whois.pconline.com.cn/ipJson.jsp?ip="+ipAddress+"&json=true","GBK");
				sellerCity = HttpUtil.get("http://whois.pconline.com.cn/ipJson.jsp?ip="+sellerOrder.getIp()+"&json=true","GBK");
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (StringUtils.isBlank(merchantCity) || StringUtils.isBlank(sellerCity)){
				return ResponseData.error("暂时无法充值");
			}
			JSONObject merchantCityObject = JSONObject.parseObject(merchantCity);
			JSONObject sellerCitybject = JSONObject.parseObject(sellerCity);
			logger.info("merchantCity:"+merchantCityObject+",sellerCity:"+sellerCity);
			merchantCity = merchantCityObject.getString("city");
			sellerCity =  sellerCitybject.getString("city");
			if (setting != null && setting.getIsSwitch().equals(1)){
				if (merchantCityObject == null && sellerCitybject == null ){
					return ResponseData.error("暂时无法充值");
				}
				if (!merchantCity.equals(sellerCity)){
					logger.info("merchantCity:"+merchantCity+",sellerCity:"+sellerCity);
					resultFlag= false;
				}
			}
			//logger.info("price:"+price+",sellNumber:"+sellNumber);
			if(resultFlag && price<= sellNumber) {
					//查询该收款类型的勾选列表数据
					List<SellerPayMethod> payMethodList = sellerMapper.findSellerPayMethodByIsCheck(sellerOrder.getSellerId(),buyConiDto.getPaytype());
					if(payMethodList != null && payMethodList.size()>0) {
						SellerPayMethod payMethod = null;
						if (payMethodList.size() ==1){
							payMethod = payMethodList.get(0);
							otcOrderService.deleteBuyCoinUsedPayMethodRecordNoPayMethodId(payMethod);
						}else {
							Boolean isDeletePayMethod = false;
							for (int j = 0; j < payMethodList.size(); j++) {
								List<BuyCoinUsedPayMethodRecord> recordeList = otcOrderService.findUsedPayMethodRecord(payMethodList.get(j).getPayMethodId(), sellerOrder.getSellerId(), buyConiDto.getPaytype());
								if (recordeList == null || recordeList.size() <= 0) {
									payMethod = payMethodList.get(j);
									break;
								}
								if (j == payMethodList.size() - 1) {
									payMethod = payMethodList.get(0);
									isDeletePayMethod = true;
								}
							}
							if (isDeletePayMethod){
								otcOrderService.deleteBuyCoinUsedPayMethodRecordNoPayMethodId(payMethod);
							}
						}
						logger.info("payMethod:"+JSONObject.toJSONString(payMethod));
						Map<String,Object> coinditionParam = new HashMap<>();
						coinditionParam.put("payMethodId",payMethod.getPayMethodId());
						coinditionParam.put("sellerId",sellerOrder.getSellerId());
						coinditionParam.put("status",1);
						coinditionParam.put("price",price);
						List<SellerBuyerCoinOrder> orders = sellerMapper.findSellerBuyCoinByCoindition(coinditionParam);
						if (orders != null && orders.size() >0){
							logger.info("存在同金额，price："+price+",会员id："+sellerOrder.getSellerId()+",支付id"+payMethod.getPayMethodId());
							queueNumber ++;
							if(i == orderList.size()-1){
								queueNumber =0;
								i=-1;
								if (redisUtil.get("CURRENT_TIME") == null){
									redisUtil.set("CURRENT_TIME",new Date().getTime()+"");
								}else{
									Long currentTime = Long.valueOf(redisUtil.get("CURRENT_TIME")+"") ;
									if(new Date().getTime()-currentTime > 10*1000){
										logger.info("当前支付金额火爆,请换一个金额重新支付");
										redisUtil.del("CURRENT_TIME");
										return ResponseData.error(209, "当前支付金额火爆,请换一个金额重新支付");
									}
								}
							}
							continue;
						}
						logger.info("匹配到了========");
						SellerBuyerCoinOrder coinOrder = new SellerBuyerCoinOrder();
						coinOrder.setCreateTime(new Date());
						coinOrder.setMatchingTime(sellerOrder.getCreateTime());
						coinOrder.setNumber(price);
						coinOrder.setBuyerId(user.getUserId());
						coinOrder.setSellerId(sellerOrder.getSellerId());
						coinOrder.setStatus(1);
						if(StringUtils.isBlank(buyConiDto.getNotify_url())) {
							coinOrder.setNotifyUrl(domain+"/app/buyCoin/notifyNotice");
						}else {
							coinOrder.setNotifyUrl(buyConiDto.getNotify_url());
						}
						coinOrder.setUserOrderNo(buyConiDto.getUser_order_no());
						coinOrder.setCuid(buyConiDto.getCuid());
						coinOrder.setSerialno(otcOrderService.generateSimpleSerialno(user.getUserId(), 5));
						coinOrder.setPayMethodId(payMethod.getPayMethodId());
						coinOrder.setPayMethodAccount(payMethod.getAccount());
						coinOrder.setPayMethodCardBank(payMethod.getCardBank());
						coinOrder.setPayMethodCardBankName(payMethod.getCardBankName());
						coinOrder.setPayMethodName(payMethod.getName());
						coinOrder.setPayMethodNickName(payMethod.getRemark());
						coinOrder.setPayMethodType(payMethod.getType());
						coinOrder.setPayMethodQrcode(payMethod.getQrCode());
						coinOrder.setIsAppeal(0);
						coinOrder.setIsSuccess(0);
						coinOrder.setIsNotice(0);
						coinOrder.setSellerIp(sellerOrder.getIp());
						coinOrder.setMerchantIp(ipAddress);
						coinOrder.setReturnUrl(buyConiDto.getReturn_url());
						coinOrder.setSellerOrderId(sellerOrder.getOrderId());
						coinOrder.setUpdateUser(1L);
						coinOrder.setMerchantIp(ipAddress);
						coinOrder.setOrderCode(1);
						coinOrder.setMerchantCity(merchantCity);
						coinOrder.setSellerCity(sellerCity);
						otcOrderService.addSellerBuyerCoinOrder(coinOrder);

						Double supNumber = sellerOrder.getNumber()-price;
						if(supNumber<=0) {
							sellerOrder.setStatus(1);
						}
						sellerOrder.setNumber(supNumber);
						sellerOrder.setUpdateTime(new Date());
						int result = otcOrderService.updateSellerOrder(sellerOrder);
						if(result <=0) {
							logger.info("测试模拟支付出现更新挂单数量异常，订单号:"+sellerOrder.getSerialNo());
							throw new 	WallterException("充值失败");
						}

						BuyCoinUsedPayMethodRecord record2 = new BuyCoinUsedPayMethodRecord();
						record2.setCreateTime(new Date());
						record2.setSellerId(sellerOrder.getSellerId());
						record2.setPayMethodId(payMethod.getPayMethodId());
						record2.setType(payMethod.getType());
						otcOrderService.addBuyCoinUsedPayMethodRecord(record2);


						Map<String,Object> returnMap = new HashMap<String, Object>();
						returnMap.put("price", coinOrder.getNumber());
						returnMap.put("type", payMethod.getType());
						returnMap.put("qrCode", domain+payMethod.getQrCode());
						returnMap.put("account",payMethod.getAccount());
						returnMap.put("cardBank",payMethod.getCardBank());
						returnMap.put("cardBankName",payMethod.getCardBankName());
						returnMap.put("name", payMethod.getName());
						returnMap.put("serialno", coinOrder.getSerialno());
						returnMap.put("timeOut", outTime);
						returnMap.put("remark", payMethod.getRemark());
						returnMap.put("createTime", coinOrder.getCreateTime().getTime()/1000);
						returnMap.put("qrValue", payMethod.getQrValue());
						redisUtil.set("PAY_TIME"+coinOrder.getSerialno(), coinOrder.getCreateTime().getTime()+"", 300);

						SellerBuyCoinNotice notice =sellerMapper.findSellerBuyCoinNotice(coinOrder.getSellerId());
						if(notice != null) {
							notice.setIsNotice(0);
							sellerMapper.updateSellerBuyCoinNotice(notice);
						}else {
							notice = new SellerBuyCoinNotice();
							notice.setIsNotice(0);
							notice.setSellerId(coinOrder.getSellerId());
							sellerMapper.addSellerBuyCoinNotice(notice);
						}

						SellerBuyCoinPayMethodQueue methodQueue = new SellerBuyCoinPayMethodQueue();
						methodQueue.setPayMethodId(payMethod.getPayMethodId());
						methodQueue.setPrice(price);
						methodQueue.setType(payMethod.getType());
						methodQueue.setSellerId(payMethod.getSellerId());
						methodQueue.setSellerOrderId(sellerOrder.getOrderId());
						sellerMapper.addSellerBuyCoinPayMethodQueue(methodQueue);

						if(queueNumber==orderList.size()-1) {
							redisUtil.set("QUEUE_NUMBER",0);
						}
						redisUtil.set("QUEUE_NUMBER",queueNumber+1);
						redisUtil.set("QUEUE_PAY_METHOD",buyConiDto.getPaytype().toString());
						if ( redisUtil.get("CURRENT_TIME") != null){
							redisUtil.del("CURRENT_TIME");
						}

						logger.info("商户调用testPay接口，进行订单匹配，匹配成功【"+JSONObject.toJSONString(returnMap)+"】");
						return ResponseData.success(200,"匹配成功",returnMap);
					}
			}
			queueNumber ++;
			if(i == orderList.size()-1){
				queueNumber =0;
				i=-1;
				if (redisUtil.get("CURRENT_TIME") == null){
					redisUtil.set("CURRENT_TIME",new Date().getTime()+"");
				}else{
					Long currentTime = Long.valueOf(redisUtil.get("CURRENT_TIME")+"") ;
					if(new Date().getTime()-currentTime > 10*1000){
						redisUtil.del("CURRENT_TIME");
						return ResponseData.error(209, "暂无商家出售");
					}
				}
			}
		}
		return ResponseData.error(209, "暂无商家出售");
	}


}
