package cn.stylefeng.guns.modular.app.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.DigestUtils;

import cn.stylefeng.guns.core.util.Md5Utils;

public class BuyCoinDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2568329724512442628L;
	//金额
	private String price;
	//商户ID
	private String uid;
	//支付渠道
	private Integer paytype;
	//异步回调通知
	private String notify_url;
	//同步地址
	private String return_url;
	//商户自定义订单号
	private String user_order_no;
	//商户自定义的用户唯一
	private String cuid;
	//签名
	private String sign;
	
	
	
	
	public String getReturn_url() {
		return return_url;
	}
	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Integer getPaytype() {
		return paytype;
	}
	public void setPaytype(Integer paytype) {
		this.paytype = paytype;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getUser_order_no() {
		return user_order_no;
	}
	public void setUser_order_no(String user_order_no) {
		this.user_order_no = user_order_no;
	}
	public String getCuid() {
		return cuid;
	}
	public void setCuid(String cuid) {
		this.cuid = cuid;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	
	public static void main(String[] args) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("uid", "1189471008118001673");
		param.put("price","100.0");
		param.put("paytype", "1");
		param.put("user_order_no","1452154512154211");
		param.put("notify_url", "12154154512154");
		param.put("cuid","10");
		String sbf = Md5Utils.getSign(param);
		sbf = sbf+"key=shxm9cupaxdzwr9v132a5bgeyajkxxmm";
		String sign =DigestUtils.md5DigestAsHex(sbf.getBytes()).toLowerCase();
		System.out.println(sign);
	}
	
	
}
