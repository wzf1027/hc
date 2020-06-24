package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 匹配成功会员交易收款码表
 * @author zf
 *
 */
@TableName("seller_buy_coin_pay_method_queue")
public class SellerBuyCoinPayMethodQueue implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5491412454995089836L;
	@TableId(type=IdType.AUTO,value="ID")
	private Long id;
	
	/**
	 * 收款方式id
	 */
	@TableField(value="PAYMETHOD_ID")
	private Long payMethodId;
	/**
	 * 会员 id
	 */
	@TableField("SELLER_ID")
	private Long sellerId;
	/**
	 * 类型：1表示支付宝，2表示微信，3表示银行卡
	 */
	@TableField("TYPE")
	private Integer type;
	/**
	 * 价格
	 */
	@TableField("PRICE")
	private Double price;
	/**
	 * 交易订单id
	 */
	@TableField("SELLER_ORDER_ID")
	private Long sellerOrderId;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSellerOrderId() {
		return sellerOrderId;
	}
	public void setSellerOrderId(Long sellerOrderId) {
		this.sellerOrderId = sellerOrderId;
	}
	public Long getPayMethodId() {
		return payMethodId;
	}
	public void setPayMethodId(Long payMethodId) {
		this.payMethodId = payMethodId;
	}
	public Long getSellerId() {
		return sellerId;
	}
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	
	
	

}
