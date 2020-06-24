package cn.stylefeng.guns.modular.system.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.SellerBuyerCoinOrder;
import io.lettuce.core.dynamic.annotation.Param;

public interface BuyCoinOrderMapper extends BaseMapper<SellerBuyerCoinOrder> {

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> list(@Param("page") Page page
            , @Param("serialno") String serialno
            , @Param("account") String account
            , @Param("beginTime") String beginTime
            , @Param("endTime") String endTime, @Param("userId") Long userId,
                                   @Param("status") Integer status,
                                   @Param("userOrderNo") String userOrderNo,
                                   @Param("seller") String seller,
                                   @Param("isAppeal") Integer isAppeal,
                                   @Param("isSuccess") Integer isSuccess,
                                   @Param("payMethodType") Integer payMethodType,
                                   @Param("payMethodAccount") String payMethodAccount,
                                   @Param("payMethodName") String payMethodName,
                                   @Param("payMethodName") Integer orderCode,
								   @Param("remark")String remark);

	int findNumberByStatus(@Param("userId")Long userId,@Param("status") Integer status);

	Double findTotalPrice(@Param("userId")Long userId);

	Double findTotalPayPrice(@Param("userId")Long userId);

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> findSellerBuyCoinBill(@Param("page")Page page
									,@Param("condition") String condition
									,@Param("beginTime")  String beginTime
									,@Param("endTime")  String endTime,@Param("userId") Long userId);

	List<Map<String, Object>> findSellerBuyCoinChannelInfo(@Param("userId")Long userId);

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> findSellerBuyCoinMerchant(@Param("page")Page page
									,@Param("condition") String condition
									,@Param("beginTime")  String beginTime
									,@Param("endTime")  String endTime,
									@Param("userId") Long userId,
									@Param("account") String account, 
									@Param("serialno") String serialno,
									@Param("payMethodType") String payMethodType);

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> findPlatformRechargeStatistics(@Param("page")Page page,
			@Param("beginTime") String beginTime, 
			@Param("endTime")String endTime);

	Map<String, Object> getPlatformRechargeStatisticsTotalByToday();

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> findPlatformWithdrawStatistics(@Param("page")Page page,
			@Param("beginTime") String beginTime, 
			@Param("endTime")String endTime);

	Map<String, Object> getPlatformWithdrawStatisticsTotalByToday();

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> findPlatformOtcpMoneyStatistics(@Param("page")Page page);

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> findPlatformBonusMoneyStatistics(@Param("page")Page page,
			@Param("beginTime") String beginTime, 
			@Param("endTime")String endTime);

	Map<String, Object> getPlatformBonusMoneyStatisticsTotalByToday();

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> findPlatformFeeBonusStatistics(@Param("page")Page page,
			@Param("beginTime") String beginTime, 
			@Param("endTime")String endTime);

	Map<String, Object> getPlatformFeeBonusStatisticsTotalByToday();

	Map<String, Object> getPlatformRechargeStatisticsTotal();

	Map<String, Object> getPlatformWithdrawStatisticsTotal();

	Map<String, Object> getPlatformBonusMoneyStatisticsTotal();

	Map<String, Object> getPlatformFeeBonusStatisticsTotal();

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> findPlatformMerchantStatistics(@Param("page")Page page,@Param("phone") String phone);

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> findPlatformAgentStatistics(@Param("page")Page page, @Param("account")String account);


    List<SellerBuyerCoinOrder> findNoFinishSellerBuyerCoinListByPayMethodId(@Param("payMethodId")Long payMethodId);

    int selectSellerIPAndMerchantIpByCount();
}
