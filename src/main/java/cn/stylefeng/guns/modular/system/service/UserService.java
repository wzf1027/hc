package cn.stylefeng.guns.modular.system.service;

import java.math.BigDecimal;
import java.util.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import cn.stylefeng.guns.modular.system.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.core.common.constant.Const;
import cn.stylefeng.guns.core.common.constant.state.ManagerStatus;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.node.MenuNode;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.shiro.ShiroUser;
import cn.stylefeng.guns.core.shiro.service.UserAuthService;
import cn.stylefeng.guns.core.util.ApiMenuFilter;
import cn.stylefeng.guns.core.util.KeyUtils;
import cn.stylefeng.guns.core.util.Md5Utils;
import cn.stylefeng.guns.core.util.RedisUtil;
import cn.stylefeng.guns.core.util.ShareCodeUtil;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.system.factory.UserFactory;
import cn.stylefeng.guns.modular.system.mapper.UserMapper;
import cn.stylefeng.guns.modular.system.model.UserDto;
import cn.stylefeng.roses.core.datascope.DataScope;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Resource
    private MenuService menuService;

    @Resource
    private UserAuthService userAuthService;
    
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private UserMapper userMapper;
    
    @Resource
    private SellerMapper sellerMapper;

	@Resource
	private ExchangeFeeSettingService exchangeFeeSettingService;
    
    /**
     * 添加用戶
     */
    public void addUser(UserDto user) {

        // 判断账号是否重复
        User theUser = this.getByAccount(user.getAccount());
        if (theUser != null) {
            throw new ServiceException(BizExceptionEnum.USER_ALREADY_REG);
        }

        // 完善账号信息
        String salt = ShiroKit.getRandomSalt(5);
        String password = ShiroKit.md5(user.getPassword(), salt);
        this.save(UserFactory.createUser(user, password, salt));
    }

    /**
     * 修改用户
     */
    public void editUser(UserDto user) {
        User oldUser = this.getById(user.getUserId());

        if (ShiroKit.hasRole(Const.ADMIN_NAME)) {
            this.updateById(UserFactory.editUser(user, oldUser));
        } else {
            this.assertAuth(user.getUserId());
            ShiroUser shiroUser = ShiroKit.getUserNotNull();
            if (shiroUser.getId().equals(user.getUserId())) {
                this.updateById(UserFactory.editUser(user, oldUser));
            } else {
                throw new ServiceException(BizExceptionEnum.NO_PERMITION);
            }
        }
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long userId) {

        //不能删除超级管理员
        if (userId.equals(Const.ADMIN_ID)) {
            throw new ServiceException(BizExceptionEnum.CANT_DELETE_ADMIN);
        }
        this.assertAuth(userId);
        this.setStatus(userId, ManagerStatus.DELETED.getCode());
    }

    /**
     * 修改用户状态
     */
    public int setStatus(Long userId, String status) {
        return this.baseMapper.setStatus(userId, status);
    }

    /**
     * 修改密码
     */
    public void changePwd(String oldPassword, String newPassword) {
        Long userId = ShiroKit.getUserNotNull().getId();
        User user = this.getById(userId);

        String oldMd5 = ShiroKit.md5(oldPassword, user.getSalt());

        if (user.getPassword().equals(oldMd5)) {
            String newMd5 = ShiroKit.md5(newPassword, user.getSalt());
            user.setPassword(newMd5);
            this.updateById(user);
        } else {
            throw new ServiceException(BizExceptionEnum.OLD_PWD_NOT_RIGHT);
        }
    }

    /**
     * 根据条件查询用户列表
     */
    @SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> selectUsers(DataScope dataScope, String name, String beginTime, String endTime, Long deptId) {
        Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.selectUsers(page, dataScope, name, beginTime, endTime, deptId);
    }

    /**
     * 设置用户的角色
     */
    public int setRoles(Long userId, String roleIds) {
        return this.baseMapper.setRoles(userId, roleIds);
    }

    /**
     * 通过账号获取用户
     *
     */
    public User getByAccount(String account) {
        return this.baseMapper.getByAccount(account);
    }

    /**
     * 获取用户菜单列表
     *
     */
    public List<MenuNode> getUserMenuNodes(List<Long> roleList) {
        if (roleList == null || roleList.size() == 0) {
            return new ArrayList<>();
        } else {
            List<MenuNode> menus = menuService.getMenusByRoleIds(roleList);
            List<MenuNode> titles = MenuNode.buildTitle(menus);
            return ApiMenuFilter.build(titles);
        }

    }

    /**
     * 判断当前登录的用户是否有操作这个用户的权限
     */
    public void assertAuth(Long userId) {
        if (ShiroKit.isAdmin()) {
            return;
        }
        List<Long> deptDataScope = ShiroKit.getDeptDataScope();
        User user = this.getById(userId);
        Long deptId = user.getDeptId();
        if (deptDataScope.contains(deptId)) {
            return;
        } else {
            throw new ServiceException(BizExceptionEnum.NO_PERMITION);
        }

    }

    /**
     * 刷新当前登录用户的信息
     *
     */
    public void refreshCurrentUser() {
        ShiroUser user = ShiroKit.getUserNotNull();
        Long id = user.getId();
        User currentUser = this.getById(id);
        ShiroUser shiroUser = userAuthService.shiroUser(currentUser);
        ShiroUser lastUser = ShiroKit.getUser();
        BeanUtil.copyProperties(shiroUser, lastUser);
    }

	public int submitAuth(String realName, String idCardNo, String idCardFront, String idCardReverse, String idCardImage, String contactWay) {
        Long userId = ShiroKit.getUserNotNull().getId();
        User user = this.getById(userId);
        if(user == null) {
        	return 0;
        }
        if(StringUtils.isBlank(idCardFront) 
        		|| StringUtils.isBlank(idCardReverse) 
        		||StringUtils.isBlank(idCardImage)
        		|| StringUtils.isBlank(idCardNo)
        		|| StringUtils.isBlank(realName)
        		|| StringUtils.isBlank(contactWay)) {
        	return 0;
        }
        user.setContactWay(contactWay);
        user.setRealName(realName);
        user.setIdCardNo(idCardNo);
        user.setIdCardFront(idCardFront);
        user.setIdCardReverse(idCardReverse);
        user.setIdCardImage(idCardImage);
        user.setIsAuth(1);
        boolean flag = this.updateById(user);
        if(flag) {
        	return 1;
        }
        return 0;
		
	}

	/**
	 * 商户注册
	 * @param phone 手机号码
	 * @param password 密码
	 * @param refenceCode 推荐码
	 * @param code 验证码
	 * @return ResponseData
	 */
	@Transactional
	public ResponseData userRegister(String phone, String password, String refenceCode, String code) {
		if(StringUtils.isBlank(phone)) {
			return ResponseData.error("手机号码不能为空");
		}
		if(StringUtils.isBlank(password)) {
			return ResponseData.error("密码不能为空");
		}
		if(StringUtils.isBlank(refenceCode)) {
			return ResponseData.error("邀请码不能为空");
		}
		if(StringUtils.isBlank(code)) {
			return ResponseData.error("验证码不能为空");
		}
		//判断是否存在该手机号码
		Map<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put("PHONE", phone);
		List<User> users = this.baseMapper.selectByMap(columnMap);
		if(users != null && users.size() >0) {
			return ResponseData.error("该手机号码已被注册，请重新注册或去登录");
		}
		
		if(redisUtil.get("SMS_"+phone) == null) {
			return ResponseData.error("验证码已失效，请重新获取验证码");
		}
		String saveCode = (String) redisUtil.get("SMS_"+phone);
		if(!saveCode.equals(code)) {
			return ResponseData.error("输入的验证码有误，请重新输入");
		}
		columnMap.clear();
		columnMap.put("ACCOUNT_CODE", refenceCode);
		List<User> referUsers = this.baseMapper.selectByMap(columnMap);
		if(referUsers == null || referUsers.size() <=0) {
			return ResponseData.error("输入的邀请码有误，请重新输入");
		}
		User referUser = referUsers.get(0);
		User user = new User();
		user.setAccount(phone);

		int randNumber = (int)(Math.random()*10);
		StringBuffer sbf = new StringBuffer().append(100);
		if(randNumber >3){
			for (int i =4;i<randNumber;i++){
				sbf.append(0);
			}
		}
		String account = "S"+(int)((Math.random()*9+1)*Integer.parseInt(sbf.toString()));
		user.setAccountCode(account);
		user.setCreateTime(new Date());
		user.setPhone(phone);
		user.setRoleId("2");
		user.setDeptId(0L);
		user.setStatus("ENABLE");
		user.setIsAuth(0);
		user.setVersion(1);
		user.setCreateUser(1L);
		user.setSalt(ShiroKit.getRandomSalt(5));
        user.setPassword(ShiroKit.md5(password, user.getSalt()));
        user.setAppSecret(KeyUtils.KeyValueLenght(32, false));
		userMapper.insertUser(user);
		
		String serialCode = ShareCodeUtil.toSerialCode(user.getUserId());
		user.setCode(serialCode);
		user.setUpdateTime(new Date());
		user.setUpdateUser(1L);
		this.baseMapper.updateById(user);
		
		//创建钱包
		UserWallter userWallter = new UserWallter();
		userWallter.setUserId(user.getUserId());
		userWallter.setAvailableBalance(0.0);
		userWallter.setTotalBalance(0.0);
		userWallter.setFrozenBalance(0.0);
		userWallter.setCreateTime(new Date());
		userWallter.setVersion(1);
		userWallter.setType(2);
		userWallter.setChannelType(1);
		this.userMapper.insertUserWallter(userWallter);


		UserWallter userWallter2 = new UserWallter();
		userWallter2.setUserId(user.getUserId());
		userWallter2.setAvailableBalance(0.0);
		userWallter2.setTotalBalance(0.0);
		userWallter2.setFrozenBalance(0.0);
		userWallter2.setCreateTime(new Date());
		userWallter2.setVersion(1);
		userWallter2.setType(2);
		userWallter2.setChannelType(2);
		this.userMapper.insertUserWallter(userWallter2);


		UserWallter userWallter3 = new UserWallter();
		userWallter3.setUserId(user.getUserId());
		userWallter3.setAvailableBalance(0.0);
		userWallter3.setTotalBalance(0.0);
		userWallter3.setFrozenBalance(0.0);
		userWallter3.setCreateTime(new Date());
		userWallter3.setVersion(1);
		userWallter3.setType(2);
		userWallter3.setChannelType(3);
		this.userMapper.insertUserWallter(userWallter3);


		UserWallter userWallterUsdt = new UserWallter();
		userWallterUsdt.setUserId(user.getUserId());
		userWallterUsdt.setAvailableBalance(0.0);
		userWallterUsdt.setTotalBalance(0.0);
		userWallterUsdt.setFrozenBalance(0.0);
		userWallterUsdt.setCreateTime(new Date());
		userWallterUsdt.setVersion(1);
		userWallterUsdt.setType(1);
		userWallterUsdt.setChannelType(1);
		this.userMapper.insertUserWallter(userWallterUsdt);

		UserWallter userWallterUsdt2 = new UserWallter();
		userWallterUsdt2.setUserId(user.getUserId());
		userWallterUsdt2.setAvailableBalance(0.0);
		userWallterUsdt2.setTotalBalance(0.0);
		userWallterUsdt2.setFrozenBalance(0.0);
		userWallterUsdt2.setCreateTime(new Date());
		userWallterUsdt2.setVersion(1);
		userWallterUsdt2.setType(1);
		userWallterUsdt2.setChannelType(2);
		this.userMapper.insertUserWallter(userWallterUsdt2);

		UserWallter userWallterUsdt3 = new UserWallter();
		userWallterUsdt3.setUserId(user.getUserId());
		userWallterUsdt3.setAvailableBalance(0.0);
		userWallterUsdt3.setTotalBalance(0.0);
		userWallterUsdt3.setFrozenBalance(0.0);
		userWallterUsdt3.setCreateTime(new Date());
		userWallterUsdt3.setVersion(1);
		userWallterUsdt3.setType(1);
		userWallterUsdt3.setChannelType(3);
		this.userMapper.insertUserWallter(userWallterUsdt3);

		//创建推荐关系
		UserRecommendRelation relation = new UserRecommendRelation();
		relation.setCreateTime(new Date());
		relation.setRecommendId(referUser.getUserId());
		relation.setUserId(user.getUserId());
		this.userMapper.insertUserRecommendRelation(relation);
		
		return ResponseData.success();
	}


	public ResponseData updatePwd(String phone, String password, String code) {
		if(StringUtils.isBlank(phone)) {
			return ResponseData.error("手机号码不能为空");
		}
		if(StringUtils.isBlank(password)) {
			return ResponseData.error("密码不能为空");
		}
		if(StringUtils.isBlank(code)) {
			return ResponseData.error("验证码不能为空");
		}
		//判断是否存在该手机号码
		Map<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put("PHONE", phone);
		List<User> users = this.baseMapper.selectByMap(columnMap);
		if(users == null || users.size() <0) {
			return ResponseData.error("该手机号码不存在");
		}
	//		if(redisUtil.get("SMS_"+phone) == null) {
	//		return ResponseData.error("验证码已失效，请重新获取验证码");
	//	}
		//String saveCode = (String) redisUtil.get("SMS_"+phone);
		String saveCode ="123456";
		if(!saveCode.equals(code)) {
			return ResponseData.error("输入的验证码有误，请重新输入");
		}
		User user = users.get(0);
		user.setPassword(ShiroKit.md5(password, user.getSalt()));
		user.setUpdateTime(new Date());
		user.setUpdateUser(1L);
		this.baseMapper.updateById(user);
		return ResponseData.success();
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> selectAccepterUser(String name, String beginTime, String endTime) {
		  Page page = LayuiPageFactory.defaultPage();
	      return this.baseMapper.selectAccepterUser(page, name, beginTime, endTime);
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> selectUsersByRoleId(String phone, String beginTime,
			String endTime, Integer roleId) {
		 Page page = LayuiPageFactory.defaultPage();
	      return this.baseMapper.selectUsersByRoleId(page, phone, beginTime, endTime,roleId);
	}

	public void addAgentUser(@Valid UserDto userDto) {

        // 判断账号是否重复
        User theUser = this.getByAccount(userDto.getAccount());
        if (theUser != null) {
            throw new ServiceException(BizExceptionEnum.USER_ALREADY_REG);
        }

        // 完善账号信息
        String salt = ShiroKit.getRandomSalt(5);
        String password = ShiroKit.md5(userDto.getPassword(), salt);
        User user = new User();
        BeanUtils.copyProperties(userDto, user);

		int randNumber = (int)(Math.random()*10);
		StringBuffer sbf = new StringBuffer().append(100);
		if(randNumber >3){
			for (int i =4;i<randNumber;i++){
				sbf.append(0);
			}
		}
		String account = "D"+(int)((Math.random()*9+1)*Integer.parseInt(sbf.toString()));
		user.setAccountCode(account);
        user.setCreateTime(new Date());
        user.setStatus(ManagerStatus.OK.getCode());
        user.setPassword(password);
        user.setSalt(salt);
        user.setRoleId("4");
        user.setVersion(1);
        this.userMapper.insertUser(user);
        String serialCode = ShareCodeUtil.toSerialCode(user.getUserId());
		user.setCode(serialCode);
		user.setUpdateTime(new Date());
		user.setUpdateUser(1L);
		this.baseMapper.updateById(user);
		
		//创建钱包
		UserWallter userWallter = new UserWallter();
		userWallter.setUserId(user.getUserId());
		userWallter.setAvailableBalance(0.0);
		userWallter.setTotalBalance(0.0);
		userWallter.setFrozenBalance(0.0);
		userWallter.setCreateTime(new Date());
		userWallter.setVersion(1);
		userWallter.setType(2);
		this.userMapper.insertUserWallter(userWallter);
		
		UserWallter userWallter2 = new UserWallter();
		userWallter2.setUserId(user.getUserId());
		userWallter2.setAvailableBalance(0.0);
		userWallter2.setTotalBalance(0.0);
		userWallter2.setFrozenBalance(0.0);
		userWallter2.setCreateTime(new Date());
		userWallter2.setVersion(1);
		userWallter2.setType(1);
		this.userMapper.insertUserWallter(userWallter2);
				
	}

	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> selectMerchantUsers(String phone, String beginTime, String endTime, String recommend, Integer isAuth, Integer enabled) {
		 Page page = LayuiPageFactory.defaultPage();
	      return this.baseMapper.selectMerchantUsers(page, phone, beginTime, endTime,recommend,isAuth,enabled);
	}

	public ResponseData updateAuth(Long userId, Integer status) {
		User user = this.baseMapper.selectById(userId);
		if(user != null) {
			if(user.getIsAuth() != null && user.getIsAuth() ==1) {
				if(status ==1) {
					user.setIsAuth(2);
					UserRecommendRelation relation = this.baseMapper.selectRecommendRelation(user.getUserId());
					 UserBonusSetting bonusSetting = new UserBonusSetting();
					 bonusSetting.setUserId(user.getUserId());
					 bonusSetting.setAgentId(relation.getRecommendId());
					 bonusSetting.setAlipayRatio(0.0);
					 bonusSetting.setWxRatio(0.0);
					 bonusSetting.setCardRatio(0.0);
					 bonusSetting.setCreateTime(new Date());
					 this.baseMapper.addUserBonusSetting(bonusSetting);
					 
					  UserPayMethodFeeSetting feeSetting = new UserPayMethodFeeSetting();
					  feeSetting  = new UserPayMethodFeeSetting();
					  feeSetting.setUserId(user.getUserId());
					  feeSetting.setAlipayRatio(2.0);
					  feeSetting.setWxRatio(2.0);
					  feeSetting.setCardRatio(2.0);
					  feeSetting.setCreateTime(new Date());
			        this.baseMapper.addUserPayMethodFeeSetting(feeSetting);
			        
				}else {
					user.setIsAuth(0);
					user.setIdCardFront("");
					user.setIdCardImage("");
					user.setIdCardNo("");
					user.setIdCardReverse("");
				}
				this.baseMapper.updateById(user);
				return ResponseData.success(200, "审核成功", null);
			}
		}
		return ResponseData.error("审核失败");
	}

	public ResponseData updateTradePwd(String tradePwd, String code) {
		User user = this.getById(ShiroKit.getUser().getId());
		if(user == null) {
			return ResponseData.error("设置失败");
		}
		if(StringUtils.isBlank(tradePwd)) {
			return ResponseData.error("请输入密码");
		}
		if(StringUtils.isBlank(code)) {
			return ResponseData.error("请输入验证码");
		}
		String oldCode = (String) redisUtil.get("SMS_"+user.getPhone());
		if(StringUtils.isBlank(oldCode)) {
			return ResponseData.error("验证码已过期");
		}
		if(!oldCode.equals(code)) {
			return ResponseData.error("输入的验证码有误");
		}
		user.setTradePassword(Md5Utils.GetMD5Code(tradePwd));
		user.setUpdateTime(new Date());
		user.setUpdateUser(1l);
		this.baseMapper.updateById(user);
		return ResponseData.success();
	}

	public List<UserWallter> selectUserWallter(Long userId) {
		return userMapper.selectUserWallter(userId);
	}

	public UserPayMethodFeeSetting findPayMethodFeeByUserId(Long userId) {
		return userMapper.findPayMethodFeeByUserId(userId);
	}

	public UserPayMethodFeeSetting getPayMethodFeeById(Long settingId) {
		return userMapper.getPayMethodFeeById(settingId);
	}

	public void addUserPayMethodFeeSetting(UserPayMethodFeeSetting old) {
		userMapper.addUserPayMethodFeeSetting(old);
	}

	public void updateUserPayMethodFeeSetting(UserPayMethodFeeSetting old) {
		userMapper.updateUserPayMethodFeeSetting(old);
	}

	public UserRecommendRelation selectRecommendRelation(Long userId) {
		return userMapper.selectRecommendRelation(userId);
	}

	public UserBonusSetting findUserBonusSettingByUserId(Long userId, Long agentId) {
		return userMapper.findUserBonusSettingByUserId(userId,agentId);
	}

	public UserBonusSetting getUserBonusById(Long bonusId) {
		return userMapper.getUserBonusById(bonusId);
	}

	public void addUserBonusSetting(UserBonusSetting old) {
		userMapper.addUserBonusSetting(old);
	}

	public void updateUseBonusSetting(UserBonusSetting old) {
		userMapper.updateUseBonusSetting(old);
	}

	public UserPayMethod findPayMethodById(Long id) {
		return userMapper.findPayMethodById(id);
	}

	public ResponseData getPayMethodList(Long userId) {
		List<UserPayMethod> list =  userMapper.getPayMethodList(userId);
		return ResponseData.success(list);
	}

	@Transactional
	public Object coinExchange(Double number, String place, USDTOtcpExchange exchange) {
		if(number == null || number<=0) {
			return ResponseData.error("兑换数量不能小于0");
		}
		if(StringUtils.isBlank(place)) {
			return ResponseData.error("请选择兑换方向");
		}
		if(exchange == null) {
			return ResponseData.error("兑换失败");
		}
		User user = this.getById(ShiroKit.getUser().getId());
		if(user == null) {
			return ResponseData.error("兑换失败");
		}
		UserWallter userWallter = new UserWallter();
		userWallter.setUserId(user.getUserId());
		List<UserWallter> wallterList = sellerMapper.findUserWallterList(userWallter);
		UserWallter usdtWallter = null;
		UserWallter otcpWallter = null;
		if(wallterList != null && wallterList.size()>0) {
			for (UserWallter userWallter2 : wallterList) {
				if(userWallter2.getType().equals(1)) {
					usdtWallter = new UserWallter();
					BeanUtils.copyProperties(userWallter2,usdtWallter);
				}
				if(userWallter2.getType().equals(2)) {
					otcpWallter = new UserWallter();
					BeanUtils.copyProperties(userWallter2, otcpWallter);
				}
			}
		}
		Integer roleId = 0;
		if("2".equals(user.getRoleId())) {
			roleId =2;
		}else if("4".equals(user.getRoleId())) {
			roleId =4;
		}

		if(roleId <=0) {
			return ResponseData.error("兑换失败");
		}

		if("1".equals(place)) {//从USDT兑换PAYT
			QueryWrapper<ExchangeSetting> queryWrapper = new QueryWrapper<ExchangeSetting>();
			queryWrapper.eq("ROLE_ID", roleId);
			queryWrapper.eq("TYPE", 1);
			ExchangeSetting exchangeSetting = exchangeFeeSettingService.getOne(queryWrapper);
			Double feePrice = 0.0;
			if(exchangeSetting != null) {
				feePrice = number*exchangeSetting.getExchangeValue()/100;
			}
			Double exchangeNumber = new BigDecimal(number-feePrice).multiply(new BigDecimal(exchange.getValue()))
					.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			if(usdtWallter != null && usdtWallter.getUserId()>0 && otcpWallter != null && otcpWallter.getUserId()>0) {
				if(usdtWallter.getAvailableBalance() <=0) {
					return ResponseData.error("余额不足");
				}
				if(usdtWallter.getAvailableBalance()<number) {
					return ResponseData.error("余额不足");
				}


				AccountUpdateRecord updateRecord = new AccountUpdateRecord();
				updateRecord.setBeforePrice(usdtWallter.getAvailableBalance());

				usdtWallter.setAvailableBalance(usdtWallter.getAvailableBalance()-number);
				usdtWallter.setTotalBalance(usdtWallter.getTotalBalance()-number);
				usdtWallter.setUpdateTime(new Date());
				int result = sellerMapper.updateUserWallter(usdtWallter);
				if(result <=0) {
					throw new WallterException("兑换失败");
				}

				updateRecord.setAfterPrice(usdtWallter.getAvailableBalance());
				updateRecord.setCode("USDT");
				updateRecord.setCreateTime(new Date());
				updateRecord.setPhone(user.getAccountCode());
				updateRecord.setType("兑换HC");
				updateRecord.setRemark("USDT兑换成HC");
				if (roleId.equals(2)){
					updateRecord.setSource("商户USDT");
				}else{
					updateRecord.setSource("代理USDT");
				}

				updateRecord.setPrice(-number);
				updateRecord.setRoleId(Long.valueOf(roleId));
				updateRecord.setAccountId(user.getUserId());
				sellerMapper.addAccountUpdateRecord(updateRecord);


				AccountUpdateRecord updateRecord2 = new AccountUpdateRecord();
				updateRecord2.setBeforePrice(otcpWallter.getAvailableBalance());

				otcpWallter.setAvailableBalance(otcpWallter.getAvailableBalance()+exchangeNumber);
				otcpWallter.setTotalBalance(otcpWallter.getTotalBalance()+exchangeNumber);
				otcpWallter.setUpdateTime(new Date());
				result = sellerMapper.updateUserWallter(otcpWallter);
				if(result <=0) {
					throw new WallterException("兑换失败");
				}

				updateRecord2.setAfterPrice(otcpWallter.getAvailableBalance());
				updateRecord2.setCode("HC");
				updateRecord2.setCreateTime(new Date());
				updateRecord2.setPhone(user.getAccountCode());
				updateRecord2.setType("兑换HC");
				if (roleId.equals(2)){
					updateRecord2.setSource("商户HC");
				}else{
					updateRecord2.setSource("代理HC");
				}
				updateRecord2.setRemark("USDT兑换成HC");
				updateRecord2.setPrice(exchangeNumber);
				updateRecord2.setRoleId(Long.valueOf(roleId));
				updateRecord2.setAccountId(user.getUserId());
				sellerMapper.addAccountUpdateRecord(updateRecord2);

				UserAccountFlowRecord usdtFlowRecord = new UserAccountFlowRecord();
				usdtFlowRecord.setCode("USDT");
				usdtFlowRecord.setCreateTime(new Date());
				usdtFlowRecord.setPrice(-number);
				usdtFlowRecord.setUserId(ShiroKit.getUser().getId());
				usdtFlowRecord.setSource("USDT兑换HC");
				sellerMapper.addUserAccountFlowRecord(usdtFlowRecord);
				UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
				flowRecord.setCode("HC");
				flowRecord.setCreateTime(new Date());
				flowRecord.setPrice(exchangeNumber);
				flowRecord.setUserId(ShiroKit.getUser().getId());
				flowRecord.setSource("USDT兑换HC");
				sellerMapper.addUserAccountFlowRecord(flowRecord);

				//兑换订单记录表
				ExchangeOrderRecord record = new ExchangeOrderRecord();
				record.setSource("USDT兑换成HC");
				record.setCreateTime(new Date());
				record.setCoin("HC");
				record.setFeePrice(exchangeSetting.getExchangeValue());
				record.setNumber(exchangeNumber);
				record.setTotalNumber(number);
				record.setCode(exchange.getValue()+"");
				record.setRole(roleId);
				record.setAccountId(user.getUserId());
				sellerMapper.addExchangeOrderRecord(record);

				return ResponseData.success();
			}
		}else if("2".equals(place)) {//从PAYT兑换USDT
			QueryWrapper<ExchangeSetting> queryWrapper = new QueryWrapper<ExchangeSetting>();
			queryWrapper.eq("ROLE_ID", roleId);
			queryWrapper.eq("TYPE", 2);
			ExchangeSetting exchangeSetting = exchangeFeeSettingService.getOne(queryWrapper);
			Double feePrice = 0.0;
			if(exchangeSetting != null) {
				feePrice = number*exchangeSetting.getExchangeValue()/100;
			}
			Double exchangeNumber = new BigDecimal(number-feePrice)
					.divide((new BigDecimal(exchange.getValue())),6,BigDecimal.ROUND_HALF_UP)
					.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
			if(usdtWallter != null && usdtWallter.getUserId()>0 && otcpWallter != null && otcpWallter.getUserId()>0) {
				if(otcpWallter.getAvailableBalance() <=0) {
					return ResponseData.error("余额不足");
				}
				if(otcpWallter.getAvailableBalance()<number) {
					return ResponseData.error("余额不足");
				}

				AccountUpdateRecord updateRecord = new AccountUpdateRecord();
				updateRecord.setBeforePrice(otcpWallter.getAvailableBalance());

				otcpWallter.setAvailableBalance(otcpWallter.getAvailableBalance()-number);
				otcpWallter.setTotalBalance(otcpWallter.getTotalBalance()-number);
				otcpWallter.setUpdateTime(new Date());
				int result = sellerMapper.updateUserWallter(otcpWallter);
				if(result <=0) {
					throw new WallterException("兑换失败");
				}


				updateRecord.setAfterPrice(otcpWallter.getAvailableBalance());
				updateRecord.setCode("HC");
				updateRecord.setCreateTime(new Date());
				updateRecord.setPhone(user.getAccountCode());
				updateRecord.setType("兑换USDT");
				if (roleId.equals(2)){
					updateRecord.setSource("商户HC");
				}else{
					updateRecord.setSource("代理HC");
				}
				updateRecord.setRemark("HC兑换成USDT");
				updateRecord.setPrice(-number);
				updateRecord.setRoleId(Long.valueOf(roleId));
				updateRecord.setAccountId(user.getUserId());
				sellerMapper.addAccountUpdateRecord(updateRecord);


				AccountUpdateRecord updateRecord2 = new AccountUpdateRecord();
				updateRecord2.setBeforePrice(usdtWallter.getAvailableBalance());

				usdtWallter.setAvailableBalance(usdtWallter.getAvailableBalance()+exchangeNumber);
				usdtWallter.setTotalBalance(usdtWallter.getTotalBalance()+exchangeNumber);
				usdtWallter.setUpdateTime(new Date());
				result = sellerMapper.updateUserWallter(usdtWallter);
				if(result <=0) {
					throw new WallterException("兑换失败");
				}

				updateRecord2.setAfterPrice(usdtWallter.getAvailableBalance());
				updateRecord2.setCode("USDT");
				updateRecord2.setCreateTime(new Date());
				updateRecord2.setPhone(user.getAccountCode());
				updateRecord2.setType("兑换USDT");
				updateRecord2.setRemark("HC兑换成USDT");
				if (roleId.equals(2)){
					updateRecord2.setSource("商户USDT");
				}else{
					updateRecord2.setSource("代理USDT");
				}
				updateRecord2.setPrice(exchangeNumber);
				updateRecord2.setRoleId(Long.valueOf(roleId));
				updateRecord2.setAccountId(user.getUserId());
				sellerMapper.addAccountUpdateRecord(updateRecord2);

				UserAccountFlowRecord usdtFlowRecord = new UserAccountFlowRecord();
				usdtFlowRecord.setCode("USDT");
				usdtFlowRecord.setCreateTime(new Date());
				usdtFlowRecord.setPrice(exchangeNumber);
				usdtFlowRecord.setUserId(ShiroKit.getUser().getId());
				usdtFlowRecord.setSource("HC兑换USDT");
				sellerMapper.addUserAccountFlowRecord(usdtFlowRecord);
				UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
				flowRecord.setCode("HC");
				flowRecord.setCreateTime(new Date());
				flowRecord.setPrice(-number);
				flowRecord.setUserId(ShiroKit.getUser().getId());
				flowRecord.setSource("HC兑换USDT");
				sellerMapper.addUserAccountFlowRecord(flowRecord);


				//兑换订单记录表
				ExchangeOrderRecord record = new ExchangeOrderRecord();
				record.setSource("HC兑换成USDT");
				record.setCreateTime(new Date());
				record.setCoin("USDT");
				record.setFeePrice(exchangeSetting.getExchangeValue());
				record.setNumber(exchangeNumber);
				record.setTotalNumber(number);
				record.setCode(exchange.getValue()+"");
				record.setRole(roleId);
				record.setAccountId(user.getUserId());
				sellerMapper.addExchangeOrderRecord(record);

				return ResponseData.success();
			}
		}
		return ResponseData.error("兑换失败");
	}

	
	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> selectAgentMerchantUsers(String name, String beginTime, String endTime,
			Long userId) {
		 Page page = LayuiPageFactory.defaultPage();
	      return this.baseMapper.selectAgentMerchantUsers(page, name, beginTime, endTime,userId);
	}

	public Double getTotalIntoMoneyByIsToday(boolean flag) {
		return this.baseMapper.getTotalIntoMoneyByIsToday(flag);
	}

	public Double getSellerTotalOutMoneyByIsToday(boolean flag) {
		return this.baseMapper.getSellerTotalOutMoneyByIsToday(flag);
	}

	public Double getMerchantTotalOutMoneyByIsToday(boolean flag) {
		return this.baseMapper.getMerchantTotalOutMoneyByIsToday(flag);
	}

	public Double getAgentTotalOutMoneyByIsToday(boolean flag) {
		return this.baseMapper.getAgentTotalOutMoneyByIsToday(flag);
	}

	public Double getTotalFeePriceByIsToday(boolean flag) {
		return this.baseMapper.getTotalFeePriceByIsToday(flag);
		
	}


    public void updateByGoogle(User user) {
		this.baseMapper.updateByGoogle(user);
    }
}
