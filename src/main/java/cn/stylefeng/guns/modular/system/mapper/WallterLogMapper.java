package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.WallterLog;
import io.lettuce.core.dynamic.annotation.Param;

public interface WallterLogMapper extends BaseMapper<WallterLog> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(@Param("page")Page page,@Param("phone") String phone, @Param("beginTime")String beginTime,@Param("endTime") String endTime);


}
