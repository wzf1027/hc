package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 商户支付方式表
 * @author zf
 *
 */
@TableName("sys_user_pay_method")
public class UserPayMethod implements Serializable {

	private static final long serialVersionUID = 4674111559800677388L;
	
	   /**
	  * 主键id
	  */
	@TableId(value = "PAY_METHOD_ID", type = IdType.AUTO)
	private Long payMethodId;
	/**
	 * 代理商/商户id
	 */
	@TableField("USER_ID")
	private Long userId;
	
	//支付类型 1：表示支付宝，2：表示微信，3：表示银行卡 4:聚合码
	@TableField("TYPE")
	private Integer type;
	/**
	 * 二维码
	 */
	@TableField("QR_CODE")
	private String qrCode; 
	/**
	 * 账号
	 */
	@TableField("ACCOUNT")
	private String account;
	
	/**
	 * 开户支行
	 */
	@TableField("CARD_BANK")
	private String cardBank;
	
	/**
	 * 银行名称
	 */
	@TableField("CARD_BANK_NAME")
	private String cardBankName;
	
	/**
	 * 姓名
	 */
	@TableField("NAME")
	private String name;
	/**
	 * 使用次数
	 */
	@TableField("USE_NUMBER")
	private Long useNumber;
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

	public Long getPayMethodId() {
		return payMethodId;
	}

	public void setPayMethodId(Long payMethodId) {
		this.payMethodId = payMethodId;
	}



	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCardBank() {
		return cardBank;
	}

	public void setCardBank(String cardBank) {
		this.cardBank = cardBank;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getUseNumber() {
		return useNumber;
	}

	public void setUseNumber(Long useNumber) {
		this.useNumber = useNumber;
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

	public String getCardBankName() {
		return cardBankName;
	}

	public void setCardBankName(String cardBankName) {
		this.cardBankName = cardBankName;
	}
    
    
	
	

}
