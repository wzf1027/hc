package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class CarouselWrapper extends BaseControllerWrapper{

	  public CarouselWrapper(Map<String, Object> single) {
	        super(single);
	    }

	    public CarouselWrapper(List<Map<String, Object>> multi) {
	        super(multi);
	    }

	    public CarouselWrapper(Page<Map<String, Object>> page) {
	        super(page);
	    }

	    public CarouselWrapper(PageResult<Map<String, Object>> pageResult) {
	        super(pageResult);
	    }

	    @Override
	    protected void wrapTheMap(Map<String, Object> map) {
	    	
	    }
}
