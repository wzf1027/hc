package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 流水记录
 *
 */
@Data
@TableName("seller_account_flow_record")
public class SellerAccountFlowRecord implements Serializable {

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
	 * 流水标识：USDT或者HC
	 */
	@TableField("CODE")
	private String code;
	
	
	/**
	 * 订单流水号
	 */
	@TableField("SERIAL_NO")
	private String serialno;
	
	/**
	 * 备注
	 */
	@TableField("REMARK")
	private String remark;

	/**
	 * 流水类型
	 */
	@TableField("TYPE")
	private String type;

	/**
	 * 钱包类型：1表示搬砖账户，2表示代币账号，3表示法币账户，4表示挖矿账户
	 */
	@TableField("WALLET_TYPE")
	private Integer walletType;

}
