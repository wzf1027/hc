package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class SellerWithdrawCoinAppealOrderWrapper extends BaseControllerWrapper {

	public SellerWithdrawCoinAppealOrderWrapper(Map<String, Object> single) {
        super(single);
    }

    public SellerWithdrawCoinAppealOrderWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public SellerWithdrawCoinAppealOrderWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public SellerWithdrawCoinAppealOrderWrapper(PageResult<Map<String, Object>> pageResult) {
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
