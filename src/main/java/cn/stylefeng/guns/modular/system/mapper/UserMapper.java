package cn.stylefeng.guns.modular.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.entity.UserBonusSetting;
import cn.stylefeng.guns.modular.system.entity.UserPayMethod;
import cn.stylefeng.guns.modular.system.entity.UserPayMethodFeeSetting;
import cn.stylefeng.guns.modular.system.entity.UserRecommendRelation;
import cn.stylefeng.guns.modular.system.entity.UserWallter;
import cn.stylefeng.roses.core.datascope.DataScope;

/**
 * <p>
 * 管理员表 Mapper 接口
 * </p>
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 修改用户状态
     */
    int setStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 修改密码
     */
    int changePwd(@Param("userId") Long userId, @Param("pwd") String pwd);

    /**
     * 根据条件查询用户列表
     */
    @SuppressWarnings("rawtypes")
	Page<Map<String, Object>> selectUsers(@Param("page") Page page, @Param("dataScope") DataScope dataScope, @Param("name") String name, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("deptId") Long deptId);

    /**
     * 设置用户的角色
     */
    int setRoles(@Param("userId") Long userId, @Param("roleIds") String roleIds);

    /**
     * 通过账号获取用户
     */
    User getByAccount(@Param("account") String account);

    /**
     * 	添加管理员
     * @param user
     * @return
     */
	int insertUser(User user);

	/**
	 * 添加商户/承兑商的钱包
	 * @param userWallter
	 */
	int insertUserWallter(UserWallter userWallter);

	/**
	 * 添加商户之间的关系信息
	 * @param relation
	 */
	int insertUserRecommendRelation(UserRecommendRelation relation);

	/**
	 * 获取承兑商的用户
	 * @param page
	 * @param name
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> selectAccepterUser(Page page, String name, String beginTime, String endTime);
	
	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> selectMerchantUsers(@Param("page") Page page,
			@Param("phone") String phone, 
			@Param("beginTime") String beginTime,
			@Param("endTime") String endTime, 
			@Param("recommend")String recommend, 
			@Param("isAuth")Integer isAuth, 
			@Param("enabled")Integer enabled);

	/**
	 * 根据角色查询用户列表
	 * @param page
	 * @param condition
	 * @param beginTime
	 * @param endTime
	 * @param roleId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> selectUsersByRoleId(@Param("page") Page page,
			@Param("phone") String phone, 
			@Param("beginTime") String beginTime,
			@Param("endTime") String endTime,
			@Param("roleId") Integer roleId);

	List<UserWallter> selectUserWallter(@Param("userId")Long userId);

	UserPayMethodFeeSetting findPayMethodFeeByUserId(@Param("userId")Long userId);

	UserPayMethodFeeSetting getPayMethodFeeById(@Param("settingId")Long settingId);

	void addUserPayMethodFeeSetting(UserPayMethodFeeSetting old);

	void updateUserPayMethodFeeSetting(UserPayMethodFeeSetting old);

	UserRecommendRelation selectRecommendRelation(@Param("userId")Long userId);

	UserBonusSetting findUserBonusSettingByUserId(@Param("userId")Long userId, @Param("agentId")Long agentId);

	UserBonusSetting getUserBonusById(@Param("bonusId")Long bonusId);

	void addUserBonusSetting(UserBonusSetting old);
	

	void updateUseBonusSetting(UserBonusSetting old);

	UserPayMethodFeeSetting getPayMethodFeeByUserId(Long userId);

	UserRecommendRelation getUserRecommendRelationByUserId(Long userId);

	UserBonusSetting getUserBonusSetting(UserBonusSetting setting);

	UserPayMethod findPayMethodById(@Param("id")Long id);

	List<UserPayMethod> getPayMethodList(@Param("userId")Long userId);

	@SuppressWarnings("rawtypes")
	Page<Map<String, Object>> selectAgentMerchantUsers(@Param("page") Page page,
			@Param("name") String name, 
			@Param("beginTime") String beginTime,
			@Param("endTime") String endTime,
			@Param("userId")Long userId);

	String getRecommendId(@Param("userId")Long userId);

	Double getTotalIntoMoneyByIsToday(@Param("flag")boolean flag);

	Double getSellerTotalOutMoneyByIsToday(@Param("flag")boolean flag);

	Double getMerchantTotalOutMoneyByIsToday(@Param("flag")boolean flag);

	Double getAgentTotalOutMoneyByIsToday(@Param("flag")boolean flag);

	Double getTotalFeePriceByIsToday(@Param("flag")boolean flag);

	Long getMerchantNumberByAgentId(@Param("userId")Long userId);

    void updateByGoogle(User user);
}
