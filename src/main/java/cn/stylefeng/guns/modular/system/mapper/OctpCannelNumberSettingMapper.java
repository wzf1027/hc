package cn.stylefeng.guns.modular.system.mapper;

import cn.stylefeng.guns.modular.system.entity.OtcpCannelNumberSetting;
import cn.stylefeng.guns.modular.system.entity.SellerOrderTimeSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Map;

public interface OctpCannelNumberSettingMapper extends BaseMapper<OtcpCannelNumberSetting> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(Page page, String condition);

	
}
