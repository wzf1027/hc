package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * otcp取消订单次数记录
 * @author zf
 *
 */
@TableName("otcp_order_cannel_number_record")
public class OtcpOrderCannelNumberRecord implements Serializable {

	private static final long serialVersionUID = -8235326063663383624L;
	
	@TableId(value="RECORD_ID")
	private Long recordId;
	
	/**
	 * 会员id
	 */
	@TableField("SELLER_ID")
	private Long sellerId;
	
	/**
	 * 次数
	 */
	@TableField("NUMBER")
	private Integer number;
	
	/**
	 * 创建时间
	 */
	@TableField(value="CREATE_TIME",fill=FieldFill.INSERT)
	private Date createTime;

	@TableField(value="UPDATE_TIME",fill=FieldFill.UPDATE)
	private Date updateTime;

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	

}
