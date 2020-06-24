package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class SellerWallterListWrapper extends BaseControllerWrapper {

	public SellerWallterListWrapper(Map<String, Object> single) {
        super(single);
    }

    public SellerWallterListWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public SellerWallterListWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public SellerWallterListWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    	String code =  map.get("code")+"";
    	String type =  map.get("type")+"";
    	if("1".equals(code)) {
    		map.put("code","会员挖矿");
    	}else if("2".equals(code)) {
    		map.put("code","承兑商挖矿");
    	}else if("3".equals(code)) {
    		map.put("code","推荐挖矿");
    	}
    	
    	if("1".equals(type)) {
    		map.put("code","USDT");
    	}else if("2".equals(type)) {
    		map.put("code","HC");
    	}
    	
    }
}
