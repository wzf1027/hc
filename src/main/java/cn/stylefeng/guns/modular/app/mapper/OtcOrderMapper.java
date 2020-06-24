package cn.stylefeng.guns.modular.app.mapper;

import java.util.List;
import java.util.Map;
import cn.stylefeng.guns.modular.app.dto.TeamBuyCoinDto;
import cn.stylefeng.guns.modular.system.entity.*;
import io.lettuce.core.dynamic.annotation.Param;

public interface OtcOrderMapper {

	long getOtcOrderByToday(@Param("now")String now,@Param("tomorrow") String tomorrow);

	long getSellOtcpOrderByToday(@Param("now")String now,@Param("tomorrow") String tomorrow);

	int addSellOtcpOrder(SellOtcpOrder order);

	SellOtcpOrder findSellerOtcpOrderById(@Param("id")Long id);

	int updateSellOtcpOrder(SellOtcpOrder order);

	void addOtcpOrder(OtcpOrder otcpOrder);

	OtcpOrder findOtcOrderById(@Param("id")Long id);

	void addOtcpOrderCannelNumberRecord(OtcpOrderCannelNumberRecord record);

	void updateOtcpOrder(OtcpOrder otcpOrder);

	OtcpOrderCannelNumberRecord findOtcpOrderCannelBySellerIdToday(@Param("sellerId")Long sellerId);

	void updateOtcpOrderCannelNumberRecord(OtcpOrderCannelNumberRecord record);

	long getSellerRechargeAppealOrderByToday(@Param("now")String now,@Param("tomorrow") String tomorrow);

	long getSellerOrderByToday(@Param("now")String now,@Param("tomorrow") String tomorrow);

	int addSellerOrder(SellerOrder order);

	List<SellerOrder> findSellerOrderByNoStatus();

	void addSellerBuyerCoinOrder(SellerBuyerCoinOrder coinOrder);

	int updateSellerOrder(SellerOrder sellerOrder);

	void updateSellerBuyerCoinOrder(SellerBuyerCoinOrder order);

	SellerBuyerCoinOrder findSellerBuyerOrderBySerialNo(@Param("serialno")String serialno);

	List<BuyCoinUsedPayMethodRecord> findUsedPayMethodRecord(@Param("payMethodId")Long payMethodId
			,@Param("sellerId") Long sellerId,@Param("type") Integer type);

	void addBuyCoinUsedPayMethodRecord(BuyCoinUsedPayMethodRecord record);

	int findSellerBuyCoinOrder(@Param("sellerId")Long sellerId);

	Map<String, Object> findSellerbuyCoinOrderByToday(@Param("sellerId")Long sellerId);

	long getUserRechargeCoinAppealOrderByToday(@Param("now")String now,@Param("tomorrow") String tomorrow);

	long getUserWithDrawCoinAppealOrderByToday(@Param("now")String now,@Param("tomorrow") String tomorrow);

	List<OtcpOrder> findOtcpOrderOrderByNoStatus();

	SellerBuyerCoinOrder selectSellerBuyCoinOrder(@Param("sellerId")Long sellerId);

	SellerOrder findSellerorderBySellerId(@Param("sellerId")Long sellerId);

	List<SellerBuyerCoinOrder> findSellerBuyerCoinOrderNoStatus();

	Double findSellerbuyCoinOrderByIsAppealCount(@Param("sellerId")Long sellerId);

	Double findSellerbuyCoinOrderByRunning(@Param("sellerId")Long sellerId);

	List<SellerBuyerCoinOrder> findSellerBuyCoinOrderListByOutOrderId(@Param("orderId")Long orderId);

	int findSellerBuyCoinOrderByOutOrder(@Param("userOrderNo")String userOrderNo);

	SellOtcpOrder findSellerOtcpOrderByAutoMerchantAndType(@Param("autoMerchant")Integer autoMerchant,@Param("type") Integer type,@Param("userId")Long userId);
	
	List<TeamBuyCoinDto> findSellerBuyCoinTeamByRecommdId(@Param("recommendId")Long recommendId);

	List<TeamBuyCoinDto> findMyselfBuyCoinInfoByNoRecommend();

	Double findTeamBuyCoinInfoByRecommendId(@Param("sellerId")Long sellerId);

	List<SellerBuyerCoinOrder> findSellerOrderByNoStatusAndPrice(@Param("price")Double price,@Param("sellerId") Long sellerId);

	long getSellerBuyerCoinOrderByToday(@Param("now")String now,@Param("tomorrow") String tomorrow);

	SellerBuyerCoinOrder findSellerBuyCoinByPriceAndType(@Param("sellerId")Long sellerId,@Param("price") Double price, @Param("payMethodId")Long payMethodId);

	long getSellerWithDrawCoinAppealOrderByToday(@Param("now")String now,@Param("tomorrow") String tomorrow);

    void deleteBuyCoinUsedPayMethodRecordNoPayMethodId(SellerPayMethod payMethod);

	List<SellerBuyerCoinOrder> findsellerBuyerOrderByStatusAndNoSuccess(@Param("status") Integer status, @Param("isSuccess")Integer isSuccess);

    int findBuyerOrderByPayMethodAndStatus(Map<String,Object> param);
}
