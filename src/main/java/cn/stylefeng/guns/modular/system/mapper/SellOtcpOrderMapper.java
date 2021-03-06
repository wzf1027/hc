package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.SellOtcpOrder;
import io.lettuce.core.dynamic.annotation.Param;

public interface SellOtcpOrderMapper extends BaseMapper<SellOtcpOrder> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(@Param("page")Page page
									,@Param("condition") String condition
									,@Param("phone")  String phone
									,@Param("beginTime")  String beginTime
									,@Param("endTime")  String endTime, @Param("roleType") String roleType,@Param("userId") Long userId);

	
}
