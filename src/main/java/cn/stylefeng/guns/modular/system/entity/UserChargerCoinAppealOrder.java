package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 充币审核订单表
 * @author zf
 *
 */
@TableName("user_charger_coin_appeal_order")
public class UserChargerCoinAppealOrder implements Serializable {

	private static final long serialVersionUID = 3146722342717461654L;
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
     * 代理商/商户id
     */
	@TableField("USER_ID")
	private Long userId;
	/**
	 * 充币数量
	 */
	@TableField("NUMBER")
	private Double number;
	/**
	 * hash值
	 */
	@TableField("HASH_VALUE")
	private String hashValue;
	/**
	 * 平台充币地址
	 */
	@TableField("ADDRESS")
	private String address;
	/**
	 * 状态：1表示审核中，2表示审核通过，3表示审核不通过
	 */
	@TableField("STATUS")
	private Integer status;
	
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

	public Long getAppealId() {
		return appealId;
	}


	public void setAppealId(Long appealId) {
		this.appealId = appealId;
	}



	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public Double getNumber() {
		return number;
	}


	public void setNumber(Double number) {
		this.number = number;
	}


	public String getHashValue() {
		return hashValue;
	}


	public void setHashValue(String hashValue) {
		this.hashValue = hashValue;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
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

	
	

	public String getSerialno() {
		return serialno;
	}


	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}


	public Date getUpdateTime() {
		return updateTime;
	}


	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
    
    

}
