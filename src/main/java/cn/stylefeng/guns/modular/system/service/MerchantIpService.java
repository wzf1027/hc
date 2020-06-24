package cn.stylefeng.guns.modular.system.service;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.entity.AccepterRebateSetting;
import cn.stylefeng.guns.modular.system.entity.MerchantIp;
import cn.stylefeng.guns.modular.system.mapper.AccepterRateSettingMapper;
import cn.stylefeng.guns.modular.system.mapper.MerchantIpMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class MerchantIpService extends ServiceImpl<MerchantIpMapper, MerchantIp> {



	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(Integer type, String ipAddress,String account) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, type,ipAddress,account);
	}
	
}
