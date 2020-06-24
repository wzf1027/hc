package cn.stylefeng.guns.modular.system.warpper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;

public class TeamBonusSettingWrapper extends BaseControllerWrapper {

	public TeamBonusSettingWrapper(Map<String, Object> single) {
        super(single);
    }

    public TeamBonusSettingWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public TeamBonusSettingWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public TeamBonusSettingWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    	Integer level = (Integer) map.get("level");
    	if(level != null ) {
    		map.put("levelName", level+"级");
    	}
    	Double bonusRatio = Double.valueOf(map.get("bonusRatio")+"");
    	if(bonusRatio != null) {
    		map.put("bonusRatio", bonusRatio+"%");
    	}else {
    		map.put("bonusRatio", "0%");
    	}
    }
}
