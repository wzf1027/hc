package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.SellerChargerCoinAppealOrder;
import io.lettuce.core.dynamic.annotation.Param;

public interface SellerChargerCoinAppealMapper extends BaseMapper<SellerChargerCoinAppealOrder> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(@Param("page")Page page, 
			@Param("phone")String phone, 
			@Param("serialno")String serialno,
			@Param("hashValue")String hashValue, 
			@Param("status")Integer status, 
			@Param("beginTime")String beginTime, 
			@Param("endTime")String endTime);

	
}
