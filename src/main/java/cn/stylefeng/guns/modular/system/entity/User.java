package cn.stylefeng.guns.modular.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * <p>
 * 管理员表
 * </p>
 */
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "USER_ID", type = IdType.AUTO)
    private Long userId;
    /**
     * 头像
     */
    @TableField("AVATAR")
    private String avatar;
    /**
     * 账号
     */
    @TableField("ACCOUNT")
    private String account;

	/**
	 * 账号标识
	 */
	@TableField("ACCOUNT_CODE")
	private String accountCode;

    /**
     * 密码
     */
    @TableField("PASSWORD")
    private String password;
    /**
     * md5密码盐
     */
    @TableField("SALT")
    private String salt;
    /**
     * 名字
     */
    @TableField("NAME")
    private String name;
    /**
     * 生日
     */
    @TableField("BIRTHDAY")
    private Date birthday;
    /**
     * 性别(字典)
     */
    @TableField("SEX")
    private String sex;
    /**
     * 电子邮件
     */
    @TableField("EMAIL")
    private String email;
    /**
     * 电话
     */
    @TableField("PHONE")
    private String phone;
    /**
     * 角色id(多个逗号隔开)
     */
    @TableField("ROLE_ID")
    private String roleId;
    /**
     * 部门id(多个逗号隔开)
     */
    @TableField("DEPT_ID")
    private Long deptId;
    /**
     * 状态(字典)
     */
    @TableField("STATUS")
    private String status;
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 创建人
     */
    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    private Long createUser;
    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    private Date updateTime;
    /**
     * 更新人
     */
    @TableField(value = "UPDATE_USER", fill = FieldFill.UPDATE)
    private Long updateUser;
    /**
     * 乐观锁
     */
    @TableField("VERSION")
    private Integer version;
    
    /**
     * 是否认证：0表示否，1表示审核中，2表示审核通过
     */
    @TableField("IS_AUTH")
    private Integer isAuth;
    
    /**
     * 邀请码
	*/
	@TableField("CODE")
	private String code;
	
	//真实姓名
	@TableField("REAL_NAME")
	private String realName;
	//身份证号码
	@TableField("ID_CARD_NO")
	private String idCardNo;
	//正面
	@TableField("ID_CARD_FRONT")
	private String  idCardFront;
	//反面
	@TableField("ID_CARD_REVERSE")
	private String idCardReverse;
	//手持图片
	@TableField("ID_CARD_IMAGE")
	private String idCardImage;
	
    //联系方式
	@TableField("CONTACT_WAY")
	private String contactWay;
	//交易密码
	@TableField("TRADE_PASSWORD")
	private String tradePassword;
	
	/**
	 * 密钥
	 */
	@TableField("APP_SECRET")
	private String appSecret;


	/**
	 * 是否绑定谷歌，0表示否，1表示绑定
	 */
	@TableField("BING_GOOGLE")
	private Integer bingGoogle;

	/**
	 * 谷歌验证秘钥
	 */
	@TableField("GOOGLE_SECRET")
	private String googleSecret;

	/**
	 * 是否启用禁用出售，0表示启用，1表示禁用
	 */
	@TableField("SELLER_ENABLED")
	private Integer sellEnabled;

	/**
	 * 是否启用禁用提现，0表示启用，1表示禁用
	 */
	@TableField("WITHDRAW_ENABLED")
	private Integer withDrawEnabled;


	public String getGoogleSecret() {
		return googleSecret;
	}

	public void setGoogleSecret(String googleSecret) {
		this.googleSecret = googleSecret;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public Integer getBingGoogle() {
		return bingGoogle;
	}

	public void setBingGoogle(Integer bingGoogle) {
		this.bingGoogle = bingGoogle;
	}

	public Integer getSellEnabled() {
		return sellEnabled;
	}

	public void setSellEnabled(Integer sellEnabled) {
		this.sellEnabled = sellEnabled;
	}

	public Integer getWithDrawEnabled() {
		return withDrawEnabled;
	}

	public void setWithDrawEnabled(Integer withDrawEnabled) {
		this.withDrawEnabled = withDrawEnabled;
	}

	public String getTradePassword() {
		return tradePassword;
	}
	public void setTradePassword(String tradePassword) {
		this.tradePassword = tradePassword;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Long getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(Long updateUser) {
		this.updateUser = updateUser;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Integer getIsAuth() {
		return isAuth;
	}
	public void setIsAuth(Integer isAuth) {
		this.isAuth = isAuth;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public String getContactWay() {
		return contactWay;
	}
	public void setContactWay(String contactWay) {
		this.contactWay = contactWay;
	}
	public String getIdCardImage() {
		return idCardImage;
	}
	public void setIdCardImage(String idCardImage) {
		this.idCardImage = idCardImage;
	}
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

    
    
}
