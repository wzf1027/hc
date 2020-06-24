package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class SellerRegisterSwitchWrapper extends BaseControllerWrapper {

	public SellerRegisterSwitchWrapper(Map<String, Object> single) {
        super(single);
    }

    public SellerRegisterSwitchWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public SellerRegisterSwitchWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public SellerRegisterSwitchWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    }
}
