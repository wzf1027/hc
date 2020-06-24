package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * usdt跟otcp兑换表
 * @author zf
 *
 */
@TableName("usdt_otcp_exchange")
public class USDTOtcpExchange implements Serializable {

	private static final long serialVersionUID = 8899289275629668511L;
	
	/**
	  * 主键id
	  */
	@TableId(value = "EXCHANGE_ID", type = IdType.AUTO)
	private Long exchangeId;
	/**
	 * 值
	 */
	@TableField("VALUE")
	private Double value;

	public Long getExchangeId() {
		return exchangeId;
	}

	public void setExchangeId(Long exchangeId) {
		this.exchangeId = exchangeId;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	

}
