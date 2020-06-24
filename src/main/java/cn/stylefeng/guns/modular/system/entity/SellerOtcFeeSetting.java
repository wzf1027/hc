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
 * 会员出售手续费表
 * @author zf
 *
 */
@Data
@TableName("seller_otc_fee_setting")
public class SellerOtcFeeSetting implements Serializable {

	private static final long serialVersionUID = -8972005825484567893L;
	@TableId(value="SETTING_ID",type=IdType.AUTO)
	private Long settingId;
	/**
	 * 比例
	 */
	@TableField("RATIO")
	private Double ratio;

	/**
	 * 最小出售数量
	 */
	@TableField("MIN_NUMBER")
	private  Double minNumber;

	/**
	 * 币种
	 */
	@TableField("SYMBOLS")
	private  String symbols;
	
	@TableField(value="CREATE_TIME",fill=FieldFill.INSERT)
	private Date createTime;
	
	@TableField(value="UPDATE_TIME",fill=FieldFill.UPDATE)
	private Date updateTime;

	
	
}
