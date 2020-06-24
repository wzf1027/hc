package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 平台充币地址表
 * @author zf
 *
 */
@TableName("platform_coin_address")
public class PlatformCoinAddress implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9087199474469745380L;
	
    /**
     * 主键id
     */
    @TableId(value = "COIN_ADDRESS_ID", type = IdType.AUTO)
	private Long coinAddressId;
	
    /**
     * 地址
     */
    @TableField("ADDRESS")
	private String address;
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

	public Long getCoinAddressId() {
		return coinAddressId;
	}

	public void setCoinAddressId(Long coinAddressId) {
		this.coinAddressId = coinAddressId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
