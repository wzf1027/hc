package cn.stylefeng.guns.modular.system.dto;

import java.io.Serializable;

public class SellerAccountUpdate implements Serializable {

	private static final long serialVersionUID = 4059038237650663617L;
	//订单号
	private String serialno;
	//手机号码
	private String phone;
	//资产
	private String source;
	//币种
	private  String code;
	//类型
	private String type;
	//通道
	private Integer payMethodType;
	//时间
	private String timeLimit;
	
	
	
	
	public String getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}

	public String getSerialno() {
		return serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getPayMethodType() {
		return payMethodType;
	}

	public void setPayMethodType(Integer payMethodType) {
		this.payMethodType = payMethodType;
	}
	
	

}
