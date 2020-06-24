package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.UserPayMethod;

public interface UserPayMethodMapper extends BaseMapper<UserPayMethod> {

    @SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(@Param("page") Page page, @Param("condition") String condition, @Param("userId") Long userId);

}
