package cn.stylefeng.guns.modular.system.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.system.entity.UserWithdrawFeeSetting;
import cn.stylefeng.guns.modular.system.mapper.UserWithdrawFeeSettingMapper;

@Service
public class UserWithdrawFeeSettingService extends ServiceImpl<UserWithdrawFeeSettingMapper, UserWithdrawFeeSetting> {

	@Resource
	private UserWithdrawFeeSettingMapper userWithdrawFeeSettingMapper;
	
	@Resource
	private SellerMapper sellerMapper;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String condition) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition);
	}

	public UserWithdrawFeeSetting getFeeSettingByRoleId(String roleId) {
	   Map<String,Object> columnMap = new HashMap<String, Object>();
	   columnMap.put("ROLE_ID", roleId);
	   List<UserWithdrawFeeSetting> list = this.baseMapper.selectByMap(columnMap);
	   if(list != null && list.size() >0) {
		   return list.get(0);
	   }
		return null;
	}
	
}
