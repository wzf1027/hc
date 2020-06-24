package cn.stylefeng.guns.modular.app.mapper;

import java.util.List;
import java.util.Map;

import cn.stylefeng.guns.modular.system.entity.Carousel;

public interface CarouselMapper {

	List<Carousel> selectCarouserlList(Map<String, Object> map);


}
