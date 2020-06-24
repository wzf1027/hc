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
 * 变更余额记录表
 */
@Data
@TableName("account_update_record")
public class AccountUpdateRecord implements Serializable {

	private static final long serialVersionUID = -1689456860908495849L;
	
	@TableId(value="RECORD_ID",type=IdType.AUTO)
	private Long recordId;
	/**
	 * 流水记录
	 */
	@TableField("SERIAL_NO")
	private String serialno;
	/**
	 * 账号id
	 */
	@TableField("ACCOUNT_ID")
	private Long accountId;
	
	/**
	 * 手机号码
	 */
	@TableField("PHONE")
	private String phone;
	/**
	 * 资产
	 */
	@TableField("SOURCE")
	private String source;
	
	/**
	 * 币种
	 */
	@TableField("CODE")
	private String code;
	
	/**
	 * 类型
	 */
	@TableField("TYPE")
	private String type;
	
	/**
	 * 通道（1支付宝、2微信、3银行卡）
	 */
	@TableField("PAYMETHOD_TYPE")
	private Integer payMethodType;
	
	/**
	 * 备注
	 */
	@TableField("REMARK")
	private String remark;

	/**
	 * 钱包标识:查看AccountUpdateWallet这个类
	 */
	@TableField("WALLET_CODE")
	private String walletCode;

	/**
	 * 角色：1：会员，2商户，3承兑商，4代理商
	 */
	@TableField("ROLE_ID")
	private Long roleId;

	/**
	 * 变动前
	 */
	@TableField("BEFORE_PRICE")
	private Double beforePrice;

	/**
	 * 变动后
	 */
	@TableField("AFTER_PRICE")
	private Double afterPrice;

	/**
	 * 变动余额
	 */
	@TableField("PRICE")
	private Double price;
	
	@TableField(value="CREATE_TIME",fill=FieldFill.INSERT)
	private Date createTime;
	


}
