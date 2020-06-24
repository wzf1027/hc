package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
@TableName("buy_coin_used_pay_method_record")
public class BuyCoinUsedPayMethodRecord implements Serializable {

	private static final long serialVersionUID = 8835082892014298064L;

	 @TableId(value = "RECORD_ID", type = IdType.AUTO)
	private Long recordId;
	 
	@TableField("SELLER_ID")
	private Long sellerId;
	
	@TableField("TYPE")
	private Integer type;
	
	@TableField("PAY_METHOD_ID")
	private Long payMethodId;
	 
	@TableField(value = "CREATE_TIME",fill = FieldFill.INSERT)
	private Date createTime;

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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getPayMethodId() {
		return payMethodId;
	}

	public void setPayMethodId(Long payMethodId) {
		this.payMethodId = payMethodId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	
	
	
	

}
