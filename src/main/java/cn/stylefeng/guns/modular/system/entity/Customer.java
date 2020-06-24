package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("customer")
public class Customer implements Serializable {

	private static final long serialVersionUID = 5526815135980307357L;
	
	@TableId(value="CUSTOMER_ID",type=IdType.AUTO)
	private Long customerId;
	
	@TableField("QQ_NO")
	private String qqNo;
	
	@TableField("EMAIL")
	private String email;
	
	@TableField("WEIXIN_ACCOUNT")
	private String weixinAccount;
	
	@TableField("WX_QRCODE")
	private String wxQrCode;

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	

	public String getQqNo() {
		return qqNo;
	}

	public void setQqNo(String qqNo) {
		this.qqNo = qqNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWeixinAccount() {
		return weixinAccount;
	}

	public void setWeixinAccount(String weixinAccount) {
		this.weixinAccount = weixinAccount;
	}

	public String getWxQrCode() {
		return wxQrCode;
	}

	public void setWxQrCode(String wxQrCode) {
		this.wxQrCode = wxQrCode;
	}
	
	
	
}
