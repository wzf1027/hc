package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.Carousel;
import io.lettuce.core.dynamic.annotation.Param;

public interface CarouselMgrMapper extends BaseMapper<Carousel> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(@Param("page")Page page, @Param("condition")String condition);

	Long addCarousel(Carousel carousel);

}
