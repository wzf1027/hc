package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.SellerAccpterAppeal;
import io.lettuce.core.dynamic.annotation.Param;

public interface SellerAccpterAppealMapper extends BaseMapper<SellerAccpterAppeal> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(
			@Param("page")Page page, 
			@Param("phone")String phone, 
			@Param("address")String idcardNo, 
			@Param("name")String name, 
			@Param("status")Integer status, 
			@Param("beginTime")String beginTime, 
			@Param("endTime")String endTime);

	
}
