package cn.stylefeng.guns.modular.system.service;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.entity.OtcpCannelNumberSetting;
import cn.stylefeng.guns.modular.system.mapper.OctpCannelNumberSettingMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OtcpCannelNumberSettingService extends ServiceImpl<OctpCannelNumberSettingMapper, OtcpCannelNumberSetting> {


	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String condition) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition);
	}
	
}
