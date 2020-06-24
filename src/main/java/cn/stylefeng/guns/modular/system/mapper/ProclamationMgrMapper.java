package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.Proclamation;
import io.lettuce.core.dynamic.annotation.Param;

public interface ProclamationMgrMapper extends BaseMapper<Proclamation> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(@Param("page")Page page, @Param("condition")String condition);


}
