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
 * 公告表
 */
@Data
@TableName("proclamation")
public class Proclamation implements Serializable {

	private static final long serialVersionUID = -3911523761027114624L;
	
	 /**
     * 主键
     */
    @TableId(value = "PROCLAMATION_ID", type = IdType.AUTO)
	private Long proclamationId;
	/**
	 * 内容
	 */
    @TableField("CONTENT")
	private Object content;
    /**
     * 标题
     */
	@TableField("TITLE")
	private String title;

	/**
	 * 简介
	 */
	@TableField("INTRODUCE")
	private String introduce;

	 /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
	private Date createTime;

	/**
	 * 是否轮播显示：0表示否，1表示是
	 */
    @TableField("IS_TOP")
	private Integer isTop;


}
