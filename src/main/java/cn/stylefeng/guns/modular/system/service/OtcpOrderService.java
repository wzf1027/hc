package cn.stylefeng.guns.modular.system.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.stylefeng.guns.core.util.FlowRecordConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.system.entity.AccepterRebateSetting;
import cn.stylefeng.guns.modular.system.entity.AccountUpdateRecord;
import cn.stylefeng.guns.modular.system.entity.OtcpOrder;
import cn.stylefeng.guns.modular.system.entity.OtcpOrderCannelNumberRecord;
import cn.stylefeng.guns.modular.system.entity.SellOtcpOrder;
import cn.stylefeng.guns.modular.system.entity.Seller;
import cn.stylefeng.guns.modular.system.entity.SellerAccountFlowRecord;
import cn.stylefeng.guns.modular.system.entity.SellerProfitAccountFlowRecord;
import cn.stylefeng.guns.modular.system.entity.SellerProfitWallter;
import cn.stylefeng.guns.modular.system.entity.SellerWallter;
import cn.stylefeng.guns.modular.system.entity.SuperiorAccepterRebateSetting;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.entity.UserAccountFlowRecord;
import cn.stylefeng.guns.modular.system.entity.UserWallter;
import cn.stylefeng.guns.modular.system.mapper.OtcpOrderMapper;
import cn.stylefeng.guns.modular.system.mapper.UserMapper;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Service
public class OtcpOrderService extends ServiceImpl<OtcpOrderMapper, OtcpOrder> {

	
	private Logger logger = LoggerFactory.getLogger(OtcpOrderService.class);
	
	@Resource
	private OtcpOrderMapper otcpOrderMapper;
	
	@Resource
	private SellerMapper sellerMapper;
	
	@Resource
	private UserMapper userMapper;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String condition, String buyerPhone, String sellerPhone, String serialno, String beginTime, String endTime, Long userId, String remark, Integer status, Integer isAppeal, Integer payMethodType) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition,buyerPhone,sellerPhone,serialno,beginTime,endTime,userId,remark,status,isAppeal,payMethodType);
	}

	@Transactional
	public ResponseData buyerWarn(Long id) {
		OtcpOrder otcpOrder = otcpOrderMapper.selectById(id);
		if(otcpOrder != null) {
			if(otcpOrder.getNoAppealStatus() ==2) {
				if(otcpOrder.getIsAppeal()==1) {
        			otcpOrder.setStatus(4);
        			otcpOrder.setIsAppeal(2);
        			otcpOrder.setUpdateTime(new Date());
					otcpOrder.setUpdateUserId(ShiroKit.getUser().getId());
					otcpOrder.setUserAccount(ShiroKit.getUser().getAccount());
        			otcpOrderMapper.updateById(otcpOrder);


        			if(otcpOrder.getType() ==1) {
        				
        				//买家,会员账户
            			SellerWallter buyerWallter = new SellerWallter();
            			buyerWallter.setCode("HC");
            			buyerWallter.setSellerId(otcpOrder.getBuyerId());
            			List<SellerWallter> buyerList = sellerMapper.findSellerWallter(buyerWallter);
            			buyerWallter = buyerList.get(0);
            			
            			Seller buyer = sellerMapper.findSellerbyId(otcpOrder.getBuyerId());
            			AccountUpdateRecord updateRecord = new AccountUpdateRecord();
						updateRecord.setBeforePrice(buyerWallter.getAvailableBalance());
						logger.info("otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，买家会员账号【"+buyerWallter.getSellerId()+"】，可用余额："+buyerWallter.getAvailableBalance()+",冻结余额:"+buyerWallter.getFrozenBalance()+"购买数量:"+otcpOrder.getNumber());
            			
            			buyerWallter.setAvailableBalance(buyerWallter.getAvailableBalance()+otcpOrder.getNumber());
            			buyerWallter.setTotalBalance(buyerWallter.getTotalBalance()+otcpOrder.getNumber());
            			buyerWallter.setUpdateTime(new Date());
            			int result = sellerMapper.updateSellerWallter(buyerWallter);
            			if(result <=0) {
            				throw new WallterException("确认收款失败");
            			}
            			
            			logger.info("处理后otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，买家会员账号【"+buyerWallter.getSellerId()+"】，可用余额："+buyerWallter.getAvailableBalance()+",冻结余额:"+buyerWallter.getFrozenBalance()+"购买数量:"+otcpOrder.getNumber());
            			
            			updateRecord.setAfterPrice(buyerWallter.getAvailableBalance());
						updateRecord.setCode("HC");
						updateRecord.setCreateTime(new Date());
						updateRecord.setAccountId(buyer.getSellerId());
						updateRecord.setSerialno(otcpOrder.getSerialno());
						updateRecord.setPhone(buyer.getAccount());
						updateRecord.setSource("会员HC");
						updateRecord.setType("购买");
						updateRecord.setRemark("购买");
						updateRecord.setPrice(otcpOrder.getNumber());
						updateRecord.setRoleId(1L);
						sellerMapper.addAccountUpdateRecord(updateRecord);
            			
            			SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
            			flowRecord.setCode("HC");
            			flowRecord.setCreateTime(new Date());
            			flowRecord.setPrice(otcpOrder.getNumber());
            			flowRecord.setSellerId(otcpOrder.getBuyerId());
            			flowRecord.setSource("购买HC");
            			flowRecord.setType(FlowRecordConstant.BUY_HC_COIN);
            			sellerMapper.addSellerAccountFlowRecord(flowRecord);
            			//卖家,承兑商
            			Seller sell  = sellerMapper.findSellerbyId(otcpOrder.getSellerId());
            			UserWallter userWallter = new UserWallter();
            			userWallter.setUserId(sell.getUserId());
            			List<UserWallter> userWallterList = sellerMapper.findUserWallterList(userWallter);
            			userWallter = userWallterList.get(0);
            			AccountUpdateRecord updateRecord2 = new AccountUpdateRecord();
						updateRecord2.setBeforePrice(userWallter.getAvailableBalance());
						
						logger.info("otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，卖家承兑商【"+userWallter.getUserId()+"】，可用余额："+buyerWallter.getAvailableBalance()+",冻结余额:"+buyerWallter.getFrozenBalance()+"购买数量:"+otcpOrder.getNumber());
            			
            			userWallter.setFrozenBalance(userWallter.getFrozenBalance()-otcpOrder.getNumber());
            			userWallter.setTotalBalance(userWallter.getTotalBalance()-otcpOrder.getNumber());
            			userWallter.setUpdateTime(new Date());
            			result = sellerMapper.updateUserWallter(userWallter);
            			if(result <=0) {
            				throw new WallterException("确认收款失败");
            			}
            			
            			logger.info("处理后otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，卖家承兑商【"+userWallter.getUserId()+"】，可用余额："+buyerWallter.getAvailableBalance()+",冻结余额:"+buyerWallter.getFrozenBalance()+"购买数量:"+otcpOrder.getNumber());
            			
            			updateRecord2.setAfterPrice(userWallter.getAvailableBalance());
						updateRecord2.setCode("HC");
						updateRecord2.setCreateTime(new Date());
						updateRecord2.setAccountId(userWallter.getUserId());
						updateRecord2.setSerialno(otcpOrder.getSerialno());
						updateRecord2.setPhone(sell.getAccount());
						updateRecord2.setSource("承兑HC");
						updateRecord2.setType("出售");
						updateRecord2.setRemark("出售");
						updateRecord2.setPrice(otcpOrder.getNumber());
						updateRecord2.setRoleId(3L);
						sellerMapper.addAccountUpdateRecord(updateRecord2);

						UserAccountFlowRecord flowRecord2 = new UserAccountFlowRecord();
						flowRecord2.setCode("HC");
						flowRecord2.setCreateTime(new Date());
						flowRecord2.setPrice(-otcpOrder.getNumber());
						flowRecord2.setSellerId(sell.getSellerId());
						flowRecord2.setSerialno(otcpOrder.getSerialno());
						flowRecord2.setRemark("出售HC,出售数量："+otcpOrder.getNumber());
						flowRecord2.setSource("出售已完成");
						flowRecord2.setUserId(sell.getUserId());
						flowRecord2.setType(FlowRecordConstant.SELL_HC_COIN_SUCCESS);
						sellerMapper.addUserAccountFlowRecord(flowRecord2);

						
        			}else if(otcpOrder.getType() ==2) {//承兑商区
        				SellOtcpOrder sellOtcpOrder = otcpOrderMapper.findSellerOtcpOrderById(otcpOrder.getOtcOrderId());
        				if(sellOtcpOrder.getType() ==1) {
        					//卖家,会员账户
                			SellerWallter sellerWallter = new SellerWallter();
                			sellerWallter.setCode("HC");
                			sellerWallter.setSellerId(otcpOrder.getSellerId());
                			List<SellerWallter> sellerWallterList = sellerMapper.findSellerWallter(sellerWallter);
                			sellerWallter = sellerWallterList.get(0);
                			Seller sellerOtcp = sellerMapper.findSellerbyId(otcpOrder.getSellerId());
							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(sellerWallter.getAvailableBalance());
							
							logger.info("otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，卖家会员【"
									+sellerWallter.getSellerId()+"】，可用余额："
									+sellerWallter.getAvailableBalance()
									+",冻结余额:"+sellerWallter.getFrozenBalance()
									+"购买数量:"+otcpOrder.getNumber());
			            			
							
                			sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-otcpOrder.getNumber());
                			sellerWallter.setTotalBalance(sellerWallter.getTotalBalance()-otcpOrder.getNumber());
                			sellerWallter.setUpdateTime(new Date());
                			int result = sellerMapper.updateSellerWallter(sellerWallter);
                			if(result <=0) {
                				throw new WallterException("确认收款失败");
                			}
                			
                			logger.info("处理后otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，卖家会员【"
        							+sellerWallter.getSellerId()+"】，可用余额："
        							+sellerWallter.getAvailableBalance()
        							+",冻结余额:"+sellerWallter.getFrozenBalance()
        							+"购买数量:"+otcpOrder.getNumber());
                			
                			
                			updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
							updateRecord.setCode("HC");
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(sellerOtcp.getAccount());
							updateRecord.setSource("会员HC");
							updateRecord.setType("出售");
							updateRecord.setRemark("出售");
							updateRecord.setPrice(-otcpOrder.getNumber());
							updateRecord.setRoleId(1L);
							updateRecord.setSerialno(otcpOrder.getSerialno());
							updateRecord.setAccountId(sellerWallter.getSellerId());
							sellerMapper.addAccountUpdateRecord(updateRecord);

							SellerAccountFlowRecord flowRecord2 = new SellerAccountFlowRecord();
							flowRecord2.setCode("HC");
							flowRecord2.setCreateTime(new Date());
							flowRecord2.setPrice(-otcpOrder.getNumber());
							flowRecord2.setSellerId(sellerWallter.getSellerId());
							flowRecord2.setRemark("出售HC已完成："+otcpOrder.getNumber());
							flowRecord2.setSerialno(otcpOrder.getSerialno());
							flowRecord2.setSource("出售已完成");
							flowRecord2.setType(FlowRecordConstant.SELL_HC_COIN_SUCCESS);
							sellerMapper.addSellerAccountFlowRecord(flowRecord2);
							
        				}else if(sellOtcpOrder.getType().equals(4) || sellOtcpOrder.getType().equals(3) ) {
        					//卖家,商户账户或者代理商
        					UserWallter sellerWallter = new UserWallter();
        					sellerWallter.setType(2);
        					sellerWallter.setUserId(otcpOrder.getSellerId());
                			List<UserWallter> sellerWallterList = sellerMapper.findUserWallterList(sellerWallter);
                			sellerWallter = sellerWallterList.get(0);
                			
                			AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(sellerWallter.getFrozenBalance());
							
							logger.info("otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，卖家商户/代理商【"
									+sellerWallter.getUserId()+"】，可用余额："
									+sellerWallter.getAvailableBalance()
									+",冻结余额:"+sellerWallter.getFrozenBalance()
									+"购买数量:"+otcpOrder.getNumber());
							
                			sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-otcpOrder.getNumber());
                			sellerWallter.setTotalBalance(sellerWallter.getTotalBalance()-otcpOrder.getNumber());
                			sellerWallter.setUpdateTime(new Date());
                			int result = sellerMapper.updateUserWallter(sellerWallter);
                			if(result <=0) {
                				throw new WallterException("确认收款失败");
                			}
                			
                			logger.info("处理后otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，卖家商户/代理商【"
									+sellerWallter.getUserId()+"】，可用余额："
									+sellerWallter.getAvailableBalance()
									+",冻结余额:"+sellerWallter.getFrozenBalance()
									+"购买数量:"+otcpOrder.getNumber());
                			
                			User user = userMapper.selectById(sellerWallter.getUserId());
                			updateRecord.setAfterPrice(sellerWallter.getFrozenBalance());
							updateRecord.setCode("HC");
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(user.getAccountCode());
							if("2".equals(user.getRoleId())) {
								updateRecord.setSource("商户HC");
								updateRecord.setRoleId(2L);
							}else {
								updateRecord.setSource("代理商HC");
								updateRecord.setRoleId(4L);
							}
							updateRecord.setType("出售");
							updateRecord.setRemark("出售");
							updateRecord.setPrice(-otcpOrder.getNumber());
							updateRecord.setSerialno(otcpOrder.getSerialno());
							updateRecord.setAccountId(sellerWallter.getUserId());
							sellerMapper.addAccountUpdateRecord(updateRecord);
        				}
        				
            			//买家,承兑商
            			Seller buyer  = sellerMapper.findSellerbyId(otcpOrder.getBuyerId());
            			UserWallter userWallter = new UserWallter();
            			userWallter.setUserId(buyer.getUserId());
            			List<UserWallter> userWallterList = sellerMapper.findUserWallterList(userWallter);
            			userWallter = userWallterList.get(0);
            			AccountUpdateRecord updateRecord2 = new AccountUpdateRecord();
						updateRecord2.setBeforePrice(userWallter.getAvailableBalance());
						
						logger.info("otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，买家承兑商【"
								+userWallter.getUserId()+"】，可用余额："
								+userWallter.getAvailableBalance()
								+",冻结余额:"+userWallter.getFrozenBalance()
								+"购买数量:"+otcpOrder.getNumber());
						
            			userWallter.setAvailableBalance(userWallter.getAvailableBalance()+otcpOrder.getNumber());
            			userWallter.setTotalBalance(userWallter.getTotalBalance()+otcpOrder.getNumber());
            			userWallter.setUpdateTime(new Date());
            			int result = sellerMapper.updateUserWallter(userWallter);
            			if(result <=0) {
            				throw new WallterException("确认收款失败");
            			}
            			
            			logger.info("处理后otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，买家承兑商【"
								+userWallter.getUserId()+"】，可用余额："
								+userWallter.getAvailableBalance()
								+",冻结余额:"+userWallter.getFrozenBalance()
								+"购买数量:"+otcpOrder.getNumber());
            			
            			
            			updateRecord2.setAfterPrice(userWallter.getAvailableBalance());
						updateRecord2.setCode("HC");
						updateRecord2.setCreateTime(new Date());
						updateRecord2.setPhone(buyer.getAccount());
						updateRecord2.setSource("承兑HC");
						updateRecord2.setType("购买");
						updateRecord2.setRemark("购买");
						updateRecord2.setPrice(otcpOrder.getNumber());
						updateRecord2.setRoleId(3L);
						updateRecord2.setSerialno(otcpOrder.getSerialno());
						updateRecord2.setAccountId(userWallter.getUserId());
						sellerMapper.addAccountUpdateRecord(updateRecord2);
            			
            			UserAccountFlowRecord userFlowRecord = new UserAccountFlowRecord();
            			userFlowRecord.setCode("HC");
            			userFlowRecord.setCreateTime(new Date());
            			userFlowRecord.setPrice(otcpOrder.getNumber());
            			userFlowRecord.setSellerId(buyer.getSellerId());
            			userFlowRecord.setUserId(buyer.getUserId());
            			userFlowRecord.setSource("购买HC");
            			userFlowRecord.setType(FlowRecordConstant.BUY_HC_COIN);
            			sellerMapper.addUserAccountFlowRecord(userFlowRecord);
            			
            			//返利承兑商的
            			Double rebateValue =0.0;
            			AccepterRebateSetting rebateSetting = sellerMapper.findAccepterRebateSetting(otcpOrder.getPayMethodType(),otcpOrder.getSymbols());
            			if(rebateSetting != null && rebateSetting.getValue() != null && rebateSetting.getValue() >0) {
            				SellerProfitWallter profitWallter = new SellerProfitWallter();
            				profitWallter.setCode("2");
            				profitWallter.setSellerId(buyer.getSellerId());
            				List<SellerProfitWallter> list = sellerMapper.findSellerProfitWallterList(profitWallter);
            				if(list != null && list.size() >0) {
            					profitWallter = list.get(0);
            					rebateValue = otcpOrder.getNumber()*rebateSetting.getValue()/100;
            					AccountUpdateRecord updateRecord3 = new AccountUpdateRecord();
								updateRecord3.setBeforePrice(profitWallter.getAvailableBalance());
								
								logger.info("otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，承兑商返利【"
										+profitWallter.getSellerId()+"】，可用余额："
										+profitWallter.getAvailableBalance()
										+",冻结余额:"+profitWallter.getFrozenBalance()
										+"返利余额:"+rebateValue);
								
            					profitWallter.setAvailableBalance(profitWallter.getAvailableBalance()+rebateValue);
            					profitWallter.setTotalBalance(profitWallter.getTotalBalance()+rebateValue);
            					profitWallter.setUpdateTime(new Date());
            					result =sellerMapper.updateSellerProfitWallter(profitWallter);
            					if(result <=0) {
            						throw new WallterException("确认收款失败");
            					}
            					
            					logger.info("处理后otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，承兑商返利【"
										+profitWallter.getSellerId()+"】，可用余额："
										+profitWallter.getAvailableBalance()
										+",冻结余额:"+profitWallter.getFrozenBalance()
										+"返利余额:"+rebateValue);
            					
            					updateRecord3.setAfterPrice(profitWallter.getAvailableBalance());
								updateRecord3.setCode("HC");
								updateRecord3.setCreateTime(new Date());
								updateRecord3.setPhone(buyer.getAccount());
								updateRecord3.setSource("承兑HC");
								updateRecord3.setType("承兑挖矿");
								updateRecord3.setRemark("承兑挖矿");
								updateRecord3.setPrice(rebateValue);
								updateRecord3.setRoleId(1l);
								updateRecord3.setSerialno(otcpOrder.getSerialno());
								updateRecord3.setAccountId(profitWallter.getSellerId());
								sellerMapper.addAccountUpdateRecord(updateRecord3);
            					
            					
            					//流水记录
            					SellerProfitAccountFlowRecord profitFlowRecord = new SellerProfitAccountFlowRecord();
            					profitFlowRecord.setCode(profitWallter.getCode());
            					profitFlowRecord.setCreateTime(new Date());
            					profitFlowRecord.setPrice(rebateValue);
            					profitFlowRecord.setSellerId(buyer.getSellerId());
            					profitFlowRecord.setUserId(buyer.getUserId());
            					profitFlowRecord.setSource("购买HC返利");
            					profitFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
            					sellerMapper.addSellerProfitFlowRecord(profitFlowRecord);
            					
            				}
            			}
            			
            			//返利上级承兑商
            			SuperiorAccepterRebateSetting superiorSetting = sellerMapper.findSuperiorAccepterRebateSetting(otcpOrder.getPayMethodType(),otcpOrder.getSymbols());
            			if(superiorSetting != null && superiorSetting.getValue() != null && superiorSetting.getValue()>0) {
            				Seller superiaorSeller = sellerMapper.findSellerbyId(buyer.getReferceId());
            				if(superiaorSeller != null && superiaorSeller.getIsAccepter() ==1) {
            					SellerProfitWallter superiorProfit = new SellerProfitWallter();
            					superiorProfit.setCode("2");
            					superiorProfit.setSellerId(superiaorSeller.getSellerId());
            					List<SellerProfitWallter> list = sellerMapper.findSellerProfitWallterList(superiorProfit);
                				if(list != null && list.size() >0) {
                					superiorProfit = list.get(0);
                					rebateValue = rebateValue*superiorSetting.getValue()/100;
                					

									AccountUpdateRecord updateRecord3 = new AccountUpdateRecord();
									updateRecord3.setBeforePrice(superiorProfit.getAvailableBalance());
                					
									logger.info("otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，上级承兑商返利【"
											+superiorProfit.getSellerId()+"】，可用余额："
											+superiorProfit.getAvailableBalance()
											+",冻结余额:"+superiorProfit.getFrozenBalance()
											+"返利余额:"+rebateValue);
									
                					superiorProfit.setAvailableBalance(superiorProfit.getAvailableBalance()+rebateValue);
                					superiorProfit.setTotalBalance(superiorProfit.getTotalBalance()+rebateValue);
                					superiorProfit.setUpdateTime(new Date());
                					result =sellerMapper.updateSellerProfitWallter(superiorProfit);
                					if(result <=0) {
                						throw new WallterException("确认收款失败");
                					}
                					
                					logger.info("处理后otcp冻结申诉,订单号【"+otcpOrder.getSerialno()+"】，判买家胜利，上级承兑商返利【"
											+superiorProfit.getSellerId()+"】，可用余额："
											+superiorProfit.getAvailableBalance()
											+",冻结余额:"+superiorProfit.getFrozenBalance()
											+"返利余额:"+rebateValue);
                					
                					Seller sup = sellerMapper.findSellerbyId(superiaorSeller.getSellerId());
                					updateRecord3.setAfterPrice(superiorProfit.getAvailableBalance());
									updateRecord3.setCode("HC");
									updateRecord3.setCreateTime(new Date());
									updateRecord3.setPhone(sup.getPhone());
									updateRecord3.setSource("承兑HC");
									updateRecord3.setType("上级承兑返利");
									updateRecord3.setRemark("上级承兑返利");
									updateRecord3.setPrice(rebateValue);
									updateRecord3.setRoleId(1l);
									updateRecord3.setSerialno(otcpOrder.getSerialno());
									updateRecord3.setAccountId(superiorProfit.getSellerId());
									sellerMapper.addAccountUpdateRecord(updateRecord3);
                					
                					//流水记录
                					SellerProfitAccountFlowRecord profitFlowRecord = new SellerProfitAccountFlowRecord();
                					profitFlowRecord.setCode("2");
                					profitFlowRecord.setCreateTime(new Date());
                					profitFlowRecord.setPrice(rebateValue);
                					profitFlowRecord.setSellerId(superiaorSeller.getSellerId());
                					profitFlowRecord.setUserId(superiaorSeller.getUserId());
                					profitFlowRecord.setSource("购买HC返利");
                					profitFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
                					sellerMapper.addSellerProfitFlowRecord(profitFlowRecord);
                				}
            					
            				}
            			}
        			}
					return ResponseData.success();
				}
			}
		}
		return ResponseData.error("审核失败");
	}

	@Transactional
	public ResponseData sellerWarn(Long id) {
		//卖家胜利，买家在未支付情况下，点击了我已付款，将其订单状态变成未支付
		OtcpOrder otcpOrder = otcpOrderMapper.selectById(id);
		if(otcpOrder != null) {
			if(otcpOrder.getNoAppealStatus() ==2 && otcpOrder.getIsAppeal() ==1) {
    			SellOtcpOrder sellOtcpOrder = otcpOrderMapper.findSellerOtcpOrderById(otcpOrder.getOtcOrderId());
    			if(sellOtcpOrder != null) {	
    				otcpOrder.setUpdateTime(new Date());
    				otcpOrder.setCloseTime(new Date());
    				otcpOrder.setIsAppeal(3);
    				otcpOrder.setStatus(7);
					otcpOrder.setUpdateUserId(ShiroKit.getUser().getId());
					otcpOrder.setUserAccount(ShiroKit.getUser().getAccount());
    				otcpOrderMapper.updateById(otcpOrder);			
    				sellOtcpOrder.setSupNumber(sellOtcpOrder.getSupNumber()+otcpOrder.getNumber());
    				sellOtcpOrder.setStatus(1);
    				sellOtcpOrder.setUpdateTime(new Date());
    				otcpOrderMapper.updateSellOtcpOrder(sellOtcpOrder);
    				return ResponseData.success(200, "取消成功", null);
    			}
			}
		}
		return ResponseData.error("审核失败");
	}

	@Transactional
	public ResponseData cannelTrade(Long orderId) {
		OtcpOrder otcpOrder = otcpOrderMapper.selectById(orderId);
		if(otcpOrder != null && otcpOrder.getStatus() ==1) {
			SellOtcpOrder sellOtcpOrder = otcpOrderMapper.findSellerOtcpOrderById(otcpOrder.getOtcOrderId());
			if(sellOtcpOrder != null) {	
				otcpOrder.setUpdateTime(new Date());
				otcpOrder.setCloseTime(new Date());
				otcpOrder.setUpdateUserId(ShiroKit.getUser().getId());
				otcpOrder.setUserAccount(ShiroKit.getUser().getAccount());
				otcpOrder.setStatus(7);
				otcpOrderMapper.updateById(otcpOrder);
				if(sellOtcpOrder.getStatus()==1) {
					logger.info("取消交易,订单号【"+otcpOrder.getSerialno()+"】，更新卖家出售的订单，剩余数量："
    						+sellOtcpOrder.getSupNumber()+",退回数量:"+otcpOrder.getNumber()
							);
					sellOtcpOrder.setSupNumber(sellOtcpOrder.getSupNumber()+otcpOrder.getNumber());
					sellOtcpOrder.setUpdateTime(new Date());
					otcpOrderMapper.updateSellOtcpOrder(sellOtcpOrder);
					logger.info("处理后取消交易,订单号【"+otcpOrder.getSerialno()+"】，更新卖家出售的订单，剩余数量："
    						+sellOtcpOrder.getSupNumber()+",退回数量:"+otcpOrder.getNumber()
							);
				}else {//已完成或者已取消退回商家的余额
					UserWallter userWallter = new UserWallter();
					userWallter.setType(2);
					userWallter.setUserId(sellOtcpOrder.getUserId());
					List<UserWallter> list = sellerMapper.findUserWallterList(userWallter);
					if(list != null &&  list.size()>0) {
						userWallter = list.get(0);
						Double reDouble = otcpOrder.getNumber();
						if(sellOtcpOrder.getFeePrice()  != null && sellOtcpOrder.getFeePrice() >0) {
							reDouble = reDouble +sellOtcpOrder.getFeePrice();
						}
						
						AccountUpdateRecord updateRecord3 = new AccountUpdateRecord();
						updateRecord3.setBeforePrice(userWallter.getAvailableBalance());
						
						
						logger.info("取消交易,订单号【"+otcpOrder.getSerialno()+"】，卖家【"
	    						+userWallter.getUserId()+"】,可用余额:"+userWallter.getAvailableBalance()+",冻结余额："+userWallter.getFrozenBalance()
								+",更新可用余额的数量为"+reDouble+",更新冻结数量："+otcpOrder.getNumber());
						
						userWallter.setAvailableBalance(userWallter.getAvailableBalance()+reDouble);
						userWallter.setFrozenBalance(userWallter.getFrozenBalance()-otcpOrder.getNumber());
						userWallter.setUpdateTime(new Date());
						int result = sellerMapper.updateUserWallter(userWallter);
						if(result <=0) {
							throw new WallterException("取消失败");
						}
						
						logger.info("处理后取消交易,订单号【"+otcpOrder.getSerialno()+"】，卖家【"
	    						+userWallter.getUserId()+"】,可用余额:"+userWallter.getAvailableBalance()+",冻结余额："+userWallter.getFrozenBalance()
								+",更新可用余额的数量为"+reDouble+",更新冻结数量："+otcpOrder.getNumber());
						
						User user  = userMapper.selectById(userWallter.getUserId());
						updateRecord3.setAfterPrice(userWallter.getAvailableBalance());
						updateRecord3.setCode("HC");
						updateRecord3.setCreateTime(new Date());
						updateRecord3.setPhone(user.getAccountCode());
						if("2".equals(user.getRoleId())) {
							updateRecord3.setSource("商户HC");
							updateRecord3.setRemark("商户出售，取消交易");
							updateRecord3.setRoleId(2l);
						}else {
							updateRecord3.setSource("代理商HC");
							updateRecord3.setRemark("代理商出售，取消交易");
							updateRecord3.setRoleId(4l);
						}
				
						updateRecord3.setType("取消出售交易");
						updateRecord3.setPrice(reDouble);
						updateRecord3.setSerialno(otcpOrder.getSerialno());
						updateRecord3.setAccountId(user.getUserId());
						sellerMapper.addAccountUpdateRecord(updateRecord3);
						
						UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
						flowRecord.setCode("HC");
						flowRecord.setCreateTime(new Date());
						flowRecord.setPrice(reDouble);
						flowRecord.setUserId(userWallter.getUserId());
						flowRecord.setSource("取消交易，退回资金");
						flowRecord.setType(FlowRecordConstant.SELL_HC_COIN_CANNEL);
						sellerMapper.addUserAccountFlowRecord(flowRecord);
					}
				}
				
				return ResponseData.success(200, "取消成功", null);
			}
		}
		OtcpOrderCannelNumberRecord record = otcpOrderMapper.findOtcpOrderCannelBySellerIdToday(otcpOrder.getSellerId());
		if(record == null) {
			record = new OtcpOrderCannelNumberRecord();
			record.setCreateTime(new Date());
			record.setNumber(1);
			record.setSellerId(otcpOrder.getSellerId());
			otcpOrderMapper.addOtcpOrderCannelNumberRecord(record);
		}else {
			record.setNumber(record.getNumber()+1);
			otcpOrderMapper.updateOtcpOrderCannelNumberRecord(record);
		}
		return ResponseData.error("取消失败");
	}
	
	
	@Transactional
	public ResponseData getMoney(Long orderId) {
		
		OtcpOrder otcpOrder = otcpOrderMapper.selectById(orderId);
		if(otcpOrder != null && ShiroKit.getUser().getId().equals(otcpOrder.getSellerId()) && otcpOrder.getStatus() ==2) {
			otcpOrder.setStatus(4);
			otcpOrder.setUpdateTime(new Date());
			otcpOrder.setUpdateUserId(ShiroKit.getUser().getId());
			otcpOrder.setUserAccount(ShiroKit.getUser().getAccount());
			 int ooCount=	otcpOrderMapper.updateById(otcpOrder);	
			 if(ooCount<=0)
			 {
				 throw new WallterException("订单更新异常");
			 }
			 if(otcpOrder.getType() ==2) {//承兑商区
				//卖家,商户账户或者代理商
				UserWallter userWallter = new UserWallter();
    			userWallter.setType(2);
    			userWallter.setUserId(ShiroKit.getUser().getId());
    			List<UserWallter> userWallterList = sellerMapper.findUserWallterList(userWallter);
    			userWallter = userWallterList.get(0);
    			
    			AccountUpdateRecord updateRecord2 = new AccountUpdateRecord();
				updateRecord2.setBeforePrice(userWallter.getFrozenBalance());
				logger.info("出售OTC,商户或者代理商确认收款：订单号【"+otcpOrder.getSerialno()+"】,商户/代理商【"
						+userWallter.getUserId()+"】，可用余额："
						+userWallter.getAvailableBalance()+
						",冻结余额："+userWallter.getFrozenBalance()
						+",扣除数量："+otcpOrder.getNumber());
    			userWallter.setFrozenBalance(userWallter.getFrozenBalance()-otcpOrder.getNumber());
    			userWallter.setTotalBalance(userWallter.getTotalBalance()-otcpOrder.getNumber());
    			userWallter.setUpdateTime(new Date());
    			int result = sellerMapper.updateUserWallter(userWallter);
    			if(result <=0) {
    				throw new WallterException("确认收款失败");
    			}
    			logger.info("处理后出售OTC,商户或者代理商确认收款：订单号【"+otcpOrder.getSerialno()+"】,商户/代理商【"
    					+userWallter.getUserId()+"】，可用余额："
    					+userWallter.getAvailableBalance()+
    					",冻结余额："+userWallter.getFrozenBalance()
    					+",扣除数量："+otcpOrder.getNumber());
    			
    			User user = userMapper.selectById(userWallter.getUserId());
    			updateRecord2.setAfterPrice(userWallter.getFrozenBalance());
				updateRecord2.setCode("HC");
				updateRecord2.setCreateTime(new Date());
				updateRecord2.setAccountId(userWallter.getUserId());
				updateRecord2.setSerialno(otcpOrder.getSerialno());
				updateRecord2.setPhone(user.getAccountCode());
				if("2".equals(user.getRoleId())) {
					updateRecord2.setSource("商户HC");
					updateRecord2.setRoleId(2L);
				}else {
					updateRecord2.setSource("代理商HC");
					updateRecord2.setRoleId(4L);
				}
				updateRecord2.setType("出售");
				updateRecord2.setRemark("出售");
				updateRecord2.setPrice(-otcpOrder.getNumber());
				sellerMapper.addAccountUpdateRecord(updateRecord2);
    			
    			
    			//买家,承兑商
    			Seller buyer  = sellerMapper.findSellerbyId(otcpOrder.getBuyerId());
    			UserWallter buyerWallter = new UserWallter();
    			buyerWallter.setUserId(buyer.getUserId());
    			buyerWallter.setType(2);
    			List<UserWallter> buyerWallterList = sellerMapper.findUserWallterList(buyerWallter);
    			buyerWallter = buyerWallterList.get(0);
    			
    			
    			AccountUpdateRecord updateRecord = new AccountUpdateRecord();
    			updateRecord.setBeforePrice(buyerWallter.getAvailableBalance());
    			
    			logger.info("出售OTC,商户或者代理商确认收款：订单号【"+otcpOrder.getSerialno()+"】,买家承兑商【"
    					+buyerWallter.getUserId()+"】，可用余额："
    					+buyerWallter.getAvailableBalance()+
    					",冻结余额："+buyerWallter.getFrozenBalance()
    					+",扣除数量："+otcpOrder.getNumber());
    			buyerWallter.setAvailableBalance(buyerWallter.getAvailableBalance()+otcpOrder.getNumber());
    			buyerWallter.setTotalBalance(buyerWallter.getTotalBalance()+otcpOrder.getNumber());
    			buyerWallter.setUpdateTime(new Date());
    			result = sellerMapper.updateUserWallter(buyerWallter);
    			if(result <=0) {
    				throw new WallterException("确认收款失败");
    			}
    			logger.info("处理后出售OTC,商户或者代理商确认收款：订单号【"+otcpOrder.getSerialno()+"】,买家承兑商【"
    					+buyerWallter.getUserId()+"】，可用余额："
    					+buyerWallter.getAvailableBalance()+
    					",冻结余额："+buyerWallter.getFrozenBalance()
    					+",扣除数量："+otcpOrder.getNumber());
    			updateRecord.setAfterPrice(buyerWallter.getAvailableBalance());
    			updateRecord.setCode("HC");
    			updateRecord.setCreateTime(new Date());
    			updateRecord.setPhone(buyer.getAccount());
    			updateRecord.setSource("承兑HC");
    			updateRecord.setType("购买");
    			updateRecord.setRemark("购买");
    			updateRecord.setPrice(otcpOrder.getNumber());
    			updateRecord.setRoleId(3L);
    			updateRecord.setSerialno(otcpOrder.getSerialno());
    			updateRecord.setAccountId(buyerWallter.getUserId());
    			sellerMapper.addAccountUpdateRecord(updateRecord);
    			
    			UserAccountFlowRecord userFlowRecord2 = new UserAccountFlowRecord();
    			userFlowRecord2.setCode("HC");
    			userFlowRecord2.setCreateTime(new Date());
    			userFlowRecord2.setPrice(otcpOrder.getNumber());
    			userFlowRecord2.setSellerId(buyer.getSellerId());
    			userFlowRecord2.setUserId(buyer.getUserId());
    			userFlowRecord2.setSource("购买HC");
    			userFlowRecord2.setType(FlowRecordConstant.BUY_HC_COIN);
    			sellerMapper.addUserAccountFlowRecord(userFlowRecord2);
    			
    			//返利承兑商的
    			Double rebateValue =0.0;
    			AccepterRebateSetting rebateSetting = sellerMapper.findAccepterRebateSetting(otcpOrder.getPayMethodType(),otcpOrder.getSymbols());
    			if(rebateSetting != null && rebateSetting.getValue() != null && rebateSetting.getValue() >0) {
    				SellerProfitWallter profitWallter = new SellerProfitWallter();
    				profitWallter.setCode("2");
    				profitWallter.setSellerId(buyer.getSellerId());
    				List<SellerProfitWallter> list = sellerMapper.findSellerProfitWallterList(profitWallter);
    				if(list != null && list.size() >0) {
    					profitWallter = list.get(0);
    					 rebateValue = otcpOrder.getNumber()*rebateSetting.getValue()/100;
    					 
    					 AccountUpdateRecord updateRecord3 = new AccountUpdateRecord();
							updateRecord3.setBeforePrice(profitWallter.getAvailableBalance());
							logger.info("出售OTC,商户或者代理商确认收款：订单号【"+otcpOrder.getSerialno()+"】,承兑商【"
			    					+profitWallter.getSellerId()+"】返利，可用余额："
			    					+profitWallter.getAvailableBalance()+
			    					",冻结余额："+profitWallter.getFrozenBalance()
			    					+",返利数量："+rebateValue);
    					profitWallter.setAvailableBalance(profitWallter.getAvailableBalance()+rebateValue);
    					profitWallter.setTotalBalance(profitWallter.getTotalBalance()+rebateValue);
    					profitWallter.setUpdateTime(new Date());
    					result =sellerMapper.updateSellerProfitWallter(profitWallter);
    					if(result <=0) {
    						throw new WallterException("确认收款失败");
    					}
    					
    					logger.info("处理后出售OTC,商户或者代理商确认收款：订单号【"+otcpOrder.getSerialno()+"】,承兑商【"
		    					+profitWallter.getSellerId()+"】返利，可用余额："
		    					+profitWallter.getAvailableBalance()+
		    					",冻结余额："+profitWallter.getFrozenBalance()
		    					+",返利数量："+rebateValue);
    					updateRecord3.setAfterPrice(profitWallter.getAvailableBalance());
						updateRecord3.setCode("HC");
						updateRecord3.setCreateTime(new Date());
						updateRecord3.setPhone(buyer.getAccount());
						updateRecord3.setSource("承兑HC");
						updateRecord3.setType("承兑挖矿");
						updateRecord3.setRemark("承兑挖矿");
						updateRecord3.setPrice(rebateValue);
						updateRecord3.setRoleId(1l);
						updateRecord3.setSerialno(otcpOrder.getSerialno());
						updateRecord3.setAccountId(profitWallter.getSellerId());
						sellerMapper.addAccountUpdateRecord(updateRecord3);
    					
    					//流水记录
    					SellerProfitAccountFlowRecord profitFlowRecord = new SellerProfitAccountFlowRecord();
    					profitFlowRecord.setCode(profitWallter.getCode());
    					profitFlowRecord.setCreateTime(new Date());
    					profitFlowRecord.setPrice(rebateValue);
    					profitFlowRecord.setSellerId(buyer.getSellerId());
    					profitFlowRecord.setUserId(buyer.getUserId());
    					profitFlowRecord.setSource("购买HC返利");
    					profitFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
    					sellerMapper.addSellerProfitFlowRecord(profitFlowRecord);
    					
    				}
    			}
    			
    			//返利上级承兑商
    			SuperiorAccepterRebateSetting superiorSetting = sellerMapper.findSuperiorAccepterRebateSetting(otcpOrder.getPayMethodType(),otcpOrder.getSymbols());
    			if(superiorSetting != null && superiorSetting.getValue() != null && superiorSetting.getValue()>0) {
    				Seller superiaorSeller = sellerMapper.findSellerbyId(buyer.getReferceId());
    				if(superiaorSeller != null && superiaorSeller.getIsAccepter() ==1) {
    					SellerProfitWallter superiorProfit = new SellerProfitWallter();
    					superiorProfit.setCode("HC");
    					superiorProfit.setSellerId(superiaorSeller.getSellerId());
    					List<SellerProfitWallter> list = sellerMapper.findSellerProfitWallterList(superiorProfit);
        				if(list != null && list.size() >0) {
        					superiorProfit = list.get(0);
        					rebateValue = rebateValue*superiorSetting.getValue()/100;
        					
        					AccountUpdateRecord updateRecord3 = new AccountUpdateRecord();
							updateRecord3.setBeforePrice(superiorProfit.getAvailableBalance());
							logger.info("出售OTC,商户或者代理商确认收款：订单号【"+otcpOrder.getSerialno()+"】,上级承兑商【"
			    					+superiorProfit.getSellerId()+"】返利，可用余额："
			    					+superiorProfit.getAvailableBalance()+
			    					",冻结余额："+superiorProfit.getFrozenBalance()
			    					+",返利数量："+rebateValue);
        					superiorProfit.setAvailableBalance(superiorProfit.getAvailableBalance()+rebateValue);
        					superiorProfit.setTotalBalance(superiorProfit.getTotalBalance()+rebateValue);
        					superiorProfit.setUpdateTime(new Date());
        					result =sellerMapper.updateSellerProfitWallter(superiorProfit);
        					if(result <=0) {
        						throw new WallterException("确认收款失败");
        					}
        					logger.info("处理后出售OTC,商户或者代理商确认收款：订单号【"+otcpOrder.getSerialno()+"】,上级承兑商【"
			    					+superiorProfit.getSellerId()+"】返利，可用余额："
			    					+superiorProfit.getAvailableBalance()+
			    					",冻结余额："+superiorProfit.getFrozenBalance()
			    					+",返利数量："+rebateValue);
        					Seller sup = sellerMapper.findSellerbyId(superiaorSeller.getSellerId());
        					updateRecord3.setAfterPrice(superiorProfit.getAvailableBalance());
							updateRecord3.setCode("HC");
							updateRecord3.setCreateTime(new Date());
							updateRecord3.setPhone(sup.getPhone());
							updateRecord3.setSource("承兑HC");
							updateRecord3.setType("上级承兑返利");
							updateRecord3.setRemark("上级承兑返利");
							updateRecord3.setPrice(rebateValue);
							updateRecord3.setRoleId(1l);
							updateRecord3.setSerialno(otcpOrder.getSerialno());
							updateRecord3.setAccountId(superiorProfit.getSellerId());
							sellerMapper.addAccountUpdateRecord(updateRecord3);
        					
        					//流水记录
        					SellerProfitAccountFlowRecord profitFlowRecord = new SellerProfitAccountFlowRecord();
        					profitFlowRecord.setCode("2");
        					profitFlowRecord.setCreateTime(new Date());
        					profitFlowRecord.setPrice(rebateValue);
        					profitFlowRecord.setSellerId(superiaorSeller.getSellerId());
        					profitFlowRecord.setUserId(superiaorSeller.getUserId());
        					profitFlowRecord.setSource("购买HC返利");
        					profitFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
        					sellerMapper.addSellerProfitFlowRecord(profitFlowRecord);
        				}
    					
    				}
    			}
			}
			 
			return ResponseData.success(200,"确认收款成功",null);
		}
		
		return ResponseData.error("确认收款失败");
	}

	public ResponseData sumbtiAppeal(Long orderId, String content, String image) {
		OtcpOrder otcpOrder = otcpOrderMapper.selectById(orderId);
		if(otcpOrder != null && otcpOrder.getStatus()==2) {
			otcpOrder.setAppealContent(content);
			otcpOrder.setAppealerId(ShiroKit.getUser().getId());
			otcpOrder.setAppealerRole(2);
			otcpOrder.setAppealTime(new Date());
			otcpOrder.setIsAppeal(1);
			otcpOrder.setCertificate(image);
			otcpOrder.setNoAppealStatus(otcpOrder.getStatus());
			otcpOrder.setStatus(6);
			otcpOrderMapper.updateById(otcpOrder);
			return ResponseData.success();
		}
		return ResponseData.error("申诉失败");
	}

	
}
