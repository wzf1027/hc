package cn.stylefeng.guns.modular.system.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.entity.AppVersion;
import cn.stylefeng.guns.modular.system.mapper.VersionMgrMapper;

@Service
public class VersionMgrService extends ServiceImpl<VersionMgrMapper, AppVersion> {

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> list(String condition) {
		  Page page = LayuiPageFactory.defaultPage();
	      return this.baseMapper.list(page, condition);
	}

}
