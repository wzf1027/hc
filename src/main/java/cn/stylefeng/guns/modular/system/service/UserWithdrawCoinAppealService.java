package cn.stylefeng.guns.modular.system.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cn.stylefeng.guns.core.util.IpUtil;
import cn.stylefeng.guns.modular.system.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.util.Md5Utils;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.app.service.OtcOrderService;
import cn.stylefeng.guns.modular.system.mapper.UserWithdrawCoinAppealMapper;
import cn.stylefeng.guns.modular.system.mapper.UserWithdrawFeeSettingMapper;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Service
public class UserWithdrawCoinAppealService extends ServiceImpl<UserWithdrawCoinAppealMapper, UserWithdrawCoinAppealOrder> {

	@Resource
	private UserWithdrawCoinAppealMapper appealMapper;
	
	@Resource
	private SellerMapper sellerMapper;
	
	 @Autowired
	 private UserService userService;

	 @Autowired
	   private OtcOrderService  otcOrderService;
	 
	 @Autowired
	 private UserWithdrawFeeSettingMapper feeSettingMapper;

	@Autowired
	private MerchantIpService merchantIpService;


	@Autowired
	private MoneyPasswordSettingMgrService settingService;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String condition,String phone,Long userId, String address, Integer status, String beginTime, String endTime) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition,phone,userId,address,status,beginTime,endTime);
	}

	@Transactional
	public ResponseData updateStatus(Long appealId, Integer status, String password) {
		UserWithdrawCoinAppealOrder order = this.baseMapper.selectById(appealId);
		if(order != null && order.getStatus()==1) {
			User user = userService.getById(order.getUserId());
			if(user == null) {
				return ResponseData.error("不存在该钱包");
			}
			UserWallter userWallter = new UserWallter();
			userWallter.setType(1);
			userWallter.setUserId(user.getUserId());
			List<UserWallter> list = sellerMapper.findUserWallterList(userWallter);
			if(list == null || list.size() <=0) {
				return ResponseData.error("不存在该钱包");
			}
			userWallter = list.get(0);
//			if (StringUtils.isBlank(password)){
//				return  ResponseData.error("请输入二级密码");
//			}
//			MoneyPasswordSetting setting = settingService.getOne(null);
//			if(!setting.getPassword().equals(Md5Utils.GetMD5Code(password))) {
//				return ResponseData.error("输入二级密码有误");
//			}
			if(status ==1) {//审核通过	
				order.setStatus(2);
				order.setUpdateTime(new Date());
				order.setUpdateUserId(ShiroKit.getUser().getId());
				order.setUserAccount(ShiroKit.getUser().getAccount());
				this.baseMapper.updateById(order);
				
				AccountUpdateRecord updateRecord = new AccountUpdateRecord();
				updateRecord.setBeforePrice(userWallter.getFrozenBalance());
				
				userWallter.setFrozenBalance(userWallter.getFrozenBalance()-order.getNumber());
				userWallter.setUpdateTime(new Date());
				int result = sellerMapper.updateUserWallter(userWallter);
				if(result <=0) {
					throw new WallterException("审核失败");
				}
				updateRecord.setAfterPrice(userWallter.getFrozenBalance());
				updateRecord.setCode("USDT");
				updateRecord.setCreateTime(new Date());
				updateRecord.setPhone(user.getAccountCode());
				if("2".equals(user.getRoleId())) {
					updateRecord.setSource("商户USDT");
					updateRecord.setRoleId(2L);
				}else {
					updateRecord.setSource("代理商USDT");
					updateRecord.setRoleId(4L);
				}
				updateRecord.setType("提币");
				updateRecord.setRemark("提币，扣除冻结余额的数量");
				updateRecord.setPrice(-order.getNumber());
				updateRecord.setAccountId(user.getUserId());
				updateRecord.setSerialno(order.getSerialno());
				sellerMapper.addAccountUpdateRecord(updateRecord);
				
				UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
				flowRecord.setCode("USDT");
				flowRecord.setCreateTime(new Date());
				flowRecord.setPrice(-(order.getNumber()));
				flowRecord.setUserId(order.getUserId());
				flowRecord.setSource("提币扣除");
				sellerMapper.addUserAccountFlowRecord(flowRecord);
				return ResponseData.success();
			}else {
				order.setStatus(3);
				order.setUpdateTime(new Date());
				order.setUpdateUserId(ShiroKit.getUser().getId());
				order.setUserAccount(ShiroKit.getUser().getAccount());
				this.baseMapper.updateById(order);
				
				AccountUpdateRecord updateRecord = new AccountUpdateRecord();
				updateRecord.setBeforePrice(userWallter.getAvailableBalance());
				
				userWallter.setAvailableBalance(userWallter.getAvailableBalance()+order.getNumber());
				userWallter.setFrozenBalance(userWallter.getFrozenBalance()-order.getNumber());
				userWallter.setUpdateTime(new Date());
				int result = sellerMapper.updateUserWallter(userWallter);
				if(result <=0) {
					throw new WallterException("审核失败");
				}
				updateRecord.setAfterPrice(userWallter.getAvailableBalance());
				updateRecord.setCode("USDT");
				updateRecord.setCreateTime(new Date());
				updateRecord.setPhone(user.getAccountCode());
				if("2".equals(user.getRoleId())) {
					updateRecord.setSource("商户USDT");
					updateRecord.setRoleId(2L);
				}else {
					updateRecord.setSource("代理商USDT");
					updateRecord.setRoleId(4L);
				}
				updateRecord.setType("提币");
				updateRecord.setRemark("提币审核失败，冻结余额的数量转移可用余额");
				updateRecord.setPrice(order.getNumber());
				updateRecord.setAccountId(user.getUserId());
				updateRecord.setSerialno(order.getSerialno());
				sellerMapper.addAccountUpdateRecord(updateRecord);
				
				return ResponseData.success();
			}
		}
		return ResponseData.error("审核失败");
	}

	@Transactional
	public Object sumbitWithdrawAppeal(String tradePwd, Double number, String address, HttpServletRequest request) {
		  Long userId =  ShiroKit.getUser().getId();
		   User user =  userService.getById(userId);
		   if(user == null) {
			   throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
		   }
		   if(StringUtils.isBlank(tradePwd)) {
			   return ResponseData.error("交易密码不能为空");
		   }
		   if(StringUtils.isBlank(user.getTradePassword())) {
			   return ResponseData.error("未设置交易密码");
		   }
		   if(!user.getTradePassword().equals(Md5Utils.GetMD5Code(tradePwd))) {
			   return ResponseData.error("输入的交易密码有误");
		   }
		   if(StringUtils.isBlank(address)) {
			   return ResponseData.error("提币地址不能为空");
		   }
		   if(number == null || number <=0) {
			   return ResponseData.error("数量不能小于0");
		   }
		   String roleId =null;
		   if("2".equals(user.getRoleId())){
		    	String ipAddress = IpUtil.getIpAddress(request);
			   MerchantIp merchantIp = new MerchantIp();
			   merchantIp.setIpAddress(ipAddress);
			   merchantIp.setUserId(ShiroKit.getUser().getId());
			   merchantIp.setType(1);
			   int ipCount = merchantIpService.count(new QueryWrapper<>(merchantIp));
			   if (ipCount <=0){
				   return ResponseData.error("暂时无法提现");
			   }
			   roleId = "2";
		   }else {
			   roleId = "4";
		   }
		   UserWallter userWallter = new UserWallter();
			userWallter.setType(1);
			userWallter.setUserId(ShiroKit.getUser().getId());
			List<UserWallter> list = sellerMapper.findUserWallterList(userWallter);
			if(list == null || list.size() <=0) {
				return ResponseData.error("不存在该钱包");
			}
			userWallter = list.get(0);
			if(userWallter.getAvailableBalance() <=0) {
				return ResponseData.error("余额不足");
			}
			Map<String,Object> columnMap = new HashMap<String, Object>();
			columnMap.put("ROLE_ID", roleId);
			List<UserWithdrawFeeSetting>  feeSettingList = feeSettingMapper.selectByMap(columnMap);
			Double feePrice =0.0;
			if(feeSettingList != null && feeSettingList.size()>0) {
				UserWithdrawFeeSetting feeSetting = feeSettingList.get(0);
				if(feeSetting != null) {
					if(feeSetting.getMinNumber() != null &&  feeSetting.getMinNumber() > number) {
						return ResponseData.error("提现数量不能小于"+feeSetting.getMinNumber());
					}
					if(feeSetting.getMaxNumber() != null &&  feeSetting.getMaxNumber() < number) {
						return ResponseData.error("提现数量不能大于"+feeSetting.getMaxNumber());
					}
					if(feeSetting.getStartRatioNumber() != null && feeSetting.getStartRatioNumber() >0 && number>feeSetting.getStartRatioNumber()) {
						feePrice = feeSetting.getFeeRatio()*number/100;
					}else {
						feePrice = feeSetting.getMinFeeNumber();
					}
				}
			}
			
			if(userWallter.getAvailableBalance() < number) {
				return ResponseData.error("余额不足");
			}
			if(number-feePrice <=0) {
				return ResponseData.error("提币失败");
			}
		   UserWithdrawCoinAppealOrder order = new UserWithdrawCoinAppealOrder();
		   order.setCreateTime(new Date());
		   order.setAddress(address);
		   order.setNumber(number);
		   order.setStatus(1);
		   order.setUserId(user.getUserId());
		   order.setSerialno(otcOrderService.generateSimpleSerialno(user.getUserId(), 7));
		   order.setFeePrice(feePrice);
		   order.setTotalNumber(number-feePrice);
		   order.setRoleId(roleId);
			USDTOtcpExchange exchage = sellerMapper.getOTCMarkInfo();
			if (exchage != null){
				order.setExChangeRatio(exchage.getValue());
			}else{
				order.setExChangeRatio(1.0);
			}

		   this.appealMapper.insert(order);
		   
			AccountUpdateRecord updateRecord = new AccountUpdateRecord();
			updateRecord.setBeforePrice(userWallter.getAvailableBalance()); 
			
			userWallter.setAvailableBalance(userWallter.getAvailableBalance()-number);
			userWallter.setFrozenBalance(userWallter.getFrozenBalance()+number);
			userWallter.setUpdateTime(new Date());
			int result = sellerMapper.updateUserWallter(userWallter);
			if(result <=0) {
				throw new WallterException("审核失败");
			}
			
			updateRecord.setAfterPrice(userWallter.getAvailableBalance());
			updateRecord.setCode("USDT");
			updateRecord.setCreateTime(new Date());
			updateRecord.setPhone(user.getAccountCode());
			if("2".equals(user.getRoleId())) {
				updateRecord.setSource("商户USDT");
				updateRecord.setRoleId(2L);
			}else {
				updateRecord.setSource("代理商USDT");
				updateRecord.setRoleId(4L);
			}
			updateRecord.setType("提币");
			updateRecord.setRemark("提币，将提币的数量转移冻结余额");
			updateRecord.setPrice(-number);
			updateRecord.setAccountId(user.getUserId());
			updateRecord.setSerialno(order.getSerialno());
			sellerMapper.addAccountUpdateRecord(updateRecord);
			
	       return ResponseData.success();
	}
	
}
