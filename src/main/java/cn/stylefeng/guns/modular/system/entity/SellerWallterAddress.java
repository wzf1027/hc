package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *  币商提现钱包的地址表
 * @author zf
 *
 */
@TableName("seller_wallter_address")
public class SellerWallterAddress implements Serializable {

	private static final long serialVersionUID = 1419639116081168063L;
	  /**
     * 主键
     */
    @TableId(value = "ADDRESS_ID", type = IdType.AUTO)
	private Long addressId;
	
    /**
     * 币商id
     */
	@TableField("SELLER_ID")
	private Long sellerId;
	
	/**
	 * 地址
	 */
	@TableField("ADDRESS")
	private String address;
	/**
	 * 备注
	 */
	@TableField("REMARK")
	private String remark;
	
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

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
    
	

}
