package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class SellerChargerCoinAppealWrapper extends BaseControllerWrapper {

	public SellerChargerCoinAppealWrapper(Map<String, Object> single) {
        super(single);
    }

    public SellerChargerCoinAppealWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public SellerChargerCoinAppealWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public SellerChargerCoinAppealWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    	Integer status = (Integer) map.get("status");
    	if(status != null) {
    		if(status ==1) {
        		map.put("statusName", "审核中");
        	}else if(status ==2) {
        		map.put("statusName", "审核通过");
        	}else if(status ==3) {
        		map.put("statusName", "审核不通过");
        	}
    	}
    	map.put("phone", ConstantFactory.me().getSeller((Long) map.get("sellerId")).getAccount());
    }
}
