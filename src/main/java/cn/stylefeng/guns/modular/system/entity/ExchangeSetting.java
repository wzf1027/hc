package cn.stylefeng.guns.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 兑换手续费设置
 * @author zf
 *
 */
@TableName("exchange_setting")
public class ExchangeSetting implements Serializable {

	private static final long serialVersionUID = 1673536384115382512L;
	
	@TableId(type=IdType.AUTO,value="ID")
	private Long id;
	
	/**
	 * 手续费率
	 */
	@TableField("EXCHANGE_VALUE")
	private Double exchangeValue;
	
	/**
	 * 类型：1USDT兑换成HC，2表示HC兑换成USDT
	 */
	@TableField("TYPE")
	private Integer type;
	
	/**
	 * 方式：1表示会员兑换，2表示商户兑换，3：表示承兑商兑换
	 */
	@TableField("ROLE_ID")
	private Integer roleId;
	
	 /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
	private Date createTime;
    
    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    private Date updateTime;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getExchangeValue() {
		return exchangeValue;
	}

	public void setExchangeValue(Double exchangeValue) {
		this.exchangeValue = exchangeValue;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
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
