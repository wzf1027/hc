package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.SellerOtcFeeSetting;

public interface SellerOtcFeeSettingMapper extends BaseMapper<SellerOtcFeeSetting> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(Page page, String condition);

	
}
