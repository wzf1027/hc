package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 会员挖矿账号钱包
 * @author zf
 *
 */
@TableName("seller_profit_wallter")
public class SellerProfitWallter implements Serializable {

	private static final long serialVersionUID = 4383685365847235758L;
	  /**
     * 主键
     */
    @TableId(value = "PROFIT_WALLTER_ID", type = IdType.AUTO)
	private Long profitWallterId;
	
	 /**
     *手机号码
     */
    @TableField("PHONE")
	private String phone;
    
    /**
     * 币商id
     */
    @TableField("SELLER_ID")
    private Long sellerId;
    
    
    /**
	 * 可用余额
	 */
	@TableField("AVAILABLE_BALANCE")
	private Double availableBalance;
	/**
	 * 总余额
	 */
	@TableField("TOTAL_BALANCE")
	private Double totalBalance;
	/**
	 * 冻结余额
	 */
	@TableField("FROZEN_BALANCE")
	private Double frozenBalance;
	
	  /**
     * 乐观锁
     */
    @TableField("VERSION")
    private Integer version;
    
    /**
     * 钱包表示:1表示会员挖矿，2表示承兑商挖矿，3表示推荐挖矿
     */
    @TableField("CODE")
    private String code;
    
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
    
    
	public Long getProfitWallterId() {
		return profitWallterId;
	}
	public void setProfitWallterId(Long profitWallterId) {
		this.profitWallterId = profitWallterId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Long getSellerId() {
		return sellerId;
	}
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
	public Double getAvailableBalance() {
		return availableBalance;
	}
	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}
	public Double getTotalBalance() {
		return totalBalance;
	}
	public void setTotalBalance(Double totalBalance) {
		this.totalBalance = totalBalance;
	}
	public Double getFrozenBalance() {
		return frozenBalance;
	}
	public void setFrozenBalance(Double frozenBalance) {
		this.frozenBalance = frozenBalance;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
