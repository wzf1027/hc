package cn.stylefeng.guns.modular.app.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SellerPayMethodDto implements Serializable {

	private static final long serialVersionUID = -3401240055712093275L;
	
	private Long id;

	/**
	 * 姓名
	 */
	private String name;

	/**
	 * 账号
	 */
	private String account;

	/**
	 * 二维码
	 */
	private String qrCode;

	/**
	 * 支付类型 1：表示支付宝，2：表示微信，3：表示银行卡 4:支付宝固码,5：支付宝转银行
	 */
	private Integer type ;


	private String cardBank;
	
	private String cardBankName;

	/**
	 * 昵称
	 */
	private String remark;

	/**
	 * 二维码渲染出来的链接
	 */
	private String value;

	/**
	 * 支付宝固定码的固定金额
	 */
	private Double price;
	

}
