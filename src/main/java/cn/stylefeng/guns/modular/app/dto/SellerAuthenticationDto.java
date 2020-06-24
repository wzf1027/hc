package cn.stylefeng.guns.modular.app.dto;

import java.io.Serializable;

public class SellerAuthenticationDto implements Serializable {

	private static final long serialVersionUID = 3259515417047555462L;
	
	private String realName;
	
	private String idCardNo;
	
	private String  idCardFront;//正面
	
	private String idCardReverse;//反面
	
	private String idCardImage;//手持身份证

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public String getIdCardFront() {
		return idCardFront;
	}

	public void setIdCardFront(String idCardFront) {
		this.idCardFront = idCardFront;
	}

	public String getIdCardReverse() {
		return idCardReverse;
	}

	public void setIdCardReverse(String idCardReverse) {
		this.idCardReverse = idCardReverse;
	}

	public String getIdCardImage() {
		return idCardImage;
	}

	public void setIdCardImage(String idCardImage) {
		this.idCardImage = idCardImage;
	}
	
	

}
