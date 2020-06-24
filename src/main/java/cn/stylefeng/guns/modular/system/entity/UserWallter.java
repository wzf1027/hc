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
 * 	商户/代理商钱包表
 */
@Data
@TableName("sys_user_wallter")
public class UserWallter implements Serializable {

	private static final long serialVersionUID = -8874819197786343791L;
	
	   /**
     * 主键id
     */
    @TableId(value = "USER_WALLTER_ID", type = IdType.ID_WORKER)
	private Long userWallterId;
	
    /**
     * 管理员id
     */
	@TableField("USER_ID")
	private Long userId;
	
	/**
	 * 可用余额
	 */
	@TableField("AVAILABLE_BALANCE")
	private Double availableBalance;
	/**
	 * 总余额
	 */
	@TableField("TOTAL_BALANCE")
	private Double totalBalance;
	/**
	 * 冻结余额
	 */
	@TableField("FROZEN_BALANCE")
	private Double frozenBalance;
	
	/**
	 * 钱包类型：1：usdt，2：HC
	 */
	@TableField("TYPE")
	private Integer type;

	/**
	 * 通道类型：1表示支付宝，2表示微信，3表示银行卡
	 */
	@TableField("CHANNEL_TYPE")
	private  Integer channelType;

    /**
     * 乐观锁
     */
    @TableField("VERSION")
    private Integer version;
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



    
    
}
