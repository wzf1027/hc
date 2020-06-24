package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 承兑商上级返利设置
 * @author zf
 *
 */
@Data
@TableName("superior_accepter_rebate_setting")
public class SuperiorAccepterRebateSetting implements Serializable {

	private static final long serialVersionUID = 5119103039347604213L;
	
	@TableId(value="SETTING_ID",type=IdType.AUTO)
	private Long settingId;
	/**
	 * 返利比例（%）
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
