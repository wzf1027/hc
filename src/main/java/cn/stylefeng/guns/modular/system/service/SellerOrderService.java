package cn.stylefeng.guns.modular.system.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.system.entity.SellerOrder;
import cn.stylefeng.guns.modular.system.mapper.SellerOrderMapper;
import cn.stylefeng.guns.modular.system.mapper.UserMapper;

@Service
public class SellerOrderService extends ServiceImpl<SellerOrderMapper, SellerOrder> {

	@Resource
	private SellerOrderMapper SellerOrderMapper;
	
	@Resource
	private SellerMapper sellerMapper;
	
	@Resource
	private UserMapper userMapper;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String condition, String phone, String beginTime, String endTime) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition,phone,beginTime,endTime);
	}

	
}
