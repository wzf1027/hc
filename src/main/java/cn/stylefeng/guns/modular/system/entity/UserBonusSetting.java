package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 商户跟代理的支付通道手续费表
 * @author zf
 *
 */
@TableName("user_bonus_setting")
public class UserBonusSetting implements Serializable {

	private static final long serialVersionUID = -8992991565971100169L;
	
	@TableId(value="BONUS_ID",type=IdType.AUTO)
	private Long bonusId;
	/**
	 * 商户id
	 */
	@TableField("USER_ID")
	private Long userId;
	/**
	 * 代理商id
	 */
	@TableField("AGENT_ID")
	private Long agentId;
	/**
	 * 支付宝比例
	 */
	@TableField("ALIPAY_RATIO")
	private Double alipayRatio;
	/**
	 * 微信比例
	 */
	@TableField("WX_RATIO")
	private Double wxRatio;
	
	/**
	 * 银行卡比例
	 */
	@TableField("CARD_RATIO")
	private Double cardRatio;


	@TableField("CLOUD_PAY_RATIO")
	private Double cloudPayRatio;

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

	public Double getCloudPayRatio() {
		return cloudPayRatio;
	}

	public void setCloudPayRatio(Double cloudPayRatio) {
		this.cloudPayRatio = cloudPayRatio;
	}

	public Long getBonusId() {
		return bonusId;
	}

	public void setBonusId(Long bonusId) {
		this.bonusId = bonusId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Double getAlipayRatio() {
		return alipayRatio;
	}

	public void setAlipayRatio(Double alipayRatio) {
		this.alipayRatio = alipayRatio;
	}

	public Double getWxRatio() {
		return wxRatio;
	}

	public void setWxRatio(Double wxRatio) {
		this.wxRatio = wxRatio;
	}

	public Double getCardRatio() {
		return cardRatio;
	}

	public void setCardRatio(Double cardRatio) {
		this.cardRatio = cardRatio;
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
