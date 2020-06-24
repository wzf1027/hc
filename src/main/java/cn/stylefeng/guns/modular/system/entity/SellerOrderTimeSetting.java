package cn.stylefeng.guns.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员订单失效时间设置表
 * @author zf
 *
 */
@TableName("seller_order_time_setting")
public class SellerOrderTimeSetting implements Serializable {

	private static final long serialVersionUID = 4349932682760021618L;
	
	@TableId(value="ID",type=IdType.AUTO)
	private Long id;
	
	@TableField("STAR_TIME")
	private Integer starTime;

	@TableField("END_TIME")
	private Integer endTime;

	@TableField(value="CREATE_TIME",fill= FieldFill.INSERT)
	private Date createTime;

	@TableField(value="UPDATE_TIME",fill=FieldFill.UPDATE)
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getStarTime() {
		return starTime;
	}

	public void setStarTime(Integer starTime) {
		this.starTime = starTime;
	}

	public Integer getEndTime() {
		return endTime;
	}

	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
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
