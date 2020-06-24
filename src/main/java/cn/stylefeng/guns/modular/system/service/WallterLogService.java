package cn.stylefeng.guns.modular.system.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.entity.WallterLog;
import cn.stylefeng.guns.modular.system.mapper.WallterLogMapper;

@Service
public class WallterLogService extends ServiceImpl<WallterLogMapper, WallterLog> {

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String phone, String beginTime, String endTime) {
		Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, phone,beginTime,endTime);
	
	}
	
}
