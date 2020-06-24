package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 会员挂单表
 * @author zf
 *
 */
@TableName("seller_order")
public class SellerOrder implements Serializable, Comparable<SellerOrder> {

	
	private static final long serialVersionUID = -355833742335852732L;
	
	@TableId(value = "ORDER_ID", type = IdType.AUTO)
	private Long orderId;
	//流水号
	@TableField("SERIAL_NO")
	private String serialNo; 
	//会员id
	@TableField("SELLER_ID")
	private Long sellerId;
	//挂单数量
	@TableField("NUMBER")
	private Double number;
	/**
	 *  状态：0表示开启，1表示关闭
	 */
	@TableField("STATUS")
	private Integer status;
	/**
	 * 1表示会员出售，2表示商家购买
	 */
	@TableField("TYPE")
	private Integer type;
	
	//创建时间
	 @TableField(value="CREATE_TIME",fill=FieldFill.INSERT)
	private Date createTime;
	//关闭时间
	@TableField(value="CLOSE_TIME",fill=FieldFill.UPDATE)
	private Date closeTime;
	//完成时间
	@TableField(value="FINISH_TIME",fill=FieldFill.UPDATE)
	private Date finishTime;
	//更新时间
	@TableField(value="UPDATE_TIME",fill=FieldFill.UPDATE)
	private Date updateTime; 
	
	//版本号
	@TableField("VERSION")
	private Integer version;


	@TableField("IP")
	private String ip;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getVersion() {
		return version;
	}




	public void setVersion(Integer version) {
		this.version = version;
	}




	public Integer getType() {
		return type;
	}




	public void setType(Integer type) {
		this.type = type;
	}




public Long getOrderId() {
		return orderId;
	}




	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}




	public String getSerialNo() {
		return serialNo;
	}




	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}




	public Long getSellerId() {
		return sellerId;
	}




	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}




	public Double getNumber() {
		return number;
	}




	public void setNumber(Double number) {
		this.number = number;
	}




	public Integer getStatus() {
		return status;
	}




	public void setStatus(Integer status) {
		this.status = status;
	}




	public Date getCreateTime() {
		return createTime;
	}




	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}




	public Date getCloseTime() {
		return closeTime;
	}




	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}




	public Date getFinishTime() {
		return finishTime;
	}




	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}




	public Date getUpdateTime() {
		return updateTime;
	}




	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}




/**
      *
      * @Description:当前对象和其他对象做比较，当前优先级大就返回-1，优先级小就返回1
      * 值越小优先级越高
      * @param TODO
      */
	@Override
	public int compareTo(SellerOrder o) {
		return -this.createTime.compareTo(o.createTime);
	}

}
