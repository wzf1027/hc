package cn.stylefeng.guns.modular.system.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.entity.UserOtcFeeSetting;
import cn.stylefeng.guns.modular.system.mapper.UserOtcFeeSettingMapper;

@Service
public class UserOtcFeeSettingService extends ServiceImpl<UserOtcFeeSettingMapper, UserOtcFeeSetting> {

	@Resource
	private UserOtcFeeSettingMapper userOtcFeeSettingMapper;

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String condition,Integer type) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition,type);
	}
	
}
