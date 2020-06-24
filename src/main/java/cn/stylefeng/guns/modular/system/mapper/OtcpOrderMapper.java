package cn.stylefeng.guns.modular.system.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.OtcpOrder;
import cn.stylefeng.guns.modular.system.entity.OtcpOrderCannelNumberRecord;
import cn.stylefeng.guns.modular.system.entity.SellOtcpOrder;
import io.lettuce.core.dynamic.annotation.Param;

public interface OtcpOrderMapper extends BaseMapper<OtcpOrder> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(@Param("page")Page page
									,@Param("condition") String condition
									,@Param("buyerPhone")  String buyerPhone
									,@Param("sellerPhone")  String sellerPhone
									,@Param("serialno")  String serialno
									,@Param("beginTime")  String beginTime
									,@Param("endTime")  String endTime
									, @Param("userId") Long userId,
									 @Param("remark")String remark, 
									 @Param("status")Integer status, 
									 @Param("isAppeal")Integer isAppeal, 
									 @Param("payMethodType")Integer payMethodType);

	SellOtcpOrder findSellerOtcpOrderById(@Param("otcOrderId")Long otcOrderId);

	void updateSellOtcpOrder(SellOtcpOrder sellOtcpOrder);

	OtcpOrderCannelNumberRecord findOtcpOrderCannelBySellerIdToday(@Param("sellerId")Long sellerId);

	void addOtcpOrderCannelNumberRecord(OtcpOrderCannelNumberRecord record);

	void updateOtcpOrderCannelNumberRecord(OtcpOrderCannelNumberRecord record);

	
}
