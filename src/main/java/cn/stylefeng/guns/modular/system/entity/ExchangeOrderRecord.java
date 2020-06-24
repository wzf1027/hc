package cn.stylefeng.guns.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

@TableName("exchange_order_record")
public class ExchangeOrderRecord implements Serializable {

	private static final long serialVersionUID = -4608799251527017260L;
	
	@TableId(value="ID",type=IdType.AUTO)
	private Long id;

	/**
	 * 类型
	 */
	@TableField("SOURCE")
	private String source;
	/**
	 * 到账数量
	 */
	@TableField("NUMBER")
	private Double number;

	/**
	 * 兑换成的币种
	 */
	@TableField("COIN")
	private String coin;

	/**
	 * 兑换比例
	 */
	@TableField("CODE")
	private String code;

	/**
	 * 手续费
	 */
	@TableField("FEE_PRICE")
	private Double feePrice;

	/**
	 * 被兑换币种数量
	 */
	@TableField("TOTAL_NUMBER")
	private Double totalNumber;

	/**
	 * 角色
	 */
	@TableField("ROLE")
	private Integer role;

	/**
	 * 账号id
	 */
	@TableField("ACCOUNT_ID")
	private Long  accountId;
	
	@TableField(value="CREATE_TIME",fill=FieldFill.INSERT)
	private Date createTime;

	
	
	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Double getNumber() {
		return number;
	}

	public void setNumber(Double number) {
		this.number = number;
	}

	public String getCoin() {
		return coin;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Double getFeePrice() {
		return feePrice;
	}

	public void setFeePrice(Double feePrice) {
		this.feePrice = feePrice;
	}

	public Double getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(Double totalNumber) {
		this.totalNumber = totalNumber;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	

}
