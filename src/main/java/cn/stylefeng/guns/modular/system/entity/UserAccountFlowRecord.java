package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 承兑商账户流水记录
 * @author zf
 *
 */
@Data
@TableName("user_account_flow_record")
public class UserAccountFlowRecord implements Serializable {

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


	@TableField("TYPE")
	private String type;

	/**
	 * 通道类型流水记录：1表示支付宝通道，2表示微信，3表示银行卡
	 */
	@TableField("CHANNEL_TYPE")
	private Integer channelType;


	
	

}
