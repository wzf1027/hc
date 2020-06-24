package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 商户提现手续费设置
 * @author zf
 *
 */
@TableName("user_withdraw_fee_setting")
public class UserWithdrawFeeSetting implements Serializable {

	private static final long serialVersionUID = -3164706220476638824L;
	
	/**
     * 主键
     */
    @TableId(value = "SETTING_ID", type = IdType.AUTO)
	private Long settingId;
	/**
	 * 最小提现数量
	 */
	@TableField("MIN_NUMBER")
	private Double minNumber;
	/**
	 * 最大提现数量
	 */
	@TableField("MAX_NUMBER")
	private Double maxNumber;
	/**
	 * 最小提现手续费
	 */
	@TableField("MIN_FEE_NUMBER")
	private Double minFeeNumber;
	/**
	 * 提现手续费比例
	 */
	@TableField("FEE_RATIO")
	private Double feeRatio;
	/**
	 * 超过该体现数量以提现比例算
	 */
	@TableField("START_RATIO_NUMBER")
	private Double startRatioNumber;
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
     * 角色id：2表示商户，4表示代理商
     */
	@TableField("ROLE_ID")
    private String roleId;
	
    
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public Long getSettingId() {
		return settingId;
	}
	public void setSettingId(Long settingId) {
		this.settingId = settingId;
	}
	public Double getMinNumber() {
		return minNumber;
	}
	public void setMinNumber(Double minNumber) {
		this.minNumber = minNumber;
	}
	public Double getMaxNumber() {
		return maxNumber;
	}
	public void setMaxNumber(Double maxNumber) {
		this.maxNumber = maxNumber;
	}
	public Double getMinFeeNumber() {
		return minFeeNumber;
	}
	public void setMinFeeNumber(Double minFeeNumber) {
		this.minFeeNumber = minFeeNumber;
	}
	public Double getFeeRatio() {
		return feeRatio;
	}
	public void setFeeRatio(Double feeRatio) {
		this.feeRatio = feeRatio;
	}
	public Double getStartRatioNumber() {
		return startRatioNumber;
	}
	public void setStartRatioNumber(Double startRatioNumber) {
		this.startRatioNumber = startRatioNumber;
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
