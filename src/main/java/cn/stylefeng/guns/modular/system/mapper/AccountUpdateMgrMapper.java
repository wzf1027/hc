package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.dto.SellerAccountUpdate;
import cn.stylefeng.guns.modular.system.entity.AccountUpdateRecord;
import io.lettuce.core.dynamic.annotation.Param;

public interface AccountUpdateMgrMapper extends BaseMapper<AccountUpdateRecord> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> findAccountUpdateByCondition(@Param("page") Page page,
                                                           @Param("sellerAccountUpdate") SellerAccountUpdate sellerAccountUpdate,
                                                           @Param("role") Long role,
                                                           @Param("beginTime") String beginTime
            , @Param("endTime") String endTime,@Param("userId") Long userId);


}
