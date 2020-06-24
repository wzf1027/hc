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
 * octp取消交易次数设置表
 * @author zf
 *
 */
@Data
@TableName("otcp_cannel_number_setting")
public class OtcpCannelNumberSetting implements Serializable {

	private static final long serialVersionUID = -2301023069061876005L;
	
	@TableId(value="SETTING_ID",type=IdType.AUTO)
	private Long settingId;
	/**
	 * 取消次数
	 */
	@TableField("NUMBER")
	private Integer number;


	@TableField("TIME")
	private Integer time;

	@TableField("MIN_TIME")
	private Integer minTime;

	@TableField(value="CREATE_TIME",fill=FieldFill.INSERT)
	private Date createTime;

	@TableField(value="UPDATE_TIME",fill=FieldFill.UPDATE)
	private Date updateTime;


}
