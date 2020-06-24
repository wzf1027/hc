/**
 * 
 */
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
 *  币商钱包实体类
 * @author zf
 *
 */
@Data
@TableName("seller_wallter")
public class SellerWallter implements Serializable {

	
	private static final long serialVersionUID = -2091883521327383358L;
	
	   /**
	  * 主键id
	  */
	@TableId(value = "SELLER_WALLTER_ID", type = IdType.AUTO)
	private Long sellerWallterId;
	
	/**
	 * 币商会员id
	 */
	@TableField("SELLER_ID")
	private Long sellerId;
	

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
	 * 类型：1:表示搬砖账号，2表示代付账号，3表示法币账号，4表示挖矿
	 */
	@TableField("TYPE")
	private Integer type;

	
	  /**
     * 乐观锁
     */
    @TableField("VERSION")
    private Integer version;
    
    /**
     * 钱包标识：USDT或HC
     */
    @TableField("CODE")
    private String code;
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
	 * 密钥
	 */
	private String password;

	/**
	 * 备注
	 */
	private String remark;


}
