package cn.stylefeng.guns.modular.system.warpper;

import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

public class ExchangeFeeSettingWrapper extends BaseControllerWrapper {

	public ExchangeFeeSettingWrapper(Map<String, Object> single) {
        super(single);
    }

    public ExchangeFeeSettingWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public ExchangeFeeSettingWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public ExchangeFeeSettingWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    	Integer type = (Integer) map.get("type");
    	if(type != null && type ==1) {
    		map.put("type", "USDT兑换成HC");
    	}else if(type != null && type ==2) {
    		map.put("type", "HC兑换成USDT");
    	}
    }
}
