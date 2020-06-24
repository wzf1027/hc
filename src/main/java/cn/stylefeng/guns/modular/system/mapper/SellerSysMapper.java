package cn.stylefeng.guns.modular.system.mapper;

import java.util.List;
import java.util.Map;

import cn.stylefeng.guns.modular.system.entity.SellerTimeSetting;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.Seller;
import cn.stylefeng.guns.modular.system.entity.SellerWallter;

public interface SellerSysMapper extends BaseMapper<Seller> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(@Param("page")Page page, @Param("condition")String condition, 
			@Param("phone")String phone,@Param("beginTime") String beginTime, @Param("endTime")String endTime
			,@Param("isAccepter") Integer isAccepter,
			@Param("recommend")String recommend,
			@Param("isAuth")Integer isAuth, 
			@Param("enabled")Integer enabled);

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> wallterList(@Param("page")Page page, @Param("sellerId")Long sellerId,@Param("type") Integer type);

	SellerWallter findSellerWallter(@Param("sellerWallterId")Long sellerWallterId);

	void updteSellerWallter(SellerWallter old);


    SellerTimeSetting getSellerTimeSetting();

	void updateSellerTimeSetting(SellerTimeSetting setting);

    List<Map<String, Object>> selectListTreeByCondition(@Param("condition")String condition);
}
