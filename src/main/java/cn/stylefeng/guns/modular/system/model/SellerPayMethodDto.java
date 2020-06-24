package cn.stylefeng.guns.modular.system.model;

import java.io.Serializable;
import java.util.Date;

public class SellerPayMethodDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7048347222102839399L;
	
	private Long payMethodId;
	/**
	 * 币商id
	 */
	private Long sellerId;
	
	//支付类型 1：表示支付宝，2：表示微信，3：表示银行卡 4:聚合码
	private Integer type;
	/**
	 * 二维码
	 */
	private String qrCode; 
	/**
	 * 账号
	 */
	private String account;
	
	/**
	 * 开户支行
	 */
	private String cardBank;
	
	/**
	 * 银行名称
	 */
	private String cardBankName;
	
	/**
	 * 姓名
	 */
	private String accountName;
	/**
	 * 使用次数
	 */
	private Long useNumber;
	

	private Integer isCheck;
	

	private Integer buyNumber;

	private Integer successNumber;
	
	private Double successRatio;
	
	/**
	 * 备注
	 */
	private String remark;
	
	 /**
     * 创建时间
     */
	private Date createTime;
	
    /**
     * 更新时间
     */
	private Date updateTime;

	public Long getPayMethodId() {
		return payMethodId;
	}

	public void setPayMethodId(Long payMethodId) {
		this.payMethodId = payMethodId;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCardBank() {
		return cardBank;
	}

	public void setCardBank(String cardBank) {
		this.cardBank = cardBank;
	}

	public String getCardBankName() {
		return cardBankName;
	}

	public void setCardBankName(String cardBankName) {
		this.cardBankName = cardBankName;
	}



	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Long getUseNumber() {
		return useNumber;
	}

	public void setUseNumber(Long useNumber) {
		this.useNumber = useNumber;
	}

	public Integer getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(Integer isCheck) {
		this.isCheck = isCheck;
	}

	public Integer getBuyNumber() {
		return buyNumber;
	}

	public void setBuyNumber(Integer buyNumber) {
		this.buyNumber = buyNumber;
	}

	public Integer getSuccessNumber() {
		return successNumber;
	}

	public void setSuccessNumber(Integer successNumber) {
		this.successNumber = successNumber;
	}

	public Double getSuccessRatio() {
		return successRatio;
	}

	public void setSuccessRatio(Double successRatio) {
		this.successRatio = successRatio;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
	
	

}
