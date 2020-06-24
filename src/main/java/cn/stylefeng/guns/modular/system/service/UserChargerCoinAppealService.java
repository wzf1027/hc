package cn.stylefeng.guns.modular.system.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.stylefeng.guns.core.shiro.ShiroKit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.system.entity.AccountUpdateRecord;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.entity.UserAccountFlowRecord;
import cn.stylefeng.guns.modular.system.entity.UserChargerCoinAppealOrder;
import cn.stylefeng.guns.modular.system.entity.UserWallter;
import cn.stylefeng.guns.modular.system.mapper.UserChargerCoinAppealMapper;
import cn.stylefeng.guns.modular.system.mapper.UserMapper;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Service
public class UserChargerCoinAppealService extends ServiceImpl<UserChargerCoinAppealMapper, UserChargerCoinAppealOrder> {

	@Resource
	private UserChargerCoinAppealMapper appealMapper;
	
	@Resource
	private SellerMapper sellerMapper;
	
	@Resource
	private UserMapper userMapper;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String condition,Long userId, String phone, String serialno, String hashValue, Integer status, String beginTime, String endTime) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition,userId,phone,serialno,hashValue,status,beginTime,endTime);
	}

	@Transactional
	public ResponseData updateStatus(Long appealId, Integer status) {
		UserChargerCoinAppealOrder order = this.baseMapper.selectById(appealId);
		if(order != null && order.getStatus()==1) {
			if(status ==1) {//审核通过
				order.setStatus(2);
				order.setUpdateTime(new Date());
				order.setUpdateUserId(ShiroKit.getUser().getId());
				order.setUserAccount(ShiroKit.getUser().getAccount());
				this.baseMapper.updateById(order);
				
				User user = userMapper.selectById(order.getUserId());
				UserWallter userWallter = new UserWallter();
				userWallter.setType(1);
				userWallter.setUserId(order.getUserId());
				List<UserWallter> list = sellerMapper.findUserWallterList(userWallter);
				if(list != null && list.size() >0) {
					userWallter = list.get(0);
					
					AccountUpdateRecord updateRecord = new AccountUpdateRecord();
					updateRecord.setBeforePrice(userWallter.getAvailableBalance());
					
					userWallter.setAvailableBalance(userWallter.getAvailableBalance()+order.getNumber());
					userWallter.setTotalBalance(userWallter.getTotalBalance()+order.getNumber());
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
						updateRecord.setRoleId(2L);
						updateRecord.setSource("商户USDT");
					}else {
						updateRecord.setRoleId(4L);
						updateRecord.setSource("代理商USDT");
					}
					updateRecord.setAccountId(user.getUserId());
					updateRecord.setSerialno(order.getSerialno());
					updateRecord.setType("充币");
					updateRecord.setRemark("充币");
					updateRecord.setPrice(order.getNumber());
					sellerMapper.addAccountUpdateRecord(updateRecord);
					
					UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
					flowRecord.setCode("USDT");
					flowRecord.setCreateTime(new Date());
					flowRecord.setPrice(order.getNumber());
					flowRecord.setUserId(order.getUserId());
					flowRecord.setSource("充币到账");
					sellerMapper.addUserAccountFlowRecord(flowRecord);
					return ResponseData.success();
				}
			}else {
				order.setStatus(3);
				order.setUpdateTime(new Date());
				order.setUpdateUserId(ShiroKit.getUser().getId());
				order.setUserAccount(ShiroKit.getUser().getAccount());
				this.baseMapper.updateById(order);
				return ResponseData.success();
			}
		}
		return ResponseData.error("审核失败");
	}
	
}
