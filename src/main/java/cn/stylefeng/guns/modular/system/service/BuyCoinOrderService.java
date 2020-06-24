package cn.stylefeng.guns.modular.system.service;

import java.io.IOException;
import java.util.*;
import javax.annotation.Resource;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.util.FlowRecordConstant;
import cn.stylefeng.guns.core.util.HttpClientUtil;
import cn.stylefeng.guns.core.util.HttpUtil;
import cn.stylefeng.guns.modular.app.service.OtcOrderService;
import cn.stylefeng.guns.modular.system.entity.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.util.Md5Utils;
import cn.stylefeng.guns.modular.app.mapper.OtcOrderMapper;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.system.mapper.BuyCoinOrderMapper;
import cn.stylefeng.guns.modular.system.mapper.UserMapper;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import org.springframework.util.DigestUtils;

@Service
public class BuyCoinOrderService extends ServiceImpl<BuyCoinOrderMapper, SellerBuyerCoinOrder> {

	
	private Logger logger = LoggerFactory.getLogger(BuyCoinOrderService.class);
	
	@Resource
	private BuyCoinOrderMapper buyCoinOrderMapper;
	
	@Resource
	private OtcOrderMapper orderMapper;
	
	@Resource
	private SellerMapper sellerDao;
	
	@Resource
	private UserMapper userMapper;

	@Resource
	private PromotionRateService promotionRateService;

	@Resource
	private MoneyPasswordSettingMgrService settingService;

	@Resource
	private OtcOrderService otcOrderService;


	@Value("${platform.DOMAIN}")
	private String domain;
	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String serialno, String account, String beginTime, String endTime, Long userId, Integer status, String userOrderNo, String seller, Integer isAppeal, Integer isSuccess, Integer payMethodType, String payMethodAccount, String payMethodName,Integer orderCode, String remark) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, serialno,account,beginTime,endTime,userId,status,userOrderNo,seller,isAppeal,isSuccess,payMethodType,payMethodAccount,payMethodName,orderCode,remark);
	}

	public ResponseData buyCoinOrderService(Long orderId,String password) {
		if (StringUtils.isBlank(password)){
			return  ResponseData.error("请输入二级密码");
		}
		MoneyPasswordSetting setting = settingService.getOne(null);
		if(!setting.getPassword().equals(Md5Utils.GetMD5Code(password))) {
			return ResponseData.error("输入二级密码有误");
		}
		SellerBuyerCoinOrder order = this.baseMapper.selectById(orderId);
		if(order != null && order.getStatus() <=2) {
			order.setIsAppeal(1);
			order.setUpdateTime(new Date());
			order.setRemark("平台");
			order.setDealer(ShiroKit.getUser().getAccount());
			this.baseMapper.updateById(order);
			return ResponseData.success();
		}
		return ResponseData.error("申诉失败");
	}

	@Transactional
	public ResponseData confirmUpdateStatus(Long orderId, String password) throws Exception {
		if (StringUtils.isBlank(password)){
			return  ResponseData.error("请输入二级密码");
		}
		MoneyPasswordSetting setting = settingService.getOne(null);
		if(!setting.getPassword().equals(Md5Utils.GetMD5Code(password))) {
			return ResponseData.error("输入二级密码有误");
		}
		SellerBuyerCoinOrder order = this.baseMapper.selectById(orderId);
		if(order != null 
				&&( order.getStatus()==1 ||  order.getStatus()==2) 
				&& order.getIsAppeal() ==1) {
			//申诉审核前，校验冻结余额是否充足
			SellerWallter sellerWallter_ = new SellerWallter();
			sellerWallter_.setCode("HC");
			sellerWallter_.setSellerId(order.getSellerId());
			List<SellerWallter> wallterList_ =sellerDao.findSellerWallter(sellerWallter_);
			if(wallterList_ != null && wallterList_.size() >0) {
				sellerWallter_ = wallterList_.get(0);
				if(sellerWallter_.getFrozenBalance()<order.getNumber()){
					return ResponseData.error("冻结余额不足！");
				}
				
			}
   			 //查询支付通道
   			 Double feeRatio =0.0;
   			 Double agentFeeRatio = 0.0;
   			 UserPayMethodFeeSetting feeSetting = sellerDao.findUserPayMethodFeeSettingByUserId(order.getBuyerId());
   			 if(feeSetting != null) {
   				 Double number =order.getNumber();
				 if ((order.getPayMethodType().equals(1)
						 || order.getPayMethodType().equals(5)
						 || order.getPayMethodType().equals(7) )&& feeSetting.getAlipayRatio() != null
						 && feeSetting.getAlipayRatio() > 0) {
					 feeRatio = feeSetting.getAlipayRatio();
					 number = order.getNumber() * (1 - feeSetting.getAlipayRatio() / 100);
				 } else if ((order.getPayMethodType().equals(2)
						 || order.getPayMethodType().equals(6)
						 || order.getPayMethodType().equals(8)
						 || order.getPayMethodType().equals(9)
				 )&& feeSetting.getWxRatio() != null
						 && feeSetting.getWxRatio() > 0) {
					 feeRatio = feeSetting.getWxRatio();
					 number = order.getNumber() * (1 - feeSetting.getWxRatio() / 100);
				 } else if (order.getPayMethodType().equals(3) && feeSetting.getCardRatio() != null
						 && feeSetting.getCardRatio() > 0) {
					 feeRatio = feeSetting.getCardRatio();
					 number = order.getNumber() * (1 - feeSetting.getCardRatio() / 100);
				 }else if (order.getPayMethodType().equals(4)  && feeSetting.getCloudPayRatio()!=null && feeSetting.getCloudPayRatio() >0){
					 feeRatio = feeSetting.getCloudPayRatio();
					 number = order.getNumber() * (1 - feeSetting.getCloudPayRatio() / 100);
				 }
   				 if(number >0) { 
	   					User merchantUser = new User();
	   					merchantUser.setUserId(order.getBuyerId());
	   					merchantUser = sellerDao.findUserOne(merchantUser);	 
	   					UserWallter userWallter = new UserWallter();
     					userWallter.setUserId(merchantUser.getUserId());
     					userWallter.setType(2);
     					List<UserWallter> userWallterList =sellerDao.findUserWallterList(userWallter);
     					if(userWallterList != null && userWallterList.size()>0) {//商户返利

							// 传递给商家那边
							Map<String, String> params = new HashMap<String, String>();
							params.put("cuid", order.getCuid());
							params.put("uid", merchantUser.getAccountCode()+ "");
							params.put("price", order.getNumber() + "");
							params.put("cuid", order.getCuid());
							params.put("user_order_no", order.getUserOrderNo());
							params.put("paytype", order.getPayMethodType() + "");
							params.put("orderno", order.getSerialno());
							params.put("realprice", order.getNumber() + "");
							String sbf = Md5Utils.getSign(params);
							sbf = sbf + "key=" + merchantUser.getAppSecret();
							logger.info("sign之前的：" + sbf);
							String sign = DigestUtils.md5DigestAsHex(sbf.getBytes()).toLowerCase();
							params.put("sign", sign);
							logger.info("sign之后：" + sign);
							logger.info(JSONObject.toJSONString(params));
							String notifyResult = null;
							String localhostNotifyUrl = domain+"/app/buyCoin/notifyNotice";
							if (localhostNotifyUrl.equals(order.getNotifyUrl())){
								notifyResult = "success";
							}else{
								for (int i = 0; i < 3; i++) {
									try {
										notifyResult = HttpUtil.postForm(order.getNotifyUrl(), params);
									} catch (IOException e) {
										e.printStackTrace();
									}
									logger.info("result:" + notifyResult);
									notifyResult = notifyResult.replaceAll("\"","");
									if ("success".equals(notifyResult)) {
										break;
									}
								}
							}
							logger.info("最后返回的结果为"+notifyResult);
							if (!"success".equals(notifyResult)){
								return ResponseData.success(200,"需等待平台确认",null);
							}

     						userWallter = userWallterList.get(0);
     						logger.info("交易订单确认收款商户返利余额，订单号【"+order.getSerialno()+"】，返利前，用户【"
									+userWallter.getUserId()+"】,可用余额:"
									+userWallter.getAvailableBalance()
									+",冻结余额："+userWallter.getFrozenBalance()+",返利数量:"+number);
     						
     						AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(userWallter.getAvailableBalance());
							
     						userWallter.setAvailableBalance(userWallter.getAvailableBalance()+number);
     						userWallter.setTotalBalance(userWallter.getTotalBalance()+number);
     						userWallter.setUpdateTime(new Date());
     						int resultNumber = sellerDao.updateUserWallter(userWallter);
     						if(resultNumber <=0) {
     							throw new WallterException("确认失败");
     						}
     						updateRecord.setAfterPrice(userWallter.getAvailableBalance());
							updateRecord.setCode("HC");
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(merchantUser.getAccountCode());
							updateRecord.setSource("商户HC");
							updateRecord.setType("接单交易");
							updateRecord.setRemark("接单交易");
							updateRecord.setPrice(number);
							updateRecord.setRoleId(2L);
							updateRecord.setPayMethodType(order.getPayMethodType());
							updateRecord.setAccountId(userWallter.getUserId());
							updateRecord.setSerialno(order.getSerialno());
							sellerDao.addAccountUpdateRecord(updateRecord);
							logger.info("交易订单确认收款商户返利余额，订单号【"+order.getSerialno()+"】，返利后，用户【"
									+userWallter.getUserId()+"】,可用余额:"
									+userWallter.getAvailableBalance()
									+",冻结余额："+userWallter.getFrozenBalance()+",返利数量:"+number);
     						
     						UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
     						flowRecord.setCode("HC");
     						flowRecord.setUserId(merchantUser.getUserId());
     						flowRecord.setSource("交易返额");
     						flowRecord.setPrice(number);
     						flowRecord.setCreateTime(new Date());
     						flowRecord.setSerialno(order.getSerialno());
     						sellerDao.addUserAccountFlowRecord(flowRecord);
     					}	
     					
     					 Double bonusNumber = 0.0;
       					 UserRecommendRelation relation = new UserRecommendRelation();
       					 relation.setUserId(merchantUser.getUserId());
       					 relation= sellerDao.findUserRecommendRelation(relation);
     						//代理商分红
     						if(relation != null) {
     							UserBonusSetting bonusSetting = new UserBonusSetting();
         						bonusSetting.setAgentId(relation.getRecommendId());
         						bonusSetting.setUserId(merchantUser.getUserId());
         						bonusSetting = sellerDao.findUserBonusSetting(bonusSetting);
         						if(bonusSetting !=null) {
         							if(order.getPayMethodType().equals(1) 
         	        						&& feeSetting.getAlipayRatio() != null 
         	        						&& feeSetting.getAlipayRatio() >0) {
         								agentFeeRatio = bonusSetting.getAlipayRatio();
         								bonusNumber = order.getNumber()*bonusSetting.getAlipayRatio()/100;
         	        				 }else if(order.getPayMethodType().equals(2)
         	        						 && feeSetting.getWxRatio() != null 
         	         						&& feeSetting.getWxRatio() >0) {
         	        					agentFeeRatio = bonusSetting.getWxRatio();
         	        					bonusNumber = order.getNumber()*bonusSetting.getWxRatio()/100;
         	        				 }else if(order.getPayMethodType().equals(3)
         	        						 && feeSetting.getCardRatio() != null 
         	         						&& feeSetting.getCardRatio() >0) {
         	        					agentFeeRatio = bonusSetting.getCardRatio();
         	        					bonusNumber = order.getNumber()*bonusSetting.getCardRatio()/100;
         	        				 } 
         							if(bonusNumber >0) {
         								UserWallter agentWallter = new UserWallter();
             							agentWallter.setUserId(relation.getRecommendId());
             							agentWallter.setType(2);
             							List<UserWallter> agentWallterList = sellerDao.findUserWallterList(agentWallter);
             							if(agentWallterList != null && agentWallterList.size()>0) {
             								agentWallter = agentWallterList.get(0);
             								logger.info("交易订单确认收款代理商返利余额，订单号【"+order.getSerialno()+"】，返利前，用户【"
													+agentWallter.getUserId()+"】,可用余额:"
													+agentWallter.getAvailableBalance()
													+",冻结余额："+agentWallter.getFrozenBalance()+",返利数量:"+bonusNumber);
             								AccountUpdateRecord updateRecord = new AccountUpdateRecord();
											updateRecord.setBeforePrice(agentWallter.getAvailableBalance());
											
             								
             								agentWallter.setAvailableBalance(agentWallter.getAvailableBalance()+bonusNumber);
             								agentWallter.setTotalBalance(agentWallter.getTotalBalance()+bonusNumber);
             								agentWallter.setUpdateTime(new Date());
                     						int resultNumber = sellerDao.updateUserWallter(agentWallter);
                     						if(resultNumber <=0) {
                     							throw new WallterException("确认失败");
                     						}
                     						
                     						User agentUser = userMapper.selectById(agentWallter.getUserId());
											updateRecord.setSerialno(order.getSerialno());
											updateRecord.setAccountId(agentWallter.getUserId());
											updateRecord.setAfterPrice(agentWallter.getAvailableBalance());
											updateRecord.setCode("HC");
											updateRecord.setCreateTime(new Date());
											updateRecord.setPhone(agentUser.getAccountCode());
											updateRecord.setSource("代理商HC");
											updateRecord.setType("交易返利");
											updateRecord.setRemark("交易返利");
											updateRecord.setPayMethodType(order.getPayMethodType());
											updateRecord.setPrice(bonusNumber);
											updateRecord.setRoleId(4L);
											sellerDao.addAccountUpdateRecord(updateRecord);
											logger.info("交易订单确认收款代理商返利余额，订单号【"+order.getSerialno()+"】，返利后，用户【"
													+agentWallter.getUserId()+"】,可用余额:"
													+agentWallter.getAvailableBalance()
													+",冻结余额："+agentWallter.getFrozenBalance()+",返利数量:"+bonusNumber);
											
											
                     						UserAccountFlowRecord agentflowRecord = new UserAccountFlowRecord();
                     						agentflowRecord.setCode("HC");
                     						agentflowRecord.setUserId(relation.getRecommendId());
                     						agentflowRecord.setSource("交易挖矿");
                     						agentflowRecord.setPrice(bonusNumber);
                     						agentflowRecord.setSerialno(order.getSerialno());
                     						agentflowRecord.setCreateTime(new Date());
                     						sellerDao.addUserAccountFlowRecord(agentflowRecord);
             							}
         							}
         						}
     						}
         					//会员获奖
     						Double awardNumber =0.0;
         					SellerAwardSetting awardSetting = sellerDao.getOneSellerAwardSetting();
         					Seller seller = sellerDao.findSellerbyId(order.getSellerId());
         					if(awardSetting != null && awardSetting.getValue() != null && awardSetting.getValue() >0 ) {
         						awardNumber = (order.getNumber()-number-bonusNumber)*awardSetting.getValue()/100;
         						if(awardNumber >0) {
         							SellerProfitWallter profitWallter = new SellerProfitWallter();
         							profitWallter.setCode("1");
         							profitWallter.setSellerId(order.getSellerId());
         							List<SellerProfitWallter> profitWallterList = sellerDao.findSellerProfitWallterList(profitWallter);
         							if(profitWallterList != null && profitWallterList.size()>0) {
         								profitWallter = profitWallterList.get(0);
         								
         								logger.info("交易订单确认收款会员挖矿返利余额，订单号【"+order.getSerialno()+"】，返利前，用户【"
												+profitWallter.getSellerId()+"】,可用余额:"
												+profitWallter.getAvailableBalance()
												+",冻结余额："+profitWallter.getFrozenBalance()+",返利数量:"+awardNumber);
										
										AccountUpdateRecord updateRecord = new AccountUpdateRecord();
										updateRecord.setBeforePrice(profitWallter.getAvailableBalance());
         								
         								profitWallter.setAvailableBalance(profitWallter.getAvailableBalance()+awardNumber);
         								profitWallter.setTotalBalance(profitWallter.getTotalBalance()+awardNumber);
         								profitWallter.setUpdateTime(new Date());
         								int resultNumber = sellerDao.updateSellerProfitWallter(profitWallter);
         								if(resultNumber <=0) {
         									throw new WallterException("确认失败");
         								}
         								updateRecord.setAfterPrice(profitWallter.getAvailableBalance());
										updateRecord.setCode("HC");
										updateRecord.setCreateTime(new Date());
										updateRecord.setPhone(seller.getAccount());
										updateRecord.setSource("会员挖矿");
										updateRecord.setType("接单交易");
										updateRecord.setRemark("接单交易");
										updateRecord.setPrice(awardNumber);
										updateRecord.setRoleId(1L);
										updateRecord.setPayMethodType(order.getPayMethodType());
										updateRecord.setAccountId(profitWallter.getSellerId());
										updateRecord.setSerialno(order.getSerialno());
										sellerDao.addAccountUpdateRecord(updateRecord);
										logger.info("交易订单确认收款会员挖矿返利余额，订单号【"+order.getSerialno()+"】，返利后，用户【"
												+profitWallter.getSellerId()+"】,可用余额:"
												+profitWallter.getAvailableBalance()
												+",冻结余额："+profitWallter.getFrozenBalance()+",返利数量:"+awardNumber);
										
         								
         								SellerProfitAccountFlowRecord profitAccountFlowRecord = new SellerProfitAccountFlowRecord();
         								profitAccountFlowRecord.setCode("1");
         								profitAccountFlowRecord.setCreateTime(new Date());
         								profitAccountFlowRecord.setPrice(awardNumber);
         								profitAccountFlowRecord.setSellerId(order.getSellerId());
         								profitAccountFlowRecord.setSource("交易挖矿");
         								profitAccountFlowRecord.setSerialno(order.getSerialno());
         								sellerDao.addSellerProfitFlowRecord(profitAccountFlowRecord);
         							}
         						}
         					}
         					
         						
         						SellerWallter sellerWallter = new SellerWallter();
         						sellerWallter.setCode("HC");
         						sellerWallter.setSellerId(order.getSellerId());
         						List<SellerWallter> wallterList =sellerDao.findSellerWallter(sellerWallter);
         						if(wallterList != null && wallterList.size() >0) {
         							sellerWallter = wallterList.get(0);
         							logger.info("交易订单确认收款扣除冻结余额,订单号【"+order.getSerialno()+"】，扣除前，用户【"
    										+sellerWallter.getSellerId()+"】,可用余额:"
    										+sellerWallter.getAvailableBalance()
    										+",冻结余额："+sellerWallter.getFrozenBalance()+",扣除数量为:"+order.getNumber());
         							if(sellerWallter.getFrozenBalance()<0 
         									|| sellerWallter.getFrozenBalance()-order.getNumber() <0) {
         								return ResponseData.error("确认失败"); 
         							}
         							
         							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
									updateRecord.setBeforePrice(sellerWallter.getFrozenBalance());
         							
         							sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-order.getNumber());
         							sellerWallter.setUpdateTime(new Date());
         							int reslut = sellerDao.updateSellerWallter(sellerWallter);
         							if(reslut <=0) {
         								throw new WallterException("确认失败");
         							}
         							updateRecord.setAfterPrice(sellerWallter.getFrozenBalance());
									updateRecord.setCode("HC");
									updateRecord.setCreateTime(new Date());
									updateRecord.setPhone(seller.getAccount());
									updateRecord.setSource("会员HC");
									updateRecord.setType("接单交易");
									updateRecord.setRemark("接单交易");
									updateRecord.setPrice(-order.getNumber());
									updateRecord.setRoleId(1L);
									updateRecord.setPayMethodType(order.getPayMethodType());
									updateRecord.setAccountId(order.getSellerId());
									updateRecord.setSerialno(order.getSerialno());
									sellerDao.addAccountUpdateRecord(updateRecord);
         							
         							logger.info("交易订单确认收款扣除冻结余额,订单号【"+order.getSerialno()+"】，扣除后，用户【"
    										+sellerWallter.getSellerId()+"】,可用余额:"
    										+sellerWallter.getAvailableBalance()
    										+",冻结余额："+sellerWallter.getFrozenBalance()+",扣除数量为:"+order.getNumber());
         							
         							SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
         							flowRecord.setCode("HC");
         							flowRecord.setCreateTime(new Date());
         							flowRecord.setPrice(-order.getNumber());
         							flowRecord.setSellerId(order.getSellerId());
         							flowRecord.setSource("交易扣除");
         							flowRecord.setSerialno(order.getSerialno());
         							sellerDao.addSellerAccountFlowRecord(flowRecord);
         							
         						}
         						
         						
         				   		 
         				   		order.setStatus(4);
 								order.setIsSuccess(1);
         						order.setUpdateTime(new Date());
         						order.setCloseTime(new Date());
         						order.setIntoNumber(number);
         						order.setFeePrice(feeRatio);
         						order.setAgentFeePrice(agentFeeRatio);
         						order.setBonusNumber(awardNumber<0?0:awardNumber);
         						order.setRemark("平台");
					 			order.setDealer(ShiroKit.getUser().getAccount());
         						this.baseMapper.updateById(order);

					 SellerPayMethod payMethod = sellerDao.findSellerPayMethodById(order.getPayMethodId());
					 payMethod.setFailNumber(0);
					 sellerDao.updateSellerPayMethod(payMethod);


					 //直推返利
					 if (StringUtils.isNotBlank(seller.getReferceIds())){
						 String[] pIds =  seller.getReferceIds().split(",");
						 Integer rateType = 0;
						 if(order.getPayMethodType().equals(1)
								 || order.getPayMethodType().equals(5)
								 || order.getPayMethodType().equals(7) ){
							 rateType = 1;
						 }else if(order.getPayMethodType().equals(2)
								 || order.getPayMethodType().equals(6)
								 || order.getPayMethodType().equals(8)
								 || order.getPayMethodType().equals(9)){
							 rateType = 2;
						 }else if(order.getPayMethodType().equals(3)){
							 rateType=3;
						 }else{
							 rateType=4;
						 }
						 List<PromotionRate> promotionList =  promotionRateService.selectListByLeveAsc(rateType);
						 if(promotionList != null && promotionList.size()>0){
							 int size = promotionList.size()-pIds.length >0?pIds.length:promotionList.size();
							 for (int i=0;i<size;i++){
								 Double rebateValue =0.0;
								 SellerProfitWallter profitWallter = new SellerProfitWallter();
								 profitWallter.setCode("3");
								 profitWallter.setSellerId(Long.valueOf(pIds[i]));
								 List<SellerProfitWallter> list = sellerDao.findSellerProfitWallterList(profitWallter);
								 if (list != null && list.size() > 0) {
									 profitWallter = list.get(0);
									 rebateValue = order.getNumber() *promotionList.get(i).getBonusRatio() / 100;

									 Seller sell = this.sellerDao.findSellerbyId(profitWallter.getSellerId());


									 AccountUpdateRecord updateRecord3 = new AccountUpdateRecord();
									 updateRecord3.setBeforePrice(profitWallter.getAvailableBalance());
									 profitWallter
											 .setAvailableBalance(profitWallter.getAvailableBalance() + rebateValue);
									 profitWallter.setTotalBalance(profitWallter.getTotalBalance() + rebateValue);
									 profitWallter.setUpdateTime(new Date());
									 int result = sellerDao.updateSellerProfitWallter(profitWallter);
									 if (result <= 0) {
										 throw new WallterException("确认收款失败");
									 }

									 updateRecord3.setAfterPrice(profitWallter.getAvailableBalance());
									 updateRecord3.setCode("HC");
									 updateRecord3.setCreateTime(new Date());
									 updateRecord3.setPhone(sell.getAccount());
									 updateRecord3.setSource("推荐HC");
									 updateRecord3.setType("推荐挖矿");
									 updateRecord3.setRemark("推荐挖矿");
									 updateRecord3.setPrice(rebateValue);
									 updateRecord3.setRoleId(1l);
									 updateRecord3.setSerialno(order.getSerialno());
									 updateRecord3.setAccountId(profitWallter.getSellerId());
									 sellerDao.addAccountUpdateRecord(updateRecord3);

									 // 流水记录
									 SellerProfitAccountFlowRecord profitFlowRecord = new SellerProfitAccountFlowRecord();
									 profitFlowRecord.setCode(profitWallter.getCode());
									 profitFlowRecord.setCreateTime(new Date());
									 profitFlowRecord.setPrice(rebateValue);
									 profitFlowRecord.setSellerId(sell.getSellerId());
									 profitFlowRecord.setUserId(sell.getUserId());
									 profitFlowRecord.setSource("推荐返利");
									 sellerDao.addSellerProfitFlowRecord(profitFlowRecord);
								 }
							 }
						 }
					 }
                	 return ResponseData.success(200,"确认收款成功",null);
         				}
     			}
     		}
			return ResponseData.error("确认失败");
	}

	@Transactional
	public ResponseData noConfirmUpdateStatus(Long orderId, String password) {
		if (StringUtils.isBlank(password)){
			return  ResponseData.error("请输入二级密码");
		}
		MoneyPasswordSetting setting = settingService.getOne(null);
		if(!setting.getPassword().equals(Md5Utils.GetMD5Code(password))) {
			return ResponseData.error("输入二级密码有误");
		}
		//拒绝收款，就是买家没有支付，退回卖家的钱包中
		SellerBuyerCoinOrder order = this.baseMapper.selectById(orderId);
		if(order != null 
				&& (order.getStatus()==2 || order.getStatus() ==1) 
				&& order.getIsAppeal() ==1) {
			//退回钱包中,先判断是否有接单中
			SellerOrder sellerOrder =orderMapper.findSellerorderBySellerId(order.getSellerId());
			if(sellerOrder != null) {
				logger.info("申诉冻结，拒绝交易，退回余额,订单号【"+order.getSerialno()+"】，扣除前扣除数量为:"+order.getNumber()+",挂单数量为"+order.getNumber());
				sellerOrder.setNumber(sellerOrder.getNumber()+order.getNumber());
				sellerOrder.setUpdateTime(new Date());
				int result = orderMapper.updateSellerOrder(sellerOrder);
				if(result <=0) {
					throw new WallterException("更新失败");
				}
				logger.info("申诉冻结，拒绝交易，退回余额,订单号【"+order.getSerialno()+"】，退回数量为:"+sellerOrder.getNumber());
			}else {
				//退回钱包中
				SellerWallter sellerWallter = new SellerWallter();
				sellerWallter.setSellerId(order.getSellerId());
				sellerWallter.setCode("HC");
				List<SellerWallter> wallterList = sellerDao.findSellerWallter(sellerWallter);
				if(wallterList != null && wallterList.size() >0) {
					sellerWallter = wallterList.get(0);
					if(sellerWallter.getFrozenBalance()<=0){
						return ResponseData.error("拒绝失败,冻结余额为0");
					}
					if(sellerWallter.getFrozenBalance()<order.getNumber()){
						return ResponseData.error("拒绝失败,冻结余额小于订单金额");
					}
					logger.info("申诉冻结，拒绝交易，退回余额,订单号【"+order.getSerialno()+"】，扣除前，用户【"
							+sellerWallter.getSellerId()+"】,可用余额:"
							+sellerWallter.getAvailableBalance()
							+",冻结余额："+sellerWallter.getFrozenBalance()+",扣除数量为:"+order.getNumber());
					
					AccountUpdateRecord updateRecord = new AccountUpdateRecord();
					updateRecord.setBeforePrice(sellerWallter.getFrozenBalance());
					if(sellerWallter.getFrozenBalance()-order.getNumber() < 0) {
						throw new WallterException("处理失败");
					}
					sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-order.getNumber());				
					sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()+order.getNumber());
					int reslut = sellerDao.updateSellerWallter(sellerWallter);
					if(reslut <=0) {
						throw new WallterException("处理失败");
					}
					Seller seller = sellerDao.findSellerbyId(order.getSellerId());
					updateRecord.setAfterPrice(sellerWallter.getFrozenBalance());
					updateRecord.setCode("HC");
					updateRecord.setCreateTime(new Date());
					updateRecord.setPhone(seller.getAccount());
					updateRecord.setSource("会员HC");
					updateRecord.setType("接单交易");
					updateRecord.setRemark("接单交易,申诉冻结，拒绝收款");
					updateRecord.setPrice(order.getNumber());
					updateRecord.setRoleId(1L);
					updateRecord.setPayMethodType(order.getPayMethodType());
					updateRecord.setAccountId(order.getSellerId());
					updateRecord.setSerialno(order.getSerialno());
					sellerDao.addAccountUpdateRecord(updateRecord);
					
					logger.info("申诉冻结，拒绝交易，退回余额,订单号【"+order.getSerialno()+"】，扣除后，用户【"
							+sellerWallter.getSellerId()+"】,可用余额:"
							+sellerWallter.getAvailableBalance()
							+",冻结余额："+sellerWallter.getFrozenBalance()+",扣除数量为:"+order.getNumber());
					
					SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
					flowRecord.setCode("HC");
					flowRecord.setCreateTime(new Date());
					flowRecord.setSellerId(order.getSellerId());
					flowRecord.setPrice(order.getNumber());
					flowRecord.setSource("交易未支付，退回");
					flowRecord.setSerialno(order.getSerialno());
					flowRecord.setRemark("申诉，拒绝交易，未支付，退回");
					sellerDao.addSellerAccountFlowRecord(flowRecord);
				}
			}
			order.setStatus(7);
			order.setIsAppeal(3);
			order.setCannelTime(new Date());
			order.setCloseTime(new Date());
			order.setUpdateTime(new Date());
			order.setRemark("平台");
			order.setDealer(ShiroKit.getUser().getAccount());
			this.baseMapper.updateById(order);
			return ResponseData.success();
		}
		return ResponseData.error("拒绝失败");
	}

	public int findNumberByStatus(Long userId, Integer status) {
		return buyCoinOrderMapper.findNumberByStatus(userId,status);
	}

	public Double findTotalPrice(Long userId) {
		return buyCoinOrderMapper.findTotalPrice(userId);
	}

	public Double findTotalPayPrice(Long userId) {
		return buyCoinOrderMapper.findTotalPayPrice(userId);
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> findSellerBuyCoinBill(String condition, String beginTime, String endTime,
			Long userId) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.findSellerBuyCoinBill(page, condition,beginTime,endTime,userId);
	
	}

	public List<Map<String, Object>> findSellerBuyCoinChannelInfo(Long userId) {
		return buyCoinOrderMapper.findSellerBuyCoinChannelInfo(userId);
	}

	@Transactional
	public ResponseData finishStatus(Long orderId, String password) throws Exception {
		if (StringUtils.isBlank(password)){
			return  ResponseData.error("请输入二级密码");
		}
		MoneyPasswordSetting setting = settingService.getOne(null);
		if(!setting.getPassword().equals(Md5Utils.GetMD5Code(password))) {
			return ResponseData.error("输入二级密码有误");
		}
		SellerBuyerCoinOrder order = this.baseMapper.selectById(orderId);
		  SellerWallter sellerWallter_ = new SellerWallter();
		  sellerWallter_.setCode("HC");
		  sellerWallter_.setSellerId(order.getSellerId());
			List<SellerWallter> wallterList_ =sellerDao.findSellerWallter(sellerWallter_);
			if(wallterList_ != null && wallterList_.size() >0) {
				sellerWallter_ = wallterList_.get(0);
				//确认收款前，判断冻结余额不足，如果冻结余额不足，确认订单，出现，冻结资产为负数的情况
				if(sellerWallter_.getFrozenBalance().doubleValue()<order.getNumber().doubleValue()){
					
					return ResponseData.error("冻结余额不足，无法确认收款");
				}
			}
		
		if(order != null && (order.getStatus() ==2 || order.getStatus() ==1)) {

   			 Double feeRatio =0.0;
   			 Double agentFeeRatio = 0.0;
   			 //查询支付通道费用
   			 UserPayMethodFeeSetting feeSetting = sellerDao.findUserPayMethodFeeSettingByUserId(order.getBuyerId());
   			 if(feeSetting != null) {
   				 Double number =order.getNumber();
				 if ((order.getPayMethodType().equals(1)
						 || order.getPayMethodType().equals(5)
						 || order.getPayMethodType().equals(7) )&& feeSetting.getAlipayRatio() != null
						 && feeSetting.getAlipayRatio() > 0) {
					 feeRatio = feeSetting.getAlipayRatio();
					 number = order.getNumber() * (1 - feeSetting.getAlipayRatio() / 100);
				 } else if ((order.getPayMethodType().equals(2)
						 || order.getPayMethodType().equals(6)
						 || order.getPayMethodType().equals(8)
						 || order.getPayMethodType().equals(9)
				 )&& feeSetting.getWxRatio() != null
						 && feeSetting.getWxRatio() > 0) {
					 feeRatio = feeSetting.getWxRatio();
					 number = order.getNumber() * (1 - feeSetting.getWxRatio() / 100);
				 } else if (order.getPayMethodType().equals(3) && feeSetting.getCardRatio() != null
						 && feeSetting.getCardRatio() > 0) {
					 feeRatio = feeSetting.getCardRatio();
					 number = order.getNumber() * (1 - feeSetting.getCardRatio() / 100);
				 }else if (order.getPayMethodType().equals(4)  && feeSetting.getCloudPayRatio()!=null && feeSetting.getCloudPayRatio() >0){
					 feeRatio = feeSetting.getCloudPayRatio();
					 number = order.getNumber() * (1 - feeSetting.getCloudPayRatio() / 100);
				 }
   				 
   				 if(number >0) {
   					 	User merchantUser = new User();
   					 	merchantUser.setUserId(order.getBuyerId());
   					 	merchantUser = sellerDao.findUserOne(merchantUser);	 
   					 	UserWallter userWallter = new UserWallter();
     					userWallter.setUserId(merchantUser.getUserId());
     					userWallter.setType(2);
     					List<UserWallter> userWallterList =sellerDao.findUserWallterList(userWallter);
     					if(userWallterList != null && userWallterList.size()>0) {//商户返利

							// 传递给商家那边
							Map<String, String> params = new HashMap<String, String>();
							params.put("cuid", order.getCuid());
							params.put("uid", merchantUser.getAccountCode()+ "");
							params.put("price", order.getNumber() + "");
							params.put("cuid", order.getCuid());
							params.put("user_order_no", order.getUserOrderNo());
							params.put("paytype", order.getPayMethodType() + "");
							params.put("orderno", order.getSerialno());
							params.put("realprice", order.getNumber() + "");
							String sbf = Md5Utils.getSign(params);
							sbf = sbf + "key=" + merchantUser.getAppSecret();
							logger.info("sign之前的：" + sbf);
							String sign = DigestUtils.md5DigestAsHex(sbf.getBytes()).toLowerCase();
							params.put("sign", sign);
							logger.info("sign之后：" + sign);
							logger.info(JSONObject.toJSONString(params));
							String notifyResult = null;
							String localhostNotifyUrl = domain+"/app/buyCoin/notifyNotice";
							if (localhostNotifyUrl.equals(order.getNotifyUrl())){
								notifyResult = "success";
							}else{
								for (int i = 0; i < 3; i++) {
									try {
										notifyResult = HttpUtil.postForm(order.getNotifyUrl(), params);
									} catch (IOException e) {
										e.printStackTrace();
									}
									logger.info("result:" + notifyResult);
									notifyResult = notifyResult.replaceAll("\"","");
									if ("success".equals(notifyResult)) {
										break;
									}
								}
							}
							logger.info("最后返回的结果为"+notifyResult);
							if (!"success".equals(notifyResult)){
								return ResponseData.success(200,"需等待平台确认",null);
							}


     						userWallter = userWallterList.get(0);
     						logger.info("后台操作确认收款交易订单确认收款商户返利余额，订单号【"+order.getSerialno()+"】，返利前，用户【"
									+userWallter.getUserId()+"】,可用余额:"
									+userWallter.getAvailableBalance()
									+",冻结余额："+userWallter.getFrozenBalance()+",返利数量:"+number);
     						
     						AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(userWallter.getAvailableBalance());
							
     						userWallter.setAvailableBalance(userWallter.getAvailableBalance()+number);
     						userWallter.setTotalBalance(userWallter.getTotalBalance()+number);
     						userWallter.setUpdateTime(new Date());
     						int resultNumber = sellerDao.updateUserWallter(userWallter);
     						if(resultNumber <=0) {
     							throw new WallterException("确认失败");
     						} 
     						
     						updateRecord.setAfterPrice(userWallter.getAvailableBalance());
							updateRecord.setCode("HC");
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(merchantUser.getAccountCode());
							updateRecord.setSource("商户HC");
							updateRecord.setType("接单交易");
							updateRecord.setRemark("接单交易");
							updateRecord.setPrice(number);
							updateRecord.setRoleId(2L);
							updateRecord.setPayMethodType(order.getPayMethodType());
							updateRecord.setAccountId(userWallter.getUserId());
							updateRecord.setSerialno(order.getSerialno());
							sellerDao.addAccountUpdateRecord(updateRecord);
     						
     						logger.info("交易订单确认收款商户返利余额，订单号【"+order.getSerialno()+"】，返利后，用户【"
									+userWallter.getUserId()+"】,可用余额:"
									+userWallter.getAvailableBalance()
									+",冻结余额："+userWallter.getFrozenBalance()+",返利数量:"+number);
     						
     						UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
     						flowRecord.setCode("HC");
     						flowRecord.setUserId(merchantUser.getUserId());
     						flowRecord.setSource("交易返额");
     						flowRecord.setPrice(number);
     						flowRecord.setCreateTime(new Date());
     						flowRecord.setSerialno(order.getSerialno());
							flowRecord.setType(FlowRecordConstant.GRAD_HC_COIN);
     						sellerDao.addUserAccountFlowRecord(flowRecord);
     					}	
     						 Double bonusNumber = 0.0;
     						 UserRecommendRelation relation = new UserRecommendRelation();
     						 relation.setUserId(merchantUser.getUserId());
     						 relation= sellerDao.findUserRecommendRelation(relation);
     						//代理商分红
     						if(relation != null) {
     							UserBonusSetting bonusSetting = new UserBonusSetting();
         						bonusSetting.setAgentId(relation.getRecommendId());
         						bonusSetting.setUserId(merchantUser.getUserId());
         						bonusSetting = sellerDao.findUserBonusSetting(bonusSetting);
         						if(bonusSetting !=null) {
         							if(order.getPayMethodType().equals(1) 
         	        						&& feeSetting.getAlipayRatio() != null 
         	        						&& feeSetting.getAlipayRatio() >0) {
         								agentFeeRatio =bonusSetting.getAlipayRatio();
         								bonusNumber = order.getNumber()*bonusSetting.getAlipayRatio()/100;
         	        				 }else if(order.getPayMethodType().equals(2)
         	        						 && feeSetting.getWxRatio() != null 
         	         						&& feeSetting.getWxRatio() >0) {
         	        					agentFeeRatio = bonusSetting.getWxRatio();
         	        					bonusNumber = order.getNumber()*bonusSetting.getWxRatio()/100;
         	        				 }else if(order.getPayMethodType().equals(3)
         	        						 && feeSetting.getCardRatio() != null 
         	         						&& feeSetting.getCardRatio() >0) {
         	        					agentFeeRatio = bonusSetting.getCardRatio();
         	        					bonusNumber = order.getNumber()*bonusSetting.getCardRatio()/100;
         	        				 } 
         							if(bonusNumber >0) {
         								UserWallter agentWallter = new UserWallter();
             							agentWallter.setUserId(relation.getRecommendId());
             							agentWallter.setType(2);
             							List<UserWallter> agentWallterList = sellerDao.findUserWallterList(agentWallter);
             							if(agentWallterList != null && agentWallterList.size()>0) {
             								agentWallter = agentWallterList.get(0);
             								logger.info("后台操作确认收款交易订单确认收款代理商返利余额，订单号【"+order.getSerialno()+"】，返利前，用户【"
													+agentWallter.getUserId()+"】,可用余额:"
													+agentWallter.getAvailableBalance()
													+",冻结余额："+agentWallter.getFrozenBalance()+",返利数量:"+bonusNumber);
             								AccountUpdateRecord updateRecord = new AccountUpdateRecord();
											updateRecord.setBeforePrice(agentWallter.getAvailableBalance());
											
												
             								agentWallter.setAvailableBalance(agentWallter.getAvailableBalance()+bonusNumber);
             								agentWallter.setTotalBalance(agentWallter.getTotalBalance()+bonusNumber);
             								agentWallter.setUpdateTime(new Date());
                     						int resultNumber = sellerDao.updateUserWallter(agentWallter);
                     						if(resultNumber <=0) {
                     							throw new WallterException("确认失败");
                     						}
                     						User agentUser = userMapper.selectById(agentWallter.getUserId());
											updateRecord.setSerialno(order.getSerialno());
											updateRecord.setAccountId(agentWallter.getUserId());
											updateRecord.setAfterPrice(agentWallter.getAvailableBalance());
											updateRecord.setCode("HC");
											updateRecord.setCreateTime(new Date());
											updateRecord.setPhone(agentUser.getAccountCode());
											updateRecord.setSource("代理商HC");
											updateRecord.setType("交易返利");
											updateRecord.setRemark("交易返利");
											updateRecord.setPayMethodType(order.getPayMethodType());
											updateRecord.setPrice(bonusNumber);
											updateRecord.setRoleId(4L);
											sellerDao.addAccountUpdateRecord(updateRecord);
                     						
                     						logger.info("后台操作确认收款交易订单确认收款代理商返利余额，订单号【"+order.getSerialno()+"】，返利后，用户【"
													+agentWallter.getUserId()+"】,可用余额:"
													+agentWallter.getAvailableBalance()
													+",冻结余额："+agentWallter.getFrozenBalance()+",返利数量:"+bonusNumber);
                     						
                     						UserAccountFlowRecord agentflowRecord = new UserAccountFlowRecord();
                     						agentflowRecord.setCode("HC");
                     						agentflowRecord.setUserId(relation.getRecommendId());
                     						agentflowRecord.setSource("交易挖矿");
                     						agentflowRecord.setPrice(bonusNumber);
                     						agentflowRecord.setCreateTime(new Date());
                     						agentflowRecord.setSerialno(order.getSerialno());
											agentflowRecord.setType(FlowRecordConstant.GRAD_HC_COIN);
                     						sellerDao.addUserAccountFlowRecord(agentflowRecord);
             							}
         							}
         						}
     						}
     						Seller seller = sellerDao.findSellerbyId(order.getSellerId());
         					//会员获奖
     						Double awardNumber =0.0;
         					SellerAwardSetting awardSetting = sellerDao.getOneSellerAwardSetting();
         					if(awardSetting != null && awardSetting.getValue() != null && awardSetting.getValue() >0 ) {
         						awardNumber = (order.getNumber()-number-bonusNumber)*awardSetting.getValue()/100;
         						if(awardNumber >0) {
         							SellerProfitWallter profitWallter = new SellerProfitWallter();
         							profitWallter.setCode("1");
         							profitWallter.setSellerId(order.getSellerId());
         							List<SellerProfitWallter> profitWallterList = sellerDao.findSellerProfitWallterList(profitWallter);
         							if(profitWallterList != null && profitWallterList.size()>0) {
         								profitWallter = profitWallterList.get(0);
         								logger.info("后台操作确认收款交易订单确认收款会员挖矿返利余额，订单号【"+order.getSerialno()+"】，返利前，用户【"
												+profitWallter.getSellerId()+"】,可用余额:"
												+profitWallter.getAvailableBalance()
												+",冻结余额："+profitWallter.getFrozenBalance()+",返利数量:"+awardNumber);
         								
         								AccountUpdateRecord updateRecord = new AccountUpdateRecord();
										updateRecord.setBeforePrice(profitWallter.getAvailableBalance());
										
         								profitWallter.setAvailableBalance(profitWallter.getAvailableBalance()+awardNumber);
         								profitWallter.setTotalBalance(profitWallter.getTotalBalance()+awardNumber);
         								profitWallter.setUpdateTime(new Date());
         								int resultNumber = sellerDao.updateSellerProfitWallter(profitWallter);
         								if(resultNumber <=0) {
         									throw new WallterException("确认失败");
         								}
         								updateRecord.setAfterPrice(profitWallter.getAvailableBalance());
										updateRecord.setCode("HC");
										updateRecord.setCreateTime(new Date());
										updateRecord.setPhone(seller.getAccount());
										updateRecord.setSource("会员挖矿");
										updateRecord.setType("接单交易");
										updateRecord.setRemark("接单交易");
										updateRecord.setPrice(awardNumber);
										updateRecord.setRoleId(1L);
										updateRecord.setPayMethodType(order.getPayMethodType());
										updateRecord.setAccountId(profitWallter.getSellerId());
										updateRecord.setSerialno(order.getSerialno());
										sellerDao.addAccountUpdateRecord(updateRecord);
         								
         								logger.info("后台操作确认收款交易订单确认收款会员挖矿返利余额，订单号【"+order.getSerialno()+"】，返利后，用户【"
												+profitWallter.getSellerId()+"】,可用余额:"
												+profitWallter.getAvailableBalance()
												+",冻结余额："+profitWallter.getFrozenBalance()+",返利数量:"+awardNumber);
         								SellerProfitAccountFlowRecord profitAccountFlowRecord = new SellerProfitAccountFlowRecord();
         								profitAccountFlowRecord.setCode("1");
         								profitAccountFlowRecord.setCreateTime(new Date());
         								profitAccountFlowRecord.setPrice(awardNumber);
         								profitAccountFlowRecord.setSellerId(order.getSellerId());
         								profitAccountFlowRecord.setSerialno(order.getSerialno());
         								profitAccountFlowRecord.setSource("交易挖矿");
										profitAccountFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
         								sellerDao.addSellerProfitFlowRecord(profitAccountFlowRecord);
         							}
         						}
         					}
         						
         						
     						SellerWallter sellerWallter = new SellerWallter();
     						sellerWallter.setCode("HC");
     						sellerWallter.setSellerId(order.getSellerId());
     						List<SellerWallter> wallterList =sellerDao.findSellerWallter(sellerWallter);
     						if(wallterList != null && wallterList.size() >0) {
     							sellerWallter = wallterList.get(0);
     							logger.info("后台操作确认收款交易订单确认收款扣除冻结余额,订单号【"+order.getSerialno()+"】，扣除前，用户【"
										+sellerWallter.getSellerId()+"】,可用余额:"
										+sellerWallter.getAvailableBalance()
										+",冻结余额："+sellerWallter.getFrozenBalance()+",扣除数量为:"+order.getNumber());
     							
     							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
								updateRecord.setBeforePrice(sellerWallter.getFrozenBalance());
								
     							sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-order.getNumber());
     							sellerWallter.setUpdateTime(new Date());
     							int reslut = sellerDao.updateSellerWallter(sellerWallter);
     							if(reslut <=0) {
     								throw new WallterException("确认失败");
     							}
     							updateRecord.setAfterPrice(sellerWallter.getFrozenBalance());
								updateRecord.setCode("HC");
								updateRecord.setCreateTime(new Date());
								updateRecord.setPhone(seller.getAccount());
								updateRecord.setSource("会员HC");
								updateRecord.setType("接单交易");
								updateRecord.setRemark("接单交易");
								updateRecord.setPrice(-order.getNumber());
								updateRecord.setRoleId(1L);
								updateRecord.setPayMethodType(order.getPayMethodType());
								updateRecord.setAccountId(order.getSellerId());
								updateRecord.setSerialno(order.getSerialno());
								sellerDao.addAccountUpdateRecord(updateRecord);
     							
     							logger.info("后台操作确认收款交易订单确认收款扣除冻结余额,订单号【"+order.getSerialno()+"】，扣除后，用户【"
										+sellerWallter.getSellerId()+"】,可用余额:"
										+sellerWallter.getAvailableBalance()
										+",冻结余额："+sellerWallter.getFrozenBalance());
     							//流水记录
     							SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
     							flowRecord.setCode("HC");
     							flowRecord.setCreateTime(new Date());
     							flowRecord.setPrice(-order.getNumber());
     							flowRecord.setSellerId(order.getSellerId());
     							flowRecord.setSerialno(order.getSerialno());
     							flowRecord.setSource("交易扣除");
								flowRecord.setType(FlowRecordConstant.GRAD_HC_COIN);
     							sellerDao.addSellerAccountFlowRecord(flowRecord);
     							
     						}

					 //直推返利
					 if (StringUtils.isNotBlank(seller.getReferceIds())){
						 String[] pIds =  seller.getReferceIds().split(",");
						 Integer rateType = 0;
						 if(order.getPayMethodType().equals(1)
								 || order.getPayMethodType().equals(5)
								 || order.getPayMethodType().equals(7) ){
							 rateType = 1;
						 }else if(order.getPayMethodType().equals(2)
								 || order.getPayMethodType().equals(6)
								 || order.getPayMethodType().equals(8)
								 || order.getPayMethodType().equals(9)){
							 rateType = 2;
						 }else if(order.getPayMethodType().equals(3)){
							 rateType=3;
						 }else{
							 rateType=4;
						 }
						 List<PromotionRate> promotionList =  promotionRateService.selectListByLeveAsc(rateType);
						 if(promotionList != null && promotionList.size()>0){
							 int size = promotionList.size()-pIds.length >0?pIds.length:promotionList.size();
							 for (int i=0;i<size;i++){
								 Double rebateValue =0.0;
								 SellerProfitWallter profitWallter = new SellerProfitWallter();
								 profitWallter.setCode("3");
								 profitWallter.setSellerId(Long.valueOf(pIds[i]));
								 List<SellerProfitWallter> list = sellerDao.findSellerProfitWallterList(profitWallter);
								 if (list != null && list.size() > 0) {
									 profitWallter = list.get(0);
									 rebateValue = order.getNumber() *promotionList.get(i).getBonusRatio() / 100;

									 Seller sell = this.sellerDao.findSellerbyId(profitWallter.getSellerId());


									 AccountUpdateRecord updateRecord3 = new AccountUpdateRecord();
									 updateRecord3.setBeforePrice(profitWallter.getAvailableBalance());
									 profitWallter
											 .setAvailableBalance(profitWallter.getAvailableBalance() + rebateValue);
									 profitWallter.setTotalBalance(profitWallter.getTotalBalance() + rebateValue);
									 profitWallter.setUpdateTime(new Date());
									 int result = sellerDao.updateSellerProfitWallter(profitWallter);
									 if (result <= 0) {
										 throw new WallterException("确认收款失败");
									 }

									 updateRecord3.setAfterPrice(profitWallter.getAvailableBalance());
									 updateRecord3.setCode("HC");
									 updateRecord3.setCreateTime(new Date());
									 updateRecord3.setPhone(sell.getAccount());
									 updateRecord3.setSource("推荐HC");
									 updateRecord3.setType("推荐挖矿");
									 updateRecord3.setRemark("推荐挖矿");
									 updateRecord3.setPrice(rebateValue);
									 updateRecord3.setRoleId(1l);
									 updateRecord3.setSerialno(order.getSerialno());
									 updateRecord3.setAccountId(profitWallter.getSellerId());
									 sellerDao.addAccountUpdateRecord(updateRecord3);

									 // 流水记录
									 SellerProfitAccountFlowRecord profitFlowRecord = new SellerProfitAccountFlowRecord();
									 profitFlowRecord.setCode(profitWallter.getCode());
									 profitFlowRecord.setCreateTime(new Date());
									 profitFlowRecord.setPrice(rebateValue);
									 profitFlowRecord.setSellerId(sell.getSellerId());
									 profitFlowRecord.setUserId(sell.getUserId());
									 profitFlowRecord.setSource("推荐返利");
									 profitFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
									 sellerDao.addSellerProfitFlowRecord(profitFlowRecord);
								 }
							 }
						 }
					 }



					 		//更新订单信息
         				   	order.setStatus(4);
							order.setIsSuccess(1);
     						order.setUpdateTime(new Date());
     						order.setCloseTime(new Date());
     						order.setIntoNumber(number);
     						order.setFeePrice(feeRatio);
     						order.setAgentFeePrice(agentFeeRatio);
     						order.setBonusNumber(awardNumber<0?0:awardNumber);
     						order.setUpdateUser(ShiroKit.getUser().getId());
					 		order.setDealer(ShiroKit.getUser().getAccount());
					 		order.setRemark("平台");
     						this.baseMapper.updateById(order);

					 SellerPayMethod payMethod = sellerDao.findSellerPayMethodById(order.getPayMethodId());
					 payMethod.setFailNumber(0);
					 sellerDao.updateSellerPayMethod(payMethod);

//					 SellerNotice notice = new SellerNotice();
//					 notice.setSellerId(order.getSellerId());
//					 notice.setCreateTime(new Date());
//					 notice.setIsSee(0);
//					 notice.setContent("您的订单号【"+order.getSerialno()+"】，接单金额为"+order.getNumber()+",已确认收款完成");
//					 sellerDao.addSellerNotice(notice);
//
         					return ResponseData.success(200,"确认收款成功",null); 
         				}
     				}	
				}
			return ResponseData.error("确认收款失败");
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> findSellerBuyCoinMerchant(String condition, String beginTime, String endTime,
			Long userId, String account, String serialno, String payMethodType) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.findSellerBuyCoinMerchant(page, condition,beginTime,endTime,userId,account,serialno,payMethodType);
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> findPlatformRechargeStatistics(String beginTime, String endTime) {
		Page page = LayuiPageFactory.defaultPage();
		return this.baseMapper.findPlatformRechargeStatistics(page,beginTime,endTime);
	}

	public Map<String, Object> getPlatformRechargeStatisticsTotalByToday() {
		return baseMapper.getPlatformRechargeStatisticsTotalByToday();
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> findPlatformWithdrawStatistics(String beginTime, String endTime) {
		Page page = LayuiPageFactory.defaultPage();
		return this.baseMapper.findPlatformWithdrawStatistics(page,beginTime,endTime);
	}

	public Map<String, Object> getPlatformWithdrawStatisticsTotalByToday() {
		return baseMapper.getPlatformWithdrawStatisticsTotalByToday();
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> findPlatformOtcpMoneyStatistics() {
		Page page = LayuiPageFactory.defaultPage();
		return baseMapper.findPlatformOtcpMoneyStatistics(page);
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> findPlatformBonusMoneyStatistics(String beginTime, String endTime) {
		Page page = LayuiPageFactory.defaultPage();
		return this.baseMapper.findPlatformBonusMoneyStatistics(page,beginTime,endTime);
	}

	public Map<String, Object> getPlatformBonusMoneyStatisticsTotalByToday() {
		return baseMapper.getPlatformBonusMoneyStatisticsTotalByToday();
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> findPlatformFeeBonusStatistics(String beginTime, String endTime) {
		Page page = LayuiPageFactory.defaultPage();
		return this.baseMapper.findPlatformFeeBonusStatistics(page,beginTime,endTime);
	}

	public Map<String, Object> getPlatformFeeBonusStatisticsTotalByToday() {
		return baseMapper.getPlatformFeeBonusStatisticsTotalByToday();
	}

	public Map<String, Object> getPlatformRechargeStatisticsTotal() {
		return baseMapper.getPlatformRechargeStatisticsTotal();
	}

	public Map<String, Object> getPlatformWithdrawStatisticsTotal() {
		return baseMapper.getPlatformWithdrawStatisticsTotal();
	}

	public Map<String, Object> getPlatformBonusMoneyStatisticsTotal() {
		return baseMapper.getPlatformBonusMoneyStatisticsTotal();
	}

	public Map<String, Object> getPlatformFeeBonusStatisticsTotal() {
		return baseMapper.getPlatformFeeBonusStatisticsTotal();
	}
	
	/**
	 * @Desc: 取消订单重新激活
	 * @param orderId
	 * @param password
	 * @return
	 */
	@Transactional
	public ResponseData updateOrderStatus(Long orderId, String password) {
		if (StringUtils.isBlank(password)){
			return  ResponseData.error("请输入二级密码");
		}
		MoneyPasswordSetting setting = settingService.getOne(null);
		if(!setting.getPassword().equals(Md5Utils.GetMD5Code(password))) {
			return ResponseData.error("输入二级密码有误");
		}
		SellerBuyerCoinOrder order = this.baseMapper.selectById(orderId);
		if(order != null && order.getStatus() ==7) {
			Seller seller = sellerDao.findSellerbyId(order.getSellerId());
			if(seller != null) {
				SellerWallter sellerWallter = new SellerWallter();
				sellerWallter.setCode("HC");
				sellerWallter.setSellerId(seller.getSellerId());
				List<SellerWallter> list = sellerDao.findSellerWallter(sellerWallter);
				if(list != null && list.size() >0) {
					sellerWallter = list.get(0);
					if(sellerWallter.getAvailableBalance() < order.getNumber()) {
						return ResponseData.error("会员可用余额不足无法激活");
					}
					logger.info("重新激活扣除冻结余额,订单号【"+order.getSerialno()+"】，扣除前，用户【"
							+sellerWallter.getSellerId()+"】,可用余额:"
							+sellerWallter.getAvailableBalance()
							+",冻结余额："+sellerWallter.getFrozenBalance());
					
					AccountUpdateRecord updateRecord = new AccountUpdateRecord();
					updateRecord.setBeforePrice(sellerWallter.getFrozenBalance());
					
					sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()-order.getNumber());
					sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()+order.getNumber());
					int result =sellerDao.updateSellerWallter(sellerWallter);
					if(result <=0) {
						throw new WallterException("重新激活失败");
					}
					
					updateRecord.setAfterPrice(sellerWallter.getFrozenBalance());
					updateRecord.setCode("HC");
					updateRecord.setCreateTime(new Date());
					updateRecord.setPhone(seller.getAccount());
					updateRecord.setSource("会员HC");
					updateRecord.setType("接单交易");
					updateRecord.setRemark("接单交易");
					updateRecord.setPrice(-order.getNumber());
					updateRecord.setRoleId(1L);
					updateRecord.setPayMethodType(order.getPayMethodType());
					updateRecord.setAccountId(order.getSellerId());
					updateRecord.setSerialno(order.getSerialno());
					sellerDao.addAccountUpdateRecord(updateRecord);
					
					logger.info("重新激活扣除冻结余额,订单号【"+order.getSerialno()+"】，扣除后，用户【"
							+sellerWallter.getSellerId()+"】,可用余额:"
							+sellerWallter.getAvailableBalance()
							+",冻结余额："+sellerWallter.getFrozenBalance());

					
					order.setStatus(2);
					order.setUpdateTime(new Date());	
					order.setCreateTime(new Date());
					order.setIsAppeal(0);
					order.setRemark("平台");
					order.setDealer(ShiroKit.getUser().getAccount());
					int updateOrderCount=this.baseMapper.updateById(order);
					if(updateOrderCount<=0){
						throw new WallterException("更新订单失败");
					}
					return ResponseData.success();
				}
			}
		}
		return ResponseData.error("重新激活失败");
	}

	@Transactional
	public ResponseData updateNumber(Long orderId, Double number) {
		SellerBuyerCoinOrder order = this.baseMapper.selectById(orderId);
		if(order != null && (order.getStatus() ==1 || order.getStatus() ==2)) {
			if(number == null || number <=0) {
				return ResponseData.error("输入金额不能为空");
			}
			if(order.getNumber() >number) {//补单余额小于订单余额，给会员退额
				Seller seller = sellerDao.findSellerbyId(order.getSellerId());
				if(seller != null) {
					SellerWallter sellerWallter = new SellerWallter();
					sellerWallter.setCode("HC");
					sellerWallter.setSellerId(seller.getSellerId());
					List<SellerWallter> list = sellerDao.findSellerWallter(sellerWallter);
					if(list != null && list.size() >0) {
						sellerWallter = list.get(0);
						Double addNumber = order.getNumber()-number;
						logger.info("后台进行补单操作，补单前：订单号【"+order.getSerialno()+"】，会员【"+sellerWallter.getSellerId()+"】，可用余额："+sellerWallter.getAvailableBalance()+",冻结余额："+sellerWallter.getFrozenBalance()+",补差价为："+addNumber);
						
						AccountUpdateRecord updateRecord = new AccountUpdateRecord();
						updateRecord.setBeforePrice(sellerWallter.getAvailableBalance());
						
						sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()+addNumber);
						sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-addNumber);
						int result =sellerDao.updateSellerWallter(sellerWallter);
						if(result <=0) {
							throw new WallterException("补单失败");
						}
						updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
						updateRecord.setCode("HC");
						updateRecord.setCreateTime(new Date());
						updateRecord.setAccountId(sellerWallter.getSellerId());
						updateRecord.setSerialno(order.getSerialno());
						updateRecord.setPhone(seller.getAccount());
						updateRecord.setSource("会员HC");
						updateRecord.setType("接单交易");
						updateRecord.setRemark("接单交易，补单");
						updateRecord.setPrice(addNumber);
						updateRecord.setRoleId(1L);
						sellerDao.addAccountUpdateRecord(updateRecord);
						
						logger.info("后台进行补单操作，补单后：订单号【"+order.getSerialno()+"】，会员【"+sellerWallter.getSellerId()+"】，可用余额："+sellerWallter.getAvailableBalance()+",冻结余额："+sellerWallter.getFrozenBalance()+",补差价为："+addNumber);
						
						
						SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
						flowRecord.setCode("HC");
						flowRecord.setCreateTime(new Date());
						flowRecord.setPrice(addNumber);
						flowRecord.setSellerId(seller.getSellerId());
						flowRecord.setSerialno(order.getSerialno());
						flowRecord.setSource("交易退回");
						flowRecord.setRemark("补单退回余额");
						sellerDao.addSellerAccountFlowRecord(flowRecord);
						order.setNumber(number);
						order.setUpdateTime(new Date());
						order.setCreateTime(new Date());
						order.setRemark("平台");
						order.setDealer(ShiroKit.getUser().getAccount());
						this.baseMapper.updateById(order);
						return ResponseData.success();
					}
				}	
			}else if(order.getNumber() < number) {
				//大于订单余额，从会员钱包扣除
				Seller seller = sellerDao.findSellerbyId(order.getSellerId());
				if(seller != null) {
					SellerWallter sellerWallter = new SellerWallter();
					sellerWallter.setCode("HC");
					sellerWallter.setSellerId(seller.getSellerId());
					List<SellerWallter> list = sellerDao.findSellerWallter(sellerWallter);
					if(list != null && list.size() >0) {
						sellerWallter = list.get(0);
						Double addNumber = number-order.getNumber();
						if(sellerWallter.getAvailableBalance() < addNumber) {
							return ResponseData.error("会员余额不足");
						}
						logger.info("后台进行补单操作，补单前：订单号【"+order.getSerialno()+"】，会员【"+sellerWallter.getSellerId()+"】，可用余额："+sellerWallter.getAvailableBalance()+",冻结余额："+sellerWallter.getFrozenBalance()+",补差价为："+addNumber);
						AccountUpdateRecord updateRecord = new AccountUpdateRecord();
						updateRecord.setBeforePrice(sellerWallter.getAvailableBalance());
						sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()-addNumber);
						sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()+addNumber);
						int result =sellerDao.updateSellerWallter(sellerWallter);
						if(result <=0) {
							throw new WallterException("补单失败");
						}
						updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
						updateRecord.setCode("HC");
						updateRecord.setCreateTime(new Date());
						updateRecord.setAccountId(sellerWallter.getSellerId());
						updateRecord.setSerialno(order.getSerialno());
						updateRecord.setPhone(seller.getAccount());
						updateRecord.setSource("会员HC");
						updateRecord.setType("接单交易");
						updateRecord.setRemark("接单交易，补单");
						updateRecord.setPrice(-addNumber);
						updateRecord.setRoleId(1L);
						sellerDao.addAccountUpdateRecord(updateRecord);
						
						logger.info("后台进行补单操作，补单后：订单号【"+order.getSerialno()+"】，会员【"+sellerWallter.getSellerId()+"】，可用余额："+sellerWallter.getAvailableBalance()+",冻结余额："+sellerWallter.getFrozenBalance()+",补差价为："+addNumber);
						
						SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
						flowRecord.setCode("HC");
						flowRecord.setCreateTime(new Date());
						flowRecord.setPrice(-addNumber);
						flowRecord.setSellerId(seller.getSellerId());
						flowRecord.setSerialno(order.getSerialno());
						flowRecord.setSource("交易扣除");
						flowRecord.setRemark("补单扣除余额");
						sellerDao.addSellerAccountFlowRecord(flowRecord);
						
						
						order.setNumber(number);
						order.setUpdateTime(new Date());
						order.setCreateTime(new Date());
						order.setRemark("平台");
						order.setDealer(ShiroKit.getUser().getAccount());
						this.baseMapper.updateById(order);
						
						return ResponseData.success();
					}
				}
			}
		}
		return ResponseData.error("补单失败");
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> findPlatformMerchantStatistics(String phone) {
		Page page = LayuiPageFactory.defaultPage();
		return this.baseMapper.findPlatformMerchantStatistics(page,phone);
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> findPlatformAgentStatistics(String account) {
		Page page = LayuiPageFactory.defaultPage();
		return this.baseMapper.findPlatformAgentStatistics(page,account);
	}


	public List<SellerBuyerCoinOrder> findNoFinishSellerBuyerCoinListByPayMethodId(Long payMethodId) {
		return this.buyCoinOrderMapper.findNoFinishSellerBuyerCoinListByPayMethodId(payMethodId);
	}

	@Transactional
    public ResponseData addOrder(Integer payMethodType, String serialno, Double number, String merchantAccount, String sellerAccount,String password) {
		if (payMethodType == null || payMethodType <=0){
			return  ResponseData.error("请选择支付通道");
		}
		if (StringUtils.isBlank(serialno)){
			return ResponseData.error("请填写商户订单号");
		}
		if (number == null || number <=0){
			return  ResponseData.error("补单金额不能小于0");
		}
		if (StringUtils.isBlank(merchantAccount)){
			return  ResponseData.error("请输入商户ID");
		}
		if (StringUtils.isBlank(sellerAccount)){
			return  ResponseData.error("请输入码商ID");
		}
		if (StringUtils.isBlank(password)){
			return  ResponseData.error("请输入二级密码");
		}
		MoneyPasswordSetting setting = settingService.getOne(null);
		if(!setting.getPassword().equals(Md5Utils.GetMD5Code(password))) {
			return ResponseData.error("输入二级密码有误");
		}
		Map<String,Object> map = new HashMap<>();
		map.put("ACCOUNT_CODE", merchantAccount);
		map.put("ROLE_ID", 2);
		map.put("STATUS","ENABLE");
		List<User> users = userMapper.selectByMap(map);
		if(users == null || users.size() <=0) {
			return ResponseData.error("输入的商户ID不存在");
		}
		User user = users.get(0);
		Seller seller = this.sellerDao.findSellerByAccount(sellerAccount);
		if (seller == null){
			return ResponseData.error("输入的码商ID不存在");
		}
			SellerWallter sellerWallter = new SellerWallter();
			sellerWallter.setCode("HC");
			sellerWallter.setSellerId(seller.getSellerId());
			List<SellerWallter> list = sellerDao.findSellerWallter(sellerWallter);
			if(list != null && list.size() >0) {
				sellerWallter = list.get(0);
				if(sellerWallter.getAvailableBalance() <=0){
					return ResponseData.error("码商可用余额不足");
				}
				if (sellerWallter.getAvailableBalance()<number){
					return ResponseData.error("输入的补单金额不能超过"+sellerWallter.getAvailableBalance());
				}
				List<SellerPayMethod> payMethodList = this.sellerDao.findSellerPayMethodByIsCheck(seller.getSellerId(),payMethodType);
				if (payMethodList == null || payMethodList.size() <=0){
					return ResponseData.error("该码商暂无该类型的支付通道");
				}
				int num = (int)(Math.random()*(payMethodList.size()));
				SellerPayMethod  payMethod = payMethodList.get(num);

				SellerBuyerCoinOrder coinOrder = new SellerBuyerCoinOrder();
				coinOrder.setCreateTime(new Date());
				coinOrder.setNumber(number);
				coinOrder.setBuyerId(user.getUserId());
				coinOrder.setSellerId(seller.getSellerId());
				coinOrder.setStatus(1);
				coinOrder.setNotifyUrl(domain+"/app/buyCoin/notifyNotice");
				coinOrder.setUserOrderNo(serialno);
				coinOrder.setCuid(serialno);
				coinOrder.setSerialno(otcOrderService.generateSimpleSerialno(user.getUserId(), 5));
				coinOrder.setPayMethodId(payMethod.getPayMethodId());
				coinOrder.setPayMethodAccount(payMethod.getAccount());
				coinOrder.setPayMethodCardBank(payMethod.getCardBank());
				coinOrder.setPayMethodCardBankName(payMethod.getCardBankName());
				coinOrder.setPayMethodName(payMethod.getName());
				coinOrder.setPayMethodType(payMethod.getType());
				coinOrder.setPayMethodQrcode(payMethod.getQrCode());
				coinOrder.setPayMethodNickName(payMethod.getRemark());
				coinOrder.setIsAppeal(0);
				coinOrder.setIsSuccess(0);
				coinOrder.setIsNotice(0);
				coinOrder.setUpdateUser(ShiroKit.getUser().getId());
				coinOrder.setRemark("用户："+ShiroKit.getUser().getAccount()+"进行补空单操作");
				coinOrder.setOrderCode(2);
				coinOrder.setDealer(ShiroKit.getUser().getAccount());
				coinOrder.setRemark("平台");
				otcOrderService.addSellerBuyerCoinOrder(coinOrder);

				BuyCoinUsedPayMethodRecord record2 = new BuyCoinUsedPayMethodRecord();
				record2.setCreateTime(new Date());
				record2.setSellerId(seller.getSellerId());
				record2.setPayMethodId(payMethod.getPayMethodId());
				record2.setType(payMethod.getType());
				otcOrderService.addBuyCoinUsedPayMethodRecord(record2);

				SellerBuyCoinNotice notice =sellerDao.findSellerBuyCoinNotice(coinOrder.getSellerId());
				if(notice != null) {
					notice.setIsNotice(0);
					sellerDao.updateSellerBuyCoinNotice(notice);
				}else {
					notice = new SellerBuyCoinNotice();
					notice.setIsNotice(0);
					notice.setSellerId(coinOrder.getSellerId());
					sellerDao.addSellerBuyCoinNotice(notice);
				}

				sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()-number);
				sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()+number);
				sellerWallter.setUpdateTime(new Date());
				int res = this.sellerDao.updateSellerWallter(sellerWallter);
				if (res <=0){
					throw new WallterException("补单失败");
				}



				return ResponseData.success();
			}
		return  ResponseData.error("补单失败");
    }

	public ResponseData returnBuyCoinOrder(Long orderId, String password) {
		if (StringUtils.isBlank(password)){
			return  ResponseData.error("请输入二级密码");
		}
		MoneyPasswordSetting setting = settingService.getOne(null);
		if(!setting.getPassword().equals(Md5Utils.GetMD5Code(password))) {
			return ResponseData.error("输入二级密码有误");
		}
		SellerBuyerCoinOrder order = this.baseMapper.selectById(orderId);
		if (order != null && order.getStatus().equals(4)){
			Seller seller = this.sellerDao.findSellerbyId(order.getSellerId());
			SellerWallter sellerWallter = new SellerWallter();
			sellerWallter.setCode("HC");
			sellerWallter.setSellerId(order.getSellerId());
			List<SellerWallter> list = sellerDao.findSellerWallter(sellerWallter);
			if(list != null && list.size() >0) {
				sellerWallter = list.get(0);
				AccountUpdateRecord updateRecord = new AccountUpdateRecord();
				updateRecord.setBeforePrice(sellerWallter.getAvailableBalance());

				sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()+order.getIntoNumber());
				sellerWallter.setUpdateTime(new Date());
				int result =sellerDao.updateSellerWallter(sellerWallter);
				if(result <=0) {
					throw new WallterException("返补单失败");
				}
				updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
				updateRecord.setCode("HC");
				updateRecord.setCreateTime(new Date());
				updateRecord.setAccountId(sellerWallter.getSellerId());
				updateRecord.setSerialno(order.getSerialno());
				updateRecord.setPhone(seller.getAccount());
				updateRecord.setSource("会员HC");
				updateRecord.setType("返补单");
				updateRecord.setRemark("返补单");
				updateRecord.setPrice(order.getIntoNumber());
				updateRecord.setRoleId(1L);
				sellerDao.addAccountUpdateRecord(updateRecord);

				SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
				flowRecord.setCode("HC");
				flowRecord.setCreateTime(new Date());
				flowRecord.setPrice(order.getIntoNumber());
				flowRecord.setSellerId(seller.getSellerId());
				flowRecord.setSerialno(order.getSerialno());
				flowRecord.setSource("订单确认失败，返回余额");
				flowRecord.setRemark("订单确认失败，返回余额");
				flowRecord.setType(FlowRecordConstant.GRAD_HC_COIN_CANNEL);
				sellerDao.addSellerAccountFlowRecord(flowRecord);


				User merchantUser = new User();
				merchantUser.setUserId(order.getBuyerId());
				merchantUser = sellerDao.findUserOne(merchantUser);
				UserWallter userWallter = new UserWallter();
				userWallter.setUserId(merchantUser.getUserId());
				userWallter.setType(2);
				List<UserWallter> userWallterList =sellerDao.findUserWallterList(userWallter);
				if(userWallterList != null && userWallterList.size()>0) {//商户返扣
					userWallter = userWallterList.get(0);
					if (userWallter.getAvailableBalance() < order.getIntoNumber()){
						throw new WallterException("商户余额不足");
					}
					updateRecord = new AccountUpdateRecord();
					updateRecord.setBeforePrice(userWallter.getAvailableBalance());

					userWallter.setAvailableBalance(userWallter.getAvailableBalance()-order.getIntoNumber());
					userWallter.setUpdateTime(new Date());
					int resultNumber = sellerDao.updateUserWallter(userWallter);
					if(resultNumber <=0) {
						throw new WallterException("确认失败");
					}

					updateRecord.setAfterPrice(userWallter.getAvailableBalance());
					updateRecord.setCode("HC");
					updateRecord.setCreateTime(new Date());
					updateRecord.setPhone(merchantUser.getAccountCode());
					updateRecord.setSource("商户HC");
					updateRecord.setType("返补单扣除");
					updateRecord.setRemark("返补单扣除");
					updateRecord.setPrice(-order.getIntoNumber());
					updateRecord.setRoleId(2L);
					updateRecord.setPayMethodType(order.getPayMethodType());
					updateRecord.setAccountId(userWallter.getUserId());
					updateRecord.setSerialno(order.getSerialno());
					sellerDao.addAccountUpdateRecord(updateRecord);

					UserAccountFlowRecord flowRecord1 = new UserAccountFlowRecord();
					flowRecord1.setCode("HC");
					flowRecord1.setUserId(merchantUser.getUserId());
					flowRecord1.setSource("返补单扣除");
					flowRecord1.setPrice(-order.getIntoNumber());
					flowRecord1.setCreateTime(new Date());
					flowRecord1.setSerialno(order.getSerialno());
					flowRecord1.setType(FlowRecordConstant.GRAD_HC_COIN_CANNEL);
					sellerDao.addUserAccountFlowRecord(flowRecord1);
				}

				order.setOrderCode(3);
				order.setUpdateUser(ShiroKit.getUser().getId());
				order.setRemark("平台");
				order.setDealer(ShiroKit.getUser().getAccount());
				this.baseMapper.updateById(order);
				return ResponseData.success();
			}
		}
		return  ResponseData.error("返补失败");
	}

    public ResponseData warn(Boolean flag) {
		if (flag){
			int count = this.buyCoinOrderMapper.selectSellerIPAndMerchantIpByCount();
			if(count >0){
				return ResponseData.success();
			}
		}
		return ResponseData.error(null);
    }
}
