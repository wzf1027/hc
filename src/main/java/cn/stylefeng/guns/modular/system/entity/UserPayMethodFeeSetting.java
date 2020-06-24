package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 商户通道手续费设置表
 * @author zf
 *
 */
@TableName("user_pay_method_fee_setting")
public class UserPayMethodFeeSetting implements Serializable {

	private static final long serialVersionUID = -8992991565971100169L;
	
	@TableId(value="SETTING_ID",type=IdType.AUTO)
	private Long settingId;
	/**
	 * 商户id
	 */
	@TableField("USER_ID")
	private Long userId;
	/**
	 * 支付宝比例
	 */
	@TableField("ALIPAY_RATIO")
	private Double alipayRatio;

	/**
	 * 最小支付宝数额
	 */
	@TableField("MIN_ALIPAY_VALUE")
	private Double minAlipayValue;

	/**
	 * 最大支付宝数额
	 */
	@TableField("MAX_ALIPAY_VALUE")
	private Double maxAlipayValue;


	/**
	 * 微信比例
	 */
	@TableField("WX_RATIO")
	private Double wxRatio;

	/**
	 * 最小微信数额
	 */
	@TableField("MIN_WX_VALUE")
	private Double minWxValue;

	/**
	 * 最大微信数额
	 */
	@TableField("MAX_WX_VALUE")
	private Double maxWxValue;
	
	/**
	 * 银行卡比例
	 */
	@TableField("CARD_RATIO")
	private Double cardRatio;

	/**
	 * 最小银行卡数额
	 */
	@TableField("MIN_CARD_VALUE")
	private Double minCardValue;

	/**
	 * 最大银行卡数额
	 */
	@TableField("MAX_CARD_VALUE")
	private Double maxCardValue;

	/**
	 * 云闪付费率
	 */
	@TableField("CLOUD_PAY_RATIO")
	private Double cloudPayRatio;

	/**
	 * 最小云闪付数额
	 */
	@TableField("MIN_CLOUD_PAY_VALUE")
	private Double minCloudPayValue;

	/**
	 * 最大云闪付数额
	 */
	@TableField("MAX_CLOUD_PAY_VALUE")
	private Double maxCloudPayValue;
	
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

    private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getSettingId() {
		return settingId;
	}

	public void setSettingId(Long settingId) {
		this.settingId = settingId;
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

	public Double getMinAlipayValue() {
		return minAlipayValue;
	}

	public void setMinAlipayValue(Double minAlipayValue) {
		this.minAlipayValue = minAlipayValue;
	}

	public Double getMaxAlipayValue() {
		return maxAlipayValue;
	}

	public void setMaxAlipayValue(Double maxAlipayValue) {
		this.maxAlipayValue = maxAlipayValue;
	}

	public Double getMinWxValue() {
		return minWxValue;
	}

	public void setMinWxValue(Double minWxValue) {
		this.minWxValue = minWxValue;
	}

	public Double getMaxWxValue() {
		return maxWxValue;
	}

	public void setMaxWxValue(Double maxWxValue) {
		this.maxWxValue = maxWxValue;
	}

	public Double getMinCardValue() {
		return minCardValue;
	}

	public void setMinCardValue(Double minCardValue) {
		this.minCardValue = minCardValue;
	}

	public Double getMaxCardValue() {
		return maxCardValue;
	}

	public void setMaxCardValue(Double maxCardValue) {
		this.maxCardValue = maxCardValue;
	}

	public Double getCloudPayRatio() {
		return cloudPayRatio;
	}

	public void setCloudPayRatio(Double cloudPayRatio) {
		this.cloudPayRatio = cloudPayRatio;
	}

	public Double getMinCloudPayValue() {
		return minCloudPayValue;
	}

	public void setMinCloudPayValue(Double minCloudPayValue) {
		this.minCloudPayValue = minCloudPayValue;
	}

	public Double getMaxCloudPayValue() {
		return maxCloudPayValue;
	}

	public void setMaxCloudPayValue(Double maxCloudPayValue) {
		this.maxCloudPayValue = maxCloudPayValue;
	}


}
