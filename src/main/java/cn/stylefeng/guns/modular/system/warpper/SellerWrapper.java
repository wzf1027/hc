package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import cn.stylefeng.guns.modular.system.entity.Seller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class SellerWrapper extends BaseControllerWrapper {

	public SellerWrapper(Map<String, Object> single) {
        super(single);
    }

    public SellerWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public SellerWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public SellerWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    	Long referceId = (Long) map.get("referceId");
        Seller referee = ConstantFactory.me().getSeller(referceId);
    	map.put("refereePhone", referee == null ? null : referee.getAccount());
    	map.put("authStatus", map.get("isAuth") == null ? "未认证" : ((Integer)map.get("isAuth")  == 0 ? "未认证" : ( (Integer)map.get("isAuth")  == 1  ? "待审核" :"已审核")));
    	Integer isAccepter = (Integer) map.get("isAccepter");
    	if(isAccepter ==0) {
    		map.put("isAccepter","否");
    	}else if(isAccepter ==1) {
    		map.put("isAccepter","是");
    	}else {
    		map.put("isAccepter","否");
    	}
        map.put("bingGoogleName", map.get("bingGoogle") == null ? "未绑定" : ((Integer)map.get("bingGoogle")  == 0 ? "未绑定" :"已绑定"));

    }
}
