package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.UserWithdrawCoinAppealOrder;
import io.lettuce.core.dynamic.annotation.Param;

public interface UserWithdrawCoinAppealMapper extends BaseMapper<UserWithdrawCoinAppealOrder> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(@Param("page")Page page, 
			@Param("condition")String condition,
			@Param("phone")String phone,
			@Param("userId")Long userId, 
			@Param("address")String address,
			@Param("status")Integer status,
			@Param("beginTime")String beginTime,
			@Param("endTime") String endTime);

	
}
