package cn.stylefeng.guns.core.common.constant.factory;

import cn.stylefeng.guns.modular.system.entity.Dict;
import cn.stylefeng.guns.modular.system.entity.Menu;
import cn.stylefeng.guns.modular.system.entity.Seller;
import cn.stylefeng.guns.modular.system.entity.UserBonusSetting;
import cn.stylefeng.guns.modular.system.entity.UserPayMethodFeeSetting;
import cn.stylefeng.guns.modular.system.entity.UserRecommendRelation;
import cn.stylefeng.guns.modular.system.entity.UserWallter;

import java.util.List;

/**
 * 常量生产工厂的接口
 */
public interface IConstantFactory {

    /**
     * 根据用户id获取用户名称
     *
     * 
     * 
     */
    String getUserNameById(Long userId);

    /**
     * 根据用户id获取用户账号
     *
     */
    String getUserAccountById(Long userId);

    /**
     * 通过角色ids获取角色名称
     */
    String getRoleName(String roleIds);

    /**
     * 通过角色id获取角色名称
     */
    String getSingleRoleName(Long roleId);

    /**
     * 通过角色id获取角色英文名称
     */
    String getSingleRoleTip(Long roleId);

    /**
     * 获取部门名称
     */
    String getDeptName(Long deptId);

    /**
     * 获取菜单的名称们(多个)
     */
    String getMenuNames(String menuIds);

    /**
     * 获取菜单名称
     */
    String getMenuName(Long menuId);

    /**
     * 获取菜单通过编号
     */
    Menu getMenuByCode(String code);

    /**
     * 获取菜单名称通过编号
     */
    String getMenuNameByCode(String code);

    /**
     * 获取菜单名称通过编号
     */
    Long getMenuIdByCode(String code);

    /**
     * 获取字典名称
     */
    String getDictName(Long dictId);

    /**
     * 获取通知标题
     */
    String getNoticeTitle(Long dictId);

    /**
     * 根据字典名称和字典中的值获取对应的名称
     */
    String getDictsByName(String name, String code);

    /**
     * 获取性别名称
     */
    String getSexName(String sexCode);

    /**
     * 获取用户登录状态
     */
    String getStatusName(String status);

    /**
     * 获取菜单状态
     */
    String getMenuStatusName(String status);

    /**
     * 查询字典
     */
    List<Dict> findInDict(Long id);

    /**
     * 获取被缓存的对象(用户删除业务)
     */
    String getCacheObject(String para);

    /**
     * 获取子部门id
     */
    List<Long> getSubDeptId(Long deptId);

    /**
     * 获取所有父部门id
     */
    List<Long> getParentDeptIds(Long deptId);

    /**
     * 会员手机号码
     * @param sellerId
     * @return
     */
	String getSellerPhone(Long sellerId);

	/**
	 * 商户手机号码
	 * @param userId
	 * @return
	 */
	String getUserPhone(Long userId);

	Seller getSeller(Long sellerId);

	UserPayMethodFeeSetting getUserPayMethodFeeSetting(Long userId);

	UserRecommendRelation getUserRecommendRelationByUserId(Long userId);

	UserBonusSetting getUserBonusSetting(UserBonusSetting setting);

	String  getRecommendId(Long userId);

	List<UserWallter> getUserWallterListByUserId(Long userId);

	Long getMerchantNumberByAgentId(Long userId);

    String getUserAccountCodeById(Long userId);
}
