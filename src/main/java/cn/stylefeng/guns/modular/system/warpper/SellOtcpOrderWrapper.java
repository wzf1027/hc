package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class SellOtcpOrderWrapper extends BaseControllerWrapper {

	public SellOtcpOrderWrapper(Map<String, Object> single) {
        super(single);
    }

    public SellOtcpOrderWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public SellOtcpOrderWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public SellOtcpOrderWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    	Integer type = (Integer) map.get("type");
    	if(type != null && type ==1 || type ==2) {
    		Long sellerId = (Long) map.get("sellerId");
    		map.put("sellName", ConstantFactory.me().getSeller(sellerId).getAccount());
			map.put("phone", ConstantFactory.me().getSeller(sellerId).getAccount());
    		if(type ==1) {
    			map.put("typeName", "会员出售");
    		}else {
    			map.put("typeName", "承兑商出售");
    		}
    	}
    	
    	if(type != null && type.equals(3)) {
    		Long userId = (Long) map.get("userId");
    		map.put("sellName", ConstantFactory.me().getUserAccountCodeById(userId));
			map.put("phone", ConstantFactory.me().getUserAccountCodeById(userId));
    		map.put("typeName", "商户出售");
    	}
    	if(type != null && type.equals(4)) {
    		Long userId = (Long) map.get("userId");
    		String sellPhoe = (String) map.get("phone");
    		map.put("phone",ConstantFactory.me().getUserAccountCodeById(userId) );
    		String phone = ConstantFactory.me().getUserPhone(userId);
    		map.put("sellName",ConstantFactory.me().getUserAccountCodeById(userId));
    		map.put("typeName", "代理商出售");
    	}
    	
    	Integer status = (Integer) map.get("status");
    	if(status != null) {
    		if(status ==1) {
    			map.put("statusName", "出售中");
    		}else if(status ==2) {
    			map.put("statusName", "已全部出售完");
    		}else if(status ==3) {
    			map.put("statusName", "已取消");
    		}
    	}
    	Double feePrice = (Double) map.get("feePrice");
    	if(feePrice == null) {
    		map.put("feePrice", 0);
    	}
    	
    }
}
