package cn.stylefeng.guns.modular.system.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.entity.Carousel;
import cn.stylefeng.guns.modular.system.service.CarouselMgrService;
import cn.stylefeng.guns.modular.system.warpper.CarouselWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;


@Controller
@RequestMapping("/carouselMgr")
public class CarouselMgrController extends BaseController {
	
	@Value("${platform.DOMAIN}")
	private String domain;

	 private String PREFIX = "/modular/system/carousel/";

	    @Autowired
	    private CarouselMgrService carouselMgrService;

	
	    @RequestMapping("")
	    public String index() {
	        return PREFIX + "carousel.html";
	    }


	    @RequestMapping("/carousel_add")
	    public String noticeAdd() {
	        return PREFIX + "carousel_add.html";
	    }


	    @RequestMapping("/carousel_update/{carouselId}")
	    public String noticeUpdate(@PathVariable Long carouselId, Model model) {
	        Carousel carousel = this.carouselMgrService.getById(carouselId);
	        model.addAllAttributes(BeanUtil.beanToMap(carousel));
	        LogObjectHolder.me().set(carousel);
	        return PREFIX + "carousel_edit.html";
	    }



	    @RequestMapping(value = "/list")
	    @ResponseBody
	    public Object list(String condition) {
	        Page<Map<String, Object>> list = this.carouselMgrService.list(condition);
	        Page<Map<String, Object>> wrap = new CarouselWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }


	    @RequestMapping(value = "/add")
	    @ResponseBody
	    public Object add(Carousel carousel) {
	    	if(StringUtils.isBlank(carousel.getImage())) {
	    		throw new ServiceException(400,"请上传图片");
	    	}
	        if (ToolUtil.isOneEmpty(carousel, carousel.getImage())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        Boolean flag = false;
	       if(StringUtils.isBlank(carousel.getHref())) {
	    	   flag =true;
	       }
	       this.carouselMgrService.addCarousel(carousel);
	       if(flag) {
	    	   carousel.setHref(domain+"/app/caursel/carouselPage?id="+carousel.getCarouselId());
	    	   this.carouselMgrService.updateById(carousel);
	       }
	        return SUCCESS_TIP;
	    }


	    @RequestMapping(value = "/delete")
	    @ResponseBody
	    public Object delete(@RequestParam Long carouselId) {
	        this.carouselMgrService.removeById(carouselId);
	        return SUCCESS_TIP;
	    }


	    @RequestMapping(value = "/update")
	    @ResponseBody
	    public Object update(Carousel carousel) {
	    	if(StringUtils.isBlank(carousel.getImage())) {
	    		throw new ServiceException(400,"请上传图片");
	    	}
	        if (ToolUtil.isOneEmpty(carousel, carousel.getCarouselId(),carousel.getImage())) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);	
	        }
	        Carousel old = this.carouselMgrService.getById(carousel.getCarouselId());
	        old.setCarouselName(carousel.getCarouselName());
	        old.setImage(carousel.getImage());
	        old.setSort(carousel.getSort());
		    if(StringUtils.isBlank(carousel.getHref())) {
		    	old.setHref(domain+"/app/caursel/carouselPage?id="+old.getCarouselId());
		     }else {
			       old.setHref(carousel.getHref()); 
		    }
	        old.setContent(carousel.getContent());
	        this.carouselMgrService.updateById(old);
	        return SUCCESS_TIP;
	    }
	
}
