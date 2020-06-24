package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class WallterLogWrapper extends BaseControllerWrapper {

	public WallterLogWrapper(Map<String, Object> single) {
        super(single);
    }

    public WallterLogWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public WallterLogWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public WallterLogWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    	map.put("account", ConstantFactory.me().getSeller((Long)map.get("sellerId")).getAccount());
	
    }
}
