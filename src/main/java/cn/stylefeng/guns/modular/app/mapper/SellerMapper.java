package cn.stylefeng.guns.modular.app.mapper;

import java.util.List;
import java.util.Map;

import cn.stylefeng.guns.modular.app.dto.SellOtcpOrderDto;
import cn.stylefeng.guns.modular.system.entity.*;
import org.apache.ibatis.annotations.Param;

import cn.stylefeng.guns.core.util.Query;

public interface SellerMapper {

	SellerRegisterSwitchSetting findRegisterSwitchSetting();

	Seller findSellerByPhone(@Param("phone")String phone);

	int addSeller(Seller seller);

	int addSellerWallter(SellerWallter wallet);

	int addSellerProfitWallter(SellerProfitWallter profitWallter);

	PlatformRecommdSetting getPlatformRecommendSetting();

	int updateSeller(Seller seller);

	Seller findSellerByCode(@Param("code")String code);

	int findSellerbyIdCardNoCount(@Param("idCardNo")String idCardNo);

	AppVersion selectOneAppVersion(@Param("type")Integer type);

	SellerPayMethod findSellerPayMethodById(Long id);

	int updateSellerPayMethod(SellerPayMethod sellerPayMethod);

	int addPayMethod(SellerPayMethod sellerPayMethod);

	List<SellerPayMethod> getSellerPayMethodList(Query query);

	int getSellerPaymethodListCount(Query query);

	int deletePaymethodById(@Param("id")Long id);

	int addSellerWallterAddress(SellerWallterAddress wallterAddress);

	int deleteSellerWallterAddress(Long addressId);

	SellerWallterAddress findSellerWallterAddressById(@Param("id")Long id);

	List<SellerWallterAddress> getSellerWallterAddressList(Query query);

	int getSellerWallterAddressListCount(Query query);

	USDTOtcpExchange getOTCMarkInfo();

	List<SellerWallter> findSellerWallter(SellerWallter sellerWallter);

	int updateSellerWallter(SellerWallter usdtWallter);

	int addSellerAccountFlowRecord(SellerAccountFlowRecord flowRecord);

	User findUserOne(User user);

	List<UserWallter> findUserWallterList(UserWallter userWallter);

	List<SellerProfitWallter> findSellerProfitWallterList(SellerProfitWallter profitWallter);

	PlatformCoinAddress findChargeCoinAddress();

	int addSellerChargerCoinAppealOrder(SellerChargerCoinAppealOrder appeal);

	List<SellerChargerCoinAppealOrder> getSellerChargerCoinAppealList(Query query);

	int getSellerChargerCoinAppealListCount(Query query);

	SellerWithdrawFeeSetting findSellerWithdrawFeeSettingOne();

	int addSellerWithdrawCoinAppealOrder(SellerWithdrawCoinAppealOrder order);

	List<SellerWithdrawCoinAppealOrder> getSellerWithdrawCoinAppealList(Query query);

	int getSellerWithdrawCoinAppealListCount(Query query);

	int addUserAccountFlowRecord(UserAccountFlowRecord userFlowRecord);

	int updateUserWallter(UserWallter userWallter);

	int addSellerTransferRecord(SellerTransferRecord record);

	int updateSellerProfitWallter(SellerProfitWallter profitWallter);

	int addSellerProfitFlowRecord(SellerProfitAccountFlowRecord profitFlowRecord);

	List<SellerTransferRecord> getTransferCoinListList(Query query);

	int getTransferCoinListListCount(Query query);

	List<SellerAccountFlowRecord> getSellerAccountFlowRecordList(Query query);

	int getSellerAccountFlowRecordListCount(Query query);

	List<UserAccountFlowRecord> getUserAccountFlowRecordList(Query query);

	int getUserAccountFlowRecordListCount(Query query);

	List<SellerProfitAccountFlowRecord> getSellerProfitAccountFlowRecordList(Query query);

	int getSellerProfitAccountFlowRecordListCount(Query query);

	int addSellerAccpterAppeal(SellerAccpterAppeal appeal);

	List<SellOtcpOrderDto> getSellOtcpOrderList(Query query);

	int getSellOtcpOrderListCount(Query query);

	Seller findSellerbyId(@Param("id")Long id);

	SellerOtcFeeSetting findSellerOtcFeeSettingOne(@Param("symbols")String symbols);

	OtcpPirceSetting findOtcpPriceSettingOne(@Param("symbols")String symbols);

	List<OtcpOrder> getOtcpOrderList(Query query);

	int getOtcpOrderListCount(Query query);

	OtcpCannelNumberSetting findOtcpCannelNumberSetting();

	AccepterRebateSetting findAccepterRebateSetting(@Param("channelType")Integer channelType,@Param("symbols")String symbols);

	SuperiorAccepterRebateSetting findSuperiorAccepterRebateSetting(@Param("channelType")Integer channelType,@Param("symbols")String symbols);

	List<Help> getHelpList(Query query);

	int getHelpListCount(Query query);

	Help getHelpDetail(@Param("id")Long id);

	List<SellerPayMethod> findSellerPayMethodByIsCheck(@Param("sellerId")Long sellerId,@Param("type") Integer type);

	List<SellerPayMethod> findsellerPayMethodBySellerId(@Param("sellerId")Long sellerId,@Param("type") Integer type);

	List<SellerBuyerCoinOrder> getSellerBuyerCoinOrderList(Query query);

	int getSellerBuyerCoinOrderListCount(Query query);

	UserPayMethod findUserPayMethod(@Param("id")Long id);

	UserPayMethodFeeSetting findUserPayMethodFeeSettingByUserId(@Param("userId")Long userId);

	UserBonusSetting findUserBonusSetting(UserBonusSetting bonusSetting);

	UserRecommendRelation findUserRecommendRelation(UserRecommendRelation relation);

	SellerAwardSetting getOneSellerAwardSetting();

	SellerBuyCoinNotice findSellerBuyCoinNotice(@Param("sellerId")Long sellerId);

	void updateSellerBuyCoinNotice(SellerBuyCoinNotice notice);

	void addSellerBuyCoinNotice(SellerBuyCoinNotice notice);

	int findOtcpOrderBySellerIdAndNoFinish(@Param("sellerId")Long sellerId);

	MerchantPay findMerchatPayOne();

	Map<String, Object> findMyselfTeamByToday(@Param("sellerId")Long sellerId);

	List<TeamBonusSetting> findTeamBonusSettingList();

	List<Map<String, Object>> findMyselfSubordinateByToday(Query query);

	Integer findMyselfSubordinateByTodayCount(Query query);

	List<Map<String, Object>> subordinateBonusList(Query query);

	Integer subordinateBonusListCount(Query query);

	Map<String, Object> teamBonusInfo(@Param("sellerId")Long sellerId);

	List<SellerBuyCoinPayMethodQueue> findSellerBuyCoinMethodQueue(SellerBuyCoinPayMethodQueue queue);

	void addSellerBuyCoinPayMethodQueue(SellerBuyCoinPayMethodQueue methodQueue);

	UserWallter findUserWallet(@Param("userId")Long userId);

	void addAccountUpdateRecord(AccountUpdateRecord updateRecord);

	List<Customer> findCustomerList();


    SellerBuyerCoinOrder getSellerBuyerCoinOrderLast(@Param("sellerId")Long sellerId);

	SellerTimeSetting getSellerTimeOne();

	SellOtcpOrder getSellOtcpOrderLast(@Param("id")Long id,@Param("type") Integer type);

    SellerCash selectSellerCashBySellerId(@Param("sellerId")Long sellerId);

	void updateSellerCash(SellerCash cash);

	void addSellerCash(SellerCash cash);

    Double getSellerBuyerCoinOrderTotalPriceByPayMethodId(@Param("sellerId")Long sellerId, @Param("payMethodId")Long payMethodId);

	int getSuccessNumberOrderByPayMethodId(@Param("sellerId")Long sellerId, @Param("payMethodId")Long payMethodId);

	int getTotalNumberOrderByPayMethodId(@Param("sellerId")Long sellerId,@Param("payMethodId") Long payMethodId);

	SellerBuySoldOutSetting getSellerBuySoldOutSetting();

    List<SellerNotice> getSellerNoticeList(Query query);

	int getSellerNoticeListCount(Query query);

	SellerNotice getSellerNoticeById(@Param("id")Long id);


	void updateSellerNotice(SellerNotice notice);

    ExchangeSetting getExchangeFeeSetting(@Param("type")Integer type);

	List<ExchangeOrderRecord> getSellerExchangeOrderRecordList(Query query);

	int getSellerExchangeOrderRecordListCount(Query query);

    Seller findSellerByEmail(@Param("account")String account);

	void addExchangeOrderRecord(ExchangeOrderRecord record);

    Seller findSellerByAccount(@Param("account")String account);

    void deleteSellerCashById(SellerCash cash);

	void addSellerNotice(SellerNotice notice);

    SellerAuthSwitchSetting findSellerAuthSwithSettingOne();

	List<SellerPayMethod> findSellerPayMethodList(SellerPayMethod payMethod);

    Map<String, Object> getFlowRecordStatistics(Map<String, Object> params);

	Map<String, Object> getUserAccountFlowRecordStatistics(Map<String, Object> params);

	Map<String, Object> getSellerProfitAccountFlowRecordStatistics(Map<String, Object> params);

    int findSellerNoticeCount(@Param("sellerId")Long sellerId);

    List<SellerPayMethod> findSellerPayMethodByException(@Param("sellerId")Long sellerId);

    List<SellerBuyerCoinOrder> findSellerBuyCoinByCoindition(Map<String, Object> coinditionParam);

}
