package cn.stylefeng.guns.modular.system.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.dto.SellerAccountUpdate;
import cn.stylefeng.guns.modular.system.entity.AccountUpdateRecord;
import cn.stylefeng.guns.modular.system.mapper.AccountUpdateMgrMapper;

@Service
public class AccountUpdateMgrService extends ServiceImpl<AccountUpdateMgrMapper, AccountUpdateRecord> {

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> findAccountUpdateByCondition(SellerAccountUpdate sellerAccountUpdate, Long role,String beginTime
			,String endTime,Long userId) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.findAccountUpdateByCondition(page, sellerAccountUpdate,role,beginTime,endTime,userId);
	}

}
