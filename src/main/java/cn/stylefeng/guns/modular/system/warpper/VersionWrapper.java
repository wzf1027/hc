package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class VersionWrapper extends BaseControllerWrapper{

	  public VersionWrapper(Map<String, Object> single) {
	        super(single);
	    }

	    public VersionWrapper(List<Map<String, Object>> multi) {
	        super(multi);
	    }

	    public VersionWrapper(Page<Map<String, Object>> page) {
	        super(page);
	    }

	    public VersionWrapper(PageResult<Map<String, Object>> pageResult) {
	        super(pageResult);
	    }

	    @Override
	    protected void wrapTheMap(Map<String, Object> map) {
	    	Integer type = (Integer) map.get("type");
	    	if(type == 1) {
	    		map.put("type", "安卓");
	    	}else if(type ==2) {
	    		map.put("type", "苹果");
	    	}
	    }
}
