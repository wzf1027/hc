package cn.stylefeng.guns.modular.system.mapper;

import cn.stylefeng.guns.modular.system.entity.MerchantIp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.Map;

public interface MerchantIpMapper extends BaseMapper<MerchantIp> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(@Param("page") Page page,@Param("type") Integer type,@Param("ipAddress") String ipAddress,@Param("account")String account);

	
}
