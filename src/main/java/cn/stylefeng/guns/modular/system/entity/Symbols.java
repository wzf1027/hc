package cn.stylefeng.guns.modular.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 	币种
 *
 */
@Data
@TableName("symbols")
public class Symbols implements Serializable {
	
	
	private static final long serialVersionUID = 2631868576726371912L;
	/**
	 * 主键
	 */
	@TableId(value="ID",type=IdType.AUTO)
	private Long id;

	/**
	 * 币种名称
	 */
	@TableField("SYMBOLS_NAME")
	private String symbolsName;

	/**
	 * 币种标识
	 */
	@TableField("CODE")
	private String code;

	/**
	 * 杠杆
	 */
	@TableField("LEVERAGE")
	private Integer leverage;

	/**
	 * 排序
	 */
	@TableField("SORT")
	private Integer sort;

	/**
	 * 开始时间
	 */
	@TableField("STAR_TIME")
	private Date starTime;
	/**
	 * 结束时间
	 */
	@TableField("END_TIME")
	private Date endTime;
	
}
