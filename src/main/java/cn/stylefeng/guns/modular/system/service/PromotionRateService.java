package cn.stylefeng.guns.modular.system.service;

import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.modular.system.entity.PromotionRate;
import cn.stylefeng.guns.modular.system.mapper.PromotionRateMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * 推广等级
 */
@Service
public class PromotionRateService extends ServiceImpl<PromotionRateMapper, PromotionRate> {


    public Page<Map<String, Object>> list(String condition) {
        Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition);
    }

    public PromotionRate getLastPromotionRateOne(PromotionRate rate) {
        return this.baseMapper.getLastPromotionRateOne(rate);
    }

    public List<PromotionRate> selectListByLeveAsc(Integer type) {
        return this.baseMapper.selectListByLeveAsc(type);
    }

    public List<PromotionRate> selectListByLeveDesc() {
        return this.baseMapper.selectListByLeveDesc();
    }
}