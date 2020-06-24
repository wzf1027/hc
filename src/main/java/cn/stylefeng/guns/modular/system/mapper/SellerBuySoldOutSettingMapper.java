package cn.stylefeng.guns.modular.system.mapper;

import cn.stylefeng.guns.modular.system.entity.SellerBuySoldOutSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Map;

public interface SellerBuySoldOutSettingMapper extends BaseMapper<SellerBuySoldOutSetting> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(Page page, String condition);

	
}
