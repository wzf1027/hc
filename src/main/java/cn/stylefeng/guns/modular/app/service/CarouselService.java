package cn.stylefeng.guns.modular.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import cn.stylefeng.guns.modular.app.mapper.CarouselMapper;
import cn.stylefeng.guns.modular.system.entity.Carousel;
import cn.stylefeng.guns.core.util.ResponseData;


@Service
public class CarouselService {

	@Resource
	private CarouselMapper carouselMapper;

	public ResponseData getCarouserlList() {
		List<Carousel>  carouserlList = carouselMapper.selectCarouserlList(null);
		List<Map<String,Object>>  list =null;
		if(carouserlList != null && carouserlList.size() >0) {
			list = new ArrayList<Map<String,Object>>();
			for (Carousel carousel : carouserlList) {
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("image", carousel.getImage());
				map.put("href", carousel.getHref());
				map.put("title", carousel.getCarouselName());
				list.add(map);
			}
		}
		return ResponseData.success(200, "获取成功", list);
	}

	public Carousel getCarouserlDetail(Long id) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("carouselId", id);
		List<Carousel>  carouserlList = carouselMapper.selectCarouserlList(map);
		if(carouserlList != null && carouserlList.size() >0) {
			return carouserlList.get(0);
		}
		return null;
	}
	

}
