package cn.stylefeng.guns.modular.system.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.util.FlowRecordConstant;
import cn.stylefeng.guns.modular.system.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.biz.service.SendSMSExtService;
import cn.stylefeng.guns.modular.system.mapper.SellerChargerCoinAppealMapper;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Service
public class SellerChargerCoinAppealService extends ServiceImpl<SellerChargerCoinAppealMapper, SellerChargerCoinAppealOrder> {

	@Resource
	private SellerChargerCoinAppealMapper appealMapper;
	
	@Resource
	private SellerMapper sellerMapper;
	
   @Resource
   private SendSMSExtService sendSMSExtService;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String phone, String serialno, String hashValue, Integer status, String beginTime, String endTime) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, phone,serialno,hashValue,status,beginTime,endTime);
	}

	@Transactional
	public ResponseData updateStatus(Long appealId, Integer status) {
		SellerChargerCoinAppealOrder order = this.baseMapper.selectById(appealId);
		if(order != null && order.getStatus()==1) {
			if(status ==1) {//审核通过
				order.setStatus(2);
				order.setUpdateTime(new Date());
				order.setUpdateUserId(ShiroKit.getUser().getId());
				order.setUserAccount(ShiroKit.getUser().getAccount());
				this.baseMapper.updateById(order);
				Seller seller = sellerMapper.findSellerbyId(order.getSellerId());
				if(seller != null) {
					SellerWallter sellerWallter = new SellerWallter();
					sellerWallter.setCode("USDT");
					sellerWallter.setSellerId(order.getSellerId());
					List<SellerWallter> list = sellerMapper.findSellerWallter(sellerWallter);
					if(list != null && list.size() >0) {
						sellerWallter = list.get(0);
						AccountUpdateRecord updateRecord = new AccountUpdateRecord();
						updateRecord.setBeforePrice(sellerWallter.getAvailableBalance());
						sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()+order.getNumber());
						//sellerWallter.setTotalBalance(sellerWallter.getTotalBalance()+order.getNumber());
						sellerWallter.setUpdateTime(new Date());
						int result = sellerMapper.updateSellerWallter(sellerWallter);
						if(result <=0) {
							throw new WallterException("审核失败");
						}
						
						updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
						updateRecord.setCode(sellerWallter.getCode());
						updateRecord.setCreateTime(new Date());
						updateRecord.setPhone(seller.getAccount());
						updateRecord.setSource("会员USDT");
						updateRecord.setType("充币");
						updateRecord.setRemark("充币");
						updateRecord.setPrice(order.getNumber());
						updateRecord.setRoleId(1L);
						updateRecord.setAccountId(seller.getSellerId());
						updateRecord.setSerialno(order.getSerialno());
						sellerMapper.addAccountUpdateRecord(updateRecord);

						
						SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
						flowRecord.setCode("USDT");
						flowRecord.setCreateTime(new Date());
						flowRecord.setPrice(order.getNumber());
						flowRecord.setSellerId(order.getSellerId());
						flowRecord.setType(FlowRecordConstant.RECHARGE_COIN);
						flowRecord.setSource("充币到账");
						sellerMapper.addSellerAccountFlowRecord(flowRecord);
						
						String content = "【码力】您充值的 "+order.getNumber()+"usdt 已经到账，可以进行交易。HC";
						SellerNotice notice = new SellerNotice();
						notice.setIsSee(0);
						notice.setContent(content);
						notice.setCreateTime(new Date());
						notice.setSellerId(seller.getSellerId());
						this.sellerMapper.addSellerNotice(notice);
						return ResponseData.success();
					}
				}
			}else {
				order.setStatus(3);
				order.setUpdateTime(new Date());
				order.setUserAccount(ShiroKit.getUser().getAccount());
				this.baseMapper.updateById(order);
				return ResponseData.success();
			}
		}
		return ResponseData.error("审核失败");
	}
	
}
