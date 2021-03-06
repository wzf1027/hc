package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *提币申请订单表
 * @author zf
 *
 */
@TableName("user_withdraw_coin_appeal_order")
public class UserWithdrawCoinAppealOrder implements Serializable {

	private static final long serialVersionUID = -6865339062477953037L;
	
	 /**
     * 主键
     */
    @TableId(value = "APPEAL_ID", type = IdType.AUTO)
	private Long appealId;
    
    
    /**
   	 * 流水订单号
   	 */
   	@TableField("SERIALNO")
   	private String serialno;
   	
   	
    /**
	 * 提币数量
	 */
	@TableField("NUMBER")
	private Double number;
	/**
	 *提现地址
	 */
	@TableField("ADDRESS")
	private String address;
	
	/**
	 *手续费
	 */
	@TableField("FEE_PRICE")
	private Double feePrice;
	   /**
     * 商户/代理商id
     */
	@TableField("USER_ID")
	private Long userId;
	 /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
	private Date createTime;
    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
	private Date updateTime;
    /**
	 * 状态：1表示审核中，2表示审核通过，3表示审核不通过
	 */
	@TableField("STATUS")
	private Integer status;
	
	/**
	 * 总数量
	 */
	@TableField("TOTAL_NUMBER")
	private Double totalNumber;
	
	/**
	 * 角色id
	 */
	@TableField("ROLE_ID")
	private String roleId;

	/**
	 * HC兑换比例
	 */
	@TableField("EXCHANGE_RATIO")
	private Double exChangeRatio;


	@TableField("UPDATE_USER_ID")
	private Long updateUserId;

	@TableField("USER_ACCOUNT")
	private String userAccount;


	public Long getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Long updateUserId) {
		this.updateUserId = updateUserId;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public Double getExChangeRatio() {
		return exChangeRatio;
	}

	public void setExChangeRatio(Double exChangeRatio) {
		this.exChangeRatio = exChangeRatio;
	}

	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public Long getAppealId() {
		return appealId;
	}
	public void setAppealId(Long appealId) {
		this.appealId = appealId;
	}
	public Double getNumber() {
		return number;
	}
	public void setNumber(Double number) {
		this.number = number;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Double getFeePrice() {
		return feePrice;
	}
	public void setFeePrice(Double feePrice) {
		this.feePrice = feePrice;
	}

	
	public String getSerialno() {
		return serialno;
	}
	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Double getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(Double totalNumber) {
		this.totalNumber = totalNumber;
	}
	
	
	
	

}
