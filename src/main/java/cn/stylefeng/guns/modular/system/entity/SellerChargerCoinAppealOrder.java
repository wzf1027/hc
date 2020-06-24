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
 * 充币审核订单表
 *
 */
@Data
@TableName("seller_charger_coin_appeal_order")
public class SellerChargerCoinAppealOrder implements Serializable {

	private static final long serialVersionUID = 3146722342717461654L;
	 /**
     * 主键
     */
    @TableId(value = "APPEAL_ID", type = IdType.AUTO)
	private Long appealId;
    
    /**
	 * 流水订单号
	 */
	@TableField("SERIALNO")
	private String serialno;
    
    /**
     * 币商id
     */
	@TableField("SELLER_ID")
	private Long sellerId;
	/**
	 * 充币数量
	 */
	@TableField("NUMBER")
	private Double number;
	/**
	 * hash值
	 */
	@TableField("HASH_VALUE")
	private String hashValue;
	/**
	 * 平台充币地址
	 */
	@TableField("ADDRESS")
	private String address;
	/**
	 * 状态：1表示审核中，2表示审核通过，3表示审核不通过
	 */
	@TableField("STATUS")
	private Integer status;
	
	 /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
	private Date createTime;
    
    
	 /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
	private Date updateTime;

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


}
