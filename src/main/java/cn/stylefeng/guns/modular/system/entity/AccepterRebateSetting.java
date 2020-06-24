package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 承兑商otc交易返利设置表
 * @author zf
 *
 */
@Data
@TableName("accepter_rebate_setting")
public class AccepterRebateSetting implements Serializable {

	private static final long serialVersionUID = 4349932682760021618L;
	
	@TableId(value="REBATE_ID",type=IdType.AUTO)
	private Long rebateId;
	/**
	 *返利比例
	 */
	@TableField("VALUE")
	private Double value;

	/**
	 * 通道类型：1表示支付宝，2：表示微信，3表示银行卡
	 */
	@TableField("CHANNEL_TYPE")
	private Integer channelType;

	/**
	 * 币种
	 */
	@TableField("SYMBOLS")
	private String symbols;

}
