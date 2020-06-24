package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.core.util.FlowRecordConstant;
import cn.stylefeng.guns.core.util.ResponseData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 挖矿账号账户流水记录
 * @author zf
 *
 */
@TableName("seller_profit_account_flow_record")
public class SellerProfitAccountFlowRecord implements Serializable {

	private static final long serialVersionUID = 2987057360303620388L;
	
	/**
	 * 主键id
	 */
    @TableId(value = "FLOW_RECORD_ID", type = IdType.AUTO)
	private Long flowRecordId;
	
	/**
	 * 来源
	 */
	@TableField("SOURCE")
	private String source;
	/**
	 * 价格
	 */
	@TableField("PRICE")
	private Double price;
	/**
	 * 时间
	 */
	@TableField("CREATE_TIME")
	private Date createTime;
	
	/**
	 * 用户id
	 */
	@TableField("SELLER_ID")
	private Long sellerId; 
	
	/**
	 * 承兑商流水记录
	 */
	@TableField("USER_ID")
	private Long userId;
	
	/**
	 * 流水标识：1表示会员挖矿，2表示承兑商挖矿，3表示推荐挖矿
	 */
	@TableField("CODE")
	private String code;
	
	/**
	 * 订单流水号
	 */
	@TableField("SERIAL_NO")
	private String serialno;


	@TableField("TYPE")
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSerialno() {
		return serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}

	public Long getFlowRecordId() {
		return flowRecordId;
	}

	public void setFlowRecordId(Long flowRecordId) {
		this.flowRecordId = flowRecordId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}





	

}
