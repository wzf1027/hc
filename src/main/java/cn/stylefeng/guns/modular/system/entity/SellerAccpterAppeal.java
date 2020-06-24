package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 承兑商申请记录表
 * @author zf
 *
 */
@TableName("seller_accpter_appeal")
public class SellerAccpterAppeal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1281453401411940015L;
	
	/**
     * 主键id
     */
    @TableId(value = "APPEAL_ID", type = IdType.AUTO)
	private Long appealId;
	/**
	 * 会员id
	 */
    @TableField("SELLER_ID")
	private Long sellerId;
    /**
     * 真实姓名
     */
	@TableField("NAME")
	private String name;
	
	/**
	 * 身份证号码
	 */
	@TableField("IDCARD_NO")
	private String idCardNo;
	/**
	 * 联系方式
	 */
	@TableField("PHONE")
	private String phone;
	/**
	 * 状态：1表示审核中，2表示已审核通过，3表示审核失败
	 */
	@TableField("STATUS")
	private Integer status;
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

	@TableField("UPDATE_USER_ID")
	private Long updateUserId;

	@TableField("USER_ACCOUNT")
	private String userAccount;

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public Long getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Long updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Long getAppealId() {
		return appealId;
	}
	public void setAppealId(Long appealId) {
		this.appealId = appealId;
	}
	public Long getSellerId() {
		return sellerId;
	}
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdCardNo() {
		return idCardNo;
	}
	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
