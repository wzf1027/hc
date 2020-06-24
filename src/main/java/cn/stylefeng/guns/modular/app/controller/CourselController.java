package cn.stylefeng.guns.modular.app.controller;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.stylefeng.guns.modular.app.service.CarouselService;
import cn.stylefeng.guns.modular.system.entity.Carousel;
import cn.stylefeng.guns.core.util.ResponseData;

@Controller
@RequestMapping("/app/caursel/")
public class CourselController {
	
	@Resource
	private CarouselService carouselService;
	
	/**
	 * 轮播图列表数据
	 * @return
	 */
	@RequestMapping(value = "/getCarouserlList")
	@ResponseBody
	public ResponseData getCarouserlList() {
		return carouselService.getCarouserlList();
	}
	
	/**
	 * 某个轮播图的详情内容
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/carouselPage")
	public String carouselPage(Long id,Model model) {
		Carousel carousel = carouselService.getCarouserlDetail(id);
		if(carousel != null) {
			model.addAttribute("title", carousel.getCarouselName());
			model.addAttribute("content",carousel.getContent());
		}else {
			model.addAttribute("title", null);
			model.addAttribute("content",null);
		}
		return "/modular/frame/carouselDetail.html";
	}
    
	
}
