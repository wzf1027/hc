package cn.stylefeng.guns.modular.system.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SellOtcpDto implements Serializable {

	private static final long serialVersionUID = -3682745383774172654L;
	
	private Double minNumber;
	
	private Double maxNumber;
	
	private String payMethodId;
	
	private String account;
	
	private String cardBank;
	
	private String realName;
	
	private String cardBankName;
	
	private Double number;

	private String symbols;

	
	

}
