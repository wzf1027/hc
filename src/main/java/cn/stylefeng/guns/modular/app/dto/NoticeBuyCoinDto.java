package cn.stylefeng.guns.modular.app.dto;

import java.io.Serializable;

public class NoticeBuyCoinDto implements Serializable {

	private static final long serialVersionUID = 3428963646004659192L;
	
	private String uid;
	
	private String price;
	
	private String cuid;
	
	private String user_order_no;
	
	private String paytype;
	
	private String orderno;
	
	private String realprice;
	
	private String sign;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCuid() {
		return cuid;
	}

	public void setCuid(String cuid) {
		this.cuid = cuid;
	}

	public String getUser_order_no() {
		return user_order_no;
	}

	public void setUser_order_no(String user_order_no) {
		this.user_order_no = user_order_no;
	}

	public String getPaytype() {
		return paytype;
	}

	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getRealprice() {
		return realprice;
	}

	public void setRealprice(String realprice) {
		this.realprice = realprice;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	

}
