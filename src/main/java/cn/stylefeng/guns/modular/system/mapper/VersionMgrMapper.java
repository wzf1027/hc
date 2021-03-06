package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.AppVersion;

public interface VersionMgrMapper extends BaseMapper<AppVersion> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(Page page, String condition);

}
