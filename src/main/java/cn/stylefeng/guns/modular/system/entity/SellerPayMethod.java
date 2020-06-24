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
 * 币商支付方式表
 *
 */
@Data
@TableName("seller_pay_method")
public class SellerPayMethod implements Serializable {

	private static final long serialVersionUID = 4674111559800677388L;
	
	   /**
	  * 主键id
	  */
	@TableId(value = "PAY_METHOD_ID", type = IdType.AUTO)
	private Long payMethodId;

	/**
	 * 币商id
	 */
	@TableField("SELLER_ID")
	private Long sellerId;

	/**
	 *  支付类型 1：表示支付宝，2：表示微信，3：表示银行卡 4:支付宝固码,5：支付宝转银行
	 */
	@TableField("TYPE")
	private Integer type;

	/**
	 * 二维码
	 */
	@TableField("QR_CODE")
	private String qrCode;

	/**
	 * 账号
	 */
	@TableField("ACCOUNT")
	private String account;
	
	/**
	 * 开户支行
	 */
	@TableField("CARD_BANK")
	private String cardBank;
	
	/**
	 * 银行名称
	 */
	@TableField("CARD_BANK_NAME")
	private String cardBankName;
	
	/**
	 * 姓名
	 */
	@TableField("NAME")
	private String name;
	/**
	 * 使用次数
	 */
	@TableField("USE_NUMBER")
	private Long useNumber;
	
	/**
	 * 是否勾选：0表示否，1表示勾选
	 */
	@TableField("IS_CHECK")
	private Integer isCheck;
	
	/**
	 * 购买次数
	 */
	@TableField("BUY_NUMBER")
	private Integer buyNumber;
	
	/**
	 * 支付成功次数
	 */
	@TableField("SUCCESS_NUMBER")
	private Integer successNumber;
	
	/**
	 * 支付成功概率
	 */
	@TableField("SUCCESS_RATIO")
	private Double successRatio;
	
	/**
	 *失败次数
	 */
	@TableField("FAIL_NUMBER")
	private Integer failNumber;
	
	/**
	 * 是否失败通知过：0表示没有，1表示通知
	 */
	@TableField("FAIL_NOTICE")
	private Integer failNotice;
	
	
	/**
	 * 备注
	 */
	@TableField("REMARK")
	private String remark;
	
	 /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
	private Date createTime;
    
    /**
     * 二维码的值
     */
    @TableField("QR_VALUE")
    private String qrValue;


	/**
	 * 收款码价格
	 */
	@TableField("PRICE")
    private Double price;

	/**
	 * 审核状态：0 表示待审核，1表示审核通过，2表示审核不通过
	 */
	@TableField("STATUS")
	private Integer status;

	
    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
	private Date updateTime;

	/**
	 * 是否下架：0表示否，1表示是
	 */
	@TableField("IS_SOLD_OUT")
    private  Integer isSoldOut;

	/**
	 * 下架时间
	 */
	@TableField(value = "SOLD_OUT_TIME", fill = FieldFill.UPDATE)
    private Date soldOutTime;




}
