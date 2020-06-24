package cn.stylefeng.guns.modular.system.service;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.system.entity.Seller;
import cn.stylefeng.guns.modular.system.entity.SellerAccpterAppeal;
import cn.stylefeng.guns.modular.system.entity.SellerProfitWallter;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.entity.UserWallter;
import cn.stylefeng.guns.modular.system.mapper.SellerAccpterAppealMapper;
import cn.stylefeng.guns.modular.system.mapper.UserMapper;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Service
public class SellerAccpterAppealService extends ServiceImpl<SellerAccpterAppealMapper, SellerAccpterAppeal> {

	@Resource
	private SellerAccpterAppealMapper sellerAccpterAppealMapper;
	
	@Resource
	private SellerMapper sellerMapper;
	
	@Resource
	private UserMapper userMapper;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String phone, String idcardNo, String name, Integer status, String beginTime, String endTime) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, phone,idcardNo,name,status,beginTime,endTime);
	}

	@Transactional
	public ResponseData updateStatus(Long appealId, Integer status) {
		SellerAccpterAppeal order = this.baseMapper.selectById(appealId);
		if(order != null && order.getStatus()==1) {
			Seller seller = sellerMapper.findSellerbyId(order.getSellerId());
			if(seller == null) {
				 throw new ServiceException(BizExceptionEnum.REQUEST_NULL); 
			}
			if(status ==1) {//审核通过
				order.setStatus(2);
				order.setUpdateTime(new Date());
				order.setUpdateUserId(ShiroKit.getUser().getId());
				order.setUserAccount(ShiroKit.getUser().getAccount());
				this.baseMapper.updateById(order);
				
				//创建承兑商账号
				User user = new User();
				user.setAccount(seller.getPhone());
				user.setAvatar(seller.getIcon());
				user.setCreateTime(new Date());
				user.setCreateTime(new Date());
				user.setPhone(seller.getPhone());
				user.setRoleId("3");
				user.setDeptId(0L);
				user.setStatus("ENABLE");
				user.setIsAuth(0);
				user.setVersion(1);
				user.setCreateUser(1L);
				user.setSalt(ShiroKit.getRandomSalt(5));
		        user.setPassword(ShiroKit.md5("123456", user.getSalt()));
				userMapper.insertUser(user);
				
				UserWallter userWallter = new UserWallter();
				userWallter.setAvailableBalance(0.0);
				userWallter.setCreateTime(new Date());
				userWallter.setFrozenBalance(0.0);
				userWallter.setTotalBalance(0.0);
				userWallter.setType(2);
				userWallter.setVersion(1);
				userWallter.setUserId(user.getUserId());
				userMapper.insertUserWallter(userWallter);
				
				UserWallter userWallter2 = new UserWallter();
				userWallter2.setAvailableBalance(0.0);
				userWallter2.setCreateTime(new Date());
				userWallter2.setFrozenBalance(0.0);
				userWallter2.setTotalBalance(0.0);
				userWallter2.setType(1);
				userWallter2.setVersion(1);
				userWallter2.setUserId(user.getUserId());
				userMapper.insertUserWallter(userWallter2);
				
				SellerProfitWallter sellerProfitWallter = new SellerProfitWallter();
				sellerProfitWallter.setAvailableBalance(0.0);
				sellerProfitWallter.setCode("2");
				sellerProfitWallter.setCreateTime(new Date());
				sellerProfitWallter.setFrozenBalance(0.0);
				sellerProfitWallter.setSellerId(seller.getSellerId());
				sellerProfitWallter.setTotalBalance(0.0);
				sellerProfitWallter.setVersion(1);
				sellerMapper.addSellerProfitWallter(sellerProfitWallter);
				
				seller.setIsAccepter(1);
				seller.setUserId(user.getUserId());
				seller.setUpdateTime(new Date());
				int result = sellerMapper.updateSeller(seller);
				if(result <=0) {
					  throw new ServiceException(BizExceptionEnum.REQUEST_NULL); 
				}
				return ResponseData.success();
			}else {
				order.setStatus(3);
				order.setUpdateTime(new Date());
				order.setUpdateUserId(ShiroKit.getUser().getId());
				order.setUserAccount(ShiroKit.getUser().getAccount());
				this.baseMapper.updateById(order);
				seller.setIsAccepter(0);
				seller.setUpdateTime(new Date());
				int result = sellerMapper.updateSeller(seller);
				if(result <=0) {
					  throw new ServiceException(BizExceptionEnum.REQUEST_NULL); 
				}
				return ResponseData.success();
			}
		}
		return ResponseData.error("审核失败");
	}
	
}
