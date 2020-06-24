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
 * otcp交易的价格表
 * @author zf
 *
 */
@Data
@TableName("otcp_price_setting")
public class OtcpPirceSetting implements Serializable {

	private static final long serialVersionUID = -1417973008795341674L;
	
	@TableId(value="SETTING_ID",type=IdType.AUTO)
	private Long settingId;
	/**
	 * 出售的单价
	 */
	@TableField("PRICE")
	private Double price;

	/**
	 * 币种
	 */
	@TableField("SYMBOLS")
	private String symbols;

	/**
	 * 创建时间
	 */
	@TableField(value="CREATE_TIME",fill=FieldFill.INSERT)
	private Date createTime;
	/**
	 * 更新时间
	 */
	@TableField(value="UPDATE_TIME",fill=FieldFill.UPDATE)
	private Date updateTime;

	

}
