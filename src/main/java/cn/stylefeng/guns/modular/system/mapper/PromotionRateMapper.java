package cn.stylefeng.guns.modular.system.mapper;


import cn.stylefeng.guns.modular.system.entity.PromotionRate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;


public interface PromotionRateMapper extends BaseMapper<PromotionRate> {

    Page<Map<String, Object>> list(Page page, String condition);

    PromotionRate getLastPromotionRateOne(PromotionRate rate);

    List<PromotionRate> selectListByLeveAsc(@Param("type") Integer type);

    List<PromotionRate> selectListByLeveDesc();
}