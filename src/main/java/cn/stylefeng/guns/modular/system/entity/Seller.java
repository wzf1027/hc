package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 币商实体表
 * @author zf
 *
 */
@TableName("seller")
public class Seller implements Serializable {

	private static final long serialVersionUID = 411054194866997217L;
	
	  /**
     * 主键
     */
    @TableId(value = "SELLER_ID", type = IdType.ID_WORKER)
	private Long sellerId;
	
    /**
     *手机号码
     */
    @TableField("PHONE")
	private String phone;
    
    /**
            * 登陆密码
     */
    @TableField("PASSWORD")
  	private String password;
  	/**
  	 * 是否认证：0：未认证，1：待审核，2：已认证
  	 */
  	@TableField("IS_AUTH")
  	private Integer isAuth;

	/**
	 * 账号
	 */
	@TableField("ACCOUNT")
	private String account;

  	/**
  	 * 头像
  	 */
  	@TableField("ICON")
  	private String icon;
  	/**
  	 * 真实姓名
  	 */
  	@TableField("REAL_NAME")
  	private String realName;
  	/**
  	 * 身份证号码
  	 */
  	@TableField("ID_CARD_NO")
  	private String idCardNo;
  	/**
  	 * 正面
  	 */
  	@TableField("ID_CARD_FRONT")
  	private String  idCardFront;
  	/**
  	 * 反面
  	 */
  	@TableField("ID_CARD_REVERSE")
  	private String idCardReverse;
  	/**
  	 * 手持身份证
  	 */
  	@TableField("ID_CARD_IMAGE")
  	private String idCardImage;
  	/**
  	 * 交易密码
  	 */
  	@TableField("TRADER_PASSWORD")
  	private String traderPassword;	
  	/**
  	 * 用户昵称
  	 */
  	@TableField("NICK_NAME")
  	private String nickName;
  	
  	/**
  	 * 状态
  	 */
  	@TableField("STATUS")
  	private Integer status;//0表示未删除，1表示已删除
  	
  	/**
  	 * 启禁用状态：0表示启用，1表示禁用
  	 */
  	@TableField("ENABLED")
  	private Integer enabled;

	/**
	 * 是否启用禁用接单，0表示启用，1表示禁用
	 */
	@TableField("GRAD_ENABLED")
	private Integer gradEnabled;

	/**
	 * 是否启用禁用出售，0表示启用，1表示禁用
	 */
	@TableField("SELLER_ENABLED")
	private Integer sellEnabled;

	/**
	 * 是否启用禁用划转，0表示启用，1表示禁用
	 */
	@TableField("TRANFE_ENABLED")
	private Integer tranferEnabled;

	/**
	 * 是否启用禁用购买，0表示启用，1表示禁用
	 */
	@TableField("BUY_ENABLED")
	private Integer buyEnabled;

	/**
	 * 是否绑定谷歌，0表示否，1表示绑定
	 */
	@TableField("BING_GOOGLE")
	private Integer bingGoogle;

  	
    /**
     * 邀请码
	*/
	@TableField("CODE")
	private String code;
	
	/**
	 * 推荐人ids
	 */
	@TableField("REFERCE_IDS")
	private String referceIds;
	/**
	 * 推荐人id
	 */
	@TableField("REFERCE_ID")
	private Long referceId;
	
	/**
	 * 是否有承兑商角色：0表示否，1表示有,2表示申请中
	 */
	@TableField("IS_ACCEPTER")
	private Integer isAccepter;
	
	/**
	 * 承兑商id
	 */
	@TableField("USER_ID")
	private Long userId;
  	
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
	 * 谷歌验证秘钥
	 */
	@TableField("GOOGLE_SECRET")
	private String googleSecret;

	/**
	 * 登录方式：0表示还未登录，1表示手机登录，2表示邮箱登录
	 */
	@TableField("LOGIN_METHOD")
	private Integer loginMethod;


	@TableField("EMAIL")
	private String email ;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(Integer isAuth) {
		this.isAuth = isAuth;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public String getIdCardFront() {
		return idCardFront;
	}

	public void setIdCardFront(String idCardFront) {
		this.idCardFront = idCardFront;
	}

	public String getIdCardReverse() {
		return idCardReverse;
	}

	public void setIdCardReverse(String idCardReverse) {
		this.idCardReverse = idCardReverse;
	}

	public String getIdCardImage() {
		return idCardImage;
	}

	public void setIdCardImage(String idCardImage) {
		this.idCardImage = idCardImage;
	}

	public String getTraderPassword() {
		return traderPassword;
	}

	public void setTraderPassword(String traderPassword) {
		this.traderPassword = traderPassword;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getReferceIds() {
		return referceIds;
	}

	public void setReferceIds(String referceIds) {
		this.referceIds = referceIds;
	}

	public Integer getIsAccepter() {
		return isAccepter;
	}

	public void setIsAccepter(Integer isAccepter) {
		this.isAccepter = isAccepter;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getReferceId() {
		return referceId;
	}

	public void setReferceId(Long referceId) {
		this.referceId = referceId;
	}


	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getGradEnabled() {
		return gradEnabled;
	}

	public void setGradEnabled(Integer gradEnabled) {
		this.gradEnabled = gradEnabled;
	}

	public Integer getSellEnabled() {
		return sellEnabled;
	}

	public void setSellEnabled(Integer sellEnabled) {
		this.sellEnabled = sellEnabled;
	}

	public Integer getTranferEnabled() {
		return tranferEnabled;
	}

	public void setTranferEnabled(Integer tranferEnabled) {
		this.tranferEnabled = tranferEnabled;
	}

	public Integer getBuyEnabled() {
		return buyEnabled;
	}

	public void setBuyEnabled(Integer buyEnabled) {
		this.buyEnabled = buyEnabled;
	}

	public Integer getBingGoogle() {
		return bingGoogle;
	}

	public void setBingGoogle(Integer bingGoogle) {
		this.bingGoogle = bingGoogle;
	}

	public String getGoogleSecret() {
		return googleSecret;
	}

	public void setGoogleSecret(String googleSecret) {
		this.googleSecret = googleSecret;
	}

	public Integer getLoginMethod() {
		return loginMethod;
	}

	public void setLoginMethod(Integer loginMethod) {
		this.loginMethod = loginMethod;
	}
}
