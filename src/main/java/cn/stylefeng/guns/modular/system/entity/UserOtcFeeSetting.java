package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 商户手动出售手续费表
 * @author zf
 *
 */
@TableName("user_otc_fee_setting")
public class UserOtcFeeSetting implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5193836488228827773L;
	
	@TableId(value="SETTING_ID",type=IdType.AUTO)
	private Long settingId;
	/**
	 * 比例
	 */
	@TableField("RATIO")
	private Double ratio;
	
	@TableField("TYPE")
	private Integer type;
	
	@TableField(value="CREATE_TIME",fill=FieldFill.INSERT)
	private Date createTime;
	
	@TableField(value="UPDATE_TIME",fill=FieldFill.UPDATE)
	private Date updateTime;

	public Long getSettingId() {
		return settingId;
	}

	
	
	public Integer getType() {
		return type;
	}



	public void setType(Integer type) {
		this.type = type;
	}



	public void setSettingId(Long settingId) {
		this.settingId = settingId;
	}

	public Double getRatio() {
		return ratio;
	}

	public void setRatio(Double ratio) {
		this.ratio = ratio;
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
