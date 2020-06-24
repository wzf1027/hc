package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 划转记录表
 * @author zf
 *
 */
@TableName("seller_transfer_record")
public class SellerTransferRecord implements Serializable {

	private static final long serialVersionUID = -54906283430170483L;
	 /**
	  * 主键id
	  */
	@TableId(value = "RECORD_ID", type = IdType.AUTO)
	private Long recordId;
	/**
	 * 币商
	 */
	@TableField("SELLER_ID")
	private Long sellerId;
	/**
	 * 承兑商
	 */
	@TableField("USER_ID")
	private Long userId;
	/**
	 * 数量
	 */
	@TableField("NUMBER")
	private Double number;
	/**
	 * 类型
	 */
	@TableField("TYPE")
	private String type;
	//来自账号
	@TableField("FROM_ACCOUNT")
	private String fromAccount;
	//到那个账号
	@TableField("TO_ACCOUNT")
	private String toAccount;
	
	 /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
	private Date createTime;

    
    
	public String getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}

	public String getToAccount() {
		return toAccount;
	}

	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	

}
