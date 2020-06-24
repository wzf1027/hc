package cn.stylefeng.guns.modular.system.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.app.service.OtcOrderService;
import cn.stylefeng.guns.modular.system.entity.AccountUpdateRecord;
import cn.stylefeng.guns.modular.system.entity.OtcpPirceSetting;
import cn.stylefeng.guns.modular.system.entity.SellOtcpOrder;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.entity.UserAccountFlowRecord;
import cn.stylefeng.guns.modular.system.entity.UserPayMethod;
import cn.stylefeng.guns.modular.system.entity.UserWallter;
import cn.stylefeng.guns.modular.system.mapper.SellOtcpOrderMapper;
import cn.stylefeng.guns.modular.system.mapper.UserMapper;
import cn.stylefeng.guns.modular.system.model.SellOtcpDto;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Service
public class SellOtcpOrderService extends ServiceImpl<SellOtcpOrderMapper, SellOtcpOrder> {

	private Logger logger = LoggerFactory.getLogger(SellOtcpOrderService.class);
	
	@Resource
	private SellOtcpOrderMapper sellOtcpOrderMapper;
	
	@Resource
	private SellerMapper sellerMapper;
	
	@Resource
	private UserMapper userMapper;
	
	@Autowired
	private OtcOrderService otcOrderService;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String condition, String phone, String beginTime, String endTime, String roleType, Long userId) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition,phone,beginTime,endTime,roleType,userId);
	}

	@Transactional
	public ResponseData autoAdd(SellOtcpDto sellOtcpDto) {
		 if(sellOtcpDto.getMinNumber()== null || sellOtcpDto.getMinNumber()<0) {
	        	return ResponseData.error("最小金额不能小于0");
	        }
	        if(sellOtcpDto.getMaxNumber()== null || sellOtcpDto.getMaxNumber()<0) {
	        	return ResponseData.error("最大金额不能小于0");
	        }
	        if(sellOtcpDto.getMaxNumber()<sellOtcpDto.getMinNumber()) {
	        	return ResponseData.error("最小金额不能大于最大金额");
	        }
	        if(StringUtils.isBlank(sellOtcpDto.getPayMethodId())) {
	        	return ResponseData.error("请选择银行卡");
	        }
	        if (StringUtils.isBlank(sellOtcpDto.getSymbols())){
				return ResponseData.error("请选择出售的币种");
			}
	       UserPayMethod payMethod = userMapper.findPayMethodById(Long.parseLong(sellOtcpDto.getPayMethodId()));
	       if(payMethod == null) {
	    	   return ResponseData.error("出售失败");
	       }
	       if(!ShiroKit.getUser().getId().equals(payMethod.getUserId())) {
	    	   return ResponseData.error("出售失败");
	       }
	       OtcpPirceSetting priceSetting = sellerMapper.findOtcpPriceSettingOne(sellOtcpDto.getSymbols());
	       if (priceSetting == null) {
	    	   return ResponseData.error("出售失败");
	       }
	       User user  =userMapper.selectById(ShiroKit.getUser().getId());
	       UserWallter userWallter = new UserWallter();
			userWallter.setType(2);
			userWallter.setUserId(user.getUserId());
			List<UserWallter> list = sellerMapper.findUserWallterList(userWallter);
			if(list != null  && list.size() >0) {
				userWallter = list.get(0);
			}
			if(userWallter.getAvailableBalance()<=0) {
				return ResponseData.error("余额不足");
			}
			if(userWallter.getAvailableBalance() < sellOtcpDto.getMinNumber()) {
				return ResponseData.error("最小金额不能大于可用余额");
			}
			if(userWallter.getAvailableBalance() < sellOtcpDto.getMaxNumber()) {
				return ResponseData.error("最大金额不能大于可用余额");
			}
			Map<String,Object> payMethodMap = new HashMap<String, Object>();
			payMethodMap.put("type", 3);
			payMethodMap.put("id", payMethod.getPayMethodId());
			List<Map<String,Object>> payMethodList = new ArrayList<Map<String,Object>>();
			payMethodList.add(payMethodMap);
			SellOtcpOrder order = otcOrderService.findSellerOtcpOrderByAutoMerchantAndType(1,3,userWallter.getUserId());
			if(order != null) {
				logger.info("商户或者代理商自动出售otc，有订单号【"
						+order.getSerialno()+"】，可用数量:"+order.getSupNumber());
				order.setNumber(order.getNumber()+userWallter.getAvailableBalance());
				order.setMinNumber(sellOtcpDto.getMinNumber());
				order.setMaxNumber(sellOtcpDto.getMaxNumber());
				order.setPayMethodIds(JSONObject.toJSONString(payMethodList));
				order.setSupNumber(order.getSupNumber()+userWallter.getAvailableBalance());
				order.setPrice(priceSetting.getPrice());
				order.setCreateTime(new Date());
				if(order.getStatus() ==3) {
					order.setStatus(1);
				}
				order.setTotalPrice(order.getNumber()*priceSetting.getPrice());
				int res = otcOrderService.updateSellOtcpOrder(order);
				if (res <=0){
					throw  new WallterException("出售失败");
				}
				logger.info("处理后商户或者代理商自动出售otc，有订单号【"
						+order.getSerialno()+"】，可用数量:"+order.getSupNumber());
			}else {
				order  = new SellOtcpOrder();
				String serialNo = otcOrderService.generateSimpleSerialno(ShiroKit.getUser().getId(),2);
				order.setSerialno(serialNo);
				order.setSellerId(0L);
				order.setUserId(ShiroKit.getUser().getId());
				order.setNumber(userWallter.getAvailableBalance());
				order.setMinNumber(sellOtcpDto.getMinNumber());
				order.setMaxNumber(sellOtcpDto.getMaxNumber());
				order.setSupNumber(userWallter.getAvailableBalance());
				order.setPayMethodIds(JSONObject.toJSONString(payMethodList));
				order.setPrice(priceSetting.getPrice());
				order.setCreateTime(new Date());
				order.setStatus(1);
				if("2".equals(user.getRoleId())) {
					order.setType(3);
					order.setRoleId(2);
				}else {
					order.setType(4);
					order.setRoleId(4);
				}
				order.setAutoMerchant(1);
				order.setTotalPrice(userWallter.getAvailableBalance()*priceSetting.getPrice());
				otcOrderService.addSellOtcpOrder(order);
			}		
			//更新钱包
			Double outNumber = userWallter.getAvailableBalance();
			logger.info("商户或者代理商自动出售otc，订单号【"+order.getSerialno()+"】，可用余额:"+userWallter.getAvailableBalance()+",冻结余额："+userWallter.getFrozenBalance()+",出售数量:"+userWallter.getAvailableBalance());

			userWallter.setFrozenBalance(userWallter.getFrozenBalance()+userWallter.getAvailableBalance());
			userWallter.setUpdateTime(new Date());
			userWallter.setAvailableBalance(0.0);
			int result = sellerMapper.updateUserWallter(userWallter);
			if(result <=0) {
				throw new WallterException("出售失败");
			}
			logger.info("处理后商户或者代理商自动出售otc，订单号【"+order.getSerialno()+"】，可用余额:"+userWallter.getAvailableBalance()+",冻结余额："+userWallter.getFrozenBalance()+",出售数量:"+userWallter.getAvailableBalance());
			AccountUpdateRecord updateRecord = new AccountUpdateRecord();
			updateRecord.setBeforePrice(outNumber);
			updateRecord.setAfterPrice(userWallter.getAvailableBalance());
			updateRecord.setCode(sellOtcpDto.getSymbols());
			updateRecord.setCreateTime(new Date());
			updateRecord.setPhone(user.getAccountCode());
			if("2".equals(user.getRoleId())) {
				updateRecord.setSource("商户HC");
				updateRecord.setRoleId(2L);
			}else {
				updateRecord.setSource("代理商HC");
				updateRecord.setRoleId(4l);
			}
		
			updateRecord.setType("自动出售");
			updateRecord.setRemark("自动出售,将可用余额转冻结余额中");
			updateRecord.setPrice(outNumber);
			updateRecord.setAccountId(user.getUserId());
			updateRecord.setSerialno(order.getSerialno());
			sellerMapper.addAccountUpdateRecord(updateRecord);
			
			UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
			flowRecord.setCode("HC");
			flowRecord.setCreateTime(new Date());
			flowRecord.setPrice(-outNumber);
			flowRecord.setUserId(ShiroKit.getUser().getId());;
			flowRecord.setSource("挂单出售");
			sellerMapper.addUserAccountFlowRecord(flowRecord);
			return ResponseData.success(200,"出售成功",null);
	}

	@Transactional
	public ResponseData noAutoAdd(SellOtcpDto sellOtcpDto, Double ratio) {
		if(sellOtcpDto.getNumber() == null || sellOtcpDto.getNumber() <=0) {
			return ResponseData.error("金额不能小于0");
		}
	        if(StringUtils.isBlank(sellOtcpDto.getPayMethodId())|| Long.valueOf(sellOtcpDto.getPayMethodId()) <=0) {
	        	return ResponseData.error("请选择银行卡");
	        }
	       UserPayMethod payMethod = userMapper.findPayMethodById( Long.valueOf(sellOtcpDto.getPayMethodId()));
	       if(payMethod == null) {
	    	   return ResponseData.error("出售失败");
	       }
	       if(!ShiroKit.getUser().getId().equals(payMethod.getUserId())) {
	    	   return ResponseData.error("出售失败");
	       }
	       OtcpPirceSetting priceSetting = sellerMapper.findOtcpPriceSettingOne(sellOtcpDto.getSymbols());
	       if (priceSetting == null) {
	    	   return ResponseData.error("出售失败");
	       }
	       User user = userMapper.selectById(ShiroKit.getUser().getId());
	       UserWallter userWallter = new UserWallter();
			userWallter.setType(2);
			userWallter.setUserId(user.getUserId());
			List<UserWallter> list = sellerMapper.findUserWallterList(userWallter);
			if(list != null  && list.size() >0) {
				userWallter = list.get(0);
			}
			if(userWallter.getAvailableBalance()<=0) {
				return ResponseData.error("余额不足");
			}
			Double feePrice = sellOtcpDto.getNumber()*ratio/100;
			if(userWallter.getAvailableBalance() <sellOtcpDto.getNumber()) {
				return ResponseData.error("余额不足"); 
			}
			Map<String,Object> payMethodMap = new HashMap<String, Object>();
			payMethodMap.put("type", 3);
			payMethodMap.put("id", payMethod.getPayMethodId());
			List<Map<String,Object>> payMethodList = new ArrayList<Map<String,Object>>();
			payMethodList.add(payMethodMap);
			SellOtcpOrder order  = new SellOtcpOrder();
			String serialNo = otcOrderService.generateSimpleSerialno(ShiroKit.getUser().getId(),2);
			order.setSerialno(serialNo);
			order.setSellerId(0L);
			order.setUserId(ShiroKit.getUser().getId());
			
			order.setNumber(sellOtcpDto.getNumber());
			order.setSupNumber(sellOtcpDto.getNumber()-feePrice);
			order.setFeePrice(feePrice);
			order.setFeeRatio(ratio);
			order.setMinNumber(order.getSupNumber()*priceSetting.getPrice());
			order.setMaxNumber(order.getSupNumber()*priceSetting.getPrice());
			order.setPayMethodIds(JSONObject.toJSONString(payMethodList));
			order.setPrice(priceSetting.getPrice());
			order.setCreateTime(new Date());
			order.setStatus(1);
			if("2".equals(user.getRoleId())) {
				order.setType(3);
				order.setRoleId(2);
			}else {
				order.setType(4);
				order.setRoleId(4);
			}
			order.setTotalPrice(sellOtcpDto.getNumber()*priceSetting.getPrice());
			otcOrderService.addSellOtcpOrder(order);
		
			//更新钱包
			Double outNumber = sellOtcpDto.getNumber();
			AccountUpdateRecord updateRecord = new AccountUpdateRecord();
			updateRecord.setBeforePrice(userWallter.getAvailableBalance());
			logger.info("商户或者代理商自动出售otc，订单号【"+order.getSerialno()+"】，可用余额:"+userWallter.getAvailableBalance()+",冻结余额："+userWallter.getFrozenBalance()+",出售数量:"+order.getSupNumber()+"外加手续费总数量："+outNumber);
			
			userWallter.setFrozenBalance(userWallter.getFrozenBalance()+order.getSupNumber());
			userWallter.setUpdateTime(new Date());
			userWallter.setAvailableBalance(userWallter.getAvailableBalance()-outNumber);
			int result = sellerMapper.updateUserWallter(userWallter);
			if(result <=0) {
				throw new WallterException("出售失败");
			}
			logger.info("处理后商户或者代理商自动出售otc，订单号【"+order.getSerialno()+"】，可用余额:"+userWallter.getAvailableBalance()+",冻结余额："+userWallter.getFrozenBalance()+",出售数量:"+order.getSupNumber()+"外加手续费总数量："+outNumber);
			
			updateRecord.setAfterPrice(userWallter.getAvailableBalance());
			if("2".equals(user.getRoleId())) {
				updateRecord.setSource("商户HC");
				updateRecord.setRoleId(2L);
			}else {
				updateRecord.setSource("代理商HC");
				updateRecord.setRoleId(4l);
			}
			updateRecord.setCode("HC");
			updateRecord.setCreateTime(new Date());
			updateRecord.setPhone(user.getAccountCode());
			updateRecord.setType("手动出售");
			updateRecord.setRemark("手动,将可用余额转冻结余额中");
			updateRecord.setPrice(outNumber);
			updateRecord.setAccountId(user.getUserId());
			updateRecord.setSerialno(order.getSerialno());
			sellerMapper.addAccountUpdateRecord(updateRecord);
			
			UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
			flowRecord.setCode("HC");
			flowRecord.setCreateTime(new Date());
			flowRecord.setPrice(-outNumber);
			flowRecord.setUserId(ShiroKit.getUser().getId());;
			flowRecord.setSource("挂单出售");
			sellerMapper.addUserAccountFlowRecord(flowRecord);
			return ResponseData.success(200,"出售成功",null);
	
	}

	@Transactional
	public ResponseData revocation(Long orderId) {
		SellOtcpOrder order = otcOrderService.findSellerOtcpOrderById(orderId);
		if(order != null) {
			if(order.getStatus() ==2) {
				return ResponseData.error("该订单刚被买家购买");
			}else if(order.getStatus()==3) {
				return ResponseData.error("已撤销过");
			}
			if(order.getType() .equals(3) || order.getType().equals(4)) {//商户或者代理商
				UserWallter userWallter = new UserWallter();
				userWallter.setType(2);
				userWallter.setUserId(order.getUserId());
				List<UserWallter> list =sellerMapper.findUserWallterList(userWallter);
				if(list != null && list.size() >0) {
					userWallter = list.get(0);
					Double returnNumber = order.getSupNumber();
					if(order.getFeePrice() != null && order.getFeePrice() >0) {
						returnNumber =returnNumber+order.getFeePrice();
					}
					
					AccountUpdateRecord updateRecord = new AccountUpdateRecord();
					updateRecord.setBeforePrice(userWallter.getAvailableBalance());
					logger.info("商户或者代理商自动出售otc，订单号【"+order.getSerialno()+"】，可用余额:"+userWallter.getAvailableBalance()+",冻结余额："+userWallter.getFrozenBalance()+",出售数量:"+order.getSupNumber()+"外加手续费总数量变更可用的："+returnNumber);
					
					userWallter.setAvailableBalance(userWallter.getAvailableBalance()+returnNumber);
					userWallter.setFrozenBalance(userWallter.getFrozenBalance()-order.getSupNumber());
					userWallter.setUpdateTime(new Date());
					int result =  sellerMapper.updateUserWallter(userWallter);
					if(result <=0) {
						throw new WallterException("撤销失败");
					}
					
					logger.info("处理后商户或者代理商自动出售otc，订单号【"+order.getSerialno()+"】，可用余额:"+userWallter.getAvailableBalance()+",冻结余额："+userWallter.getFrozenBalance()+",出售数量:"+order.getSupNumber()+"外加手续费总数量变更可用的："+returnNumber);
					
					User user = userMapper.selectById(userWallter.getUserId());
					updateRecord.setAfterPrice(userWallter.getAvailableBalance());
					updateRecord.setCode("HC");
					updateRecord.setCreateTime(new Date());
					updateRecord.setPhone(user.getAccountCode());
					if(order.getType().equals(3)) {
						updateRecord.setSource("商户HC");
						updateRecord.setRoleId(2L);
					}else {
						updateRecord.setSource("代理商HC");
						updateRecord.setRoleId(4L);
					}
					updateRecord.setType("撤销出售");
					updateRecord.setRemark("撤销出售");
					updateRecord.setPrice(returnNumber);
					updateRecord.setAccountId(user.getUserId());
					updateRecord.setSerialno(order.getSerialno());
					sellerMapper.addAccountUpdateRecord(updateRecord);
					
					UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
					flowRecord.setCode("HC");
					flowRecord.setCreateTime(new Date());
					flowRecord.setPrice(returnNumber);
					flowRecord.setUserId(order.getUserId());
					flowRecord.setSource("撤销挂单出售");
					sellerMapper.addUserAccountFlowRecord(flowRecord);
					
					
					order.setStatus(3);
					order.setUpdateTime(new Date());
					otcOrderService.updateSellOtcpOrder(order);
					
					return ResponseData.success(200, "撤销成功", null);
				}
			}
		}
		return ResponseData.error("撤销失败");
	}

	public UserWallter findUserWallter(Long userId) {
		return sellerMapper.findUserWallet(userId);
	}

	
}
