package cn.stylefeng.guns.modular.system.mapper;

import cn.stylefeng.guns.modular.system.entity.AccepterRebateSetting;
import cn.stylefeng.guns.modular.system.entity.SellerPayMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.Map;

public interface SellerPayMethodMapper extends BaseMapper<SellerPayMethod> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(@Param("page") Page page,
                                   @Param("account") String account,
                                   @Param("phone") String phone,
                                   @Param("payMethodName") String payMethodName,
                                   @Param("payMethodType") Integer payMethodType, @Param("nickName") String nickName, @Param("isCheck")Integer isCheck);


    Integer onLineNumberByType(@Param("type") Integer type);
}
