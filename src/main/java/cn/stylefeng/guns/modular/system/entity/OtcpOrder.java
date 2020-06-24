package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * otc交易的订单表
 * @author zf
 *
 */
@Data
@TableName("otcp_order")
public class OtcpOrder implements Serializable {

	private static final long serialVersionUID = -8153953728432439801L;
	/**
	 * 主键id
	 */
	@TableId(value="ORDER_ID",type=IdType.AUTO)
	private Long orderId;
	/**
	 * 流水订单号
	 */
	@TableField("SERIALNO")
	private String serialno;
	/**
	 * 买家
	 */
	@TableField("BUYER_ID")
	private Long buyerId;
	/**
	 * 买家手机号码
	 */
	@TableField("BUYER_PHONE")
	private String buyerPhone;
	
	/**
	 * 卖家
	 */
	@TableField("SELLER_ID")
	private Long sellerId;
	/**
	 * 卖家手机号码
	 */
	@TableField("SELLER_PHONE")
	private String sellerPhone;
	
	/**
	 * 出售单价
	 */
	@TableField("PRICE")
	private Double price;
	/**
	 * 出售数量
	 */
	@TableField("NUMBER")
	private Double number;
	/**
	 * 总价格
	 */
	@TableField("TOTAL_PRICE")
	private Double totalPrice;
	/**
	 * 状态：1:表示未支付，2：表示支付成功，等待确认到账，3：卖家确认，待买家确认，4：已完成，5：已超时，6申诉中，7：取消
	 */
	@TableField("STATUS")
	private Integer status;
	/**
	 * 下单时间
	 */
	@TableField(value="CREATE_TIME",fill=FieldFill.INSERT)
	private Date createTime;
	/**
	 * 更新时间
	 */
	@TableField(value="UPDATE_TIME",fill=FieldFill.UPDATE)
	private Date updateTime;
	/**
	 * 取消时间
	 */
	@TableField(value="CANNEL_TIME",fill=FieldFill.UPDATE)
	private Date cannelTime;
	/**
	 * 收款方式ids
	 */
	@TableField("PAY_METHOD_IDS")
	private Object payMethodIds;
	
	/**
	 * 买家支付的支付方式id
	 */
	@TableField("PAY_METHOD_ID")
	private Long payMethodId;
	
	/**
	 * 买家支付的类型:1表示支付宝，2表示微信，3表示银行
	 */
	@TableField("PAY_METHOD_TYPE")
	private Integer payMethodType;

	/**
	 * 付款凭证
	 */
	@TableField("PAY_CERTIFICATE")
	private String payCertificate;
	
	
	/**
	 * 申诉内容
	 */
	@TableField("APPEAL_CONTENT")
	private String appealContent;
	/**
	 * 申诉人id：会员，承兑商，商户
	 */
	@TableField("APPEALER_ID")
	private Long appealerId;
	/**
	 * 角色：1表示会员，2表示商户，3表示承兑商
	 */
	@TableField("APPEALER_ROLE")
	private Integer appealerRole;
	
	/**
	 * 申述凭证
	 */
	@TableField("CERTIFICATE")
	private String certificate; 
	
	/**
	 * 未申诉时的订单状态
	 */
	@TableField("NO_APPEAL_STATUS")
	private Integer noAppealStatus;
	
	/**
	 * 申述状态：0表示未申诉，1表示申诉中，2表示买家胜利，3表示卖家胜利，4表示取消申诉
	 */
	@TableField("IS_APPEAL")
	private Integer isAppeal;
	/**
	 * 申述时间
	 */
	@TableField(value="APPEAL_TIME",fill=FieldFill.UPDATE)
	private Date appealTime;
	/**
	 * 结束时间
	 */
	@TableField(value="CLOSE_TIME",fill=FieldFill.UPDATE)
	private Date closeTime;
	
	/**
	 * 类型：1表示会员订单，2承兑商订单
	 */
	@TableField("TYPE")
	private Integer type;
	
	/**
	 * 购买出售类型：1表示购买，2表示出售
	 */
	@TableField("BUY_SELL_TYPE")
	private Integer buySellType;
	
	/**
	 * 参考号
	 */
	@TableField("REMARK")
	private String remark;
	
	/**
	 * 出售订单id
	 */
	@TableField("OTC_ORDER_ID")
	private Long otcOrderId;
	
	/**
	 * 手续费
	 */
	@TableField("FEE_PRICE")
	private Double feePrice;

	/**
	 * 操作人id
	 */
	@TableField("UPDATE_USER_ID")
	private Long updateUserId;

	/**
	 * 操作人
	 */
	@TableField("USER_ACCOUNT")
	private String userAccount;


	/**
	 * 币种
	 */
	@TableField("SYMBOLS")
	private String symbols;

	
}
