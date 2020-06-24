package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 会员/商户交易订单记录表
 * @author zf
 *
 */
@TableName("seller_buyer_coin_order")
public class SellerBuyerCoinOrder implements Serializable {

	private static final long serialVersionUID = -8836935406339637733L;
	
	/**
	 * 主键id
	 */
	@TableId(value="ORDER_ID",type=IdType.AUTO)
	private Long orderId;
	/**
	 * 流水订单号
	 */
	@TableField("SERIALNO")
	private String serialno;
	/**
	 * 买家
	 */
	@TableField("BUYER_ID")
	private Long buyerId;

	
	/**
	 * 卖家
	 */
	@TableField("SELLER_ID")
	private Long sellerId;
	
	/**
	 * 异步通知
	 */
	@TableField("NOTIFY_URL")
	private String notifyUrl;
	
	/**
	 * 外部订单
	 */
	@TableField("USER_ORDER_NO")
	private String userOrderNo;
	
	/**
	 * 玩家id
	 */
	@TableField("CUID")
	private String cuid;
	
	/**
	 * 出售数量
	 */
	@TableField("NUMBER")
	private Double number;

	/**
	 * 状态：1:表示未支付，2：表示支付成功，等待确认到账，3：卖家确认，待买家确认，4：已完成，5：已超时，6申诉中，7：取消
	 */
	@TableField("STATUS")
	private Integer status;
	/**
	 * 下单时间
	 */
	@TableField(value="CREATE_TIME",fill=FieldFill.INSERT)
	private Date createTime;
	/**
	 * 挂单时间
	 */
	@TableField(value="MATCHING_TIME",fill=FieldFill.INSERT)
	private Date matchingTime;
	
	/**
	 * 更新时间
	 */
	@TableField(value="UPDATE_TIME",fill=FieldFill.UPDATE)
	private Date updateTime;
	/**
	 * 取消时间
	 */
	@TableField(value="CANNEL_TIME",fill=FieldFill.UPDATE)
	private Date cannelTime;
	/**
	 * 收款方式id
	 */
	@TableField("PAY_METHOD_ID")
	private Long payMethodId;
	/**
	 * 收款方式账号
	 */
	@TableField("PAY_METHOD_ACCOUNT")
	private String payMethodAccount;
	
	/**
	 * 收款人
	 */
	@TableField("PAY_METHOD_NAME")
	private String payMethodName;
	
	/**
	 * 收款方式类型：1表示支付宝，2表示微信，3表示银行卡
	 */
	@TableField("PAY_METHOD_TYPE")
	private Integer payMethodType;
	
	/**
	 * 收款方式：所属银行
	 */
	@TableField("PAY_METHOD_CARD_BANK")
	private String payMethodCardBank;
	
	/**
	 * 收款方式：银行名称
	 */
	@TableField("PAY_METHOD_CARD_BANK_NAME")
	private String payMethodCardBankName;
	
	/**
	 * 收款方式:支付宝/微信二维码
	 */
	@TableField("PAY_METHOD_QRCODE")
	private String payMethodQrcode;

	/**
	 * 收款方式昵称
	 */
	@TableField("PAY_METHOD_NICK_NAME")
	private String payMethodNickName;
	
	/**
	 * 申诉内容
	 */
	@TableField("APPEAL_CONTENT")
	private String appealContent;
	/**
	 * 申述凭证
	 */
	@TableField("CERTIFICATE")
	private String certificate; 
	/**
	 * 申述状态：0表示未申诉，1表示申诉中，2表示买家胜利，3表示卖家胜利，4表示取消申诉
	 */
	@TableField("IS_APPEAL")
	private Integer isAppeal;
	/**
	 * 申述时间
	 */
	@TableField(value="APPEAL_TIME",fill=FieldFill.UPDATE)
	private Date appealTime;
	/**
	 * 结束时间
	 */
	@TableField(value="CLOSE_TIME",fill=FieldFill.UPDATE)
	private Date closeTime;
	/**
	 *挖矿数量
	 */
	@TableField("BONUS_NUMER")
	private Double bonusNumber;
	
	/**
	 * 商户手续费
	 */
	@TableField("FEE_PRICE")
	private Double feePrice;
	
	/**
	 * 代理商的手续费
	 */
	@TableField("AGENT_FEE_PRICE")
	private Double agentFeePrice;
	
	/**
	 * 是否通知过：0表示否，1表示已通知
	 */
	@TableField("IS_NOTICE")
	private Integer isNotice;
	
	/**
	 *是否回调成功:0表示否，1表示是
	 */
	@TableField("IS_SUCCESS")
	private Integer isSuccess;
	
	/**
	 * 到账数量
	 */
	@TableField("INTO_NUMBER")
	private Double intoNumber;
	
	/**
	 * 挂单id
	 */
	@TableField("SELLER_ORDER_ID")
	private Long sellerOrderId;
	
	/**
	 * 同步回调第三方的页面
	 */
	@TableField("RETURN_URL")
	private String returnUrl;

	@TableField("UPDATE_USER")
	private  Long  updateUser;

	/**
	 * 操作备注
	 */
	@TableField("REMARK")
	private String remark;

	/**
	 * 订单标识：1表示正常订单，2表示补单，3表示反补单，4表示重新激活订单
	 */
	@TableField("ORDER_CODE")
	private Integer orderCode;

	@TableField("SELLER_IP")
	private String sellerIp;

	@TableField("MERCHANT_IP")
	private String merchantIp;

	@TableField("MERCHANT_CITY")
	private String merchantCity;

	@TableField("SELLER_CITY")
	private String sellerCity;

	@TableField("DEALER")
	private String dealer;

	public String getDealer() {
		return dealer;
	}

	public void setDealer(String dealer) {
		this.dealer = dealer;
	}

	public String getMerchantCity() {
		return merchantCity;
	}

	public void setMerchantCity(String merchantCity) {
		this.merchantCity = merchantCity;
	}

	public String getSellerCity() {
		return sellerCity;
	}

	public void setSellerCity(String sellerCity) {
		this.sellerCity = sellerCity;
	}

	public String getSellerIp() {
		return sellerIp;
	}

	public void setSellerIp(String sellerIp) {
		this.sellerIp = sellerIp;
	}

	public String getMerchantIp() {
		return merchantIp;
	}

	public void setMerchantIp(String merchantIp) {
		this.merchantIp = merchantIp;
	}

	public Integer getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(Integer orderCode) {
		this.orderCode = orderCode;
	}

	public Long getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(Long updateUser) {
		this.updateUser = updateUser;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getSellerOrderId() {
		return sellerOrderId;
	}
	public void setSellerOrderId(Long sellerOrderId) {
		this.sellerOrderId = sellerOrderId;
	}
	public Double getAgentFeePrice() {
		return agentFeePrice;
	}
	public void setAgentFeePrice(Double agentFeePrice) {
		this.agentFeePrice = agentFeePrice;
	}
	public Double getIntoNumber() {
		return intoNumber;
	}
	public void setIntoNumber(Double intoNumber) {
		this.intoNumber = intoNumber;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getSerialno() {
		return serialno;
	}
	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}
	public Long getBuyerId() {
		return buyerId;
	}
	
	public Integer getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(Integer isSuccess) {
		this.isSuccess = isSuccess;
	}
	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}
	public Long getSellerId() {
		return sellerId;
	}
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
	
	public Double getNumber() {
		return number;
	}
	public void setNumber(Double number) {
		this.number = number;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Date getCannelTime() {
		return cannelTime;
	}
	public void setCannelTime(Date cannelTime) {
		this.cannelTime = cannelTime;
	}
	public Long getPayMethodId() {
		return payMethodId;
	}
	public void setPayMethodId(Long payMethodId) {
		this.payMethodId = payMethodId;
	}
	public String getAppealContent() {
		return appealContent;
	}
	public void setAppealContent(String appealContent) {
		this.appealContent = appealContent;
	}
	public String getCertificate() {
		return certificate;
	}
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
	public Integer getIsAppeal() {
		return isAppeal;
	}
	public void setIsAppeal(Integer isAppeal) {
		this.isAppeal = isAppeal;
	}
	public Date getAppealTime() {
		return appealTime;
	}
	public void setAppealTime(Date appealTime) {
		this.appealTime = appealTime;
	}
	public Date getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getUserOrderNo() {
		return userOrderNo;
	}
	public void setUserOrderNo(String userOrderNo) {
		this.userOrderNo = userOrderNo;
	}
	public String getCuid() {
		return cuid;
	}
	public void setCuid(String cuid) {
		this.cuid = cuid;
	}
	public String getPayMethodAccount() {
		return payMethodAccount;
	}
	public void setPayMethodAccount(String payMethodAccount) {
		this.payMethodAccount = payMethodAccount;
	}
	public String getPayMethodName() {
		return payMethodName;
	}
	public void setPayMethodName(String payMethodName) {
		this.payMethodName = payMethodName;
	}
	public Integer getPayMethodType() {
		return payMethodType;
	}
	public void setPayMethodType(Integer payMethodType) {
		this.payMethodType = payMethodType;
	}
	public String getPayMethodCardBank() {
		return payMethodCardBank;
	}
	public void setPayMethodCardBank(String payMethodCardBank) {
		this.payMethodCardBank = payMethodCardBank;
	}
	public String getPayMethodCardBankName() {
		return payMethodCardBankName;
	}
	public void setPayMethodCardBankName(String payMethodCardBankName) {
		this.payMethodCardBankName = payMethodCardBankName;
	}
	public String getPayMethodQrcode() {
		return payMethodQrcode;
	}
	public void setPayMethodQrcode(String payMethodQrcode) {
		this.payMethodQrcode = payMethodQrcode;
	}
	public Double getBonusNumber() {
		return bonusNumber;
	}
	public void setBonusNumber(Double bonusNumber) {
		this.bonusNumber = bonusNumber;
	}
	public Double getFeePrice() {
		return feePrice;
	}
	public void setFeePrice(Double feePrice) {
		this.feePrice = feePrice;
	}
	public Date getMatchingTime() {
		return matchingTime;
	}
	public void setMatchingTime(Date matchingTime) {
		this.matchingTime = matchingTime;
	}
	public Integer getIsNotice() {
		return isNotice;
	}
	public void setIsNotice(Integer isNotice) {
		this.isNotice = isNotice;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getPayMethodNickName() {
		return payMethodNickName;
	}

	public void setPayMethodNickName(String payMethodNickName) {
		this.payMethodNickName = payMethodNickName;
	}
}
