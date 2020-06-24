package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.guns.modular.system.entity.Seller;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class SellerOrderWrapper extends BaseControllerWrapper {

	public SellerOrderWrapper(Map<String, Object> single) {
        super(single);
    }

    public SellerOrderWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public SellerOrderWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public SellerOrderWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    	Long sellerId = (Long) map.get("sellerId");
    	Seller seller = ConstantFactory.me().getSeller(sellerId);
		map.put("phone", seller.getPhone());
        map.put("account", seller.getAccount());
		map.put("nickName", seller.getNickName());
		map.put("uId", seller.getCode());
    	map.put("statusName", "挂单中");
    }
}
