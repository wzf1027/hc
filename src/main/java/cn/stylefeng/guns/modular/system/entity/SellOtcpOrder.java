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
 *出售otcp表
 * @author zf
 *
 */
@TableName("sell_otcp_order")
@Data
public class SellOtcpOrder implements Serializable {

	private static final long serialVersionUID = -6811927885133269419L;
	/**
	 * 订单id
	 */
	@TableId(value="ORDER_ID",type=IdType.AUTO)
	private Long orderId;
	
	/**
	 * 流水订单号
	 */
	@TableField("SERIALNO")
	private String serialno;
	
	/**
	 * 承兑商id
	 */
	@TableField("USER_ID")
	private Long userId;
	
	/**
	 * 出售用户电话
	 */
	@TableField("PHONE")
	private String phone;
	
	/**
	 *数量
	 */
	@TableField("NUMBER")
	private Double number;
	
	/**
	 * 可用数量
	 */
	@TableField("SUP_NUMBER")
	private Double supNumber;
	/**
	 * 最小数量
	 */
	@TableField("MIN_NUMBER")
	private Double minNumber;
	/**
	 * 最大数量
	 */
	@TableField("MAX_NUMBER")
	private Double maxNumber;
	/**
	 * 角色id
	 */
	@TableField("ROLE_ID")
	private Integer roleId;
	/**
	 * 会员账号id
	 */
	@TableField("SELLER_ID")
	private Long sellerId;
	
	/**
	 * 支付方式ids集合
	 */
	@TableField("PAY_METHOD_IDS")
	private Object payMethodIds;

	/**
	 * 支付类型集合
	 */
	@TableField("PAY_METHOD_TYPE")
	private String payMethodType;


	/**
	 * 手续费
	 * 
	 */
	@TableField("FEE_PRICE")
	private Double feePrice;
	
	/**
	 *  手续费比例
	 */
	@TableField("FEE_RATIO")
	private Double feeRatio;
	
	/**
	 * 状态：1表示正在进行中，2,表示已完成，3表示已取消
	 */
	@TableField("STATUS")
	private Integer status;
	
	/**
	 * 单价
	 */
	@TableField("PRICE")
	private Double price;
	
	/**
	 * 总价
	 */
	@TableField("TOTAL_PRICE")
	private Double totalPrice;
	
	/**
	 * 类型：1:会员出售，2表示承兑商出售，3表示商户出售，4表示代理商
	 */
	@TableField("TYPE")
	private Integer type ;
	
	/**
	 *  自动出售:0表示否，1表示是
	 */
	@TableField("AUTO_MERCHANT")
	private Integer autoMerchant;
	
	  /**
	   * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
	private Date createTime;
    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
	private Date updateTime;
    
    /**
     * 版本号
     */
    @TableField("VERSION")
    private Integer version;

	/**
	 * 币种
	 */
	@TableField("SYMBOLS")
    private String symbols;



}
