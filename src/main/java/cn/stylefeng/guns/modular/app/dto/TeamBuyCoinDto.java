package cn.stylefeng.guns.modular.app.dto;

import java.io.Serializable;

public class TeamBuyCoinDto implements Serializable {

	private static final long serialVersionUID = 6260836012651302903L;
	
	private Long sellerId;
	
	private Double totalNumber;
	
	private Double teamPrice;

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public Double getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(Double totalNumber) {
		this.totalNumber = totalNumber;
	}

	public Double getTeamPrice() {
		return teamPrice;
	}

	public void setTeamPrice(Double teamPrice) {
		this.teamPrice = teamPrice;
	}
	
	

}
