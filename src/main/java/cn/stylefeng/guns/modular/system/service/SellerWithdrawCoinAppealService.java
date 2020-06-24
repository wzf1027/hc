package cn.stylefeng.guns.modular.system.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.util.FlowRecordConstant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.system.entity.AccountUpdateRecord;
import cn.stylefeng.guns.modular.system.entity.Seller;
import cn.stylefeng.guns.modular.system.entity.SellerAccountFlowRecord;
import cn.stylefeng.guns.modular.system.entity.SellerWallter;
import cn.stylefeng.guns.modular.system.entity.SellerWithdrawCoinAppealOrder;
import cn.stylefeng.guns.modular.system.mapper.SellerChargerCoinAppealMapper;
import cn.stylefeng.guns.modular.system.mapper.SellerWithdrawCoinAppealMapper;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Service
public class SellerWithdrawCoinAppealService extends ServiceImpl<SellerWithdrawCoinAppealMapper, SellerWithdrawCoinAppealOrder> {

	@Resource
	private SellerChargerCoinAppealMapper appealMapper;
	
	@Resource
	private SellerMapper sellerMapper;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String phone, String address, Integer status, String beginTime, String endTime) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, phone,address,status,beginTime,endTime);
	}

	@Transactional
	public ResponseData updateStatus(Long appealId, Integer status) {
		SellerWithdrawCoinAppealOrder order = this.baseMapper.selectById(appealId);
		if(order != null && order.getStatus()==1) {
			if(status ==1) {//审核通过
				order.setStatus(2);
				order.setUpdateTime(new Date());
				order.setUpdateUserId(ShiroKit.getUser().getId());
				order.setUserAccount(ShiroKit.getUser().getAccount());
				this.baseMapper.updateById(order);
				
				SellerWallter sellerWallter = new SellerWallter();
				sellerWallter.setCode("USDT");
				sellerWallter.setSellerId(order.getSellerId());
				List<SellerWallter> list = sellerMapper.findSellerWallter(sellerWallter);
				if(list != null && list.size() >0) {
					sellerWallter = list.get(0);
					
					AccountUpdateRecord updateRecord = new AccountUpdateRecord();
					updateRecord.setBeforePrice(sellerWallter.getFrozenBalance());
					
					sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-order.getNumber());
					sellerWallter.setTotalBalance(sellerWallter.getTotalBalance()-order.getNumber());
					sellerWallter.setUpdateTime(new Date());
					int result = sellerMapper.updateSellerWallter(sellerWallter);
					if(result <=0) {
						throw new WallterException("审核失败");
					}
					Seller seller = sellerMapper.findSellerbyId(order.getSellerId());
					updateRecord.setAfterPrice(sellerWallter.getFrozenBalance());
					updateRecord.setCode(sellerWallter.getCode());
					updateRecord.setCreateTime(new Date());
					updateRecord.setPhone(seller.getAccount());
					updateRecord.setSource("会员USDT");
					updateRecord.setType("提币");
					updateRecord.setRemark("提币通过，从冻结余额中扣除");
					updateRecord.setPrice(-order.getNumber());
					updateRecord.setRoleId(1L);
					updateRecord.setAccountId(seller.getSellerId());
					sellerMapper.addAccountUpdateRecord(updateRecord);
					
					
					SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
					flowRecord.setCode("USDT");
					flowRecord.setCreateTime(new Date());
					flowRecord.setPrice(-order.getTotalNumber());
					flowRecord.setSellerId(order.getSellerId());
					flowRecord.setType(FlowRecordConstant.WITHDRAW_COIN);
					flowRecord.setSource("提币到账");
					sellerMapper.addSellerAccountFlowRecord(flowRecord);

					SellerAccountFlowRecord flowRecord2 = new SellerAccountFlowRecord();
					flowRecord2.setCode("USDT");
					flowRecord2.setCreateTime(new Date());
					flowRecord2.setPrice(-order.getFeePrice());
					flowRecord2.setSellerId(order.getSellerId());
					flowRecord2.setType(FlowRecordConstant.WITHDRAW_COIN_FEE);
					flowRecord2.setSource("提币到账,手续费");
					sellerMapper.addSellerAccountFlowRecord(flowRecord2);
					return ResponseData.success();
				}
			}else {
				order.setStatus(3);
				order.setUpdateTime(new Date());
				order.setUpdateUserId(ShiroKit.getUser().getId());
				order.setUserAccount(ShiroKit.getUser().getAccount());
				this.baseMapper.updateById(order);
				SellerWallter sellerWallter = new SellerWallter();
				sellerWallter.setCode("USDT");
				sellerWallter.setSellerId(order.getSellerId());
				List<SellerWallter> list = sellerMapper.findSellerWallter(sellerWallter);
				if(list != null && list.size() >0) {
					sellerWallter = list.get(0);
					
					AccountUpdateRecord updateRecord = new AccountUpdateRecord();
					updateRecord.setBeforePrice(sellerWallter.getAvailableBalance());
					sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()+order.getNumber());
					sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-order.getNumber());
					sellerWallter.setUpdateTime(new Date());
					int result = sellerMapper.updateSellerWallter(sellerWallter);
					if(result <=0) {
						throw new WallterException("审核失败");
					}
					Seller seller = sellerMapper.findSellerbyId(order.getSellerId());
					updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
					updateRecord.setCode(sellerWallter.getCode());
					updateRecord.setCreateTime(new Date());
					updateRecord.setPhone(seller.getAccount());
					updateRecord.setSource("会员USDT");
					updateRecord.setType("提币");
					updateRecord.setRemark("提币不通过，从冻结余额到可用余额中");
					updateRecord.setPrice(order.getNumber());
					updateRecord.setRoleId(1L);
					updateRecord.setAccountId(seller.getSellerId());
					sellerMapper.addAccountUpdateRecord(updateRecord);
					return ResponseData.success();
				}
			}
		}
		return ResponseData.error("审核失败");
	}
	
}
