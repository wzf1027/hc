package cn.stylefeng.guns.modular.system.service;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.entity.ExchangeSetting;
import cn.stylefeng.guns.modular.system.mapper.ExchangeFeeSettingMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class ExchangeFeeSettingService extends ServiceImpl<ExchangeFeeSettingMapper, ExchangeSetting> {

	@Resource
	private ExchangeFeeSettingMapper exchangeFeeSettingMapper;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(Integer roleId) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, roleId);
	}
	
}
