package cn.stylefeng.guns.modular.app.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import cn.stylefeng.guns.core.util.*;
import cn.stylefeng.guns.modular.app.dto.SellOtcpOrderDto;
import cn.stylefeng.guns.modular.system.entity.*;
import cn.stylefeng.guns.modular.system.service.PromotionRateService;
import cn.stylefeng.guns.modular.system.service.SellerCitySwitchSettingService;
import cn.stylefeng.guns.modular.system.service.SellerGradPriceSettingService;
import cn.stylefeng.guns.modular.system.service.SellerOrderTimeSettingService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.modular.app.dto.SellerAuthenticationDto;
import cn.stylefeng.guns.modular.app.dto.SellerPayMethodDto;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.biz.service.SendSMSExtService;
import cn.stylefeng.guns.modular.system.mapper.UserMapper;

@Service
public class SellerService {

	private Logger logger = LoggerFactory.getLogger(SellerService.class);

	@Resource
	private RedisUtil redisUtil;

	@Resource
	private SellerMapper sellerDao;

	@Resource
	private OtcOrderService otcOrderService;

	@Resource
	private SendSMSExtService sendSMSExtService;
	
	@Resource
	private UserMapper userMapper;

	@Resource
	private SellerOrderTimeSettingService orderTimeSettingService;

	@Resource
	private SellerCitySwitchSettingService citySwitchSettingService;

	@Resource
	private PromotionRateService promotionRateService;

	@Value("${platform.DOMAIN}")
	private String domain;


	@Resource
	private SellerGradPriceSettingService sellerGradPriceSettingService;

	@Transactional
	public ResponseData register(String account, String password, String againPassword, String code, String recommendCode,String ckToken) {
		SellerRegisterSwitchSetting switchSetting = sellerDao.findRegisterSwitchSetting();
		if (switchSetting != null && switchSetting.getIsSwitch() == 1) {
			return ResponseData.error("暂不开放注册");
		}
		if (StringUtils.isBlank(account)) {
			return ResponseData.error("用户名不能为空");
		}
		if (StringUtils.isBlank(password)) {
			return ResponseData.error("密码不能为空");
		}
		if (StringUtils.isBlank(againPassword)){
			return ResponseData.error("确认密码不能为空");
		}

		if (StringUtils.isBlank(againPassword)){
			return ResponseData.error("确认密码不能为空");
		}

		if (StringUtils.isBlank(code)) {
			return ResponseData.error("验证码不能为空");
		}
		if (StringUtils.isBlank(ckToken)){
			return ResponseData.error("验证码已过期，请重新刷新");
		}
		if (redisUtil.get(Constant.CLIENT_TOKEN+ckToken) == null){
			return ResponseData.error("验证码已失效");
		}
		String redisCkToken = (String) redisUtil.get(Constant.CLIENT_TOKEN+ckToken);
		if (!redisCkToken.equals(code)){
			return ResponseData.error("输入的验证码有误");
		}

		Seller seller = sellerDao.findSellerByAccount(account);
		if (seller != null) {
			return ResponseData.error("该用户名已被注册,请去登录");
		}
		if (StringUtils.isBlank(recommendCode)) {
			ResponseData.error("邀请码不能为空");
		}

		seller = new Seller();
		seller.setAccount(account);
		seller.setIsAuth(0);
		seller.setPassword(Md5Utils.GetMD5Code(password));
		seller.setCreateTime(new Date());
		seller.setNickName(account);
		seller.setStatus(0);
		seller.setEnabled(0);
		seller.setIsAccepter(0);
		PlatformRecommdSetting setting = sellerDao.getPlatformRecommendSetting();
		if (setting != null && !setting.getCode().equals(recommendCode)) {
			// 不是平台的推荐码
			Seller recommend = sellerDao.findSellerByCode(recommendCode);
			if (recommend == null) {
				return ResponseData.error("输入的邀请码有误，请重新输入");
			}
			seller.setReferceId(recommend.getSellerId());
			seller.setReferceIds((recommend.getReferceIds() == null ? "0," : recommend.getReferceIds())
					+ recommend.getSellerId().toString() + ",");
		}
		sellerDao.addSeller(seller);

		// 创建搬砖账户
		SellerWallter wallet = new SellerWallter();
		wallet.setAvailableBalance(0.0000);
		wallet.setFrozenBalance(0.0000);
		wallet.setTotalBalance(0.0000);
		wallet.setSellerId(seller.getSellerId());
		wallet.setCreateTime(new Date());
		wallet.setCode("HC");
		wallet.setType(1);
		wallet.setVersion(1);
		sellerDao.addSellerWallter(wallet);


		SellerWallter wallet2 = new SellerWallter();
		wallet2.setAvailableBalance(0.0000);
		wallet2.setFrozenBalance(0.0000);
		wallet2.setTotalBalance(0.0000);
		wallet2.setSellerId(seller.getSellerId());
		wallet2.setCreateTime(new Date());
		wallet2.setCode("USDT");
		wallet2.setVersion(1);
		wallet2.setType(1);
		sellerDao.addSellerWallter(wallet2);

		//创建代币账户钱包
		SellerWallter wallet3 = new SellerWallter();
		wallet3.setAvailableBalance(0.0000);
		wallet3.setFrozenBalance(0.0000);
		wallet3.setTotalBalance(0.0000);
		wallet3.setSellerId(seller.getSellerId());
		wallet3.setCreateTime(new Date());
		wallet3.setCode("HC");
		wallet3.setType(2);
		wallet3.setVersion(1);
		sellerDao.addSellerWallter(wallet3);

		//创建法币账户钱包
		SellerWallter wallet4 = new SellerWallter();
		wallet4.setAvailableBalance(0.0000);
		wallet4.setFrozenBalance(0.0000);
		wallet4.setTotalBalance(0.0000);
		wallet4.setSellerId(seller.getSellerId());
		wallet4.setCreateTime(new Date());
		wallet4.setCode("HC");
		wallet4.setType(3);
		wallet4.setVersion(1);
		sellerDao.addSellerWallter(wallet4);
		SellerWallter wallet5 = new SellerWallter();
		wallet5.setAvailableBalance(0.0000);
		wallet5.setFrozenBalance(0.0000);
		wallet5.setTotalBalance(0.0000);
		wallet5.setSellerId(seller.getSellerId());
		wallet5.setCreateTime(new Date());
		wallet5.setCode("USDT");
		wallet5.setVersion(1);
		wallet5.setType(3);
		sellerDao.addSellerWallter(wallet5);

		//创建挖矿账户钱包
		SellerWallter wallet6 = new SellerWallter();
		wallet6.setAvailableBalance(0.0000);
		wallet6.setFrozenBalance(0.0000);
		wallet6.setTotalBalance(0.0000);
		wallet6.setSellerId(seller.getSellerId());
		wallet6.setCreateTime(new Date());
		wallet6.setCode("HC");
		wallet6.setVersion(1);
		wallet6.setType(4);
		sellerDao.addSellerWallter(wallet6);


//		SellerProfitWallter profitWallter = new SellerProfitWallter();
//		profitWallter.setAvailableBalance(0.0);
//		profitWallter.setFrozenBalance(0.0);
//		profitWallter.setTotalBalance(0.0);
//		profitWallter.setCode("1");
//		profitWallter.setCreateTime(new Date());
//		profitWallter.setVersion(1);
//		profitWallter.setPhone(seller.getPhone());
//		profitWallter.setSellerId(seller.getSellerId());
//		sellerDao.addSellerProfitWallter(profitWallter);
//		SellerProfitWallter profitWallter2 = new SellerProfitWallter();
//		profitWallter2.setAvailableBalance(0.0);
//		profitWallter2.setFrozenBalance(0.0);
//		profitWallter2.setTotalBalance(0.0);
//		profitWallter2.setCode("3");
//		profitWallter2.setCreateTime(new Date());
//		profitWallter2.setVersion(1);
//		profitWallter2.setPhone(seller.getPhone());
//		profitWallter2.setSellerId(seller.getSellerId());
//		sellerDao.addSellerProfitWallter(profitWallter2);

		// 生成邀请码
		seller.setCode(ShareCodeUtil.toSerialCode(seller.getSellerId()));
		sellerDao.updateSeller(seller);

		redisUtil.del(Constant.CLIENT_TOKEN+ckToken);

		return ResponseData.success(200, "注册成功，请去登录", null);
	}

	public ResponseData login(String account, String password,String code,String ckToken) {
		if (StringUtils.isBlank(account)) {
			return ResponseData.error("请输入用户名");
		}

		if (StringUtils.isBlank(password)) {
			return ResponseData.error("请输入密码");
		}
		if (StringUtils.isBlank(code)) {
			return ResponseData.error("图形验证码不能为空");
		}
		if (StringUtils.isBlank(ckToken)){
			return ResponseData.error("图形验证码已失效，请重新刷新");
		}
		if (redisUtil.get(Constant.CLIENT_TOKEN+ckToken) == null){
			return ResponseData.error("图形验证码已失效");
		}
		String redisCkToken = (String) redisUtil.get(Constant.CLIENT_TOKEN+ckToken);
		if (!redisCkToken.equals(code)){
			return ResponseData.error("输入的图形验证码有误");
		}


		Seller seller = sellerDao.findSellerByAccount(account);
		if (seller == null) {
			return ResponseData.error("该用户名不存在，请注册后再登录");
		}
		if (!seller.getPassword().equals(Md5Utils.GetMD5Code(password))) {
			return ResponseData.error("输入的密码有误");
		}
		if (seller.getEnabled() == 1) {
			return ResponseData.error("该手机号已被禁用");
		}
		String token = UUID.randomUUID().toString().replaceAll("-", "");
		redisUtil.set(token, seller, Constant.SAVEUSERTIME);// 30分钟
		if(redisUtil.get("OLD_"+seller.getSellerId()) == null){
			redisUtil.set("OLD_"+seller.getSellerId(),token, Constant.SAVEUSERTIME);
		}else{
			String  oldToken = (String) redisUtil.get("OLD_"+seller.getSellerId());
			if( StringUtils.isNotBlank(oldToken) && !token.equals(oldToken) ){
				redisUtil.del(oldToken);
				redisUtil.set("OLD_"+seller.getSellerId(),token, Constant.SAVEUSERTIME);
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("token", token);
		map.put("account", account);
		map.put("isAuth", seller.getIsAuth());
		map.put("icon", seller.getIcon());
		map.put("UID", seller.getAccount());
		map.put("nickName", seller.getNickName());
		map.put("isAccepter", seller.getIsAccepter());

		seller.setLoginMethod(1);
		this.sellerDao.updateSeller(seller);

		redisUtil.del(Constant.CLIENT_TOKEN+ckToken);
		return ResponseData.success(map);
	}

	public ResponseData forgetPwd(String account, String phone, String code, String password, String againPassword, String imageCode, String ckToken) {
		if (StringUtils.isBlank(account)) {
			return ResponseData.error("请输入用户名");
		}
		if (StringUtils.isBlank(phone)) {
			return ResponseData.error("请输入手机号码");
		}

		if (StringUtils.isBlank(password)) {
			return ResponseData.error("请输入新密码");
		}
		if (StringUtils.isBlank(againPassword)) {
			return ResponseData.error("请输入确认新密码");
		}

		if (!password.equals(againPassword)){
			return ResponseData.error("新密码与确认新密码不一致");
		}

		if (StringUtils.isBlank(code)) {
			return ResponseData.error("请输入手机验证码");
		}
		if (StringUtils.isBlank(imageCode)) {
			return ResponseData.error("请输入图形验证码");
		}

		String smsCode = redisUtil.get("SMS_" + phone) + "";
		if (StringUtils.isBlank(smsCode)) {
			return ResponseData.error("短信验证码已失效，请重新发送");
		}
		if (!smsCode.equals(code)) {
			return ResponseData.error("输入的短信验证码有误，请重新输入");
		}

		if (StringUtils.isBlank(ckToken)){
			return ResponseData.error("图形验证码已失效，请重新点击图形验证码");
		}
		if (redisUtil.get(Constant.CLIENT_TOKEN+ckToken) == null){
			return ResponseData.error("图形验证码已失效，请重新点击图形验证码");
		}
		String redisCkToken = (String) redisUtil.get(Constant.CLIENT_TOKEN+ckToken);
		if (!redisCkToken.equals(code)){
			return ResponseData.error("输入的图形验证码有误");
		}

		Seller seller = sellerDao.findSellerByAccount(account);
		if (seller == null) {
			return ResponseData.error("不存在该用户名，请进行注册登录");
		}
		if (StringUtils.isBlank(seller.getPhone())){
			return ResponseData.error("该用户名未绑定手机，无法找回密码");
		}
		if (!seller.getPhone().equals(phone)){
			return ResponseData.error("该用户名绑定手机与您输入的手机号不一致");
		}

		seller.setPassword(Md5Utils.GetMD5Code(againPassword));
		int result = sellerDao.updateSeller(seller);
		redisUtil.del("SMS_" + phone);
		redisUtil.del(Constant.CLIENT_TOKEN+ ckToken);
		if (result > 0) {
			return ResponseData.success(200, "修改成功，请登录", null);
		}
		return ResponseData.error("修改失败，请重新提交");
	}

	public ResponseData authentication(String token, SellerAuthenticationDto authenVo) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			if (seller.getIsAuth() == 1) {
				return ResponseData.error("已提交，等待审核");
			}
			if (seller.getIsAuth() == 2) {
				return ResponseData.error("审核已通过");
			}
			if (StringUtils.isBlank(authenVo.getRealName())) {
				return ResponseData.error("姓名不能为空，请输入");
			}
			if (StringUtils.isBlank(authenVo.getIdCardFront())) {
				return ResponseData.error("请上传身份证的正面图");
			}
			if (StringUtils.isBlank(authenVo.getIdCardReverse())) {
				return ResponseData.error("请上传身份证反面图");
			}
			if (StringUtils.isBlank(authenVo.getIdCardImage())) {
				return ResponseData.error("请上传手持身份证图片");
			}

			int count = sellerDao.findSellerbyIdCardNoCount(authenVo.getIdCardNo());
			if (count > 0) {
				return ResponseData.error("身份证已被使用");
			}
			seller.setRealName(authenVo.getRealName());
			seller.setIdCardFront(authenVo.getIdCardFront());
			seller.setIdCardReverse(authenVo.getIdCardReverse());
			seller.setIdCardImage(authenVo.getIdCardImage());
			seller.setIdCardNo(authenVo.getIdCardNo());
			seller.setIsAuth(1);// 待审核
			sellerDao.updateSeller(seller);
			return ResponseData.success(200, "提交成功,等待审核", null);
		}
		return ResponseData.error("提交失败，请重新提交");
	}

	@Transactional
	public ResponseData logout(String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				SellerOrder order = otcOrderService.findSellerorderBySellerId(seller.getSellerId());
				if (order != null) {
					order.setStatus(1);
					order.setCloseTime(new Date());
					int orderResult = otcOrderService.updateSellerOrder(order);
					if(orderResult <=0) {
						throw new WallterException("退出失败");
					}
					SellerWallter sellerWallter = new SellerWallter();
					sellerWallter.setCode("HC");
					sellerWallter.setSellerId(order.getSellerId());
					List<SellerWallter> sellerWallterList = sellerDao.findSellerWallter(sellerWallter);
					if (sellerWallterList != null && sellerWallterList.size() > 0) {
						sellerWallter = sellerWallterList.get(0);
						logger.info("用户退出登录，存在挂单的单子，进行退回余额前：订单号【"+order.getSerialNo()+"】,会员【"+sellerWallter.getSellerId()+"】,可用余额："+sellerWallter.getAvailableBalance()+",冻结余额："+sellerWallter.getFrozenBalance()+",退回数量："+order.getNumber());
						if(sellerWallter.getFrozenBalance()-order.getNumber() <0) {
							throw new WallterException("退出失败");
						}
						SellerCash cash = sellerDao.selectSellerCashBySellerId(seller.getSellerId());
						Double cashPrice = 0.0;
						if (cash != null){
							cashPrice = cash.getCash();
							sellerDao.deleteSellerCashById(cash);
						}
						sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance() + order.getNumber()+cashPrice);
						sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance() - order.getNumber());
						sellerWallter.setUpdateTime(new Date());
						int result = sellerDao.updateSellerWallter(sellerWallter);
						if (result <= 0) {
							throw new WallterException("退出失败");
						}
						logger.info("用户退出登录，存在挂单的单子，进行退回余额后：订单号【"+order.getSerialNo()+"】,会员【"+sellerWallter.getSellerId()+"】,可用余额："+sellerWallter.getAvailableBalance()+",冻结余额："+sellerWallter.getFrozenBalance()+",退回数量："+order.getNumber());
						redisUtil.del(token);
					}
				}
				return ResponseData.success(200, "退出成功", null);
			}
		}
		return ResponseData.error("退出失败");
	}

	public ResponseData tradersPwd(String code, String password,String againPassword,String imageCode,String ckToken, String token) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			if (StringUtils.isBlank(password)) {
				return ResponseData.error("请输入新密码");
			}
			if (StringUtils.isBlank(againPassword)) {
				return ResponseData.error("请输入确认新密码");
			}
			if (!password.equals(againPassword)){
				return ResponseData.error("输入的新密码与确认新密码不一致");
			}

			if (StringUtils.isBlank(code)) {
				return ResponseData.error("请输入手机验证码");
			}
			if (redisUtil.get("SMS_" + seller.getPhone()) == null) {
				return ResponseData.error("手机验证码已失效");
			}
			String smsCode = (String) redisUtil.get("SMS_" + seller.getPhone());
			if (!smsCode.equals(code)) {
				return ResponseData.error("手机验证码有误");
			}

			if (StringUtils.isBlank(imageCode)) {
				return ResponseData.error("请输入图形验证码");
			}
			if (StringUtils.isBlank(ckToken)) {
				return ResponseData.error("图形验证码已失效");
			}
			if (redisUtil.get(Constant.CLIENT_TOKEN + ckToken) == null) {
				return ResponseData.error("图形验证码已失效");
			}
			String ckCode = (String) redisUtil.get(Constant.CLIENT_TOKEN + ckToken);
			if (!ckCode.equals(code)) {
				return ResponseData.error("图形验证码有误");
			}

			String message = "交易密码设置成功";
			if (StringUtils.isNotBlank(seller.getTraderPassword())) {
				message = "交易密码修改成功";
			}
			seller.setTraderPassword(Md5Utils.GetMD5Code(againPassword));
			sellerDao.updateSeller(seller);
			redisUtil.del("SMS_" + seller.getPhone());
			redisUtil.del(Constant.CLIENT_TOKEN + ckToken);
			return ResponseData.success(200, message, null);
		}
		return ResponseData.error("交易密码设置失败");
	}

	public ResponseData updatePwd(String token, String password,String againPassword, String code,String imageCode,String ckToken) {
		if (StringUtils.isBlank(password)) {
			return ResponseData.error("请输入新密码");
		}
		if (StringUtils.isBlank(againPassword)) {
			return ResponseData.error("请输入确认新密码");
		}
		if (!password.equals(againPassword)){
			return ResponseData.error("请输入新密码与确认新密码不一致");
		}
		if (StringUtils.isBlank(imageCode)) {
			return ResponseData.error("请输入图形验证码");
		}
		if (StringUtils.isBlank(ckToken)){
			return ResponseData.error("图形验证码已失效，请重新刷新");
		}
		if (redisUtil.get(Constant.CLIENT_TOKEN+ckToken) == null){
			return ResponseData.error("图形验证码已失效");
		}
		String redisCkToken = (String) redisUtil.get(Constant.CLIENT_TOKEN+ckToken);
		if (!redisCkToken.equals(imageCode)){
			return ResponseData.error("输入的图形验证码有误");
		}

		if (StringUtils.isBlank(code)) {
			return ResponseData.error("请输入手机验证码");
		}

		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			if (StringUtils.isBlank(seller.getPhone())){
				return ResponseData.error("未绑定手机号码");
			}
			String phoneCode = (String) redisUtil.get("SMS_" + seller.getPhone());
			if (StringUtils.isBlank(phoneCode)) {
				return ResponseData.error("手机验证码已失效");
			}
			if (!phoneCode.equals(code)) {
				return ResponseData.error("手机验证码有误");
			}
			seller.setPassword(Md5Utils.GetMD5Code(againPassword));
			sellerDao.updateSeller(seller);
			redisUtil.del(token);
			redisUtil.del(Constant.CLIENT_TOKEN+ckToken);
			redisUtil.del("SMS_" + seller.getPhone());
			redisUtil.del(seller.getPhone());
			return ResponseData.success(200, "修改成功", null);
		}
		return ResponseData.error("修改失败");
	}

	public ResponseData getSellerInfo(String token) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("icon", seller.getIcon());// 头像
			map.put("account",seller.getAccount());//用户名
			map.put("phone", seller.getPhone());// 手机号码
			map.put("UID", seller.getAccount());// 邀请码
			map.put("nickName", seller.getNickName());// 昵称
			map.put("isAccepter", seller.getIsAccepter());//是否承兑商
			map.put("isAuth", seller.getIsAuth());//是否实名认证
			map.put("level", "VIP");
			//判断是否承兑商
			if (seller.getIsAccepter() != null && seller.getIsAccepter().equals(1)){
				map.put("level", "SVIP");
			}
//			map.put("isGoogle", seller.getBingGoogle()==null ? false : (seller.getBingGoogle() ==0 ? false:true) );
			return ResponseData.success(map);
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData updateIcon(String token, String icon) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			seller.setIcon(icon);
			sellerDao.updateSeller(seller);
			return ResponseData.success(200, "更新成功", null);
		}
		return ResponseData.error("更新失败");
	}

	public ResponseData updateNickName(String token, String nickName) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			seller.setNickName(nickName);
			sellerDao.updateSeller(seller);
			return ResponseData.success(200, "更新成功", null);
		}
		return ResponseData.error("更新失败");
	}

	public ResponseData getAppVersion(Integer type) {
		AppVersion version = sellerDao.selectOneAppVersion(type);
		Map<String, Object> map = new HashMap<>();
		if (version != null) {
			map.put("content", version.getContent());
			map.put("address", version.getAddress());
			map.put("version", version.getVersion());
		}
		return ResponseData.success(map);
	}

	public ResponseData getCustomerData() {
		List<Customer>  list = this.sellerDao.findCustomerList();
		Map<String,Object> map = null;
		if(list != null && list.size() > 0) {
			map = new HashMap<>();
			Customer customer = list.get(0);
			map.put("qqNo", customer.getQqNo());
		}
		return ResponseData.success(200,"获取成功",map);
	}

	public ResponseData addEditPayMethod(SellerPayMethodDto paymethod, String token) {
		if (paymethod.getType() == null || paymethod.getType() <= 0) {
			return ResponseData.error("参数有误 ");
		}
		if (StringUtils.isBlank(paymethod.getAccount())) {
			if (paymethod.getType().equals(1) || paymethod.getType().equals(4)) {
				return ResponseData.error("请输入支付宝账号");
			} else if (paymethod.getType().equals(2)) {
				return ResponseData.error("请输入微信账号");
			}else if(paymethod.getType().equals(3) || paymethod.getType().equals(5)){
				return ResponseData.error("请输入银行卡号");
			}
		}
		if (StringUtils.isBlank(paymethod.getName())) {
			return ResponseData.error("请输入姓名");
		}
		if (paymethod.getType().equals(1) || paymethod.getType().equals(4) &&  paymethod.getType().equals(2)) {
			if (StringUtils.isBlank(paymethod.getQrCode())) {
				return ResponseData.error("请上传收款码");
			}
			if(StringUtils.isBlank(paymethod.getRemark())){
				return ResponseData.error("请输入昵称");
			}
		}
		if (paymethod.getType().equals(4) && (paymethod.getPrice() == null || paymethod.getPrice() <=0)){
			return ResponseData.error("请输入收款码金额");
		}

		if (paymethod.getType().equals(3) || paymethod.getType().equals(5)) {
//			if (StringUtils.isBlank(paymethod.getCardBank())) {
//				return ResponseData.error("请输入开户支行");
//			}
			if (StringUtils.isBlank(paymethod.getCardBankName())) {
				return ResponseData.error("请输入银行名称");
			}
		}

		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			//判断是否该类型的支付方式
			SellerPayMethod method = new SellerPayMethod();
			method.setSellerId(seller.getSellerId());
			method.setType(paymethod.getType());
			method.setAccount(paymethod.getAccount());
			List<SellerPayMethod> payMethodList = sellerDao.findSellerPayMethodList(method);
			if (payMethodList.size() >0){
				if (paymethod.getId() != null && paymethod.getId() > 0){
					for (SellerPayMethod object :payMethodList){
						if (!object.getPayMethodId().equals(paymethod.getId())){
							return  ResponseData.error("该收款码或账号已被使用，请上传其他账号");
						}
					}
				}
				return  ResponseData.error("该收款码或账号已被使用，请上传其他账号");
			}

			if (paymethod.getType().equals(1)|| paymethod.getType().equals(2)){
				SellerPayMethod method2 = new SellerPayMethod();
				method2.setSellerId(seller.getSellerId());
				method2.setType(paymethod.getType());
				method2.setQrValue(paymethod.getValue());
				List<SellerPayMethod> payMethodList2 = sellerDao.findSellerPayMethodList(method2);
				if (payMethodList2.size() >0){
					if (paymethod.getId() != null && paymethod.getId() > 0){
						for (SellerPayMethod object :payMethodList2){
							if (!object.getPayMethodId().equals(paymethod.getId())){
								return  ResponseData.error("该收款码或账号已被使用，请上传其他账号");
							}
						}
					}
					return  ResponseData.error("该收款码或账号已被使用，请上传其他账号");
				}
			}
			SellerPayMethod sellerPayMethod = new SellerPayMethod();
			sellerPayMethod.setSellerId(seller.getSellerId());//会员id
			sellerPayMethod.setAccount(paymethod.getAccount());//账号
			sellerPayMethod.setName(paymethod.getName());//姓名
			sellerPayMethod.setType(paymethod.getType());//收款方式类型
			sellerPayMethod.setRemark(paymethod.getRemark());//昵称
			sellerPayMethod.setCardBank(paymethod.getCardBank());//开户行
			sellerPayMethod.setCardBankName(paymethod.getCardBankName());//银行名称
			sellerPayMethod.setQrCode(paymethod.getQrCode());//二维码图片地址
			sellerPayMethod.setQrValue(paymethod.getValue());//二维码链接值
			sellerPayMethod.setStatus(0);//审核状态
			sellerPayMethod.setPrice(paymethod.getPrice());//收款码价格
			sellerDao.addPayMethod(sellerPayMethod);
			return ResponseData.success(200, "添加成功,待审核", null);
		}
		return ResponseData.error("提交失败");
	}

	public ResponseData getPayMethodList(Integer pageSize, Integer pageNumber, String token, Integer type) {
		if (type == null || type <=0){
			return ResponseData.error("参数有误");
		}
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			int total = 0;
			List<Map<String, Object>> list = new ArrayList<>();
			if (seller != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
				params.put("type", type);
				Query query = new Query(params);
				List<SellerPayMethod> sellerPayMethodList = sellerDao.getSellerPayMethodList(query);
				for (SellerPayMethod payMethod : sellerPayMethodList) {
					Map<String, Object> map = new HashMap<>();
					if (payMethod != null) {
						map.put("id", payMethod.getPayMethodId());//id
						map.put("name", payMethod.getName());//姓名
						map.put("account", payMethod.getAccount());//账号
						map.put("qrCode", payMethod.getQrCode());//二维码
						//map.put("cardBank", payMethod.getCardBank());//银行名称
						map.put("type", payMethod.getType());//类型
						map.put("remark", payMethod.getRemark());//昵称
						map.put("isCheck",payMethod.getIsCheck());//是否勾选，0表示否，1表示勾选
						map.put("status",payMethod.getStatus());//状态：0表示审核中，1表示审核通过，2表示审核不通过
						map.put("price",payMethod.getPrice());//价格
						map.put("cardBankName", payMethod.getCardBankName());//银行名称
					}
					list.add(map);
				}
				total = sellerDao.getSellerPaymethodListCount(query);
			}
			PageUtils pageUtils = new PageUtils(total, list);
			return ResponseData.success(pageUtils);
		}
		return ResponseData.success(null);
	}

	public ResponseData deletePayMethod(String token, Long id) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			SellerPayMethod payMethod = sellerDao.findSellerPayMethodById(id);
			if (payMethod != null) {
				int count = otcOrderService.findBuyerOrderByPayMethodAndStatus(payMethod.getPayMethodId());
				if (count >0){
					return ResponseData.error("存在该收款方式的订单未处理，无法删除");
				}
				if (payMethod.getSellerId().equals(seller.getSellerId())){
					sellerDao.deletePaymethodById(payMethod.getPayMethodId());
					return ResponseData.success(200, "删除成功", null);
				}
			}
		}
		return ResponseData.error("删除失败");
	}

	public ResponseData addAddress(String remark, String address, String token) {
		if (StringUtils.isBlank(remark)) {
			return ResponseData.error("备注不能为空");
		}
		if (StringUtils.isBlank(address)) {
			return ResponseData.error("钱包地址不能为空");
		}
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			SellerWallterAddress wallterAddress = new SellerWallterAddress();
			wallterAddress.setAddress(address);
			wallterAddress.setCreateTime(new Date());
			wallterAddress.setRemark(remark);
			wallterAddress.setSellerId(seller.getSellerId());
			sellerDao.addSellerWallterAddress(wallterAddress);
			return ResponseData.success(200, "添加成功", null);
		}
		return ResponseData.error("添加失败");
	}

	public ResponseData deleteAddress(String token, Long id) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			SellerWallterAddress address = sellerDao.findSellerWallterAddressById(id);
			if (address != null && address.getSellerId().equals(seller.getSellerId())) {
				sellerDao.deleteSellerWallterAddress(address.getAddressId());
				return ResponseData.success(200, "删除成功", null);
			}
		}
		return ResponseData.error("删除失败");
	}

	public ResponseData getAddressList(Integer pageSize, Integer pageNumber, String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			int total = 0;
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (seller != null) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
				Query query = new Query(params);
				List<SellerWallterAddress> sellerWallterAddressList = sellerDao.getSellerWallterAddressList(query);
				for (SellerWallterAddress address : sellerWallterAddressList) {
					Map<String, Object> map = new HashMap<String, Object>();
					if (address != null) {
						map.put("id", address.getAddressId());
						map.put("address", address.getAddress());
						map.put("remark", address.getRemark());
						map.put("createTime", address.getCreateTime().getTime() / 1000);// 时间为秒
					}
					list.add(map);
				}
				total = sellerDao.getSellerWallterAddressListCount(query);
			}
			PageUtils pageUtils = new PageUtils(total, list);
			return ResponseData.success(pageUtils);
		}
		return ResponseData.success(null);
	}

	public ResponseData getOTCMarkInfo() {
		USDTOtcpExchange exchage = sellerDao.getOTCMarkInfo();
		Map<String, Object> map = null;
		if (exchage != null) {
			map = new HashMap<>();
			map.put("USDT", exchage.getValue());
			map.put("HC", 1.00);
		}
		return ResponseData.success(map);
	}

	@Transactional
	public ResponseData usdtExchange(Double number, String token, Integer type) {
		if (number == null || number <= 0.0) {
			return ResponseData.error("兑换数量需大于0");
		}
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				boolean flag = false;
				SellerAuthSwitchSetting setting =  sellerDao.findSellerAuthSwithSettingOne();
				if(setting != null && setting.getIsSwitch() ==0) {
					flag = true;
				}
				if(flag) {
					if (seller.getIsAuth() == null || seller.getIsAuth().equals(0)) {
						return ResponseData.error("未实名认证，请先实名认证后再来");
					}
					if (seller.getIsAuth().equals(1)) {
						return ResponseData.error("等待实名认证审核通过后再进行");
					}
				}
				SellerWallter usdtWallter = new SellerWallter();
				usdtWallter.setCode("USDT");
				usdtWallter.setSellerId(seller.getSellerId());
				List<SellerWallter> usdtWallterList = sellerDao.findSellerWallter(usdtWallter);
				if (usdtWallterList == null || usdtWallterList.size() <= 0) {
					return ResponseData.error("兑换失败");
				}
				usdtWallter = usdtWallterList.get(0);
				SellerWallter otcpWallter = new SellerWallter();
				otcpWallter.setCode("HC");
				otcpWallter.setSellerId(seller.getSellerId());
				List<SellerWallter> otcpWallterList = sellerDao.findSellerWallter(otcpWallter);
				if (otcpWallterList == null || otcpWallterList.size() <= 0) {
					return ResponseData.error("兑换失败");
				}
				otcpWallter = otcpWallterList.get(0);
				USDTOtcpExchange exchage = sellerDao.getOTCMarkInfo();
				if (exchage != null && usdtWallter != null && otcpWallter != null) {
					if (exchage.getValue() >= 0) {
						if (type == 2) {// 表示usdt兑换成otcp
							ExchangeSetting exchangeSetting = sellerDao.getExchangeFeeSetting(1);
							Double feePrice = 0.0;
							if(exchangeSetting != null) {
								feePrice = number*exchangeSetting.getExchangeValue()/100;
							}
							if (usdtWallter.getAvailableBalance() < number) {
								return ResponseData.error("余额不足");
							}
							Double getNumber = number-feePrice;
							Double otcpMoney = new BigDecimal(getNumber).multiply(BigDecimal.valueOf(exchage.getValue()))
									.doubleValue();

							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(usdtWallter.getAvailableBalance());

							usdtWallter.setAvailableBalance(usdtWallter.getAvailableBalance() - number);
							usdtWallter.setUpdateTime(new Date());
							int usdtResult = sellerDao.updateSellerWallter(usdtWallter);
							if (usdtResult <= 0) {
								throw new WallterException("兑换失败");
							}

							updateRecord.setAfterPrice(usdtWallter.getAvailableBalance());
							updateRecord.setCode("USDT");
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(seller.getAccount());
							updateRecord.setSource("搬砖-USDT");
							updateRecord.setRemark("USDT兑换成HC");
							updateRecord.setType(AccountUpdateType.EXCHANGE_HC.code());
							updateRecord.setWalletCode(AccountUpdateWallet.BANZHUAN_WALLET_USDT.code());
							updateRecord.setPrice(-number);
							updateRecord.setRoleId(1L);
							updateRecord.setAccountId(seller.getSellerId());
							sellerDao.addAccountUpdateRecord(updateRecord);

							AccountUpdateRecord updateRecord2 = new AccountUpdateRecord();
							updateRecord2.setBeforePrice(otcpWallter.getAvailableBalance());
							otcpWallter.setAvailableBalance(otcpMoney + otcpWallter.getAvailableBalance());
							otcpWallter.setUpdateTime(new Date());
							int otcpResult = sellerDao.updateSellerWallter(otcpWallter);
							if (otcpResult <= 0) {
								throw new WallterException("兑换失败");
							}

							updateRecord2.setAfterPrice(otcpWallter.getAvailableBalance());
							updateRecord2.setCode("HC");
							updateRecord2.setCreateTime(new Date());
							updateRecord2.setPhone(seller.getAccount());
							updateRecord2.setSource("搬砖-HC");
							updateRecord2.setRemark("USDT兑换成HC");
							updateRecord.setType(AccountUpdateType.EXCHANGE_HC.code());
							updateRecord.setWalletCode(AccountUpdateWallet.BANZHUAN_WALLET_HC.code());
							updateRecord2.setPrice(otcpMoney);
							updateRecord2.setRoleId(1L);
							updateRecord2.setAccountId(seller.getSellerId());
							sellerDao.addAccountUpdateRecord(updateRecord2);


							SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
							flowRecord.setCode("USDT");
							flowRecord.setCreateTime(new Date());
							flowRecord.setPrice(-number);
							flowRecord.setSellerId(seller.getSellerId());
							flowRecord.setSource("USDT兑换HC");
							flowRecord.setWalletType(1);//钱包账户类型
							flowRecord.setType(FlowRecordConstant.EXCHANGER_OUT_COIN);
							sellerDao.addSellerAccountFlowRecord(flowRecord);

							SellerAccountFlowRecord otcpflowRecord = new SellerAccountFlowRecord();
							otcpflowRecord.setCode("HC");
							otcpflowRecord.setCreateTime(new Date());
							otcpflowRecord.setPrice(otcpMoney);
							otcpflowRecord.setSellerId(seller.getSellerId());
							otcpflowRecord.setSource("USDT兑换HC");
							otcpflowRecord.setWalletType(1);////钱包账户类型
							otcpflowRecord.setType(FlowRecordConstant.EXCHANGER_IN_COIN);
							sellerDao.addSellerAccountFlowRecord(otcpflowRecord);

							//兑换订单记录表
							ExchangeOrderRecord record = new ExchangeOrderRecord();
							record.setSource("USDT兑换成HC");
							record.setCreateTime(new Date());
							record.setCoin("HC");
							record.setFeePrice(exchangeSetting.getExchangeValue());
							record.setNumber(otcpMoney);
							record.setTotalNumber(number);
							record.setCode(exchage.getValue()+"");
							record.setRole(1);
							record.setAccountId(seller.getSellerId());
							sellerDao.addExchangeOrderRecord(record);
						} else if (type.equals(1)) {
							ExchangeSetting exchangeSetting = sellerDao.getExchangeFeeSetting(2);
							Double feePrice = 0.0;
							if(exchangeSetting != null) {
								feePrice = number*exchangeSetting.getExchangeValue()/100;
							}
							if (otcpWallter.getAvailableBalance() < number) {
								return ResponseData.error("余额不足");
							}
							Double getNumber = number-feePrice;
							Double usdtMoney = new BigDecimal(getNumber)
									.divide(new BigDecimal(exchage.getValue()), 2, BigDecimal.ROUND_HALF_DOWN)
									.setScale(6, BigDecimal.ROUND_HALF_DOWN).doubleValue();

							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(usdtWallter.getAvailableBalance());

							usdtWallter.setAvailableBalance(usdtWallter.getAvailableBalance() + usdtMoney);
							usdtWallter.setUpdateTime(new Date());
							int usdtResult = sellerDao.updateSellerWallter(usdtWallter);
							if (usdtResult <= 0) {
								throw new WallterException("兑换失败");
							}

							updateRecord.setAfterPrice(usdtWallter.getAvailableBalance());
							updateRecord.setCode("USDT");
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(seller.getAccount());
							updateRecord.setSource("搬砖-USDT");
							updateRecord.setType(AccountUpdateType.EXCHANGE_USDT.code());
							updateRecord.setWalletCode(AccountUpdateWallet.BANZHUAN_WALLET_USDT.code());
							updateRecord.setRemark("HC兑换成USDT");
							updateRecord.setPrice(usdtMoney);
							updateRecord.setRoleId(1L);
							updateRecord.setAccountId(seller.getSellerId());
							sellerDao.addAccountUpdateRecord(updateRecord);


							AccountUpdateRecord updateRecord2 = new AccountUpdateRecord();
							updateRecord2.setBeforePrice(otcpWallter.getAvailableBalance());

							otcpWallter.setAvailableBalance(otcpWallter.getAvailableBalance() - number);
							otcpWallter.setUpdateTime(new Date());
							int otcpResult = sellerDao.updateSellerWallter(otcpWallter);
							if (otcpResult <= 0) {
								throw new WallterException("兑换失败");
							}
							updateRecord2.setAfterPrice(otcpWallter.getAvailableBalance());
							updateRecord2.setCode("HC");
							updateRecord2.setCreateTime(new Date());
							updateRecord2.setPhone(seller.getAccount());
							updateRecord2.setSource("搬砖-HC");
							updateRecord.setType(AccountUpdateType.EXCHANGE_USDT.code());
							updateRecord.setWalletCode(AccountUpdateWallet.BANZHUAN_WALLET_HC.code());
							updateRecord2.setRemark("USDT兑换成HC");
							updateRecord2.setPrice(-number);
							updateRecord2.setRoleId(1L);
							updateRecord2.setAccountId(seller.getSellerId());
							sellerDao.addAccountUpdateRecord(updateRecord2);


							SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
							flowRecord.setCode("USDT");
							flowRecord.setCreateTime(new Date());
							flowRecord.setPrice(usdtMoney);
							flowRecord.setSellerId(seller.getSellerId());
							flowRecord.setSource("HC兑换成USDT");
							flowRecord.setType(FlowRecordConstant.EXCHANGER_IN_COIN);
							flowRecord.setWalletType(1);//钱包账户类型
							sellerDao.addSellerAccountFlowRecord(flowRecord);

							SellerAccountFlowRecord otcpflowRecord = new SellerAccountFlowRecord();
							otcpflowRecord.setCode("HC");
							otcpflowRecord.setCreateTime(new Date());
							otcpflowRecord.setPrice(-number);
							otcpflowRecord.setSellerId(seller.getSellerId());
							otcpflowRecord.setSource("HC兑换成USDT");
							otcpflowRecord.setType(FlowRecordConstant.EXCHANGER_OUT_COIN);
							otcpflowRecord.setWalletType(1);//钱包账户类型
							sellerDao.addSellerAccountFlowRecord(otcpflowRecord);

							//兑换订单记录表
							ExchangeOrderRecord record = new ExchangeOrderRecord();
							record.setSource("HC兑换成USDT");
							record.setCreateTime(new Date());
							record.setCoin("USDT");
							record.setFeePrice(exchangeSetting.getExchangeValue());
							record.setNumber(usdtMoney);
							record.setTotalNumber(number);
							record.setCode(exchage.getValue()+"");
							record.setRole(1);
							record.setAccountId(seller.getSellerId());
							sellerDao.addExchangeOrderRecord(record);
						}

						return ResponseData.success(200, "兑换成功", null);
					}
				}
			}

		}
		return ResponseData.error("兑换失败");
	}

	public ResponseData wallterInfo(Integer type, String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				USDTOtcpExchange exchage = sellerDao.getOTCMarkInfo();
				if (exchage != null) {
					Map<String, Object> map = new HashMap<String, Object>();
					SellerWallter wallter = new SellerWallter();
					wallter.setSellerId(seller.getSellerId());
					wallter.setType(type);
					List<SellerWallter> wallterList = sellerDao.findSellerWallter(wallter);
					Double cnyPrice = 0.0;
					Double usdtPrice = 0.0;
					for (SellerWallter sellerWallter : wallterList) {
						if ("USDT".equals(sellerWallter.getCode())) {
							cnyPrice = new BigDecimal(cnyPrice)
									.add(new BigDecimal(
											sellerWallter.getAvailableBalance() + sellerWallter.getFrozenBalance())
													.multiply(new BigDecimal(exchage.getValue()))
													.setScale(2, BigDecimal.ROUND_HALF_DOWN))
									.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
							usdtPrice = usdtPrice+sellerWallter.getAvailableBalance()+sellerWallter.getFrozenBalance();
						}
						if ("HC".equals(sellerWallter.getCode())) {
							cnyPrice = cnyPrice + sellerWallter.getAvailableBalance()
									+ sellerWallter.getFrozenBalance();
							usdtPrice = new BigDecimal(usdtPrice).add(new BigDecimal(sellerWallter.getAvailableBalance()+sellerWallter.getFrozenBalance())
									.divide(new BigDecimal(exchage.getValue()),4,BigDecimal.ROUND_HALF_UP)
									.setScale(4,BigDecimal.ROUND_HALF_UP)).doubleValue();
						}
					}
					map.put("type", type);
					map.put("usdtPrice",usdtPrice);
					map.put("cnyPrice", cnyPrice);
					return ResponseData.success(map);
				}
			}
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData wallterList(Integer type, String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				USDTOtcpExchange exchage = sellerDao.getOTCMarkInfo();
				if (exchage != null) {
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
						SellerWallter wallter = new SellerWallter();
						wallter.setSellerId(seller.getSellerId());
						wallter.setType(type);
						List<SellerWallter> wallterList = sellerDao.findSellerWallter(wallter);
						for (SellerWallter sellerWallter : wallterList) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("id", sellerWallter.getSellerWallterId());
							map.put("availableBalance", sellerWallter.getAvailableBalance());
							map.put("frozenBalance", sellerWallter.getFrozenBalance());
							if ("USDT".equals(sellerWallter.getCode())) {
								Double cnyPrice = new BigDecimal(
										sellerWallter.getAvailableBalance() + sellerWallter.getFrozenBalance())
												.multiply(new BigDecimal(exchage.getValue()))
												.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
								map.put("name", "USDT");
								map.put("symbol", "USDT");
								map.put("cnyBalance", cnyPrice);
							}
							if ("HC".equals(sellerWallter.getCode())) {
								map.put("name", "HC");
								map.put("symbol", "HC");
								map.put("cnyBalance",
										sellerWallter.getAvailableBalance() + sellerWallter.getFrozenBalance());
							}
							list.add(map);
						}
						return ResponseData.success(list);
				}
			}
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData chargeCoinAddress(String token) {
		PlatformCoinAddress coinAddress = sellerDao.findChargeCoinAddress();
		if (coinAddress != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("address", coinAddress.getAddress());
			return ResponseData.success(map);
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData submitChargeCoin(String token, String hashValue, Double number) {
		if (number == null || number <= 0) {
			return ResponseData.error("请输入充币数量");
		}
		if (StringUtils.isBlank(hashValue)) {
			return ResponseData.error("请上传凭证");
		}
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			PlatformCoinAddress coinAddress = sellerDao.findChargeCoinAddress();
			if (seller != null && coinAddress != null) {
				SellerChargerCoinAppealOrder appeal = new SellerChargerCoinAppealOrder();
				appeal.setAddress(coinAddress.getAddress());
				appeal.setCreateTime(new Date());
				appeal.setHashValue(hashValue);
				appeal.setNumber(number);
				appeal.setSellerId(seller.getSellerId());
				appeal.setStatus(1);
				appeal.setSerialno(otcOrderService.generateSimpleSerialno(seller.getSellerId(), 3));
				sellerDao.addSellerChargerCoinAppealOrder(appeal);
				return ResponseData.success(200, "提交成功，静等审核", null);
			}
		}
		return ResponseData.error("提交失败");
	}

	public ResponseData ChargeCoinList(Integer pageSize, Integer pageNumber, String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			int total = 0;
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (seller != null) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
				Query query = new Query(params);
				List<SellerChargerCoinAppealOrder> orderList = sellerDao.getSellerChargerCoinAppealList(query);
				for (SellerChargerCoinAppealOrder order : orderList) {
					Map<String, Object> map = new HashMap<String, Object>();
					if (order != null) {
						map.put("id", order.getAppealId());
						if (order.getStatus() == 1) {
							map.put("statusName", "审核中");
						} else if (order.getStatus() == 2) {
							map.put("statusName", "已完成");
						} else {
							map.put("statusName", "失败");
						}
						map.put("number", order.getNumber());
						map.put("hashValue", order.getHashValue());
						map.put("symbols", "USDT");
						map.put("remark", "充币");
						map.put("createTime", order.getCreateTime().getTime() / 1000);// 时间为秒
					}
					list.add(map);
				}
				total = sellerDao.getSellerChargerCoinAppealListCount(query);
			}
			PageUtils pageUtils = new PageUtils(total, list);
			return ResponseData.success(pageUtils);
		}
		return ResponseData.success(null);
	}

	@Transactional
	public ResponseData addSellerWithdrawOrder(Double number,String address, String tradePwd, String token,String symbol) {
		if (number == null || number <= 0) {
			return ResponseData.error("请输入提币数量");
		}
		if (StringUtils.isBlank(address)) {
			return ResponseData.error("请输入提币地址");
		}
		if (StringUtils.isBlank(tradePwd)) {
			return ResponseData.error("请输入交易密码");
		}
		if(StringUtils.isBlank(symbol)) {
			return ResponseData.error("请选择提币币种");
		}

		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (StringUtils.isBlank(seller.getTraderPassword())) {
				return ResponseData.error("未设置交易密码,请进行设置");
			}
			if (!seller.getTraderPassword().equals(Md5Utils.GetMD5Code(tradePwd))) {
				return ResponseData.error("输入的交易密码有误");
			}
//			SellerTimeSetting timeSetting = sellerDao.getSellerTimeOne();
//			if (timeSetting != null && timeSetting.getValue() >0){
//			  SellerBuyerCoinOrder order =sellerDao.getSellerBuyerCoinOrderLast(seller.getSellerId());
//			  if (order == null){
//			  	return ResponseData.error("暂时无法提币");
//			  }
//			  if (timeSetting.getValue() >0){
//				  Boolean flag =  TimeUtil.judgmentDate(order.getCreateTime(),timeSetting.getValue());
//				  if (!flag){
//					  return ResponseData.error("最新接单时间需超过"+timeSetting.getValue()+"小时才可提币");
//				  }
//			  }
//			}
			SellerWithdrawFeeSetting feeSetting = sellerDao.findSellerWithdrawFeeSettingOne();
			if (seller != null && feeSetting != null) {
				SellerWallter usdtWallter = new SellerWallter();
				usdtWallter.setCode("USDT");
				usdtWallter.setType(1);
				usdtWallter.setSellerId(seller.getSellerId());
				List<SellerWallter> list = sellerDao.findSellerWallter(usdtWallter);
				if (list == null || list.size() <= 0) {
					return ResponseData.error("提币失败");
				}
				usdtWallter = list.get(0);
				if (feeSetting.getMinNumber() != null && feeSetting.getMinNumber() > 0
						&& feeSetting.getMinNumber() > number) {
					return ResponseData.error("提币数量不能小于" + feeSetting.getMinNumber());
				}
				if (feeSetting.getMaxNumber() != null && feeSetting.getMaxNumber() > 0
						&& feeSetting.getMaxNumber() < number) {
					return ResponseData.error("提币数量不能大于" + feeSetting.getMaxNumber());
				}
				if (usdtWallter.getAvailableBalance() < number) {
					return ResponseData.error("余额不足");
				}
				Double feePrice = 0.0;
				if (feeSetting.getStartRatioNumber() != null && feeSetting.getStartRatioNumber() > 0
						&& feeSetting.getStartRatioNumber() < number) {
					feePrice = number * feeSetting.getFeeRatio() / 100;
				} else {
					feePrice = feeSetting.getMinFeeNumber();
				}
				SellerWithdrawCoinAppealOrder order = new SellerWithdrawCoinAppealOrder();
				order.setAddress(address);
				order.setCreateTime(new Date());
				order.setFeePrice(feePrice);
				order.setNumber(number);
				order.setSellerId(seller.getSellerId());
				order.setStatus(1);
				order.setTotalNumber(order.getNumber() - feePrice);
				USDTOtcpExchange exchage = sellerDao.getOTCMarkInfo();
				if (exchage != null){
					order.setExChangeRatio(exchage.getValue());
				}else{
					order.setExChangeRatio(1.0);
				}

				order.setSerialNo(otcOrderService.generateSimpleSerialno(seller.getSellerId(), 8));
				sellerDao.addSellerWithdrawCoinAppealOrder(order);
				// 扣除账号余额
				logger.info("会员提币，会员【"+usdtWallter.getSellerId()+"】，可用余额："+usdtWallter.getAvailableBalance()+",冻结余额："+usdtWallter.getFrozenBalance()+",提币数量："+number);
				
				AccountUpdateRecord updateRecord = new AccountUpdateRecord();
				updateRecord.setBeforePrice(usdtWallter.getAvailableBalance());
				
				usdtWallter.setAvailableBalance(usdtWallter.getAvailableBalance() - number);
				usdtWallter.setFrozenBalance(usdtWallter.getFrozenBalance() + number);
				usdtWallter.setUpdateTime(new Date());
				int result = sellerDao.updateSellerWallter(usdtWallter);
				if (result <= 0) {
					throw new WallterException("提币失败,请重新尝试");
				}
				
				updateRecord.setAfterPrice(usdtWallter.getAvailableBalance());
				updateRecord.setCode("USDT");
				updateRecord.setCreateTime(new Date());
				updateRecord.setPhone(seller.getAccount());
				updateRecord.setSource("搬砖-USDT");
				updateRecord.setType(AccountUpdateType.WITHDRAW_USDT_FROZENBALANCE.code());
				updateRecord.setWalletCode(AccountUpdateWallet.BANZHUAN_WALLET_USDT.code());
				updateRecord.setRemark("提币,将提币的数量从可用余额转移到冻结余额中，等待审核");
				updateRecord.setPrice(-number);
				updateRecord.setRoleId(1L);
				updateRecord.setAccountId(seller.getSellerId());
				updateRecord.setSerialno(order.getSerialNo());
				sellerDao.addAccountUpdateRecord(updateRecord);
				
				logger.info("会员提币后，会员【"+usdtWallter.getSellerId()+"】，可用余额："+usdtWallter.getAvailableBalance()+",冻结余额："+usdtWallter.getFrozenBalance()+",提币数量："+number);

				
				return ResponseData.success(200, "确认提币成功，静等审核", null);
			}
		}
		return ResponseData.error("确认提币失败");
	}

	public ResponseData withdrawCoinList(Integer pageSize, Integer pageNumber, String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			int total = 0;
			List<Map<String, Object>> list = new ArrayList<>();
			if (seller != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
				Query query = new Query(params);
				List<SellerWithdrawCoinAppealOrder> orderList = sellerDao.getSellerWithdrawCoinAppealList(query);
				for (SellerWithdrawCoinAppealOrder order : orderList) {
					Map<String, Object> map = new HashMap<>();
					if (order != null) {
						map.put("id", order.getAppealId());
						if (order.getStatus() == 1) {
							map.put("statusName", "审核中");
						} else if (order.getStatus() == 2) {
							map.put("statusName", "已完成");
						} else {
							map.put("statusName", "失败");
						}
						map.put("number", order.getNumber());
						map.put("address", order.getAddress());
						map.put("feePrice", order.getFeePrice());
						map.put("symbols", "USDT");
						map.put("remark", "提币");
						map.put("createTime", order.getCreateTime().getTime() / 1000);// 时间为秒
					}
					list.add(map);
				}
				total = sellerDao.getSellerWithdrawCoinAppealListCount(query);
			}
			PageUtils pageUtils = new PageUtils(total, list);
			return ResponseData.success(pageUtils);
		}
		return ResponseData.success(null);
	}

	@Transactional
	public ResponseData transferCoin(Integer type, Double number, String symbols,
			String token) {
		if (type == null || type <= 0) {
			return ResponseData.error("参数有误");
		}
		if (number == null || number <= 0) {
			return ResponseData.error("划转数量要大于0");
		}
		if (StringUtils.isBlank(symbols)){
			return ResponseData.error("请选择币种");
		}
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				if (seller.getIsAuth() == null || seller.getIsAuth().equals(0)) {
					return ResponseData.error("未实名认证，请先实名认证后再来");
				}
				if (seller.getIsAuth().equals(1)) {
					return ResponseData.error("等待实名认证审核通过后再进行");
				}
				if(seller.getTranferEnabled() != null && seller.getTranferEnabled() ==1){
					return ResponseData.error("您已被禁用划转");
				}
					// 转出的钱包账户
					SellerWallter otcpWallter = new SellerWallter();
					otcpWallter.setCode(symbols.toUpperCase());
					otcpWallter.setSellerId(seller.getSellerId());

					//到账的钱包账户
					SellerWallter otcWallet = new SellerWallter();
					otcWallet.setCode(symbols.toUpperCase());
					otcWallet.setSellerId(seller.getSellerId());


					if (type.equals(1)){//搬砖转到法币

						otcpWallter.setType(1);//搬砖
						otcWallet.setType(3);//法币

					}else if (type.equals(2)){//法币转到搬砖

						otcpWallter.setType(3);//法币钱包
						otcWallet.setType(1);//搬砖钱包

					}else if(type.equals(3) ){//代付转出到搬砖

						otcpWallter.setType(2);//代付钱包
						otcWallet.setType(1);//搬砖钱包

					}else if(type.equals(4)){//代付转出到法币

						otcpWallter.setType(2);//代付钱包
						otcWallet.setType(3);//法币钱包

					}else if(type.equals(5)){//挖矿转到搬砖

						otcpWallter.setType(4);//挖矿钱包
						otcWallet.setType(1);//搬砖钱包

					}else{//从挖矿转到法币

						otcpWallter.setType(4);//挖矿钱包
						otcWallet.setType(3);//法币钱包
					}
					List<SellerWallter> wallterList = sellerDao.findSellerWallter(otcpWallter);
					List<SellerWallter> otcWalletList = sellerDao.findSellerWallter(otcWallet);
					if (wallterList != null && wallterList.size() > 0 && otcWalletList != null
							&& otcWalletList.size() > 0) {
						otcpWallter = wallterList.get(0);
						otcWallet = otcWalletList.get(0);
						//判断搬砖账户某个币种钱包是否足够
						if (otcpWallter.getAvailableBalance() < number) {
							return ResponseData.error("余额不足,无法划转");
						}
						Double available = otcpWallter.getAvailableBalance();
						otcpWallter.setAvailableBalance(otcpWallter.getAvailableBalance() - number);
						otcpWallter.setUpdateTime(new Date());
						int result = sellerDao.updateSellerWallter(otcpWallter);
						if (result <= 0) {
							throw new WallterException("划转失败");
						}

						//账变记录,保存某个账户转出的记录
						AccountUpdateRecord updateRecord = new AccountUpdateRecord();
						updateRecord.setBeforePrice(available);//变动前
						updateRecord.setAfterPrice(otcpWallter.getAvailableBalance());//变动后
						updateRecord.setCode(symbols.toUpperCase());//币种
						updateRecord.setCreateTime(new Date());//时间
						updateRecord.setPhone(seller.getAccount());//账户
						if (type.equals(1)){
							updateRecord.setSource("搬砖-"+symbols.toUpperCase());//来源
							if ("USDT".equals(symbols.toUpperCase())){
								updateRecord.setType(AccountUpdateType.TR_USDT_SLABS_TO_OTC.code());
							}
							if ("HC".equals(symbols.toUpperCase())){
								updateRecord.setType(AccountUpdateType.TR_HC_SLABS_TO_OTC.code());
							}
						}else  if(type .equals(2)){
							updateRecord.setSource("法币-"+symbols.toUpperCase());//来源
							if ("USDT".equals(symbols.toUpperCase())){
								updateRecord.setType(AccountUpdateType.TR_USDT_OTC_TO_SLABS.code());
							}
							if ("HC".equals(symbols.toUpperCase())){
								updateRecord.setType(AccountUpdateType.TR_HC_OTC_TO_SLABS.code());
							}
						}else if (type.equals(3) || type.equals(4)){
							updateRecord.setSource("代付-"+symbols.toUpperCase());//来源
							updateRecord.setType(AccountUpdateType.TR_HC_PAID_TO_SLABS.code());
							if (type.equals(4)){
								updateRecord.setType(AccountUpdateType.TR_HC_PAID_TO_OTC.code());
							}
						}else{
							updateRecord.setSource("挖矿-"+symbols.toUpperCase());//来源
							updateRecord.setType(AccountUpdateType.TR_HC_PROFIT_TO_OTC.code());
							if (type.equals(5)){
								updateRecord.setType(AccountUpdateType.TR_HC_PROFIT_TO_SLABS.code());
							}
						}

						updateRecord.setRemark("划转转出");
						updateRecord.setPrice(-number);
						updateRecord.setRoleId(1L);
						updateRecord.setAccountId(seller.getSellerId());
						sellerDao.addAccountUpdateRecord(updateRecord);



						Double toAvailable = otcWallet.getAvailableBalance();
						otcWallet.setAvailableBalance(otcWallet.getAvailableBalance() + number);
						otcWallet.setUpdateTime(new Date());
						result = sellerDao.updateSellerWallter(otcWallet);
						if (result <= 0) {
							throw new WallterException("划转失败");
						}
						//账变记录,保存某个账户转入的记录
						AccountUpdateRecord updateRecord2 = new AccountUpdateRecord();
						updateRecord2.setBeforePrice(toAvailable);
						updateRecord2.setAfterPrice(otcWallet.getAvailableBalance());
						updateRecord2.setCode(symbols.toUpperCase());
						updateRecord2.setCreateTime(new Date());
						updateRecord2.setPhone(seller.getAccount());
						if (type.equals(1)){
							updateRecord2.setSource("法币-"+symbols.toUpperCase());//来源
							if ("USDT".equals(symbols.toUpperCase())){
								updateRecord2.setType(AccountUpdateType.TR_USDT_SLABS_TO_OTC.code());
							}
							if ("HC".equals(symbols.toUpperCase())){
								updateRecord2.setType(AccountUpdateType.TR_HC_SLABS_TO_OTC.code());
							}
						}else  if(type .equals(2)){
							updateRecord2.setSource("搬砖-"+symbols.toUpperCase());//来源
							if ("USDT".equals(symbols.toUpperCase())){
								updateRecord2.setType(AccountUpdateType.TR_USDT_OTC_TO_SLABS.code());
							}
							if ("HC".equals(symbols.toUpperCase())){
								updateRecord2.setType(AccountUpdateType.TR_HC_OTC_TO_SLABS.code());
							}
						}else if (type.equals(3) || type.equals(4)){
							updateRecord2.setSource("搬砖-"+symbols.toUpperCase());//来源
							updateRecord2.setType(AccountUpdateType.TR_HC_PAID_TO_SLABS.code());
							if (type.equals(4)){
								updateRecord2.setSource("法币-"+symbols.toUpperCase());//来源
								updateRecord2.setType(AccountUpdateType.TR_HC_PAID_TO_OTC.code());
							}
						}else{
							updateRecord2.setSource("搬砖-"+symbols.toUpperCase());//来源
							updateRecord2.setType(AccountUpdateType.TR_HC_PROFIT_TO_SLABS.code());
							if (type.equals(6)){
								updateRecord2.setSource("法币-"+symbols.toUpperCase());//来源
								updateRecord2.setType(AccountUpdateType.TR_HC_PROFIT_TO_OTC.code());
							}
						}
						updateRecord2.setRemark("划转转入");
						updateRecord2.setPrice(number);
						updateRecord2.setRoleId(1L);
						updateRecord2.setAccountId(otcpWallter.getSellerId());
						sellerDao.addAccountUpdateRecord(updateRecord2);


						
						// 划转订单记录
						SellerTransferRecord record = new SellerTransferRecord();
						record.setCreateTime(new Date());
						record.setNumber(number);
						record.setSellerId(seller.getSellerId());


						// 转出账号流水记录
						SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
						flowRecord.setCode(symbols.toUpperCase());
						flowRecord.setCreateTime(new Date());
						flowRecord.setPrice(-number);
						flowRecord.setSellerId(seller.getSellerId());
						flowRecord.setSource("划转");
						flowRecord.setType(FlowRecordConstant.TRANFER_OUT_COIN);


						// 转入账户流水记录
						SellerAccountFlowRecord userFlowRecord = new SellerAccountFlowRecord();
						userFlowRecord.setCode(symbols.toUpperCase());
						userFlowRecord.setCreateTime(new Date());
						userFlowRecord.setPrice(number);
						userFlowRecord.setSellerId(seller.getSellerId());
						userFlowRecord.setSource("划转");
						userFlowRecord.setType(FlowRecordConstant.TRANFER_IN_COIN);

						if (type.equals(1)){

							record.setType("从搬砖到法币");

							flowRecord.setWalletType(1);
							userFlowRecord.setWalletType(3);

						}else if(type.equals(2)){
							record.setType("从法币到搬砖");

							flowRecord.setWalletType(3);
							userFlowRecord.setWalletType(1);

						}else if(type.equals(3)){
							record.setType("从代付到搬砖");
							flowRecord.setWalletType(2);
							userFlowRecord.setWalletType(1);
						}else if(type.equals(4)){
							record.setType("从代付到法币");

							flowRecord.setWalletType(2);
							userFlowRecord.setWalletType(3);

						}else if(type.equals(5)){
							record.setType("从挖矿到搬砖");
							flowRecord.setWalletType(4);
							userFlowRecord.setWalletType(1);
						}else{
							record.setType("从挖矿到法币");
							flowRecord.setWalletType(4);
							userFlowRecord.setWalletType(3);
						}
						//保存划转订单记录
						sellerDao.addSellerTransferRecord(record);
						//保存转出记录
						sellerDao.addSellerAccountFlowRecord(flowRecord);
						//保存转入记录
						sellerDao.addSellerAccountFlowRecord(userFlowRecord);

						return ResponseData.success(200, "划转成功", null);
				}
			}
		}
		return ResponseData.error("划转失败");
	}

	public ResponseData transferCoinList(Integer pageSize, Integer pageNumber, String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			int total = 0;
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (seller != null) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
				params.put("userId", seller.getUserId());
				Query query = new Query(params);
				List<SellerTransferRecord> orderList = sellerDao.getTransferCoinListList(query);
				for (SellerTransferRecord order : orderList) {
					Map<String, Object> map = new HashMap<String, Object>();
					if (order != null) {
						map.put("type", order.getType());
						map.put("number", order.getNumber());
						map.put("symbols", "HC");
						map.put("createTime", order.getCreateTime().getTime() / 1000);// 时间为秒
					}
					list.add(map);
				}
				total = sellerDao.getTransferCoinListListCount(query);
			}
			PageUtils pageUtils = new PageUtils(total, list);
			return ResponseData.success(pageUtils);
		}
		return ResponseData.success(null);
	}

	public ResponseData flowRecordList(Integer pageSize, Integer pageNumber, String type, Long id, String token, String starTime, String endTime) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			int total = 0;
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (seller != null) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
				if (StringUtils.isNotBlank(starTime)){
					params.put("starTime",starTime);
				}
				if (StringUtils.isNotBlank(endTime)){
					params.put("endTime",endTime);
				}
				if (StringUtils.isNotBlank(type)){
					params.put("type",type);//流水类型
				}
				SellerWallter sellerWallter = new SellerWallter();
				sellerWallter.setSellerWallterId(id);
				List<SellerWallter> sellerWallterList = sellerDao.findSellerWallter(sellerWallter);
				if (sellerWallterList == null || sellerWallterList.size() < 0) {
					return ResponseData.error("获取失败");
				}
				sellerWallter = sellerWallterList.get(0);
				params.put("code", sellerWallter.getCode());//币种
				params.put("walletType",sellerWallter.getType());//钱包类型
				Query query = new Query(params);
				List<SellerAccountFlowRecord> flowRecordList = sellerDao.getSellerAccountFlowRecordList(query);
				for (SellerAccountFlowRecord flowRecord : flowRecordList) {
					Map<String, Object> map = new HashMap<String, Object>();
					if (flowRecord != null) {
						map.put("number", flowRecord.getPrice());
						map.put("source", flowRecord.getSource());
						map.put("createTime", flowRecord.getCreateTime().getTime() / 1000);// 时间为秒
						map.put("symbol", sellerWallter.getCode());
					}
					list.add(map);
				}
				total = sellerDao.getSellerAccountFlowRecordListCount(query);
				PageUtils pageUtils = new PageUtils(total, list);
				return ResponseData.success(pageUtils);
			}
		}
		return ResponseData.success(null);
	}

	public ResponseData wallterMoney(Integer type, String token, Long id) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				USDTOtcpExchange exchage = sellerDao.getOTCMarkInfo();
				if (exchage != null) {
						SellerWallter sellerWallter = new SellerWallter();
						sellerWallter.setSellerWallterId(id);
						sellerWallter.setType(type);
						List<SellerWallter> list = sellerDao.findSellerWallter(sellerWallter);
						Double cnyPrice = 0.0;
						if (list != null && list.size() > 0) {
							Map<String, Object> map = new HashMap<String, Object>();
							sellerWallter = list.get(0);
							map.put("availableBalance", sellerWallter.getAvailableBalance());
							map.put("frozenBalance", sellerWallter.getFrozenBalance());
							map.put("totalBalance",
									sellerWallter.getAvailableBalance() + sellerWallter.getFrozenBalance());
							if ("HC".equals(sellerWallter.getCode())) {
								cnyPrice = new BigDecimal(
										sellerWallter.getFrozenBalance() + sellerWallter.getAvailableBalance())
												.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
							} else {
								if (exchage.getValue() != null && exchage.getValue() > 0) {
									cnyPrice = new BigDecimal(
											sellerWallter.getAvailableBalance() + sellerWallter.getFrozenBalance())
													.multiply(new BigDecimal(exchage.getValue()))
													.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
								}
							}
							map.put("cnyPrice", cnyPrice);
							return ResponseData.success(map);
						}

				}

			}
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData availableBalance(Integer type, String token, String symbols) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
					SellerWallter sellerWallter = new SellerWallter();
					sellerWallter.setCode(symbols);
					sellerWallter.setType(type);
					sellerWallter.setSellerId(seller.getSellerId());
					List<SellerWallter> list = sellerDao.findSellerWallter(sellerWallter);
					if (list != null && list.size() > 0) {
						Map<String, Object> map = new HashMap<>();
						sellerWallter = list.get(0);
						map.put("availableBalance", sellerWallter.getAvailableBalance());
						return ResponseData.success(map);
					}
			}
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData submitAccpterAppeal(String token, String name, String phone, String idcardNo) {
		if (StringUtils.isBlank(name)) {
			return ResponseData.error("请输入真实姓名");
		}
		if (StringUtils.isBlank(phone)) {
			return ResponseData.error("请输入联系方式");
		}
		if (StringUtils.isBlank(idcardNo)) {
			return ResponseData.error("请输入身份证号码");
		}
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				if (seller.getIsAuth() == null || seller.getIsAuth().equals(0)) {
					return ResponseData.error("未实名认证，请先实名认证后再来");
				}
				if (seller.getIsAuth().equals(1)) {
					return ResponseData.error("等待实名认证审核通过后再进行");
				}
				if (seller.getIsAccepter() == 1) {
					return ResponseData.success(200, "您已是承兑商", null);
				}
				if (seller.getIsAccepter() == 2) {
					return ResponseData.success(200, "您已提交过申请，等待审核", null);
				}
				SellerAccpterAppeal appeal = new SellerAccpterAppeal();
				appeal.setCreateTime(new Date());
				appeal.setIdCardNo(idcardNo);
				appeal.setName(name);
				appeal.setPhone(phone);
				appeal.setSellerId(seller.getSellerId());
				appeal.setStatus(1);
				sellerDao.addSellerAccpterAppeal(appeal);
				seller.setIsAccepter(2);
				seller.setUpdateTime(new Date());
				sellerDao.updateSeller(seller);
				return ResponseData.success(200, "提交成功，等待审核", null);
			}
		}
		return ResponseData.error("提交失败");
	}

	public ResponseData otcList(Integer pageSize, Integer pageNumber, Integer type,String symbols,Integer payMethod,Double price, String token) {
			if (StringUtils.isNotBlank(token)) {
				Seller seller = (Seller) redisUtil.get(token);
				int total = 0;
				List<Map<String, Object>> list = new ArrayList<>();
				if (seller != null) {
					Map<String, Object> params = new HashMap<>();
					params.put("pageNumber", pageNumber);
					params.put("pageSize", pageSize);
					params.put("sellerId", seller.getSellerId());
					params.put("symbols",symbols);
					params.put("payMethod",payMethod);
					params.put("price",price);
					params.put("isMySelf", "2");
					if (type == 1) {// 会员区
						params.put("type", 1);
						Query query = new Query(params);
						List<SellOtcpOrderDto> otcpList = sellerDao.getSellOtcpOrderList(query);
						for (SellOtcpOrderDto order : otcpList) {
							Map<String, Object> map = new HashMap<String, Object>();
							if (order != null) {
								map.put("id", order.getOrderId());
								Seller seller2 = sellerDao.findSellerbyId(order.getSellerId());
								map.put("nickName",seller2.getNickName());
								map.put("icon", seller2.getIcon());
								map.put("number", order.getSupNumber());
								map.put("price", order.getPrice());
								map.put("minNumber", order.getMinNumber());
								map.put("maxNumber", order.getMaxNumber());
								JSONArray jsonArray = JSONArray.parseArray(order.getPayMethodIds().toString());
								List<Integer> payMethodIs = new ArrayList<>(jsonArray.size());
								for (Object object : jsonArray) {
									JSONObject jsonObject = JSONObject.parseObject(object.toString());
									Integer payMethodType = jsonObject.getInteger("type");
									payMethodIs.add(payMethodType);
								}
								map.put("payMethodIds", payMethodIs);
								map.put("traderNum",order.getTraderNum());//交易量
								map.put("successRatio",(order.getSuccessRatio()*100)+"%");//成功率
							}
							list.add(map);
						}
						total = sellerDao.getSellOtcpOrderListCount(query);
					} else if (type == 2) {// 承兑商,查询会员，商户出售订单
						params.put("type", 2);
						Query query = new Query(params);
						List<SellOtcpOrderDto> otcpList = sellerDao.getSellOtcpOrderList(query);
						for (SellOtcpOrderDto order : otcpList) {
							Map<String, Object> map = new HashMap<>();
							if (order != null) {
								if (order.getType() == 3) {//商户
									User user = new User();
									user.setUserId(order.getUserId());
									user = sellerDao.findUserOne(user);
									map.put("nickName",user.getAccountCode());
									map.put("icon", user.getAvatar());
								}else if (order.getType() ==4) {//代理商
									User user = new User();
									user.setUserId(order.getUserId());
									user = sellerDao.findUserOne(user);
									map.put("nickName", user.getAccountCode());
									map.put("icon", user.getAvatar());
								}else {
									Seller seller2 = sellerDao.findSellerbyId(order.getSellerId());
									map.put("nickName",seller2.getNickName());
									map.put("icon", seller2.getIcon());
								}
								map.put("id", order.getOrderId());
								map.put("number", order.getSupNumber());
								map.put("price", order.getPrice());
								map.put("minNumber", order.getMinNumber());
								map.put("maxNumber", order.getMaxNumber());
								JSONArray jsonArray = JSONArray.parseArray(order.getPayMethodIds().toString());
								List<Integer> payMethodIs = new ArrayList<>(jsonArray.size());
								for (Object object : jsonArray) {
									JSONObject jsonObject = JSONObject.parseObject(object.toString());
									Integer payMethodType = jsonObject.getInteger("type");
									payMethodIs.add(payMethodType);
								}
								map.put("payMethodIds", payMethodIs);
								map.put("traderNum",order.getTraderNum());//交易量
								map.put("successRatio",(order.getSuccessRatio()*100)+"%");//成功率
							}
							list.add(map);
						}
						total = sellerDao.getSellOtcpOrderListCount(query);
					}
					PageUtils pageUtils = new PageUtils(total, list);
					return ResponseData.success(pageUtils);
				}
			}
		return ResponseData.success(null);
	}

	public ResponseData octSellerFeeAndMoney(Integer type,String symbols, String token) {
		if (StringUtils.isBlank(symbols)){
			return  ResponseData.error("参数有误");
		}
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
					SellerWallter sellerWallter = new SellerWallter();
					sellerWallter.setCode(symbols);
					sellerWallter.setType(4);
					sellerWallter.setSellerId(seller.getSellerId());
					List<SellerWallter> list = sellerDao.findSellerWallter(sellerWallter);
					SellerOtcFeeSetting feeSetting = sellerDao.findSellerOtcFeeSettingOne(symbols);
					if (list != null && list.size() > 0 && feeSetting != null) {
						sellerWallter = list.get(0);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("availableBalance", sellerWallter.getAvailableBalance());
						map.put("feePrice", feeSetting.getRatio());
						map.put("minNumber",feeSetting.getMinNumber());
						OtcpPirceSetting priceSetting = sellerDao.findOtcpPriceSettingOne(symbols);
						map.put("price",priceSetting.getPrice());
						return ResponseData.success(map);
					}
			}
		}
		return ResponseData.error("获取失败");
	}

	@Transactional
	public ResponseData submitSellOtc(String token, Integer type, Double minNumber, Double maxNumber, Double number,
			String tradePwd, String payMethodIds,String symbols,Double price) {
		if (StringUtils.isBlank(tradePwd)) {
			return ResponseData.error("请输入交易密码");
		}
		if (number == null || number <= 0) {
			return ResponseData.error("输入的数量不能小于0");
		}
		if (StringUtils.isBlank(payMethodIds)) {
			return ResponseData.error("请选择收款方式");
		}
		if (StringUtils.isBlank(symbols)){
			return  ResponseData.error("出售币种不能为空");
		}
		JSONArray jsonArray = JSONArray.parseArray(payMethodIds);
		if (jsonArray == null || jsonArray.size() <= 0) {
			return ResponseData.error("请选择收款方式");
		}
		if ("USDT".equals(symbols) && (price == null || price <=0)){
			return ResponseData.error("请设置出售单价");
		}


		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				if (seller.getIsAuth() == null || seller.getIsAuth().equals(0)) {
					return ResponseData.error("未实名认证，请先实名认证后再来");
				}
				if (seller.getIsAuth().equals(1)) {
					return ResponseData.error("等待实名认证审核通过后再进行");
				}

				if (seller.getSellEnabled() != null && seller.getSellEnabled() ==1){
					return ResponseData.error("您已被禁用出售");
				}
				SellerOtcFeeSetting feeSetting = sellerDao.findSellerOtcFeeSettingOne(symbols);
				if (feeSetting != null && number < feeSetting.getMinNumber()){
					return ResponseData.error("出售数量不能小于"+feeSetting.getMinNumber());
				}
				OtcpPirceSetting priceSetting = sellerDao.findOtcpPriceSettingOne(symbols);
				if (priceSetting != null) {
					if (StringUtils.isBlank(seller.getTraderPassword())) {
						return ResponseData.error("未设置交易密码");
					}
					if (!seller.getTraderPassword().equals(Md5Utils.GetMD5Code(tradePwd))) {
						return ResponseData.error("输入的交易密码有误");
					}
					Integer aliNumber = 0;
					Integer wxNumber = 0;
					Integer cardBankNumber = 0;
					for (Object object : jsonArray) {
						JSONObject jsonObject = JSONObject.parseObject(object.toString());
						if (jsonObject.containsKey("type")) {
							Integer type2 = jsonObject.getInteger("type");
							if (type2 == 1) {
								aliNumber = aliNumber + 1;
							} else if (type2 == 2) {
								wxNumber = wxNumber + 1;
							} else if (type2 == 3) {
								cardBankNumber = cardBankNumber + 1;
							}
						}
						if (jsonObject.containsKey("id")) {
							Long id = jsonObject.getLong("id");
							if (id != null && id > 0) {
								SellerPayMethod payMethod = sellerDao.findSellerPayMethodById(id);
								if (payMethod == null) {
									return ResponseData.error("出售失败");
								}
								if (!payMethod.getSellerId().equals(seller.getSellerId())) {
									return ResponseData.error("出售失败");
								}
							} else {
								return ResponseData.error("出售失败");
							}

						}
					}
					if (aliNumber > 1) {
						return ResponseData.error("支付宝收款方式只能选择一个");
					}
					if (wxNumber > 1) {
						return ResponseData.error("微信收款方式只能选择一个");
					}
					if (cardBankNumber > 1) {
						return ResponseData.error("银行卡收款方式只能选择一个");
					}
					if (type.equals(1)) {
						//会员出售限制判断
//					SellerBuyerCoinOrder lastOrder = sellerDao.getSellerBuyerCoinOrderLast(seller.getSellerId());
//					if (lastOrder != null){
//						SellerTimeSetting timeSetting = sellerDao.getSellerTimeOne();
//						if (timeSetting != null && timeSetting.getValue() >0){
//							Boolean flag =  TimeUtil.judgmentDate(lastOrder.getCreateTime(),timeSetting.getValue());
//							if (!flag){
//								return ResponseData.error("时间需超过"+timeSetting.getValue()+"小时才可再出售");
//							}
//						}
//						}
						SellerWallter sellerWallter = new SellerWallter();
						sellerWallter.setCode(symbols);
						sellerWallter.setType(4);
						sellerWallter.setSellerId(seller.getSellerId());
						List<SellerWallter> list = sellerDao.findSellerWallter(sellerWallter);
						if (list != null && list.size() > 0) {
							sellerWallter = list.get(0);
							Double feePrice = 0.0;
							if (type == 1 && feeSetting != null && feeSetting.getRatio() != null && feeSetting.getRatio() > 0) {
								feePrice = number * feeSetting.getRatio() / 100;
							}
							if (sellerWallter.getAvailableBalance() < number) {
								return ResponseData.error("余额不足");
							}
							SellOtcpOrder order = new SellOtcpOrder();
							String serialNo = otcOrderService.generateSimpleSerialno(seller.getSellerId(), 2);
							order.setSerialno(serialNo);
							order.setSellerId(seller.getSellerId());
							order.setNumber(number);
							order.setSupNumber(number - feePrice);
							if ("HC".equals(symbols)){
								order.setMinNumber(order.getSupNumber() * priceSetting.getPrice());
								order.setMaxNumber(order.getSupNumber() * priceSetting.getPrice());
								order.setPrice(priceSetting.getPrice());
							}else{
								order.setMinNumber(order.getSupNumber() * price);
								order.setMaxNumber(order.getSupNumber() * price);
								order.setPrice(price);
							}
							order.setPayMethodIds(payMethodIds);
							order.setCreateTime(new Date());
							order.setStatus(1);
							order.setType(1);
							order.setFeePrice(feePrice);
							order.setFeeRatio(feeSetting.getRatio());
							order.setTotalPrice(order.getSupNumber() * priceSetting.getPrice());
							otcOrderService.addSellOtcpOrder(order);


							logger.info("出售" + symbols + "扣除冻结余额,订单号【" + order.getSerialno() + "】，扣除前，用户【"
									+ sellerWallter.getSellerId() + "】,可用余额:"
									+ sellerWallter.getAvailableBalance()
									+ ",冻结余额：" + sellerWallter.getFrozenBalance() + ",出售数量:" + order.getSupNumber());

							// 更新钱包
							Double available = sellerWallter.getAvailableBalance();

							sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance() - order.getNumber());
							sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance() + order.getSupNumber());
							sellerWallter.setUpdateTime(new Date());
							int result = sellerDao.updateSellerWallter(sellerWallter);
							if (result <= 0) {
								throw new WallterException("出售失败");
							}

							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(available);
							updateRecord.setSerialno(order.getSerialno());
							updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
							updateRecord.setCode(symbols);
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(seller.getAccount());
							updateRecord.setSource("法币-" + symbols);
							if ("USDT".equals(symbols)) {
								updateRecord.setType(AccountUpdateType.SELL_OTC_USDT.code());
								updateRecord.setWalletCode(AccountUpdateWallet.OTC_WALLET_USDT.code());
							} else {
								updateRecord.setType(AccountUpdateType.SELL_OTC_HC.code());
								updateRecord.setWalletCode(AccountUpdateWallet.OTC_WALLET_HC.code());
							}
							updateRecord.setRemark("出售");
							updateRecord.setPrice(-order.getNumber());
							updateRecord.setAccountId(seller.getSellerId());
							updateRecord.setRoleId(1L);
							sellerDao.addAccountUpdateRecord(updateRecord);

							logger.info("出售" + symbols + "扣除冻结余额,订单号【" + order.getSerialno() + "】，扣除后，用户【"
									+ sellerWallter.getSellerId() + "】,可用余额:"
									+ sellerWallter.getAvailableBalance()
									+ ",冻结余额：" + sellerWallter.getFrozenBalance());

							//流水记录
							SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
							flowRecord.setCode(symbols);
							flowRecord.setCreateTime(new Date());
							flowRecord.setPrice(-order.getSupNumber());
							flowRecord.setSellerId(seller.getSellerId());
							flowRecord.setRemark("出售" + symbols + ",出售数量：" + order.getSupNumber());
							flowRecord.setSerialno(order.getSerialno());
							flowRecord.setSource("挂单出售");
							if ("USDT".equals(symbols)){
								flowRecord.setType(FlowRecordConstant.SELL_USDT_COIN);
							}else{
								flowRecord.setType(FlowRecordConstant.SELL_HC_COIN);
							}

							flowRecord.setWalletType(3);
							sellerDao.addSellerAccountFlowRecord(flowRecord);

							if (feePrice > 0) {
								SellerAccountFlowRecord flowRecord1 = new SellerAccountFlowRecord();
								flowRecord1.setCode(symbols);
								flowRecord1.setCreateTime(new Date());
								flowRecord1.setPrice(-feePrice);
								flowRecord1.setSellerId(seller.getSellerId());
								flowRecord1.setRemark("出售" + symbols + ",扣除手续费：" + feePrice);
								flowRecord1.setSerialno(order.getSerialno());
								flowRecord1.setSource("挂单出售手续费");
								flowRecord.setWalletType(3);
								flowRecord1.setType(FlowRecordConstant.SELL_HC_COIN_FEE);
								sellerDao.addSellerAccountFlowRecord(flowRecord1);
							}
							return ResponseData.success(200, "出售成功", null);
						}
					}else if (type == 2 && seller.getIsAccepter() != null && seller.getIsAccepter() == 1) {
						// 承兑商出售
						SellerWallter userWallter = new SellerWallter();
						userWallter.setCode(symbols);
						userWallter.setType(4);
						userWallter.setSellerId(seller.getSellerId());
						List<SellerWallter> list = sellerDao.findSellerWallter(userWallter);
						if (list != null && list.size() > 0) {
							userWallter = list.get(0);
						if (userWallter.getAvailableBalance() < number) {
							return ResponseData.error("余额不足");
						}

						if (minNumber == null || minNumber <=0){
							return ResponseData.error("最小购买价格不能小于0");
						}
						if (maxNumber == null || maxNumber <=0){
							return ResponseData.error("最大购买金额不能小于0");
						}
						if (minNumber > maxNumber ){
							return ResponseData.error("最小购买价格不能大于最大购买价格");
						}

						if ("HC".equals(symbols)){
							if (minNumber < priceSetting.getPrice()){
								return ResponseData.error("最小购买金额不能小于"+priceSetting.getPrice());
							}
							if (maxNumber > number*priceSetting.getPrice()){
								return ResponseData.error("最大购买金额不能大于"+number*priceSetting.getPrice());
							}
						}else{
							if (minNumber < price){
								return ResponseData.error("最小购买金额不能小于"+price);
							}
							if (maxNumber > number*price){
								return ResponseData.error("最大购买金额不能大于"+number*price);
							}
						}

						SellOtcpOrder order = new SellOtcpOrder();
						String serialNo = otcOrderService.generateSimpleSerialno(seller.getSellerId(), 2);
						order.setSerialno(serialNo);
						order.setSellerId(seller.getSellerId());
						order.setRoleId(3);
						order.setNumber(number);
						order.setMinNumber(minNumber);
						order.setMaxNumber(maxNumber);
						order.setSupNumber(number);
						order.setPayMethodIds(payMethodIds);
						if("HC".equals(symbols)){
							order.setPrice(priceSetting.getPrice());
						}else{
							order.setPrice(price);
						}
						order.setCreateTime(new Date());
						order.setStatus(1);
						order.setType(2);
						order.setFeePrice(0.0);
						order.setFeeRatio(0.0);
						order.setTotalPrice(number*price);
						otcOrderService.addSellOtcpOrder(order);


						logger.info("出售HC扣除冻结余额,订单号【"+order.getSerialno()+"】，扣除前，用户【"
								+userWallter.getSellerId()+"】,可用余额:"
								+userWallter.getAvailableBalance()
								+",冻结余额："+userWallter.getFrozenBalance()+",出售数量:"+ order.getSupNumber());
						// 更新钱包
						Double userAvailable = userWallter.getAvailableBalance();
						userWallter.setAvailableBalance(userWallter.getAvailableBalance() - number);
						userWallter.setFrozenBalance(userWallter.getFrozenBalance() + number);
						userWallter.setUpdateTime(new Date());
						int result = sellerDao.updateSellerWallter(userWallter);
						if (result <= 0) {
							throw new WallterException("出售失败");
						}

						//保存账变记录
						AccountUpdateRecord updateRecord = new AccountUpdateRecord();
						updateRecord.setBeforePrice(userAvailable);
						updateRecord.setSerialno(order.getSerialno());
						updateRecord.setAfterPrice(userWallter.getAvailableBalance());
						updateRecord.setCode(symbols);
						updateRecord.setCreateTime(new Date());
						updateRecord.setPhone(seller.getAccount());
						updateRecord.setSource("法币-"+symbols);
						if ("USDT".equals(symbols)) {
							updateRecord.setType(AccountUpdateType.SELL_OTC_USDT.code());
							updateRecord.setWalletCode(AccountUpdateWallet.OTC_WALLET_USDT.code());
						} else {
							updateRecord.setType(AccountUpdateType.SELL_OTC_HC.code());
							updateRecord.setWalletCode(AccountUpdateWallet.OTC_WALLET_HC.code());
						}
						updateRecord.setRemark("出售");
						updateRecord.setPrice(-number);
						updateRecord.setAccountId(userWallter.getSellerId());
						updateRecord.setRoleId(3L);
						sellerDao.addAccountUpdateRecord(updateRecord);

						logger.info("出售"+symbols+"扣除冻结余额,订单号【"+order.getSerialno()+"】，扣除后，用户【"
								+userWallter.getSellerId()+"】,可用余额:"
								+userWallter.getAvailableBalance()
								+",冻结余额："+userWallter.getFrozenBalance()+",出售数量:"+ order.getSupNumber());
						//流水记录
						SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
						flowRecord.setCode(symbols);
						flowRecord.setCreateTime(new Date());
						flowRecord.setPrice(-number);
						flowRecord.setSellerId(seller.getSellerId());
						flowRecord.setSerialno(order.getSerialno());
						flowRecord.setRemark("出售"+symbols+",出售数量："+number);
						flowRecord.setSource("挂单出售"+symbols);
						if ("USDT".equals(symbols)){
							flowRecord.setType(FlowRecordConstant.SELL_USDT_COIN);
						}else{
							flowRecord.setType(FlowRecordConstant.SELL_HC_COIN);
						}
						sellerDao.addSellerAccountFlowRecord(flowRecord);

						return ResponseData.success(200, "出售成功", null);
						}
					}
				}
			}
		}
		return ResponseData.error("出售失败");
	}

	public ResponseData sellOtcList(Integer pageSize, Integer pageNumber, Integer type, String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			int total = 0;
			List<Map<String, Object>> list = new ArrayList<>();
			if (seller != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
//				params.put("status", "1");
				params.put("isMySelf", "1");
				if (type == 1) {// 会员区,我的出售订单列表
					params.put("type", 1);
					Query query = new Query(params);
					List<SellOtcpOrderDto> otcpList = sellerDao.getSellOtcpOrderList(query);
					for (SellOtcpOrderDto order : otcpList) {
						Map<String, Object> map = new HashMap<>();
						if (order != null) {
							map.put("id", order.getOrderId());
							map.put("totalNumber",order.getNumber());//委托数量
							map.put("number", order.getSupNumber());//可用数量
							map.put("price", order.getPrice());//单价
							map.put("totalPrice", order.getTotalPrice());//总价
							map.put("minNumber", order.getMinNumber());//最小价格
							map.put("maxNumber", order.getMaxNumber());//最高价格
							map.put("serialNo", order.getSerialno());//订单号
							map.put("feePrice", order.getFeePrice() == null ? 0 : order.getFeePrice());//手续费
							map.put("status", order.getStatus());//状态：1表示正在进行中，2,表示已完成，3表示已取消
							map.put("symbols",order.getSymbols());//币种
							map.put("createTime", order.getCreateTime().getTime() / 1000);//时间
						}
						list.add(map);
					}
					total = sellerDao.getSellOtcpOrderListCount(query);
				} else if (type == 2 && seller.getIsAccepter() != null && seller.getIsAccepter() == 1) {
					// 承兑商,查询会员，商户出售订单
					params.put("type", 2);
					Query query = new Query(params);
					List<SellOtcpOrderDto> otcpList = sellerDao.getSellOtcpOrderList(query);
					for (SellOtcpOrderDto order : otcpList) {
						Map<String, Object> map = new HashMap<>();
						if (order != null) {
							map.put("id", order.getOrderId());
							map.put("totalNumber",order.getNumber());//委托数量
							map.put("number", order.getSupNumber());
							map.put("price", order.getPrice());
							map.put("totalPrice", order.getTotalPrice());
							map.put("minNumber", order.getMinNumber());
							map.put("maxNumber", order.getMaxNumber());
							map.put("serialNo", order.getSerialno());
							map.put("symbols",order.getSymbols());//币种
							map.put("feePrice", order.getFeePrice() == null ? 0 : order.getFeePrice());
							map.put("status", order.getStatus());
							map.put("createTime", order.getCreateTime().getTime() / 1000);
							list.add(map);
						}
					}
					total = sellerDao.getSellOtcpOrderListCount(query);
				}
				PageUtils pageUtils = new PageUtils(total, list);
				return ResponseData.success(pageUtils);
			}
		}
		return ResponseData.success(null);
	}

	/**
	 * 如果冻结余额不足，不能撤单
	 * @param token
	 * @param id
	 * @return
	 */
	@Transactional
	public ResponseData revocation(String token, Long id) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				SellOtcpOrder order = otcOrderService.findSellerOtcpOrderById(id);
				if (order != null) {
					if (order.getStatus() == 2) {
						return ResponseData.success(200, "该订单刚被买家购买", null);
					} else if (order.getStatus() == 3) {
						return ResponseData.error("已撤销过");
					}
					
					order.setStatus(3);
					order.setUpdateTime(new Date());
					int updateResult = otcOrderService.updateSellOtcpOrder(order);
					if(updateResult <=0) {
						throw new WallterException("撤销失败");
					}
					if (order.getType() == 2) {
						// 承兑商出售
						SellerWallter userWallter = new SellerWallter();
						userWallter.setType(3);
						userWallter.setCode(order.getSymbols());
						userWallter.setSellerId(order.getSellerId());
						List<SellerWallter> list = sellerDao.findSellerWallter(userWallter);
						if (list != null && list.size() > 0) {
							userWallter = list.get(0);

							if(userWallter.getFrozenBalance() - order.getSupNumber()<0){
								throw new WallterException("冻结余额小于订单金额，不可撤单");
							}
							logger.info("承兑区出售，撤销订单号【"+order.getSerialno()+"】,承兑商【"+userWallter.getSellerId()+"】,可用余额："+userWallter.getAvailableBalance()+",冻结余额："+userWallter.getFrozenBalance()+",撤销数量："+order.getNumber());

							Double available = userWallter.getAvailableBalance();
							userWallter.setAvailableBalance(userWallter.getAvailableBalance() + order.getSupNumber());
							userWallter.setFrozenBalance(userWallter.getFrozenBalance() - order.getSupNumber());
							userWallter.setUpdateTime(new Date());
							int result = sellerDao.updateSellerWallter(userWallter);
							if (result <= 0) {
								throw new WallterException("撤销失败");
							}

							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(available);
							updateRecord.setAfterPrice(userWallter.getAvailableBalance());
							updateRecord.setCode(order.getSymbols());
							updateRecord.setSerialno(order.getSerialno());
							updateRecord.setAccountId(userWallter.getSellerId());
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(seller.getAccount());
							updateRecord.setSource("法币-"+order.getSymbols());
							if ("USDT".equals(order.getSymbols())){
								updateRecord.setType(AccountUpdateType.REVOCATION_USDT.code());
							}else{
								updateRecord.setType(AccountUpdateType.REVOCATION_HC.code());
							}
							updateRecord.setRemark("撤销出售");
							updateRecord.setPrice(order.getSupNumber());
							updateRecord.setRoleId(3L);
							sellerDao.addAccountUpdateRecord(updateRecord);
							
							logger.info("承兑区出售，撤销后更新订单号【"+order.getSerialno()+"】,承兑商【"+userWallter.getSellerId()+"】,可用余额："+userWallter.getAvailableBalance()+",冻结余额："+userWallter.getFrozenBalance()+",撤销数量："+order.getNumber());
							SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
							flowRecord.setCode(order.getSymbols());
							flowRecord.setCreateTime(new Date());
							flowRecord.setPrice(order.getSupNumber());
							flowRecord.setSellerId(order.getSellerId());
							flowRecord.setWalletType(3);
							flowRecord.setSource("撤销挂单出售"+order.getSymbols());
							if ("USDT".equals(order.getSymbols())){
								flowRecord.setType(FlowRecordConstant.SELL_USDT_COIN_CANNEL);
							}else{
								flowRecord.setType(FlowRecordConstant.SELL_HC_COIN_CANNEL);
							}

							sellerDao.addSellerAccountFlowRecord(flowRecord);
							return ResponseData.success(200, "撤销成功", null);
						}	
					} else {
						// 会员出售，手续费原因
						SellerWallter sellerWallter = new SellerWallter();
						sellerWallter.setCode(order.getSymbols());
						sellerWallter.setSellerId(seller.getSellerId());
						List<SellerWallter> list = sellerDao.findSellerWallter(sellerWallter);
						if (list != null && list.size() > 0) {
							sellerWallter = list.get(0);

							Double returnTotalNumber = 0.0;
							if (order.getFeePrice() != null && order.getFeePrice() > 0) {
								returnTotalNumber = order.getSupNumber() + order.getFeePrice();
							} else {
								returnTotalNumber = order.getNumber();
							}
							if(sellerWallter.getFrozenBalance() - order.getSupNumber()<0){
								throw new WallterException("撤销失败,冻结不足订单");
							}
							logger.info("承兑区出售，撤销订单号【"+order.getSerialno()+"】,会员【"+sellerWallter.getSellerId()+"】,可用余额："+sellerWallter.getAvailableBalance()+",冻结余额："+sellerWallter.getFrozenBalance()+",撤销数量："+returnTotalNumber);

							Double available = sellerWallter.getAvailableBalance();
							sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance() + returnTotalNumber);
							sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance() - order.getSupNumber());
							sellerWallter.setUpdateTime(new Date());
							int result = sellerDao.updateSellerWallter(sellerWallter);
							if (result <= 0) {
								throw new WallterException("撤销失败");
							}
							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(available);
							updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
							updateRecord.setCode(order.getSymbols());
							updateRecord.setSerialno(order.getSerialno());
							updateRecord.setAccountId(sellerWallter.getSellerId());
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(seller.getAccount());
							updateRecord.setSource("法币-"+order.getSymbols());
							if ("USDT".equals(order.getSymbols())){
								updateRecord.setType(AccountUpdateType.REVOCATION_USDT.code());
							}else{
								updateRecord.setType(AccountUpdateType.REVOCATION_HC.code());
							}
							updateRecord.setRemark("撤销出售");
							updateRecord.setPrice(returnTotalNumber);
							updateRecord.setRoleId(1L);
							sellerDao.addAccountUpdateRecord(updateRecord);
							
							logger.info("承兑区出售，撤销后更新的订单号【"+order.getSerialno()+"】,会员【"+sellerWallter.getSellerId()+"】,可用余额："+sellerWallter.getAvailableBalance()+",冻结余额："+sellerWallter.getFrozenBalance()+",撤销数量："+returnTotalNumber);
							
							SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
							flowRecord.setCode(order.getSymbols());
							flowRecord.setSellerId(order.getSellerId());
							flowRecord.setCreateTime(new Date());
							flowRecord.setPrice(returnTotalNumber);
							flowRecord.setSource("撤销挂单出售");
							flowRecord.setWalletType(3);
							if ("USDT".equals(order.getSymbols())){
								flowRecord.setType(FlowRecordConstant.SELL_USDT_COIN_CANNEL);
							}else{
								flowRecord.setType(FlowRecordConstant.SELL_HC_COIN_CANNEL);
							}
							sellerDao.addSellerAccountFlowRecord(flowRecord);
							
							return ResponseData.success(200, "撤销成功", null);
						}
					}
				}
			}
		}
		return ResponseData.error("撤销失败");
	}

	@Transactional
	public ResponseData submitBuyOtc(String token, Integer buyType, Integer type, Double numberPrice, Long id) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				if (seller.getIsAuth() == null || seller.getIsAuth().equals(0)) {
					return ResponseData.error("未实名认证，请先实名认证后再来");
				}
				if (seller.getIsAuth().equals(1)) {
					return ResponseData.error("等待实名认证审核通过后再进行");
				}
				if (seller.getBuyEnabled() != null && seller.getBuyEnabled().equals(1)){
					return  ResponseData.error("您已被禁用购买");
				}
				// 判断是否有订单未完成的
				int orderNumber = sellerDao.findOtcpOrderBySellerIdAndNoFinish(seller.getSellerId());
				if (orderNumber > 0) {
					return ResponseData.error("您有未完成的订单，无法进行购买，需订单完成后才可以购买");
				}
				if (buyType == null || buyType <= 0) {
					return ResponseData.error("下单失败");
				}
				if (type == null || type <= 0) {
					return ResponseData.error("下单失败");
				}
				if (numberPrice == null || numberPrice <= 0) {
					if (buyType == 1) {
						return ResponseData.error("购买金额不能小于0");
					}
					return ResponseData.error("购买数量不能小于0");
				}
				OtcpOrderCannelNumberRecord otcpOrderCannelNumberRecord = otcOrderService
						.findOtcpOrderCannelBySellerIdToday(seller.getSellerId());
				if (otcpOrderCannelNumberRecord != null) {
					OtcpCannelNumberSetting setting = sellerDao.findOtcpCannelNumberSetting();
					if (setting != null) {
						Boolean flag = TimeUtil.judgmentDate(otcpOrderCannelNumberRecord.getUpdateTime(),setting.getTime());
						if (otcpOrderCannelNumberRecord.getNumber() >= setting.getNumber() && !flag) {
							return ResponseData.error("该账号当日已被限制购买");
						}
					}
				}
				SellOtcpOrder order = otcOrderService.findSellerOtcpOrderById(id);
				if (order != null && order.getStatus() == 1) {
					Double number = 0.0;
					if (order.getType() < 3) {
						if (order.getSellerId().equals(seller.getSellerId())) {
							return ResponseData.error("不能购买自己出售的产品");
						}
					}
					if (buyType == 1) {// 按金额购买
						if (order.getMinNumber() != null && order.getMinNumber() > 0
								&& numberPrice < order.getMinNumber()) {
							return ResponseData.error("输入的购买金额不能小于" + order.getMinNumber());
						}
						if (order.getMaxNumber() != null && order.getMaxNumber() > 0
								&& numberPrice > order.getMaxNumber()) {
							return ResponseData.error("输入的购买金额不能大于" + order.getMaxNumber());
						}
						number = new BigDecimal(numberPrice)
								.divide(new BigDecimal(order.getPrice()), 2, BigDecimal.ROUND_HALF_UP)
								.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					} else {// 按数量
						number = numberPrice;
						numberPrice = number * order.getPrice();
						if (order.getMinNumber() != null && order.getMinNumber() > 0
								&& numberPrice < order.getMinNumber()) {
							return ResponseData.error("输入的购买金额不能小于" + order.getMinNumber());
						}
						if (order.getMaxNumber() != null && order.getMaxNumber() > 0
								&& numberPrice > order.getMaxNumber()) {
							return ResponseData.error("输入的购买金额不能大于" + order.getMaxNumber());
						}
					}
					if (order.getSupNumber() < number) {
						return ResponseData.error("数量不足");
					}
					
					
					OtcpOrder otcpOrder = new OtcpOrder();
					int remark = (int) ((Math.random() * 9 + 1) * 100000);
					otcpOrder.setRemark(remark + "");
					otcpOrder.setSerialno(otcOrderService.generateSimpleSerialno(seller.getSellerId(), 1));
					otcpOrder.setBuyerId(seller.getSellerId());
					if (order.getType().equals(3) || order.getType().equals(4)) {
						otcpOrder.setSellerId(order.getUserId());
						User user = this.userMapper.selectById(order.getUserId());
						otcpOrder.setSellerPhone(user.getAccountCode());
					} else {
						otcpOrder.setSellerId(order.getSellerId());
						Seller sell = this.sellerDao.findSellerbyId(order.getSellerId());
						otcpOrder.setSellerPhone(sell.getAccount());
					}
					otcpOrder.setPrice(order.getPrice());
					otcpOrder.setNumber(number);
					otcpOrder.setTotalPrice(numberPrice);
					otcpOrder.setStatus(1);
					otcpOrder.setCreateTime(new Date());
					otcpOrder.setIsAppeal(0);
					if (type == 1) {
						otcpOrder.setType(1);
					} else if (type == 2) {
						otcpOrder.setType(2);
					}
					otcpOrder.setOtcOrderId(order.getOrderId());
					otcpOrder.setBuySellType(1);
					otcpOrder.setPayMethodIds(order.getPayMethodIds());
					otcpOrder.setSymbols(order.getSymbols());
					otcOrderService.addOtcpOrder(otcpOrder);
					
					logger.info("购买HC前，订单号:"+otcpOrder.getSerialno()+",购买数量："+number+",出售订单【"+order.getSerialno()+"】剩余数量："+order.getSupNumber());
					
					order.setUpdateTime(new Date());
					Double supNumber = order.getSupNumber() - number;
					if (supNumber <= 0) {
						order.setStatus(2);
					}
					order.setSupNumber(supNumber);
					int updateResult = otcOrderService.updateSellOtcpOrder(order);
					if(updateResult <=0) {
						logger.info("购买HC出现异常");
						throw new WallterException("下单失败");
					}
					
					logger.info("购买HC后，订单号:"+otcpOrder.getSerialno()+",购买数量："+number+",出售订单【"+order.getSerialno()+"】剩余数量："+order.getSupNumber());
					OtcpCannelNumberSetting setting = this.sellerDao.findOtcpCannelNumberSetting();
					//可用购买otc发送给买家短信通知
					String content ="【"+order.getSymbols()+"】订单尾号："+otcpOrder.getSerialno()+"，将在15分钟后超时取消。如您已向卖方付款，请前往订单详情点击“我已付款”，否则可能导致您的资产损失。HC";

					if (setting != null && setting.getMinTime() >0){
						content ="【"+order.getSymbols()+"】订单尾号："+otcpOrder.getSerialno()+"，将在"+setting.getMinTime()+"分钟后超时取消。如您已向卖方付款，请前往订单详情点击“我已付款”，否则可能导致您的资产损失。HC";
					}
					if(StringUtils.isNotBlank(seller.getPhone())) {
						SellerNotice notice = new SellerNotice();
						notice.setIsSee(0);
						notice.setContent(content);
						notice.setCreateTime(new Date());
						notice.setSellerId(seller.getSellerId());
						this.sellerDao.addSellerNotice(notice);
					}
					
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("number", supNumber);
					map.put("id", otcpOrder.getOrderId());
					return ResponseData.success(200, "下单成功", map);
				}
			}
		}
		return ResponseData.error("订单已撤销，下单失败");
		
	}

	public ResponseData buyOtcList(Integer pageSize, Integer pageNumber, Integer type, String token, Integer status,Integer tradeType,String time) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			int total = 0;
			List<Map<String, Object>> list = new ArrayList<>();
			if (seller != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
				params.put("tradeType",tradeType);
				params.put("time",time);
				if (status == 1) {
					params.put("status", 1);
				} else if (status == 2) {
					params.put("status", 2);
				} else if (status == 3) {
					params.put("status", 4);
				} else if (status == 4) {
					params.put("status", 7);
				} else if (status == 5) {
					params.put("status", 6);
				}
				if (type == 1) {
					params.put("type", 1);
				} else {
					params.put("type", 2);
				}
				Query query = new Query(params);
				List<OtcpOrder> otcpList = sellerDao.getOtcpOrderList(query);
				for (OtcpOrder order : otcpList) {
					Map<String, Object> map = new HashMap<>();
					if (order != null) {
						map.put("id", order.getOrderId());
						map.put("totalCnyPrice", order.getTotalPrice());
						map.put("status", order.getStatus());// 1:表示未支付，2：表示支付成功，等待确认到账，3：卖家确认，待买家确认，4：已完成，5：已超时，6申诉中，7：取消
						map.put("createTime", order.getCreateTime().getTime() / 1000);
//						if (order.getBuyerId().equals(seller.getSellerId())){
//							map.put("buySellType", 1);
//						}
//						if (order.getSellerId().equals(seller.getSellerId())){
//							map.put("buySellType", 2);
//						}
						SellOtcpOrder otcpOrder = otcOrderService.findSellerOtcpOrderById(order.getOtcOrderId());
						if (otcpOrder.getType() == 1 || otcpOrder.getType() == 2) {
							if (!order.getSellerId().equals(seller.getSellerId())) {
								map.put("buySellType", 1);
							} else {
								map.put("buySellType", 2);
							}
						} else if (otcpOrder.getType().equals(3) ||otcpOrder.getType().equals(4) ) {
							map.put("buySellType", 1);
						}
					}
					list.add(map);
				}
				total = sellerDao.getOtcpOrderListCount(query);
				PageUtils pageUtils = new PageUtils(total, list);
				return ResponseData.success(pageUtils);
			}
		}
		return ResponseData.success(null);
	}

	public ResponseData buyOtcDetail(String token, Long id) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				OtcpOrder order = otcOrderService.findOtcOrderById(id);
				if (order != null) {
					Map<String, Object> map = new HashMap<>();
					map.put("seller", null);// 卖家
					map.put("sellerPhone", null);// 卖家电话
					map.put("buyer", null);// 买家
					map.put("oId", order.getOrderId());// 订单id
					map.put("price", order.getPrice());// 单价
					map.put("remark", order.getRemark());// 参考号
					map.put("number", order.getNumber());// 数量
					map.put("totalPrice", order.getTotalPrice());// 总价格
					map.put("serialno", order.getSerialno());// 订单号
					map.put("payCertificate",order.getPayCertificate());//付款凭证
					map.put("createTime",
							order.getCreateTime() == null ? null : order.getCreateTime().getTime() / 1000);// 下单时间
					map.put("status", order.getStatus());// 状态：1:表示未支付，2：表示支付成功，等待确认到账，3：卖家确认，待买家确认，4：已完成，5：已超时，6申诉中，7：取消
					map.put("cannelTime",
							order.getCannelTime() == null ? null : order.getCannelTime().getTime() / 1000);// 取消时间
					if (order.getStatus() == 6) {
						map.put("appealTime",
								order.getAppealTime() == null ? null : order.getAppealTime().getTime() / 1000);// 申诉时间
					}
					map.put("updateTime",
							order.getUpdateTime() == null ? null : order.getUpdateTime().getTime() / 1000); // 更新时间
					map.put("certificate", order.getCertificate());//申诉 凭证
					map.put("appealContent", order.getAppealContent());//申诉内容
					if (order.getBuySellType() == 1) {// 购买
						if (order.getType() == 1) {
							Seller buyer = sellerDao.findSellerbyId(order.getBuyerId());
							map.put("buyer",buyer.getAccount());// 买家
							map.put("icon", buyer.getIcon());
						} else {
							Seller buyer = sellerDao.findSellerbyId(order.getBuyerId());
							map.put("buyer", buyer.getAccount());// 买家
							map.put("icon", buyer.getIcon());
						}
						SellOtcpOrder sellOtcpOrder = otcOrderService.findSellerOtcpOrderById(order.getOtcOrderId());
						//出售订单会员或者承兑商
						if (sellOtcpOrder.getType() == 2 || sellOtcpOrder.getType() == 1) {
							if (!order.getSellerId().equals(seller.getSellerId())) {//卖家不是自己，就是买家
								map.put("buySellType", 1);
							} else {
								map.put("buySellType", 2);
							}
							// 卖家
							Seller sell = sellerDao.findSellerbyId(order.getSellerId());
							map.put("seller",sell.getNickName());// 卖家
							map.put("sellerPhone", sell.getPhone());// 卖家电话
							if (order.getStatus() == 1) {
								List<Map<String, Object>> paymethodList = new ArrayList<>();
								JSONArray jsonArray = JSONArray.parseArray(order.getPayMethodIds().toString());
								for (Object object : jsonArray) {
									JSONObject jsonObject = JSONObject.parseObject(object.toString());
									if (jsonObject.containsKey("id")) {
										SellerPayMethod payMethod = sellerDao
												.findSellerPayMethodById(jsonObject.getLong("id"));
										if (payMethod != null) {
											Map<String, Object> payMap = new HashMap<>();
											payMap.put("id", payMethod.getPayMethodId());
											payMap.put("type", payMethod.getType());// 支付类型
											payMap.put("account", payMethod.getAccount());// 账号
											payMap.put("qrcode", payMethod.getQrCode());// 二维码
											payMap.put("name", payMethod.getName());
											payMap.put("cardBank", payMethod.getCardBank());// 开户行
											payMap.put("cardBankName", payMethod.getCardBankName());// 银行名称
											paymethodList.add(payMap);
										}
									}
								}
								map.put("paymethod", paymethodList);// 付款方式
							} else {
								List<Map<String, Object>> paymethodList = new ArrayList<>();
								SellerPayMethod payMethod = sellerDao.findSellerPayMethodById(order.getPayMethodId());
								if (payMethod != null) {
									Map<String, Object> payMap = new HashMap<>();
									payMap.put("type", payMethod.getType());// 支付类型
									payMap.put("account", payMethod.getAccount());// 账号
									payMap.put("qrcode", payMethod.getQrCode());// 二维码
									payMap.put("name", payMethod.getName());
									payMap.put("cardBank", payMethod.getCardBank());// 开户行
									payMap.put("cardBankName", payMethod.getCardBankName());// 银行名称
									paymethodList.add(payMap);
								}
								map.put("paymethod", paymethodList);// 付款方式
							}
						} else {
							//出售的订单是代理商，或者商户
							map.put("buySellType", 1);
							//卖家必为商户或代理，use
							User user = new User();
							user.setUserId(sellOtcpOrder.getUserId());
							user = sellerDao.findUserOne(user);
							map.put("seller",user.getAccountCode());// 卖家
							map.put("sellerPhone",user.getPhone());// 卖家电话
							List<Map<String, Object>> paymethodList = new ArrayList<>();// 付款方式
							if (order.getStatus() == 1) {
								JSONArray jsonArray = JSONArray.parseArray(order.getPayMethodIds().toString());
								for (Object object : jsonArray) {
									JSONObject jsonObject = JSONObject.parseObject(object.toString());
									if (jsonObject.containsKey("id")) {
										UserPayMethod payMethod = sellerDao.findUserPayMethod(jsonObject.getLong("id"));
										if (payMethod != null) {
											Map<String, Object> payMap = new HashMap<>();
											payMap.put("id", payMethod.getPayMethodId());
											payMap.put("type", payMethod.getType());// 支付类型
											payMap.put("account", payMethod.getAccount());// 账号
											payMap.put("qrcode", payMethod.getQrCode());// 二维码
											payMap.put("name", payMethod.getName());
											payMap.put("cardBank", payMethod.getCardBank());// 开户行
											payMap.put("cardBankName", payMethod.getCardBankName());// 银行名称
											paymethodList.add(payMap);
										}
									}
								}
							} else {
								UserPayMethod payMethod = sellerDao.findUserPayMethod(order.getPayMethodId());
								if (payMethod != null) {
									Map<String, Object> payMap = new HashMap<>();
									payMap.put("id", payMethod.getPayMethodId());
									payMap.put("type", payMethod.getType());// 支付类型
									payMap.put("account", payMethod.getAccount());// 账号
									payMap.put("qrcode", payMethod.getQrCode());// 二维码
									payMap.put("name", payMethod.getName());
									payMap.put("cardBank", payMethod.getCardBank());// 开户行
									payMap.put("cardBankName", payMethod.getCardBankName());// 银行名称
									paymethodList.add(payMap);
								}
							}
							map.put("paymethod", paymethodList);// 付款方式

						}

					}
					return ResponseData.success(map);
				}
			}
		}
		return ResponseData.error("获取失败");
	}


	@Transactional
	public ResponseData cannelOtcOrder(String token, Long id) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				OtcpOrder otcpOrder = otcOrderService.findOtcOrderById(id);
				//订单未付款取消或订单点击了我已付款但未真实付款进行取消
				if (otcpOrder != null && (otcpOrder.getStatus().equals(1) || otcpOrder.getStatus().equals(2))) {
					SellOtcpOrder sellOtcpOrder = otcOrderService.findSellerOtcpOrderById(otcpOrder.getOtcOrderId());
					if (sellOtcpOrder != null) {
						logger.info("出售" + sellOtcpOrder.getSymbols() + "，取消交易订单，订单号【" + otcpOrder.getSerialno() + "】取消交易，会员【"
								+ sellOtcpOrder.getSellerId() + "出售剩余数量：" + sellOtcpOrder.getSupNumber());
						//判断出售的订单还在进行中
						if (sellOtcpOrder.getStatus() < 3) {//订单还在进行中
							sellOtcpOrder.setSupNumber(sellOtcpOrder.getSupNumber() + otcpOrder.getNumber());
							sellOtcpOrder.setStatus(1);
							sellOtcpOrder.setUpdateTime(new Date());
							int sellerResult = otcOrderService.updateSellOtcpOrder(sellOtcpOrder);
							if (sellerResult <= 0) {
								throw new WallterException("取消失败");
							}
						} else if (sellOtcpOrder.getStatus().equals(3) && sellOtcpOrder.getType() < 3) {
							//出售的订单已被取消，退回会员或承兑商的法币钱包里面
							//校验是否添加，如果添加，超时不添加
							Object isAdd = redisUtil.get("TIMEOUT:" + otcpOrder.getSerialno());
							if (isAdd == null) {
								redisUtil.set("TIMEOUT:" + otcpOrder.getSerialno(), "Y");
								SellerWallter userWallter = new SellerWallter();
								userWallter.setType(3);
								userWallter.setSellerId(sellOtcpOrder.getSellerId());
								userWallter.setCode(sellOtcpOrder.getSymbols());
								List<SellerWallter> buyerList = sellerDao.findSellerWallter(userWallter);
								userWallter = buyerList.get(0);
								if (userWallter.getFrozenBalance() < otcpOrder.getNumber()) {
									throw new WallterException("订单异常");
								}
								logger.info("出售" + sellOtcpOrder.getSymbols() + "，取消交易订单，订单号【" + otcpOrder.getSerialno() + "】退款前，会员/承兑商【"
										+ userWallter.getSellerId() + "】，可用余额:" + userWallter.getAvailableBalance() + ",冻结余额:" + userWallter.getFrozenBalance() + ",订单数量：" + otcpOrder.getNumber());

								//Double frozen = userWallter.getFrozenBalance();
								Double available = userWallter.getAvailableBalance();
								userWallter.setFrozenBalance(userWallter.getFrozenBalance() - otcpOrder.getNumber());
								userWallter.setAvailableBalance(userWallter.getAvailableBalance() + otcpOrder.getNumber());
								userWallter.setUpdateTime(new Date());
								int result = sellerDao.updateSellerWallter(userWallter);
								if (result <= 0) {
									throw new WallterException("取消失败");
								}
								//保存冻结的账变记录
								AccountUpdateRecord updateRecord = new AccountUpdateRecord();
								updateRecord.setBeforePrice(available);
								updateRecord.setAfterPrice(userWallter.getAvailableBalance());
								updateRecord.setCode(sellOtcpOrder.getSymbols());
								updateRecord.setCreateTime(new Date());
								updateRecord.setPhone(seller.getAccount());
								updateRecord.setSource("法币-" + sellOtcpOrder.getSymbols());
								updateRecord.setRemark("出售取消交易");
								if (sellOtcpOrder.getType().equals(1)) {
									updateRecord.setRoleId(1L);
								} else {
									updateRecord.setRoleId(3L);
								}
								if ("USDT".equals(sellOtcpOrder.getSymbols())) {
									updateRecord.setType(AccountUpdateType.CHANNEL_OTC_USDT.code());
									updateRecord.setWalletCode(AccountUpdateWallet.OTC_WALLET_USDT.code());
								} else {
									updateRecord.setType(AccountUpdateType.CHANNEL_OTC_HC.code());
									updateRecord.setWalletCode(AccountUpdateWallet.OTC_WALLET_HC.code());
								}
								updateRecord.setAccountId(userWallter.getSellerId());
								updateRecord.setPrice(otcpOrder.getNumber());
								updateRecord.setSerialno(otcpOrder.getSerialno());
								sellerDao.addAccountUpdateRecord(updateRecord);



								logger.info("出售" + sellOtcpOrder.getSymbols() + "，取消交易订单，订单号【" + otcpOrder.getSerialno() + "】退款后，会员/承兑商【"
										+ userWallter.getSellerId() + "】，可用余额:" + userWallter.getAvailableBalance() + ",冻结余额:" + userWallter.getFrozenBalance() + ",订单数量：" + otcpOrder.getNumber());
							}
						} else if (sellOtcpOrder.getStatus().equals(3) && sellOtcpOrder.getType() >= 3) {
							//退回商户银行卡钱包里面，或者退回代理商银行卡钱包里面
							//校验是否添加，如果添加，超时不添加
							Object isAdd = redisUtil.get("TIMEOUT:" + otcpOrder.getSerialno());
							if (isAdd == null) {
								redisUtil.set("TIMEOUT:" + otcpOrder.getSerialno(), "Y");
								UserWallter userWallter = new UserWallter();
								if ("USDT".equals(sellOtcpOrder.getSymbols())) {
									userWallter.setType(1);//usdt
								} else {
									userWallter.setType(2);//hc
								}
								userWallter.setUserId(sellOtcpOrder.getUserId());
								userWallter.setChannelType(Integer.valueOf(sellOtcpOrder.getPayMethodType()));
								List<UserWallter> buyerList = sellerDao.findUserWallterList(userWallter);
								userWallter = buyerList.get(0);
								if (userWallter.getFrozenBalance() < otcpOrder.getNumber()) {
									throw new WallterException("订单异常");
								}
								logger.info("出售" + otcpOrder.getSymbols() + "，取消交易订单，订单号【" + otcpOrder.getSerialno() + "】退款前，商户/代理商【"
										+ userWallter.getUserId() + "】，可用余额:" + userWallter.getAvailableBalance() + ",冻结余额:" + userWallter.getFrozenBalance() + ",订单数量：" + otcpOrder.getNumber());

								//Double userFrozen = userWallter.getFrozenBalance();
								Double userAvailable = userWallter.getAvailableBalance();
								userWallter.setFrozenBalance(userWallter.getFrozenBalance() - otcpOrder.getNumber());
								userWallter.setAvailableBalance(userWallter.getAvailableBalance() + otcpOrder.getNumber());
								userWallter.setUpdateTime(new Date());
								int result = sellerDao.updateUserWallter(userWallter);
								if (result <= 0) {
									throw new WallterException("取消失败");
								}

								//保存商户或代理商的账变记录
								AccountUpdateRecord updateRecord = new AccountUpdateRecord();
								updateRecord.setBeforePrice(userAvailable);
								updateRecord.setAfterPrice(userWallter.getAvailableBalance());
								updateRecord.setCode(sellOtcpOrder.getSymbols());
								updateRecord.setCreateTime(new Date());
								updateRecord.setPhone(seller.getAccount());
								if (sellOtcpOrder.getType().equals(3)) {
									updateRecord.setRoleId(3L);
								} else {
									updateRecord.setRoleId(4L);
								}
								if ("USDT".equals(sellOtcpOrder.getSymbols())) {
									updateRecord.setType(AccountUpdateType.CHANNEL_OTC_USDT.code());
									updateRecord.setWalletCode(AccountUpdateWallet.OTC_WALLET_USDT.code());
								} else {
									updateRecord.setType(AccountUpdateType.CHANNEL_OTC_HC.code());
									updateRecord.setWalletCode(AccountUpdateWallet.OTC_WALLET_HC.code());
								}
								updateRecord.setPrice(otcpOrder.getNumber());
								updateRecord.setSerialno(otcpOrder.getSerialno());
								sellerDao.addAccountUpdateRecord(updateRecord);

								//保存商户或代理商的流水记录
								UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
								flowRecord.setCode(otcpOrder.getSymbols());
								flowRecord.setCreateTime(new Date());
								flowRecord.setPrice(otcpOrder.getNumber());
								flowRecord.setUserId(userWallter.getUserId());
								flowRecord.setChannelType(Integer.valueOf(sellOtcpOrder.getPayMethodType()));
								flowRecord.setSerialno(otcpOrder.getSerialno());
								flowRecord.setRemark("出售"+otcpOrder.getSymbols()+"被取消，取消数量："+otcpOrder.getNumber());
								flowRecord.setSource("挂单出售"+otcpOrder.getSymbols());
								flowRecord.setCreateTime(new Date());
								sellerDao.addUserAccountFlowRecord(flowRecord);

								logger.info("出售" + sellOtcpOrder.getSymbols() + "，取消交易订单，订单号【" + otcpOrder.getSerialno() + "】退款后，商户/代理商【"
										+ userWallter.getUserId() + "】，可用余额:" + userWallter.getAvailableBalance() + ",冻结余额:" + userWallter.getFrozenBalance() + ",订单数量：" + otcpOrder.getNumber());
							}

							logger.info("出售" + otcpOrder.getSymbols() + "，更新后，取消交易订单，订单号【" + otcpOrder.getSerialno() + "】取消交易，会员【"
									+ sellOtcpOrder.getSellerId() + "出售剩余数量：" + sellOtcpOrder.getSupNumber());

							OtcpOrderCannelNumberRecord record = otcOrderService
									.findOtcpOrderCannelBySellerIdToday(seller.getSellerId());
							if (record == null) {
								record = new OtcpOrderCannelNumberRecord();
								record.setCreateTime(new Date());
								record.setNumber(1);
								record.setSellerId(seller.getSellerId());
								record.setUpdateTime(new Date());
								otcOrderService.addOtcpOrderCannelNumberRecord(record);
							} else {
								record.setNumber(record.getNumber() + 1);
								record.setUpdateTime(new Date());
								otcOrderService.updateOtcpOrderCannelNumberRecord(record);
							}
							//更新订单
							otcpOrder.setUpdateTime(new Date());
							otcpOrder.setCloseTime(new Date());
							otcpOrder.setStatus(7);
							otcOrderService.updateOtcpOrder(otcpOrder);

							return ResponseData.success(200, "取消成功", null);
						}
					}
				}
			}
		}
		return ResponseData.error("取消交易失败");
	}

	@Transactional
	public ResponseData alreayPay(String token, Long id, Long payMethodId,String credentials) {
		if (id == null || id <=0){
			return ResponseData.error("参数有误");
		}
		if (StringUtils.isBlank(credentials)){
			return ResponseData.error("请上传凭证");
		}
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				OtcpOrder otcpOrder = otcOrderService.findOtcOrderById(id);
				//判断购买的订单是否自己的，订单状态是否还未付款状态
				if (otcpOrder != null && otcpOrder.getStatus() == 1 && seller.getSellerId().equals(otcpOrder.getBuyerId())) {
					if (otcpOrder.getType() == 1) {//会员订单
						SellerPayMethod payMethod = sellerDao.findSellerPayMethodById(payMethodId);
						if (payMethod != null) {
							otcpOrder.setStatus(2);
							otcpOrder.setPayMethodId(payMethodId);//收款方式id
							otcpOrder.setUpdateTime(new Date());
							otcpOrder.setPayCertificate(credentials);//付款凭证
							otcpOrder.setPayMethodType(payMethod.getType());//收款方式类型
							otcOrderService.updateOtcpOrder(otcpOrder);
							//卖家
							Seller sell = this.sellerDao.findSellerbyId(otcpOrder.getSellerId());
							if(sell != null) {
								//买家确认我已支付，发送短信通知给卖家
								String content ="【"+otcpOrder.getSymbols()+"】买家"+seller.getAccount()+"将订单标记为“已付款”状态，请登录收款账户确认收到此笔转账后，完成放行。HC";
								SellerNotice notice = new SellerNotice();
								notice.setIsSee(0);
								notice.setContent(content);
								notice.setCreateTime(new Date());
								notice.setSellerId(sell.getSellerId());
								this.sellerDao.addSellerNotice(notice);
							}	
							return ResponseData.success(200, "确认付款成功，等待商家审核", null);
						}
					} else if (otcpOrder.getType() == 2) {//承兑商订单
						SellOtcpOrder sellOtcpOrder = otcOrderService
								.findSellerOtcpOrderById(otcpOrder.getOtcOrderId());
						if (sellOtcpOrder.getType() == 1) {//会员出售的订单
							SellerPayMethod payMethod = sellerDao.findSellerPayMethodById(payMethodId);
							if (payMethod != null) {
								otcpOrder.setStatus(2);
								otcpOrder.setPayMethodId(payMethodId);//付款方式id
								otcpOrder.setUpdateTime(new Date());
								otcpOrder.setPayMethodType(payMethod.getType());//收款方式类型
								otcpOrder.setPayCertificate(credentials);
								//更新订单信息
								otcOrderService.updateOtcpOrder(otcpOrder);

								//买家确认我已支付，发送短信通知给卖家
								Seller sell = this.sellerDao.findSellerbyId(sellOtcpOrder.getSellerId());
								if(sell != null) {//买家确认我已支付，发送短信通知给卖家
									String content ="【"+otcpOrder.getSymbols()+"】买家"+seller.getNickName()+"将订单标记为“已付款”状态，请登录收款账户确认收到此笔转账后，完成放行。HC";
									SellerNotice notice = new SellerNotice();
									notice.setIsSee(0);
									notice.setContent(content);
									notice.setCreateTime(new Date());
									notice.setSellerId(sell.getSellerId());
									this.sellerDao.addSellerNotice(notice);
								}
								return ResponseData.success(200, "确认付款成功，等待商家审核", null);
							}
						} else if (sellOtcpOrder.getType().equals(3) || sellOtcpOrder.getType().equals(4)) {
							//出售订单是商户或者代理商
							UserPayMethod payMethod = sellerDao.findUserPayMethod(payMethodId);
							if (payMethod != null) {
								otcpOrder.setStatus(2);
								otcpOrder.setPayMethodId(payMethodId);
								otcpOrder.setUpdateTime(new Date());
								otcpOrder.setPayMethodType(payMethod.getType());
								otcpOrder.setPayCertificate(credentials);
								otcOrderService.updateOtcpOrder(otcpOrder);
								return ResponseData.success(200, "确认付款成功，等待商家审核", null);
							}
						}
					}
				}else if(otcpOrder != null && otcpOrder.getStatus().equals(7)) {
					return ResponseData.error(200, "卖家已取消该交易，请重新购买", null);
				}
			}
		}
		return ResponseData.error("确认付款失败");
	}

	@Transactional
	public ResponseData ConfirmPayOtcp(String token, Long id) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				OtcpOrder otcpOrder = otcOrderService.findOtcOrderById(id);
				if (otcpOrder != null && otcpOrder.getSellerId().equals(seller.getSellerId())) {
					//判断该订单是否自己出售的，并且订单的状态是否已付款
					if (otcpOrder.getStatus() == 2 && otcpOrder.getSellerId().equals(seller.getSellerId())) {
						//会员订单，承兑商出售
						if (otcpOrder.getType() == 1) {
							// 买家,会员账户
							Seller buySeller = sellerDao.findSellerbyId(otcpOrder.getBuyerId());
							if (buySeller == null) {
								throw new WallterException("确认收款失败");
							}
							//买家的法币钱包
							SellerWallter buyerWallter = new SellerWallter();
							buyerWallter.setCode(otcpOrder.getSymbols());
							buyerWallter.setType(3);
							buyerWallter.setSellerId(otcpOrder.getBuyerId());
							Seller buyer = sellerDao.findSellerbyId(otcpOrder.getBuyerId());
							List<SellerWallter> buyerList = sellerDao.findSellerWallter(buyerWallter);
							buyerWallter = buyerList.get(0);
							logger.info("出售"+buyerWallter.getCode()+"，确认收款，订单号【"+otcpOrder.getSerialno()+"】,买家更改前冻结余额:"+buyerWallter.getFrozenBalance()+",可用余额为"+buyerWallter.getAvailableBalance());

							//变动前的可用余额
							Double buyerAvailable = buyerWallter.getAvailableBalance();

							buyerWallter
									.setAvailableBalance(buyerWallter.getAvailableBalance() + otcpOrder.getNumber());
							buyerWallter.setUpdateTime(new Date());
							int result = sellerDao.updateSellerWallter(buyerWallter);
							if (result <= 0) {
								throw new WallterException("确认收款失败");
							}

							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(buyerAvailable);
							updateRecord.setAfterPrice(buyerWallter.getAvailableBalance());
							updateRecord.setCode(buyerWallter.getCode());
							updateRecord.setCreateTime(new Date());
							updateRecord.setAccountId(buyer.getSellerId());
							updateRecord.setSerialno(otcpOrder.getSerialno());
							updateRecord.setPhone(buyer.getAccount());
							updateRecord.setSource("法币-"+otcpOrder.getSymbols());
							if("USDT".equals(otcpOrder.getSymbols())){
								updateRecord.setType(AccountUpdateType.BUY_OTC_USDT.code());

							}else{
								updateRecord.setType(AccountUpdateType.BUY_OTC_HC.code());
							}
							updateRecord.setRemark("购买"+otcpOrder.getSymbols());
							updateRecord.setPrice(otcpOrder.getNumber());
							updateRecord.setRoleId(1L);
							sellerDao.addAccountUpdateRecord(updateRecord);

							//通知买家到账信息
							SellerNotice notice = new SellerNotice();
							notice.setSellerId(buyer.getSellerId());
							notice.setCreateTime(new Date());
							notice.setIsSee(0);
							notice.setContent("您购买的"+otcpOrder.getSymbols()+"订单号【"+otcpOrder.getSerialno()+"】，数量为"+otcpOrder.getNumber()+",已确认收款完成");
							sellerDao.addSellerNotice(notice);
							
							logger.info("出售"+otcpOrder.getSymbols()+"，确认收款，订单号【"+otcpOrder.getSerialno()+"】,买家更改后冻结余额:"+buyerWallter.getFrozenBalance()+",可用余额为"+buyerWallter.getAvailableBalance());

							//买家流水记录
							SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
							flowRecord.setCode(otcpOrder.getSymbols());
							flowRecord.setWalletType(3);
							flowRecord.setCreateTime(new Date());
							flowRecord.setPrice(otcpOrder.getNumber());
							flowRecord.setSellerId(otcpOrder.getBuyerId());
							flowRecord.setSerialno(otcpOrder.getSerialno());
							flowRecord.setRemark("购买"+otcpOrder.getSymbols()+"增加可用额度："+otcpOrder.getNumber());
							flowRecord.setSource("购买"+otcpOrder.getSymbols());
							if("USDT".equals(otcpOrder.getSymbols())){
								flowRecord.setType(FlowRecordConstant.BUY_USDT_COIN);
							}else{
								flowRecord.setType(FlowRecordConstant.BUY_HC_COIN);
							}
							sellerDao.addSellerAccountFlowRecord(flowRecord);
							
							// 卖家,承兑商
							Seller sell = sellerDao.findSellerbyId(otcpOrder.getSellerId());
							SellerWallter userWallter = new SellerWallter();
							userWallter.setCode(otcpOrder.getSymbols());
							userWallter.setType(3);
							userWallter.setSellerId(otcpOrder.getSellerId());
							List<SellerWallter> userWallterList = sellerDao.findSellerWallter(userWallter);
							userWallter = userWallterList.get(0);

							logger.info("出售"+otcpOrder.getSymbols()+"，确认收款，订单号【"+otcpOrder.getSerialno()+"】,卖家更改前冻结余额:"+userWallter.getFrozenBalance()+",可用余额："+userWallter.getAvailableBalance());


							if(userWallter.getFrozenBalance() <0 || userWallter.getFrozenBalance() - otcpOrder.getNumber()<0) {
								throw new WallterException("确认收款失败");
							}

							Double sellFrozen = userWallter.getFrozenBalance();

							userWallter.setFrozenBalance(userWallter.getFrozenBalance() - otcpOrder.getNumber());
							userWallter.setUpdateTime(new Date());
							result = sellerDao.updateSellerWallter(userWallter);
							if (result <= 0) {
								throw new WallterException("确认收款失败");
							}

							//卖家流水记录
							SellerAccountFlowRecord flowRecord2 = new SellerAccountFlowRecord();
							flowRecord2.setCode(otcpOrder.getSymbols());
							flowRecord2.setCreateTime(new Date());
							flowRecord2.setPrice(-otcpOrder.getNumber());
							flowRecord2.setSellerId(seller.getSellerId());
							flowRecord2.setSerialno(otcpOrder.getSerialno());
							flowRecord2.setRemark("出售"+otcpOrder.getSymbols()+",出售数量："+otcpOrder.getNumber());
							flowRecord2.setSource("出售已完成");
							flowRecord2.setWalletType(3);
							flowRecord2.setSellerId(userWallter.getSellerId());
							if("USDT".equals(otcpOrder.getSymbols())){
								flowRecord2.setType(FlowRecordConstant.SELL_USDT_COIN_SUCCESS);
							}else{
								flowRecord2.setType(FlowRecordConstant.SELL_HC_COIN_SUCCESS);
							}
							sellerDao.addSellerAccountFlowRecord(flowRecord2);

							//卖家账变记录
							AccountUpdateRecord updateRecord2 = new AccountUpdateRecord();
							updateRecord2.setBeforePrice(sellFrozen);
							updateRecord2.setAfterPrice(userWallter.getFrozenBalance());
							updateRecord2.setCode(otcpOrder.getSymbols());
							updateRecord2.setCreateTime(new Date());
							updateRecord2.setAccountId(userWallter.getSellerId());
							updateRecord2.setSerialno(otcpOrder.getSerialno());
							updateRecord2.setPhone(sell.getAccount());
							updateRecord2.setSource("法币-"+otcpOrder.getSymbols());
							if("USDT".equals(otcpOrder.getSymbols())){
								updateRecord2.setType(AccountUpdateType.SELL_OTC_USDT.code());
								updateRecord2.setRemark("出售USDT");
							}else{
								updateRecord2.setType(AccountUpdateType.SELL_OTC_HC.code());
								updateRecord2.setRemark("出售HC");
							}
							updateRecord2.setPrice(otcpOrder.getNumber());
							updateRecord2.setRoleId(3L);
							sellerDao.addAccountUpdateRecord(updateRecord2);

							//通知卖家出售的订单已完成
							SellerNotice notice1 = new SellerNotice();
							notice1.setSellerId(seller.getSellerId());
							notice1.setCreateTime(new Date());
							notice1.setIsSee(0);
							notice1.setContent("您出售的订单号【"+otcpOrder.getSerialno()+"】，数量为"+otcpOrder.getNumber()+",已确认收款完成");
							sellerDao.addSellerNotice(notice1);
							logger.info("出售"+otcpOrder.getSymbols()+"，确认收款，订单号【"+otcpOrder.getSerialno()+"】,卖家更改后冻结余额:"+userWallter.getFrozenBalance()+",可用余额："+userWallter.getAvailableBalance()+",扣除出售数量："+otcpOrder.getNumber());
							
						} else if (otcpOrder.getType() == 2) {// 承兑商区（卖家会员）
							// 卖家,会员账户
							SellerWallter buyerWallter = new SellerWallter();
							buyerWallter.setCode(otcpOrder.getSymbols());
							buyerWallter.setType(3);
							buyerWallter.setSellerId(otcpOrder.getSellerId());
							List<SellerWallter> buyerList = sellerDao.findSellerWallter(buyerWallter);
							buyerWallter = buyerList.get(0);

							logger.info("出售"+otcpOrder.getSymbols()+"，确认收款，订单号【"+otcpOrder.getSerialno()+"】,卖家更改前的冻结余额:"+buyerWallter.getFrozenBalance()+",可用余额："+buyerWallter.getAvailableBalance());

							if(buyerWallter.getFrozenBalance() <0 || buyerWallter.getFrozenBalance() - otcpOrder.getNumber() <0) {
								return ResponseData.error("确认收款失败");
							}

							//获取卖家出售的订单
							Seller sellerOtcp = sellerDao.findSellerbyId(otcpOrder.getSellerId());

							Double frozen = buyerWallter.getFrozenBalance();
							buyerWallter.setFrozenBalance(buyerWallter.getFrozenBalance() - otcpOrder.getNumber());
							buyerWallter.setUpdateTime(new Date());
							int result = sellerDao.updateSellerWallter(buyerWallter);
							if (result <= 0) {
								throw new WallterException("确认收款失败");
							}

							AccountUpdateRecord updateRecord = new AccountUpdateRecord();
							updateRecord.setBeforePrice(frozen);
							updateRecord.setAfterPrice(buyerWallter.getFrozenBalance());
							updateRecord.setCode(otcpOrder.getSymbols());
							updateRecord.setCreateTime(new Date());
							updateRecord.setPhone(sellerOtcp.getAccount());
							updateRecord.setSource("法币-"+otcpOrder.getSymbols());
							if("USDT".equals(otcpOrder.getSymbols())){
								updateRecord.setType(AccountUpdateType.SELL_OTC_USDT.code());
								updateRecord.setRemark("出售USDT");
							}else{
								updateRecord.setType(AccountUpdateType.SELL_OTC_HC.code());
								updateRecord.setRemark("出售HC");
							}
							updateRecord.setPrice(-otcpOrder.getNumber());
							updateRecord.setRoleId(1L);
							updateRecord.setSerialno(otcpOrder.getSerialno());
							updateRecord.setAccountId(buyerWallter.getSellerId());
							sellerDao.addAccountUpdateRecord(updateRecord);


							//通知卖家出售的订单已完成
							SellerNotice notice1 = new SellerNotice();
							notice1.setSellerId(otcpOrder.getSellerId());
							notice1.setCreateTime(new Date());
							notice1.setIsSee(0);
							notice1.setContent("您出售的订单号【"+otcpOrder.getSerialno()+"】，数量为"+otcpOrder.getNumber()+",已确认收款完成");
							sellerDao.addSellerNotice(notice1);

							//保存卖家的流水记录
							SellerAccountFlowRecord flowRecord2 = new SellerAccountFlowRecord();
							flowRecord2.setCode(otcpOrder.getSymbols());
							flowRecord2.setWalletType(3);
							flowRecord2.setCreateTime(new Date());
							flowRecord2.setPrice(-otcpOrder.getNumber());
							flowRecord2.setSellerId(seller.getSellerId());
							flowRecord2.setRemark("出售"+otcpOrder.getSymbols()+"已完成："+otcpOrder.getNumber());
							flowRecord2.setSerialno(otcpOrder.getSerialno());
							flowRecord2.setSource("出售已完成");
							if("USDT".equals(otcpOrder.getSymbols())){
								flowRecord2.setType(FlowRecordConstant.SELL_USDT_COIN_SUCCESS);
							}else{
								flowRecord2.setType(FlowRecordConstant.SELL_HC_COIN_SUCCESS);
							}
							sellerDao.addSellerAccountFlowRecord(flowRecord2);


							logger.info("出售"+otcpOrder.getSymbols()+"，确认收款，订单号【"+otcpOrder.getSerialno()+"】,卖家更改后冻结余额:"+buyerWallter.getFrozenBalance()+",可用余额："+buyerWallter.getAvailableBalance()+",扣除出售数量："+otcpOrder.getNumber());

							// 买家,承兑商
							Seller sell = sellerDao.findSellerbyId(otcpOrder.getBuyerId());
							if (sell == null)throw new WallterException("确认收款失败");
							SellerWallter userWallter = new SellerWallter();
							userWallter.setSellerId(sell.getSellerId());
							userWallter.setType(3);
							userWallter.setCode(otcpOrder.getSymbols());
							List<SellerWallter> userWallterList = sellerDao.findSellerWallter(userWallter);
							userWallter = userWallterList.get(0);

							logger.info("出售"+otcpOrder.getSymbols()+"，确认收款，订单号【"+otcpOrder.getSerialno()+"】,买家更改前冻结余额:"+userWallter.getFrozenBalance()+",可用余额为"+userWallter.getAvailableBalance());

							Double buyFrozen = userWallter.getAvailableBalance();
							userWallter.setAvailableBalance(userWallter.getAvailableBalance() + otcpOrder.getNumber());
							userWallter.setUpdateTime(new Date());
							result = sellerDao.updateSellerWallter(userWallter);
							if (result <= 0) {
								throw new WallterException("确认收款失败");
							}

							//买家通知
							SellerNotice notice2 = new SellerNotice();
							notice2.setSellerId(otcpOrder.getBuyerId());
							notice2.setCreateTime(new Date());
							notice2.setIsSee(0);
							notice2.setContent("您出售的订单号【"+otcpOrder.getSerialno()+"】，数量为"+otcpOrder.getNumber()+",已确认收款完成");
							sellerDao.addSellerNotice(notice2);

							//保存买家账变记录
							AccountUpdateRecord updateRecord2 = new AccountUpdateRecord();
							updateRecord2.setBeforePrice(buyFrozen);
							updateRecord2.setAfterPrice(userWallter.getAvailableBalance());
							updateRecord2.setCode(otcpOrder.getSymbols());
							updateRecord2.setCreateTime(new Date());
							updateRecord2.setPhone(sell.getAccount());
							updateRecord2.setSource("法币-"+otcpOrder.getSymbols());
							updateRecord2.setRemark("购买");
							if("USDT".equals(otcpOrder.getSymbols())){
								updateRecord2.setType(AccountUpdateType.BUY_OTC_USDT.code());

							}else{
								updateRecord2.setType(AccountUpdateType.BUY_OTC_HC.code());
							}
							updateRecord2.setPrice(otcpOrder.getNumber());
							updateRecord2.setRoleId(3L);
							updateRecord2.setSerialno(otcpOrder.getSerialno());
							updateRecord2.setAccountId(userWallter.getSellerId());
							sellerDao.addAccountUpdateRecord(updateRecord2);
							
							logger.info("出售"+otcpOrder.getSymbols()+"，确认收款，订单号【"+otcpOrder.getSerialno()+"】,买家更改后冻结余额:"+userWallter.getFrozenBalance()+",可用余额为"+userWallter.getAvailableBalance());

							//保存买家流水记录
							SellerAccountFlowRecord userFlowRecord = new SellerAccountFlowRecord();
							userFlowRecord.setCode(otcpOrder.getSymbols());
							userFlowRecord.setCreateTime(new Date());
							userFlowRecord.setPrice(otcpOrder.getNumber());
							userFlowRecord.setSellerId(sell.getSellerId());
							userFlowRecord.setWalletType(3);
							userFlowRecord.setSource("购买"+otcpOrder.getSymbols());
							userFlowRecord.setSerialno(otcpOrder.getSerialno());
							userFlowRecord.setRemark("购买"+otcpOrder.getSymbols()+",获取数量："+otcpOrder.getNumber());
							if("USDT".equals(otcpOrder.getSymbols())){
								userFlowRecord.setType(FlowRecordConstant.BUY_USDT_COIN);
							}else{
								userFlowRecord.setType(FlowRecordConstant.BUY_HC_COIN);
							}
							sellerDao.addSellerAccountFlowRecord(userFlowRecord);

							// 返利承兑商的
							Double rebateValue = 0.0;


							AccepterRebateSetting rebateSetting = sellerDao.findAccepterRebateSetting(otcpOrder.getPayMethodType(),otcpOrder.getSymbols());
							if (rebateSetting != null && rebateSetting.getValue() != null
									&& rebateSetting.getValue() > 0) {

								SellerWallter profitWallter = new SellerWallter();
								profitWallter.setSellerId(sell.getSellerId());
								profitWallter.setType(4);
								profitWallter.setCode(otcpOrder.getSymbols());
								List<SellerWallter> list = sellerDao.findSellerWallter(profitWallter);
								if (list != null && list.size() > 0) {
									profitWallter = list.get(0);
									rebateValue = otcpOrder.getNumber() * rebateSetting.getValue() / 100;

									logger.info("出售HC，确认收款，订单号【"
											+otcpOrder.getSerialno()+"】,承兑商【"+sell.getSellerId()
											+"】返利前,可用余额为"+userWallter.getAvailableBalance()
											+",冻结余额："
											+userWallter.getFrozenBalance()
											+",返利余额："
											+rebateValue);

									Double profitWallteAvailable = profitWallter.getAvailableBalance();

									profitWallter
											.setAvailableBalance(profitWallter.getAvailableBalance() + rebateValue);
									profitWallter.setTotalBalance(profitWallter.getTotalBalance() + rebateValue);
									profitWallter.setUpdateTime(new Date());

									result = sellerDao.updateSellerWallter(profitWallter);
									if (result <= 0) {
										throw new WallterException("确认收款失败");
									}
									logger.info("出售HC，确认收款，订单号【"
											+otcpOrder.getSerialno()+"】,承兑商【"+sell.getSellerId()
											+"】返利后,可用余额为"+userWallter.getAvailableBalance()
											+",冻结余额："
											+userWallter.getFrozenBalance()
											+",返利余额："
											+rebateValue);

									AccountUpdateRecord updateRecord3 = new AccountUpdateRecord();
									updateRecord3.setBeforePrice(profitWallteAvailable);
									updateRecord3.setAfterPrice(profitWallter.getAvailableBalance());
									updateRecord3.setCode(otcpOrder.getSymbols());
									updateRecord3.setCreateTime(new Date());
									updateRecord3.setPhone(sell.getAccount());
									updateRecord3.setSource("承兑挖矿HC");
									updateRecord3.setType(AccountUpdateType.ACCEPTER_PROFIT.code());
									updateRecord3.setRemark("承兑挖矿");
									updateRecord3.setPrice(rebateValue);
									updateRecord3.setRoleId(1l);
									updateRecord.setWalletCode(AccountUpdateWallet.PROFIT_WALLET_HC.code());
									updateRecord3.setSerialno(otcpOrder.getSerialno());
									updateRecord3.setPayMethodType(otcpOrder.getPayMethodType());
									updateRecord3.setAccountId(profitWallter.getSellerId());
									sellerDao.addAccountUpdateRecord(updateRecord3);
									
									// 挖矿流水流水记录
									SellerProfitAccountFlowRecord profitFlowRecord = new SellerProfitAccountFlowRecord();
									profitFlowRecord.setCode(profitWallter.getCode());
									profitFlowRecord.setCreateTime(new Date());
									profitFlowRecord.setPrice(rebateValue);
									profitFlowRecord.setSellerId(sell.getSellerId());
									profitFlowRecord.setUserId(sell.getUserId());
									profitFlowRecord.setSource("购买"+otcpOrder.getSymbols()+"返利");
									profitFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
									sellerDao.addSellerProfitFlowRecord(profitFlowRecord);

								}
							}

							// 返利上级承兑商
							SuperiorAccepterRebateSetting superiorSetting = sellerDao
									.findSuperiorAccepterRebateSetting(otcpOrder.getPayMethodType(),otcpOrder.getSymbols());
							if (superiorSetting != null && superiorSetting.getValue() != null
									&& superiorSetting.getValue() > 0) {

								Seller superiaorSeller = sellerDao.findSellerbyId(sell.getReferceId());
								if (superiaorSeller != null && superiaorSeller.getIsAccepter() == 1) {

									SellerWallter superiorProfit = new SellerWallter();
									superiorProfit.setCode(otcpOrder.getSymbols());
									superiorProfit.setType(4);
									superiorProfit.setSellerId(superiaorSeller.getSellerId());
									Seller sup = sellerDao.findSellerbyId(superiaorSeller.getSellerId());
									List<SellerWallter> list = sellerDao.findSellerWallter(superiorProfit);
									if (list != null && list.size() > 0) {
										superiorProfit = list.get(0);
										rebateValue = rebateValue * superiorSetting.getValue() / 100;

										Double supAvailable = superiorProfit.getAvailableBalance();
										
										logger.info("出售HC，确认收款，订单号【"
												+otcpOrder.getSerialno()+"】,上级承兑商【"+superiaorSeller.getSellerId()
												+"】返利前,可用余额为"+superiorProfit.getAvailableBalance()
												+",冻结余额："
												+superiorProfit.getFrozenBalance()
												+",返利余额："
												+rebateValue);
										superiorProfit.setAvailableBalance(superiorProfit.getAvailableBalance() + rebateValue);
										superiorProfit.setTotalBalance(superiorProfit.getTotalBalance() + rebateValue);
										superiorProfit.setUpdateTime(new Date());
										result = sellerDao.updateSellerWallter(superiorProfit);
										if (result <= 0) {
											throw new WallterException("确认收款失败");
										}
										
										
										logger.info("出售HC，确认收款，订单号【"
												+otcpOrder.getSerialno()+"】,上级承兑商【"+superiaorSeller.getSellerId()
												+"】返利后,可用余额为"+superiorProfit.getAvailableBalance()
												+",冻结余额："
												+superiorProfit.getFrozenBalance()
												+",返利余额："
												+rebateValue);


										AccountUpdateRecord updateRecord3 = new AccountUpdateRecord();
										updateRecord3.setBeforePrice(supAvailable);
										updateRecord3.setAfterPrice(superiorProfit.getAvailableBalance());
										updateRecord3.setCode(otcpOrder.getSymbols());
										updateRecord3.setCreateTime(new Date());
										updateRecord3.setPhone(sup.getPhone());
										updateRecord3.setSource("承兑挖矿HC");
										updateRecord3.setType(AccountUpdateType.SUPERIOR_PROFIT.code());
										updateRecord3.setRemark("上级承兑返利");
										updateRecord3.setPrice(rebateValue);
										updateRecord3.setRoleId(1l);
										updateRecord.setWalletCode(AccountUpdateWallet.PROFIT_WALLET_HC.code());
										updateRecord3.setSerialno(otcpOrder.getSerialno());
										updateRecord3.setAccountId(superiorProfit.getSellerId());
										sellerDao.addAccountUpdateRecord(updateRecord3);
										
										// 流水记录
										SellerProfitAccountFlowRecord profitFlowRecord = new SellerProfitAccountFlowRecord();
										profitFlowRecord.setCode(superiorProfit.getCode());
										profitFlowRecord.setCreateTime(new Date());
										profitFlowRecord.setPrice(rebateValue);
										profitFlowRecord.setSellerId(superiaorSeller.getSellerId());
										profitFlowRecord.setUserId(superiaorSeller.getUserId());
										profitFlowRecord.setSource("购买"+otcpOrder.getSymbols()+"返利");
										profitFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
										sellerDao.addSellerProfitFlowRecord(profitFlowRecord);
									}
								}
							}
						}
						//更新订单
						otcpOrder.setStatus(4);
						otcpOrder.setUpdateTime(new Date());
						otcOrderService.updateOtcpOrder(otcpOrder);
						return ResponseData.success(200, "确认收款成功", null);

					} else if (otcpOrder.getStatus() == 6) {
						return ResponseData.error("该订单已被买家申诉");
					} else if (otcpOrder.getStatus() == 7) {
						return ResponseData.error("该订单已被取消");
					}

				}
			}
		}
		return ResponseData.error("确认收款失败");
	}

	public ResponseData submitAppealOtc(String token, Long id, String content, String certificate) {
		if (StringUtils.isBlank(content)) {
			return ResponseData.error("请输入申诉内容");
		}
		if (StringUtils.isBlank(certificate)) {
			return ResponseData.error("请上传凭证");
		}
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				OtcpOrder otcpOrder = otcOrderService.findOtcOrderById(id);
				//订单买家已付款，卖家未收到付款金额，进行申诉
				if (otcpOrder != null && otcpOrder.getStatus() == 2
						&& otcpOrder.getSellerId().equals(seller.getSellerId())) {
					// 表示卖家进行申诉
					SellOtcpOrder order = otcOrderService.findSellerOtcpOrderById(otcpOrder.getOtcOrderId());
					if (order != null) {
						if (order.getType() == 1) {
							// 会员出售，承兑商购买，会员未收到承兑商的金额，进行申诉
							otcpOrder.setAppealerRole(1);
						} else if (order.getType() == 2) {
							// 承兑商出售，会员购买，承兑商未收到会员打款的余额，进行申诉
							otcpOrder.setAppealerRole(3);
						}
						otcpOrder.setNoAppealStatus(otcpOrder.getStatus());
						otcpOrder.setAppealerId(otcpOrder.getSellerId());
						otcpOrder.setStatus(6);
						otcpOrder.setIsAppeal(1);
						otcpOrder.setAppealTime(new Date());
						otcpOrder.setAppealContent(content);
						otcpOrder.setCertificate(certificate);
						otcpOrder.setUpdateTime(new Date());
						otcOrderService.updateOtcpOrder(otcpOrder);
						return ResponseData.success(200, "提交成功，等待审核", null);
					}
				} else if (otcpOrder != null && otcpOrder.getStatus() == 2
						&& otcpOrder.getBuyerId().equals(seller.getSellerId())) {//买家已付款，但卖家十五分钟未放行，进行申诉
					if (otcpOrder.getType() == 1) {
						otcpOrder.setAppealerRole(1);
					} else {
						otcpOrder.setAppealerRole(3);
					}
					otcpOrder.setNoAppealStatus(otcpOrder.getStatus());
					otcpOrder.setAppealerId(otcpOrder.getBuyerId());
					otcpOrder.setStatus(6);
					otcpOrder.setIsAppeal(1);
					otcpOrder.setAppealTime(new Date());
					otcpOrder.setAppealContent(content);
					otcpOrder.setCertificate(certificate);
					otcpOrder.setUpdateTime(new Date());
					otcOrderService.updateOtcpOrder(otcpOrder);
					return ResponseData.success(200, "提交成功，等待审核", null);

				}
			}
		}
		return ResponseData.error("申诉失败");
	}

	public ResponseData helpList(Integer pageSize, Integer pageNumber) {
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> params = new HashMap<>();
		params.put("pageNumber", pageNumber);
		params.put("pageSize", pageSize);
		Query query = new Query(params);
		List<Help> helpList = sellerDao.getHelpList(query);
		for (Help help : helpList) {
			Map<String, Object> map = new HashMap<>();
			if (help != null) {
				map.put("id", help.getId());
				map.put("title", help.getTitle());
				map.put("createTime",help.getCreateTime());
				map.put("content",help.getContent());
			}
			list.add(map);
		}
		int total = sellerDao.getHelpListCount(query);
		PageUtils pageUtils = new PageUtils(total, list);
		return ResponseData.success(pageUtils);
	}

	public Help getHelpDetail(Long id) {
		return sellerDao.getHelpDetail(id);
	}

	public ResponseData withdrawFee(String token) {
		SellerWithdrawFeeSetting feeSetting = sellerDao.findSellerWithdrawFeeSettingOne();
		if (feeSetting != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("feeRatio", feeSetting.getFeeRatio());
			map.put("minFeeNumber", feeSetting.getMinFeeNumber());
			map.put("minNumber", feeSetting.getMinNumber());
			map.put("starNumber",feeSetting.getStartRatioNumber());
			map.put("maxNumber",feeSetting.getMaxNumber());
			return ResponseData.success(map);
		}
		return ResponseData.error("获取失败");
	}

	@Transactional
	public ResponseData sellerNumber(String token, HttpServletRequest request) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				if (seller.getIsAuth() == null || seller.getIsAuth().equals(0)) {
					return ResponseData.error("未实名认证，请先实名认证后再来");
				}
				if (seller.getIsAuth().equals(1)) {
					return ResponseData.error("等待实名认证审核通过后再进行");
				}
				if (seller.getGradEnabled() != null && seller.getGradEnabled().equals(1)){
					return ResponseData.error("您已被禁用接单");
				}
				List<SellerPayMethod> paymehtodList = sellerDao.findSellerPayMethodByIsCheck(seller.getSellerId(),
						null);
				if (paymehtodList == null || paymehtodList.size() <= 0) {
					return ResponseData.error("请去设置中勾选收款方式");
				}
				//查询判断一下是否开启接单中
				SellerOrder sellerOrder = otcOrderService.findSellerorderBySellerId(seller.getSellerId());
				if (sellerOrder != null){
					return ResponseData.error("您接单还开着，需等待一下再进行操作！！！");
				}
				SellerWallter sellerWallter = new SellerWallter();
				sellerWallter.setCode("HC");
				sellerWallter.setType(1);
				sellerWallter.setSellerId(seller.getSellerId());
				List<SellerWallter> list = sellerDao.findSellerWallter(sellerWallter);
				if (list != null && list.size() > 0) {
					sellerWallter = list.get(0);
					if (sellerWallter.getAvailableBalance() <= 0) {
						return ResponseData.error("余额不足");
					}
					Double availableBalance = sellerWallter.getAvailableBalance();

					SellerGradPriceSetting gradPriceSetting = sellerGradPriceSettingService.getOne(null);
					if (gradPriceSetting != null){
						if (gradPriceSetting.getCash() > 0 && gradPriceSetting.getCash() >sellerWallter.getAvailableBalance()){
							return ResponseData.error("余额不足，需足够"+gradPriceSetting.getCash()+"元才能开启接单");
						}
						SellerCash cash = sellerDao.selectSellerCashBySellerId(seller.getSellerId());
						if (cash != null){
							cash.setCash(gradPriceSetting.getCash());
							cash.setUpdateTime(new Date());
							sellerDao.updateSellerCash(cash);
						}else{
							cash = new SellerCash();
							cash.setCash(gradPriceSetting.getCash());
							cash.setCreateTime(new Date());
							cash.setSellerId(seller.getSellerId());
							sellerDao.addSellerCash(cash);
						}
						availableBalance=availableBalance-gradPriceSetting.getCash();
					}
					SellerOrder order = new SellerOrder();
					order.setCreateTime(new Date());
					order.setSellerId(seller.getSellerId());
					order.setNumber(availableBalance);
					order.setStatus(0);
					order.setType(1);
					order.setSerialNo(otcOrderService.generateSimpleSerialno(seller.getSellerId(), 4));
					order.setVersion(1);
					String ipAddress = IpUtil.getIpAddress(request);
					order.setIp(ipAddress);
					otcOrderService.addSellerOrder(order);
					logger.info("会员挂单HC，等待玩家匹配前，会员【"+sellerWallter.getSellerId()+"】,可用余额："+sellerWallter.getAvailableBalance()+",冻结余额："+sellerWallter.getFrozenBalance());
					
					
					Double sellerAvailable = sellerWallter.getAvailableBalance();
					sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance() + availableBalance);
					sellerWallter.setAvailableBalance(0.0);
					sellerWallter.setUpdateTime(new Date());
					int result = sellerDao.updateSellerWallter(sellerWallter);
					if (result <= 0) {
						logger.info("开始挂单出售订单有误，"+seller.getPhone());
						throw new WallterException("挂单失败");
					}
					
					logger.info("会员挂单HC，等待玩家匹配，会员【"+sellerWallter.getSellerId()+"】,可用余额："+sellerWallter.getAvailableBalance()+",冻结余额："+sellerWallter.getFrozenBalance());
					AccountUpdateRecord updateRecord = new AccountUpdateRecord();
					updateRecord.setBeforePrice(sellerAvailable);
					updateRecord.setPrice(sellerWallter.getAvailableBalance());
					updateRecord.setAfterPrice(0.0);
					updateRecord.setCode(sellerWallter.getCode());
					updateRecord.setCreateTime(new Date());
					updateRecord.setPhone(seller.getAccount());
					updateRecord.setSource("搬砖-HC");
					updateRecord.setType(AccountUpdateType.OPEN_SELL_HC.code());
					updateRecord.setWalletCode(AccountUpdateWallet.BANZHUAN_WALLET_HC.code());
					updateRecord.setSerialno(order.getSerialNo());
					updateRecord.setRemark("挂单出售");
					updateRecord.setRoleId(1L);
					updateRecord.setAccountId(seller.getSellerId());
					sellerDao.addAccountUpdateRecord(updateRecord);

					return ResponseData.success(200, "挂单成功，请等待匹配", null);
				}
			}
		}
		return ResponseData.error("挂单失败");
	}

	public ResponseData getPaymethod(String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				Map<String, Object> mapList = new HashMap<String, Object>();
				mapList.put("alipayList", null);
				mapList.put("wxList", null);
				mapList.put("cardBankList", null);
				mapList.put("alipayPriceList", null);
				mapList.put("alipayCardList", null);
//				mapList.put("cloudPayList",null);
//				mapList.put("alipayAccountList",null);
//				mapList.put("wxAccountList",null);
//				mapList.put("alipayCardList",null);
//				mapList.put("wxCardList",null);
//				mapList.put("wxZanList",null);
				List<SellerPayMethod> list = sellerDao.findsellerPayMethodBySellerId(seller.getSellerId(), null);
				if (list != null && list.size() > 0) {
					List<Map<String, Object>> aliPayList = new ArrayList<>();
					List<Map<String, Object>> wxList = new ArrayList<>();
					List<Map<String, Object>> cardBankList = new ArrayList<>();
					List<Map<String, Object>> alipayPriceList = new ArrayList<>();
					List<Map<String, Object>> alipayCardList = new ArrayList<>();
//					List<Map<String, Object>> cloudPayList = new ArrayList<Map<String, Object>>();
//					List<Map<String, Object>> alipayAccountList = new ArrayList<Map<String, Object>>();
//					List<Map<String, Object>> wxAccountList = new ArrayList<Map<String, Object>>();
//					List<Map<String, Object>> wxCardList = new ArrayList<Map<String, Object>>();
//					List<Map<String, Object>> wxZanList = new ArrayList<Map<String, Object>>();
					for (SellerPayMethod sellerPayMethod : list) {
						Map<String, Object> map = new HashMap<>();
						map.put("id", sellerPayMethod.getPayMethodId());
						map.put("name", sellerPayMethod.getName());
						map.put("account", sellerPayMethod.getAccount());
						map.put("remark", sellerPayMethod.getRemark());
						int todaySuccessNumber = sellerDao.getSuccessNumberOrderByPayMethodId(seller.getSellerId(),sellerPayMethod.getPayMethodId());
						int buyNumber = sellerDao.getTotalNumberOrderByPayMethodId(seller.getSellerId(),sellerPayMethod.getPayMethodId());
						if (buyNumber >0){
							map.put("successRatio",new BigDecimal(todaySuccessNumber).divide(new BigDecimal(buyNumber),2,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).toString());
						}else{
							map.put("successRatio",100);
						}
						map.put("successNumber",todaySuccessNumber);
						map.put("buyNumber",buyNumber);
						map.put("isCheck", sellerPayMethod.getIsCheck());
						//查询用户某个收款方式的收款金额
						Double price = sellerDao.getSellerBuyerCoinOrderTotalPriceByPayMethodId(seller.getSellerId(),sellerPayMethod.getPayMethodId());
						map.put("price",price);
						if (sellerPayMethod.getType() == 1) {
							aliPayList.add(map);
						}
						if (sellerPayMethod.getType() == 2) {
							wxList.add(map);
						}
						if (sellerPayMethod.getType() == 3) {
							cardBankList.add(map);
						}
						if (sellerPayMethod.getType().equals(4)){
							alipayPriceList.add(map);
						}
						if (sellerPayMethod.getType().equals(5)){
							alipayCardList.add(map);
						}
//						if (sellerPayMethod.getType().equals(6)){
//							wxAccountList.add(map);
//						}
//						if (sellerPayMethod.getType().equals(7)){
//							alipayCardList.add(map);
//						}
//						if (sellerPayMethod.getType().equals(8)){
//							wxCardList.add(map);
//						}
//						if (sellerPayMethod.getType().equals(9)){
//							wxZanList.add(map);
//						}

					}
					mapList.put("alipayList", aliPayList);
					mapList.put("wxList", wxList);
					mapList.put("cardBankList", cardBankList);
					mapList.put("alipayPriceList", aliPayList);
					mapList.put("alipayCardList", alipayCardList);
//					mapList.put("cloudPayList", cloudPayList);
//					mapList.put("alipayAccountList",alipayAccountList);
//					mapList.put("wxAccountList",wxAccountList);
//					mapList.put("alipayCardList",alipayCardList);
//					mapList.put("wxCardList",wxCardList);
//					mapList.put("wxZanList",wxZanList);
					return ResponseData.success(mapList);
				}
				mapList.put("alipayList", null);
				mapList.put("wxList", null);
				mapList.put("cardBankList", null);
				mapList.put("alipayPriceList", null);
				mapList.put("alipayCardList", null);
//				mapList.put("cloudPayList",null);
//				mapList.put("alipayAccountList",null);
//				mapList.put("wxAccountList",null);
//				mapList.put("alipayCardList",null);
//				mapList.put("wxCardList",null);
//				mapList.put("wxZanList",null);
				return ResponseData.success(mapList);
			}
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData updatePaymethod(String token, String payMethodList) {
		if (StringUtils.isBlank(payMethodList)) {
			return ResponseData.error("确认失败");
		}
	  SellerBuySoldOutSetting sellerBuySoldOutSetting = sellerDao.getSellerBuySoldOutSetting();
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				JSONObject jsonObject = JSONObject.parseObject(payMethodList);

				List<SellerPayMethod> list = sellerDao.findsellerPayMethodBySellerId(seller.getSellerId(), 1);
				JSONArray jsonArray = null;
				if (jsonObject.containsKey("alipayList")) {
					jsonArray = JSONArray.parseArray(jsonObject.get("alipayList").toString());
				}
				for (SellerPayMethod sellerPayMethod : list) {
					boolean flag = false;
					if (jsonArray != null) {
						for (Object object : jsonArray) {
							JSONObject payMethodJson = JSONObject.parseObject(object.toString());
							if (sellerPayMethod.getPayMethodId().equals(Long.valueOf(payMethodJson.get("id") + ""))) {
								if(sellerPayMethod.getIsSoldOut() != null && sellerPayMethod.getIsSoldOut().equals(1)){
									Boolean timeFlag = TimeUtil.judgmentDate(sellerPayMethod.getSoldOutTime(),sellerBuySoldOutSetting.getTime());
									if(timeFlag){
										flag = true;
										sellerPayMethod.setIsCheck(1);
										sellerPayMethod.setIsSoldOut(0);
									}else{
										return ResponseData.error("支付宝收款码中，该账号"+sellerPayMethod.getAccount()+"被禁用");
									}
								}else{
									flag = true;
									sellerPayMethod.setIsCheck(1);
									sellerPayMethod.setIsSoldOut(0);
								}

							}
						}
					}
					if (!flag) {
						sellerPayMethod.setIsCheck(0);
					}
					sellerPayMethod.setUpdateTime(new Date());
					sellerDao.updateSellerPayMethod(sellerPayMethod);
				}
				JSONArray jsonArray1 = null;
				if (jsonObject.containsKey("wxList")) {
					jsonArray1 = JSONArray.parseArray(jsonObject.get("wxList").toString());
				}
				list = sellerDao.findsellerPayMethodBySellerId(seller.getSellerId(), 2);
				for (SellerPayMethod sellerPayMethod : list) {
					boolean flag = false;
					if (jsonArray1 != null) {
						for (Object object : jsonArray1) {
							JSONObject payMethodJson = JSONObject.parseObject(object.toString());
							if (sellerPayMethod.getPayMethodId().equals(Long.valueOf(payMethodJson.get("id") + ""))) {

								if(sellerPayMethod.getIsSoldOut() != null && sellerPayMethod.getIsSoldOut().equals(1)){
									Boolean timeFlag = TimeUtil.judgmentDate(sellerPayMethod.getSoldOutTime(),sellerBuySoldOutSetting.getTime());
									if(timeFlag){
										flag = true;
										sellerPayMethod.setIsCheck(1);
										sellerPayMethod.setIsSoldOut(0);
									}else{
										return ResponseData.error("微信收款码中，该账号"+sellerPayMethod.getAccount()+"被禁用");
									}
								}else{
									flag = true;
									sellerPayMethod.setIsCheck(1);
									sellerPayMethod.setIsSoldOut(0);
								}
							}
						}
					}
					if (!flag) {
						sellerPayMethod.setIsCheck(0);
					}
					sellerPayMethod.setUpdateTime(new Date());
					sellerDao.updateSellerPayMethod(sellerPayMethod);
				}
				JSONArray jsonArray2 = null;
				if (jsonObject.containsKey("cardBankList")) {
					jsonArray2 = JSONArray.parseArray(jsonObject.get("cardBankList").toString());
				}
				list = sellerDao.findsellerPayMethodBySellerId(seller.getSellerId(), 3);
				for (SellerPayMethod sellerPayMethod : list) {
					boolean flag = false;
					if (jsonArray2 != null) {
						for (Object object : jsonArray2) {
							JSONObject payMethodJson = JSONObject.parseObject(object.toString());
							if (sellerPayMethod.getPayMethodId().equals(Long.valueOf(payMethodJson.get("id") + ""))) {
								if(sellerPayMethod.getIsSoldOut() != null &&  sellerPayMethod.getIsSoldOut().equals(1)){
									Boolean timeFlag = TimeUtil.judgmentDate(sellerPayMethod.getSoldOutTime(),sellerBuySoldOutSetting.getTime());
									if(timeFlag){
										flag = true;
										sellerPayMethod.setIsCheck(1);
										sellerPayMethod.setIsSoldOut(0);
									}else{
										return ResponseData.error("银行卡收款码中，该账号"+sellerPayMethod.getAccount()+"被禁用");
									}
								}else{
									flag = true;
									sellerPayMethod.setIsCheck(1);
									sellerPayMethod.setIsSoldOut(0);
								}
							}
						}
						if (!flag) {
							sellerPayMethod.setIsCheck(0);
						}
						sellerPayMethod.setUpdateTime(new Date());
						sellerDao.updateSellerPayMethod(sellerPayMethod);
					}
				}


				if (jsonObject.containsKey("alipayPriceList")) {
					jsonArray2 = JSONArray.parseArray(jsonObject.get("alipayPriceList").toString());
				}
				list = sellerDao.findsellerPayMethodBySellerId(seller.getSellerId(), 4);
				for (SellerPayMethod sellerPayMethod : list) {
					boolean flag = false;
					if (jsonArray2 != null) {
						for (Object object : jsonArray2) {
							JSONObject payMethodJson = JSONObject.parseObject(object.toString());
							if (sellerPayMethod.getPayMethodId().equals(Long.valueOf(payMethodJson.get("id") + ""))) {
								if(sellerPayMethod.getIsSoldOut() != null &&  sellerPayMethod.getIsSoldOut().equals(1)){
									Boolean timeFlag = TimeUtil.judgmentDate(sellerPayMethod.getSoldOutTime(),sellerBuySoldOutSetting.getTime());
									if(timeFlag){
										flag = true;
										sellerPayMethod.setIsCheck(1);
										sellerPayMethod.setIsSoldOut(0);
									}else{
										return ResponseData.error("支付宝固定码收款码中，该账号"+sellerPayMethod.getAccount()+"被禁用");
									}
								}else{
									flag = true;
									sellerPayMethod.setIsCheck(1);
									sellerPayMethod.setIsSoldOut(0);
								}
							}
						}
						if (!flag) {
							sellerPayMethod.setIsCheck(0);
						}
						sellerPayMethod.setUpdateTime(new Date());
						sellerDao.updateSellerPayMethod(sellerPayMethod);
					}
				}

				if (jsonObject.containsKey("alipayCardList")) {
					jsonArray2 = JSONArray.parseArray(jsonObject.get("alipayCardList").toString());
				}
				list = sellerDao.findsellerPayMethodBySellerId(seller.getSellerId(), 5);
				for (SellerPayMethod sellerPayMethod : list) {
					boolean flag = false;
					if (jsonArray2 != null) {
						for (Object object : jsonArray2) {
							JSONObject payMethodJson = JSONObject.parseObject(object.toString());
							if (sellerPayMethod.getPayMethodId().equals(Long.valueOf(payMethodJson.get("id") + ""))) {
								if(sellerPayMethod.getIsSoldOut() != null &&  sellerPayMethod.getIsSoldOut().equals(1)){
									Boolean timeFlag = TimeUtil.judgmentDate(sellerPayMethod.getSoldOutTime(),sellerBuySoldOutSetting.getTime());
									if(timeFlag){
										flag = true;
										sellerPayMethod.setIsCheck(1);
										sellerPayMethod.setIsSoldOut(0);
									}else{
										return ResponseData.error("支付宝转银行收款码中，该账号"+sellerPayMethod.getAccount()+"被禁用");
									}
								}else{
									flag = true;
									sellerPayMethod.setIsCheck(1);
									sellerPayMethod.setIsSoldOut(0);
								}
							}
						}
						if (!flag) {
							sellerPayMethod.setIsCheck(0);
						}
						sellerPayMethod.setUpdateTime(new Date());
						sellerDao.updateSellerPayMethod(sellerPayMethod);
					}
				}


//				if (jsonObject.containsKey("cloudPayList")) {
//					jsonArray2 = JSONArray.parseArray(jsonObject.get("cloudPayList").toString());
//				}
//				list = sellerDao.findsellerPayMethodBySellerId(seller.getSellerId(), 4);
//				for (SellerPayMethod sellerPayMethod : list) {
//					boolean flag = false;
//					if (jsonArray2 != null) {
//						for (Object object : jsonArray2) {
//							JSONObject payMethodJson = JSONObject.parseObject(object.toString());
//							if (sellerPayMethod.getPayMethodId().equals(Long.valueOf(payMethodJson.get("id") + ""))) {
//								if(sellerPayMethod.getIsSoldOut() != null &&  sellerPayMethod.getIsSoldOut().equals(1)){
//									Boolean timeFlag = TimeUtil.judgmentDate(sellerPayMethod.getSoldOutTime(),sellerBuySoldOutSetting.getTime());
//									if(timeFlag){
//										flag = true;
//										sellerPayMethod.setIsCheck(1);
//										sellerPayMethod.setIsSoldOut(0);
//									}else{
//										return ResponseData.error("云闪付收款码中，该账号"+sellerPayMethod.getAccount()+"被禁用");
//									}
//								}else{
//									flag = true;
//									sellerPayMethod.setIsCheck(1);
//									sellerPayMethod.setIsSoldOut(0);
//								}
//							}
//						}
//						if (!flag) {
//							sellerPayMethod.setIsCheck(0);
//						}
//						sellerPayMethod.setUpdateTime(new Date());
//						sellerDao.updateSellerPayMethod(sellerPayMethod);
//					}
//				}
//				if (jsonObject.containsKey("alipayAccountList")) {
//					jsonArray2 = JSONArray.parseArray(jsonObject.get("alipayAccountList").toString());
//				}
//				list = sellerDao.findsellerPayMethodBySellerId(seller.getSellerId(), 5);
//				for (SellerPayMethod sellerPayMethod : list) {
//					boolean flag = false;
//					if (jsonArray2 != null) {
//						for (Object object : jsonArray2) {
//							JSONObject payMethodJson = JSONObject.parseObject(object.toString());
//							if (sellerPayMethod.getPayMethodId().equals(Long.valueOf(payMethodJson.get("id") + ""))) {
//								if(sellerPayMethod.getIsSoldOut() != null && sellerPayMethod.getIsSoldOut().equals(1)){
//									Boolean timeFlag = TimeUtil.judgmentDate(sellerPayMethod.getSoldOutTime(),sellerBuySoldOutSetting.getTime());
//									if(timeFlag){
//										flag = true;
//										sellerPayMethod.setIsCheck(1);
//										sellerPayMethod.setIsSoldOut(0);
//									}else{
//										return ResponseData.error("支付宝账号转账中，该账号"+sellerPayMethod.getAccount()+"被禁用");
//									}
//								}else{
//									flag = true;
//									sellerPayMethod.setIsCheck(1);
//									sellerPayMethod.setIsSoldOut(0);
//								}
//							}
//						}
//						if (!flag) {
//							sellerPayMethod.setIsCheck(0);
//						}
//						sellerPayMethod.setUpdateTime(new Date());
//						sellerDao.updateSellerPayMethod(sellerPayMethod);
//					}
//				}
//				if (jsonObject.containsKey("wxAccountList")) {
//					jsonArray2 = JSONArray.parseArray(jsonObject.get("wxAccountList").toString());
//				}
//				list = sellerDao.findsellerPayMethodBySellerId(seller.getSellerId(), 6);
//				for (SellerPayMethod sellerPayMethod : list) {
//					boolean flag = false;
//					if (jsonArray2 != null) {
//						for (Object object : jsonArray2) {
//							JSONObject payMethodJson = JSONObject.parseObject(object.toString());
//							if (sellerPayMethod.getPayMethodId().equals(Long.valueOf(payMethodJson.get("id") + ""))) {
//								if(sellerPayMethod.getIsSoldOut() != null && sellerPayMethod.getIsSoldOut().equals(1)){
//									Boolean timeFlag = TimeUtil.judgmentDate(sellerPayMethod.getSoldOutTime(),sellerBuySoldOutSetting.getTime());
//									if(timeFlag){
//										flag = true;
//										sellerPayMethod.setIsCheck(1);
//										sellerPayMethod.setIsSoldOut(0);
//									}else{
//										return ResponseData.error("微信账号转账中，该账号"+sellerPayMethod.getAccount()+"被禁用");
//									}
//								}else{
//									flag = true;
//									sellerPayMethod.setIsCheck(1);
//									sellerPayMethod.setIsSoldOut(0);
//								}
//							}
//						}
//						if (!flag) {
//							sellerPayMethod.setIsCheck(0);
//						}
//						sellerPayMethod.setUpdateTime(new Date());
//						sellerDao.updateSellerPayMethod(sellerPayMethod);
//					}
//				}
//				if (jsonObject.containsKey("alipayCardList")) {
//					jsonArray2 = JSONArray.parseArray(jsonObject.get("alipayCardList").toString());
//				}
//				list = sellerDao.findsellerPayMethodBySellerId(seller.getSellerId(), 7);
//				for (SellerPayMethod sellerPayMethod : list) {
//					boolean flag = false;
//					if (jsonArray2 != null) {
//						for (Object object : jsonArray2) {
//							JSONObject payMethodJson = JSONObject.parseObject(object.toString());
//							if (sellerPayMethod.getPayMethodId().equals(Long.valueOf(payMethodJson.get("id") + ""))) {
//								if(sellerPayMethod.getIsSoldOut() != null &&  sellerPayMethod.getIsSoldOut().equals(1)){
//									Boolean timeFlag = TimeUtil.judgmentDate(sellerPayMethod.getSoldOutTime(),sellerBuySoldOutSetting.getTime());
//									if(timeFlag){
//										flag = true;
//										sellerPayMethod.setIsCheck(1);
//										sellerPayMethod.setIsSoldOut(0);
//									}else{
//										return ResponseData.error("支付宝转银行卡中，该账号"+sellerPayMethod.getAccount()+"被禁用");
//									}
//								}else{
//									flag = true;
//									sellerPayMethod.setIsCheck(1);
//									sellerPayMethod.setIsSoldOut(0);
//								}
//							}
//						}
//						if (!flag) {
//							sellerPayMethod.setIsCheck(0);
//						}
//						sellerPayMethod.setUpdateTime(new Date());
//						sellerDao.updateSellerPayMethod(sellerPayMethod);
//					}
//				}
//				if (jsonObject.containsKey("wxCardList")) {
//					jsonArray2 = JSONArray.parseArray(jsonObject.get("wxCardList").toString());
//				}
//				list = sellerDao.findsellerPayMethodBySellerId(seller.getSellerId(), 8);
//				for (SellerPayMethod sellerPayMethod : list) {
//					boolean flag = false;
//					if (jsonArray2 != null) {
//						for (Object object : jsonArray2) {
//							JSONObject payMethodJson = JSONObject.parseObject(object.toString());
//							if (sellerPayMethod.getPayMethodId().equals(Long.valueOf(payMethodJson.get("id") + ""))) {
//								if(sellerPayMethod.getIsSoldOut() != null && sellerPayMethod.getIsSoldOut().equals(1)){
//									Boolean timeFlag = TimeUtil.judgmentDate(sellerPayMethod.getSoldOutTime(),sellerBuySoldOutSetting.getTime());
//									if(timeFlag){
//										flag = true;
//										sellerPayMethod.setIsCheck(1);
//										sellerPayMethod.setIsSoldOut(0);
//									}else{
//										return ResponseData.error("微信转银行卡中，该账号"+sellerPayMethod.getAccount()+"被禁用");
//									}
//								}else{
//									flag = true;
//									sellerPayMethod.setIsCheck(1);
//									sellerPayMethod.setIsSoldOut(0);
//								}
//							}
//						}
//						if (!flag) {
//							sellerPayMethod.setIsCheck(0);
//						}
//						sellerPayMethod.setUpdateTime(new Date());
//						sellerDao.updateSellerPayMethod(sellerPayMethod);
//					}
//				}
//				if (jsonObject.containsKey("wxZanList")) {
//					jsonArray2 = JSONArray.parseArray(jsonObject.get("wxZanList").toString());
//				}
//				list = sellerDao.findsellerPayMethodBySellerId(seller.getSellerId(), 9);
//				for (SellerPayMethod sellerPayMethod : list) {
//					boolean flag = false;
//					if (jsonArray2 != null) {
//						for (Object object : jsonArray2) {
//							JSONObject payMethodJson = JSONObject.parseObject(object.toString());
//							if (sellerPayMethod.getPayMethodId().equals(Long.valueOf(payMethodJson.get("id") + ""))) {
//								if(sellerPayMethod.getIsSoldOut() != null && sellerPayMethod.getIsSoldOut().equals(1)){
//									Boolean timeFlag = TimeUtil.judgmentDate(sellerPayMethod.getSoldOutTime(),sellerBuySoldOutSetting.getTime());
//									if(timeFlag){
//										flag = true;
//										sellerPayMethod.setIsCheck(1);
//										sellerPayMethod.setIsSoldOut(0);
//									}else{
//										return ResponseData.error("微信赞赏码中，该账号"+sellerPayMethod.getAccount()+"被禁用");
//									}
//								}else{
//									flag = true;
//									sellerPayMethod.setIsCheck(1);
//									sellerPayMethod.setIsSoldOut(0);
//								}
//							}
//						}
//						if (!flag) {
//							sellerPayMethod.setIsCheck(0);
//						}
//						sellerPayMethod.setUpdateTime(new Date());
//						sellerDao.updateSellerPayMethod(sellerPayMethod);
//					}
//				}
				return ResponseData.success(200, "确认成功", null);
			}
		}
		return ResponseData.error("确认失败");
	}

	public ResponseData checkStart(String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				int count = otcOrderService.findSellerBuyCoinOrder(seller.getSellerId());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("isSwitch", false);
				if (count > 0) {
					map.put("isSwitch", true);
				}
				return ResponseData.success(map);
			}
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData getSellerOrderList(Integer pageSize, Integer pageNumber, String token, Integer type,
										   Integer status, String serialno,String starDate,String endDate) {
		SellerOrderTimeSetting orderTimeSetting = orderTimeSettingService.getOne(null);
		int starTime = 5;
		if (orderTimeSetting != null){
			starTime = orderTimeSetting.getStarTime() == null ? 5 : orderTimeSetting.getStarTime();
		}
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			int total = 0;
			List<Map<String, Object>> list = new ArrayList<>();
			if (seller != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
				params.put("status", status);
				params.put("starTime",starTime);
				params.put("type", type);
				params.put("serialno",serialno);
				params.put("begainTime",starDate);
				params.put("endTime",endDate);
				Query query = new Query(params);
				List<SellerBuyerCoinOrder> otcpList = sellerDao.getSellerBuyerCoinOrderList(query);
				for (SellerBuyerCoinOrder order : otcpList) {
					Map<String, Object> map = new HashMap<>();
					if (order != null) {
						map.put("serialNo", order.getSerialno());
						map.put("status", order.getStatus());
						map.put("type", order.getPayMethodType());
						map.put("price", order.getNumber());
						map.put("createTime", order.getCreateTime().getTime() / 1000);
						map.put("nickName",order.getPayMethodNickName());
						if (order.getStatus().equals(4)) {
							map.put("bonusNumber", order.getBonusNumber() == null ? 0 : order.getBonusNumber());
						} else {
							UserPayMethodFeeSetting feeSetting = sellerDao
									.findUserPayMethodFeeSettingByUserId(order.getBuyerId());
							if (feeSetting != null) {
								Double number = order.getNumber();
								if ((order.getPayMethodType().equals(1)
										|| order.getPayMethodType().equals(4)
										|| order.getPayMethodType().equals(5)
								)  && feeSetting.getAlipayRatio() != null
										&& feeSetting.getAlipayRatio() > 0) {
									number = order.getNumber() * (1 - feeSetting.getAlipayRatio() / 100);
								} else if ((order.getPayMethodType().equals(2)) && feeSetting.getWxRatio() != null
										&& feeSetting.getWxRatio() > 0) {
									number = order.getNumber() * (1 - feeSetting.getWxRatio() / 100);
								} else if (order.getPayMethodType().equals(3) && feeSetting.getCardRatio() != null
										&& feeSetting.getCardRatio() > 0) {
									number = order.getNumber() * (1 - feeSetting.getCardRatio() / 100);
								}
								if (number > 0) {
									Double bonusNumber = 0.0;
									UserRecommendRelation relation = new UserRecommendRelation();
									relation.setUserId(order.getBuyerId());
									relation = sellerDao.findUserRecommendRelation(relation);
									// 代理商分红
									if (relation != null) {
										UserBonusSetting bonusSetting = new UserBonusSetting();
										bonusSetting.setAgentId(relation.getRecommendId());
										bonusSetting.setUserId(order.getBuyerId());
										bonusSetting = sellerDao.findUserBonusSetting(bonusSetting);
										if (bonusSetting != null) {
											if ((order.getPayMethodType().equals(1)
													|| order.getPayMethodType().equals(5)
													|| order.getPayMethodType().equals(4) ) && bonusSetting.getAlipayRatio() != null
													&& bonusSetting.getAlipayRatio() > 0) {
												bonusNumber = order.getNumber() * bonusSetting.getAlipayRatio() / 100;
											} else if ((order.getPayMethodType().equals(2)
											) && bonusSetting.getWxRatio() != null
													&& bonusSetting.getWxRatio() > 0) {
												bonusNumber = order.getNumber() * bonusSetting.getWxRatio() / 100;
											} else if (order.getPayMethodType().equals(3)
													&& bonusSetting.getCardRatio() != null && bonusSetting.getCardRatio() > 0) {
												bonusNumber = order.getNumber() * bonusSetting.getCardRatio() / 100;
											}
										}
									}
									// 会员获奖
									Double awardNumber = 0.0;
									SellerAwardSetting awardSetting = sellerDao.getOneSellerAwardSetting();
									if (awardSetting != null && awardSetting.getValue() != null
											&& awardSetting.getValue() > 0) {
										awardNumber = (order.getNumber() - number - bonusNumber)
												* awardSetting.getValue() / 100;
									}
									map.put("bonusNumber", awardNumber);
								}
							}
						}
						list.add(map);
					}
				}
				total = sellerDao.getSellerBuyerCoinOrderListCount(query);
				PageUtils pageUtils = new PageUtils(total, list);
				return ResponseData.success(pageUtils);
			}
		}
		return ResponseData.success(null);
	}

	public ResponseData getSellerOrderDetail(String serialno, String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				SellerBuyerCoinOrder order = otcOrderService.findSellerBuyerOrderBySerialNo(serialno);
				if (order != null && order.getSellerId().equals(seller.getSellerId())) {
					Map<String, Object> map = new HashMap<>();
					map.put("serialNo", order.getSerialno());
					map.put("price", order.getNumber());
					map.put("status", order.getStatus());
					map.put("type", order.getPayMethodType());
					map.put("name", order.getPayMethodName());
					map.put("account", order.getPayMethodAccount());
					map.put("cardBank", order.getPayMethodCardBank());
					map.put("cardBankName", order.getPayMethodCardBankName());
					map.put("qrCode", order.getPayMethodQrcode());
					map.put("createTime", order.getCreateTime().getTime() / 1000);
					map.put("remark",order.getRemark());
					if (order.getStatus().equals(4)) {
						map.put("bonusNumber", order.getBonusNumber() == null ? 0 : order.getBonusNumber());
					} else {
						UserPayMethodFeeSetting feeSetting = sellerDao
								.findUserPayMethodFeeSettingByUserId(order.getBuyerId());
						if (feeSetting != null) {
							Double number = order.getNumber();
							if ((order.getPayMethodType().equals(1)
									|| order.getPayMethodType().equals(5)
									|| order.getPayMethodType().equals(4) ) && feeSetting.getAlipayRatio() != null
									&& feeSetting.getAlipayRatio() > 0) {
								number = order.getNumber() * (1 - feeSetting.getAlipayRatio() / 100);
							} else if ((order.getPayMethodType().equals(2)
							) && feeSetting.getWxRatio() != null
									&& feeSetting.getWxRatio() > 0) {
								number = order.getNumber() * (1 - feeSetting.getWxRatio() / 100);
							} else if (order.getPayMethodType().equals(3) && feeSetting.getCardRatio() != null
									&& feeSetting.getCardRatio() > 0) {
								number = order.getNumber() * (1 - feeSetting.getCardRatio() / 100);
							}
							if (number > 0) {
								Double bonusNumber = 0.0;
								UserRecommendRelation relation = new UserRecommendRelation();
								relation.setUserId(order.getBuyerId());
								relation = sellerDao.findUserRecommendRelation(relation);
								// 代理商分红
								if (relation != null) {
									UserBonusSetting bonusSetting = new UserBonusSetting();
									bonusSetting.setAgentId(relation.getRecommendId());
									bonusSetting.setUserId(order.getBuyerId());
									bonusSetting = sellerDao.findUserBonusSetting(bonusSetting);
									if (bonusSetting != null) {
										if ((order.getPayMethodType().equals(1)
												|| order.getPayMethodType().equals(4)
												|| order.getPayMethodType().equals(5) ) && bonusSetting.getAlipayRatio() != null
												&& bonusSetting.getAlipayRatio() > 0) {
											bonusNumber = order.getNumber() * bonusSetting.getAlipayRatio() / 100;
										} else if ((order.getPayMethodType().equals(2)
										) && bonusSetting.getWxRatio() != null
												&& bonusSetting.getWxRatio() > 0) {
											bonusNumber = order.getNumber() * bonusSetting.getWxRatio() / 100;
										} else if (order.getPayMethodType().equals(3)
												&& bonusSetting.getCardRatio() != null && bonusSetting.getCardRatio() > 0) {
											bonusNumber = order.getNumber() * bonusSetting.getCardRatio() / 100;
										}
									}
								}
								// 会员获奖
								Double awardNumber = 0.0;
								SellerAwardSetting awardSetting = sellerDao.getOneSellerAwardSetting();
								if (awardSetting != null && awardSetting.getValue() != null
										&& awardSetting.getValue() > 0) {
									awardNumber = (order.getNumber() - number - bonusNumber) * awardSetting.getValue()
											/ 100;
								}
								map.put("bonusNumber", awardNumber);
							}
						}
					}
					return ResponseData.success(map);
				}
			}
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData buyCoinInfo(String token,Integer type) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				if (type.equals(1)){
					SellerWallter sellerWallter = new SellerWallter();
					sellerWallter.setCode("HC");
					sellerWallter.setType(1);
					sellerWallter.setSellerId(seller.getSellerId());
					List<SellerWallter> list = sellerDao.findSellerWallter(sellerWallter);
					sellerWallter = list.get(0);
					Map<String, Object> map = new HashMap<>();
					SellerOrder order = otcOrderService.findSellerorderBySellerId(seller.getSellerId());
					if (order != null) {
						map.put("availableBalance", order.getNumber());
					} else {
						map.put("availableBalance", sellerWallter.getAvailableBalance());
					}
					Double frozenBalance = otcOrderService.findSellerbuyCoinOrderByIsAppealCount(seller.getSellerId());
					Double runningBalance = otcOrderService.findSellerbuyCoinOrderByRunning(seller.getSellerId());
					map.put("frozenBalance", frozenBalance);
					map.put("runningBalance", runningBalance);
					Map<String, Object> paramMap = otcOrderService.findSellerbuyCoinOrderByToday(seller.getSellerId());
					map.put("today", paramMap);
					map.put("cash",0);
					//获取押金
					SellerCash sellerCash = sellerDao.selectSellerCashBySellerId(seller.getSellerId());
					if (sellerCash != null){
						map.put("cash",sellerCash.getCash());
					}
					return ResponseData.success(map);
				}


			}
		}
		return ResponseData.error("获取失败");
	}

	@Transactional
	public ResponseData finishPayOtcp(String token, Long id) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				OtcpOrder otcpOrder = otcOrderService.findOtcOrderById(id);
				if (otcpOrder != null) {
					if (otcpOrder.getStatus() == 3) {
						otcpOrder.setStatus(4);
						otcpOrder.setUpdateTime(new Date());
						otcpOrder.setCloseTime(new Date());
						otcOrderService.updateOtcpOrder(otcpOrder);
						return ResponseData.success(200, "确认到账完成", null);
					} else if (otcpOrder.getStatus() == 6) {
						return ResponseData.success(200, "该订单已被买家申诉", null);
					} else if (otcpOrder.getStatus() == 7) {
						return ResponseData.success(200, "该订单已被取消", null);
					}
				}
			}
		}
		return ResponseData.error("确认到账失败");
	}

	@Transactional
	public ResponseData ConfirmFinishTrader(String token, String serialno, HttpServletRequest request,Double price) throws IOException {
		if (price == null || price <=0){
			return ResponseData.error("请输订单金额");
		}
		String ipAddr = getIpAddr(request);
		logger.info("IPAddr:"+ipAddr);
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				SellerBuyerCoinOrder order = otcOrderService.findSellerBuyerOrderBySerialNo(serialno);
				//取消的和已完成的不让进, 未支付的订单才能进入
				if (order != null && order.getSellerId().equals(seller.getSellerId())
						&& !order.getStatus().equals(7)
						&& !order.getStatus().equals(4)) {
					if (!order.getNumber().equals(price)){
						return ResponseData.error("输入的订单金额有误");
					}
					/**
					 * 回调前，先判断订单余额是否充足
					 */
					SellerWallter sellerWallter_ = new SellerWallter();
					sellerWallter_.setCode("HC");
					sellerWallter_.setType(1);
					sellerWallter_.setSellerId(order.getSellerId());
					List<SellerWallter> wallterList_ = sellerDao.findSellerWallter(sellerWallter_);
					if (wallterList_ != null && wallterList_.size() > 0) {
						sellerWallter_ = wallterList_.get(0);
						if (sellerWallter_.getFrozenBalance().doubleValue() <= 0) {//订单负数不让过
							logger.info("订单确认收款，会员用户冻结出现负数，无法确认，订单号【"+order.getSerialno()+"】");
							throw new WallterException("确认失败");
						}
						if(sellerWallter_.getFrozenBalance().doubleValue()<order.getNumber().doubleValue()){
							//冻结金额小于订单金额的异常单不让通过，异常抛出
							throw new WallterException("冻结金额小于订单金额的异常单");
						}
					}


					Double feeRatio = 0.0;
					Double agentFeeRatio = 0.0;
					UserPayMethodFeeSetting feeSetting = sellerDao
							.findUserPayMethodFeeSettingByUserId(order.getBuyerId());
					if (feeSetting != null) {
						Double number = order.getNumber();
						if ((order.getPayMethodType().equals(1)
								|| order.getPayMethodType().equals(5)
								|| order.getPayMethodType().equals(4) )&& feeSetting.getAlipayRatio() != null
								&& feeSetting.getAlipayRatio() > 0) {
							feeRatio = feeSetting.getAlipayRatio();
							number = order.getNumber() * (1 - feeSetting.getAlipayRatio() / 100);
						} else if ((order.getPayMethodType().equals(2)
						)&& feeSetting.getWxRatio() != null
								&& feeSetting.getWxRatio() > 0) {
							feeRatio = feeSetting.getWxRatio();
							number = order.getNumber() * (1 - feeSetting.getWxRatio() / 100);
						} else if (order.getPayMethodType().equals(3) && feeSetting.getCardRatio() != null
								&& feeSetting.getCardRatio() > 0) {
							feeRatio = feeSetting.getCardRatio();
							number = order.getNumber() * (1 - feeSetting.getCardRatio() / 100);
						}
						if (number > 0) {
							User merchantUser = new User();
							merchantUser.setUserId(order.getBuyerId());
							merchantUser = sellerDao.findUserOne(merchantUser);
							UserWallter userWallter = new UserWallter();
							userWallter.setUserId(merchantUser.getUserId());
							userWallter.setType(2);
							if ((order.getPayMethodType().equals(1)
									|| order.getPayMethodType().equals(5)
									|| order.getPayMethodType().equals(4) )) {
								userWallter.setChannelType(1);
							} else if (order.getPayMethodType().equals(2)) {
								userWallter.setChannelType(2);
							} else if (order.getPayMethodType().equals(3) ) {
								userWallter.setChannelType(3);
							}
							List<UserWallter> userWallterList = sellerDao.findUserWallterList(userWallter);
							if (userWallterList != null && userWallterList.size() > 0) {// 商户返利
								userWallter = userWallterList.get(0);

								logger.info("交易订单确认收款商户返利余额，订单号【"+order.getSerialno()+"】，返利前，用户【"
										+userWallter.getUserId()+"】,可用余额:"
										+userWallter.getAvailableBalance()
										+",冻结余额："+userWallter.getFrozenBalance()+",返利数量:"+number);


								Double available = userWallter.getAvailableBalance();
								userWallter.setAvailableBalance(userWallter.getAvailableBalance() + number);
								userWallter.setTotalBalance(userWallter.getTotalBalance() + number);
								userWallter.setUpdateTime(new Date());
								int resultNumber = sellerDao.updateUserWallter(userWallter);
								if (resultNumber <= 0) {
									throw new WallterException("确认失败");
								}

								AccountUpdateRecord updateRecord = new AccountUpdateRecord();
								updateRecord.setBeforePrice(available);
								updateRecord.setAfterPrice(userWallter.getAvailableBalance());
								updateRecord.setCode("HC");
								updateRecord.setCreateTime(new Date());
								updateRecord.setPhone(merchantUser.getAccountCode());
								updateRecord.setSource("商户HC");
								updateRecord.setType(AccountUpdateType.SUCCESS_SELL_HC.code());
								updateRecord.setWalletCode(AccountUpdateWallet.BANZHUAN_WALLET_HC.code());
								updateRecord.setRemark("接单交易");
								updateRecord.setPrice(number);
								updateRecord.setRoleId(2L);
								updateRecord.setPayMethodType(order.getPayMethodType());
								updateRecord.setAccountId(userWallter.getUserId());
								updateRecord.setSerialno(order.getSerialno());
								sellerDao.addAccountUpdateRecord(updateRecord);

								logger.info("交易订单确认收款商户返利余额，订单号【"+order.getSerialno()+"】，返利后，用户【"
										+userWallter.getUserId()+"】,可用余额:"
										+userWallter.getAvailableBalance()
										+",冻结余额："+userWallter.getFrozenBalance()+",返利数量:"+number);

								UserAccountFlowRecord flowRecord = new UserAccountFlowRecord();
								flowRecord.setCode("HC");
								flowRecord.setUserId(merchantUser.getUserId());
								flowRecord.setSource("交易返额");
								flowRecord.setPrice(number);
								flowRecord.setCreateTime(new Date());
								flowRecord.setSerialno(order.getSerialno());
								flowRecord.setChannelType(order.getPayMethodType());
								flowRecord.setType(FlowRecordConstant.GRAD_HC_COIN);
								sellerDao.addUserAccountFlowRecord(flowRecord);
							}
							Double bonusNumber = 0.0;
							UserRecommendRelation relation = new UserRecommendRelation();
							relation.setUserId(merchantUser.getUserId());
							relation = sellerDao.findUserRecommendRelation(relation);
							// 代理商分红
							if (relation != null) {
								UserBonusSetting bonusSetting = new UserBonusSetting();
								bonusSetting.setAgentId(relation.getRecommendId());
								bonusSetting.setUserId(merchantUser.getUserId());
								bonusSetting = sellerDao.findUserBonusSetting(bonusSetting);
								if (bonusSetting != null) {
									if ((order.getPayMethodType().equals(1)
											|| order.getPayMethodType().equals(5)
											|| order.getPayMethodType().equals(4) ) && bonusSetting.getAlipayRatio() != null
											&& bonusSetting.getAlipayRatio() > 0) {
										agentFeeRatio =bonusSetting.getAlipayRatio();
										bonusNumber = order.getNumber() * bonusSetting.getAlipayRatio() / 100;
									} else if ((order.getPayMethodType().equals(2)
									) && bonusSetting.getWxRatio() != null
											&& bonusSetting.getWxRatio() > 0) {
										agentFeeRatio = bonusSetting.getWxRatio();
										bonusNumber = order.getNumber() * bonusSetting.getWxRatio() / 100;
									} else if (order.getPayMethodType().equals(3)
											&& bonusSetting.getCardRatio() != null && bonusSetting.getCardRatio() > 0) {
										agentFeeRatio = bonusSetting.getCardRatio();
										bonusNumber = order.getNumber() * bonusSetting.getCardRatio() / 100;
									}
									if (bonusNumber > 0) {
										UserWallter agentWallter = new UserWallter();
										agentWallter.setUserId(relation.getRecommendId());
										agentWallter.setType(2);
										List<UserWallter> agentWallterList = sellerDao
												.findUserWallterList(agentWallter);
										if (agentWallterList != null && agentWallterList.size() > 0) {
											agentWallter = agentWallterList.get(0);

											logger.info("交易订单确认收款代理商返利余额，订单号【"+order.getSerialno()+"】，返利前，用户【"
													+agentWallter.getUserId()+"】,可用余额:"
													+agentWallter.getAvailableBalance()
													+",冻结余额："+agentWallter.getFrozenBalance()+",返利数量:"+bonusNumber);

											Double agentAvailable = agentWallter.getAvailableBalance();
											agentWallter.setAvailableBalance(
													agentWallter.getAvailableBalance() + bonusNumber);
											agentWallter
													.setTotalBalance(agentWallter.getTotalBalance() + bonusNumber);
											agentWallter.setUpdateTime(new Date());
											int resultNumber = sellerDao.updateUserWallter(agentWallter);
											if (resultNumber <= 0) {
												throw new WallterException("确认失败");
											}
											User agentUser = userMapper.selectById(agentWallter.getUserId());
											AccountUpdateRecord updateRecord = new AccountUpdateRecord();
											updateRecord.setBeforePrice(agentAvailable);
											updateRecord.setSerialno(order.getSerialno());
											updateRecord.setAccountId(agentWallter.getUserId());
											updateRecord.setAfterPrice(agentWallter.getAvailableBalance());
											updateRecord.setCode("HC");
											updateRecord.setCreateTime(new Date());
											updateRecord.setPhone(agentUser.getAccountCode());
											updateRecord.setSource("代理商HC");
											updateRecord.setType(AccountUpdateType.BUY_HC_RETURN_AWARD.code());
											updateRecord.setWalletCode(AccountUpdateWallet.BANZHUAN_WALLET_HC.code());
											updateRecord.setRemark("交易返利");
											updateRecord.setPayMethodType(order.getPayMethodType());
											updateRecord.setPrice(bonusNumber);
											updateRecord.setRoleId(4L);
											sellerDao.addAccountUpdateRecord(updateRecord);


											logger.info("交易订单确认收款代理商返利余额，订单号【"+order.getSerialno()+"】，返利后，用户【"
													+agentWallter.getUserId()+"】,可用余额:"
													+agentWallter.getAvailableBalance()
													+",冻结余额："+agentWallter.getFrozenBalance()+",返利数量:"+bonusNumber);

											UserAccountFlowRecord agentflowRecord = new UserAccountFlowRecord();
											agentflowRecord.setCode("HC");
											agentflowRecord.setUserId(relation.getRecommendId());
											agentflowRecord.setSource("交易挖矿");
											agentflowRecord.setPrice(bonusNumber);
											agentflowRecord.setCreateTime(new Date());
											agentflowRecord.setSerialno(order.getSerialno());
											agentflowRecord.setChannelType(order.getPayMethodType());
											agentflowRecord.setType(FlowRecordConstant.GRAD_HC_COIN);
											sellerDao.addUserAccountFlowRecord(agentflowRecord);
										}
									}
								}
							}
							// 会员获奖
							Double awardNumber = 0.0;
							SellerAwardSetting awardSetting = sellerDao.getOneSellerAwardSetting();
							if (awardSetting != null && awardSetting.getValue() != null
									&& awardSetting.getValue() > 0) {
								awardNumber = (order.getNumber() - number - bonusNumber) * awardSetting.getValue()
										/ 100;
								if (awardNumber > 0) {
									SellerWallter profitWallter = new SellerWallter();
									profitWallter.setCode("HC");
									profitWallter.setType(4);
									profitWallter.setSellerId(order.getSellerId());
									List<SellerWallter> profitWallterList = sellerDao
											.findSellerWallter(profitWallter);
									if (profitWallterList != null && profitWallterList.size() > 0) {
										profitWallter = profitWallterList.get(0);

										logger.info("交易订单确认收款会员挖矿返利余额，订单号【"+order.getSerialno()+"】，返利前，用户【"
												+profitWallter.getSellerId()+"】,可用余额:"
												+profitWallter.getAvailableBalance()
												+",冻结余额："+profitWallter.getFrozenBalance()+",返利数量:"+awardNumber);

										Double profitAvailable = profitWallter.getAvailableBalance();
										profitWallter.setAvailableBalance(
												profitWallter.getAvailableBalance() + awardNumber);
										profitWallter
												.setTotalBalance(profitWallter.getTotalBalance() + awardNumber);
										profitWallter.setUpdateTime(new Date());
										int resultNumber = sellerDao.updateSellerWallter(profitWallter);
										if (resultNumber <= 0) {
											throw new WallterException("确认失败");
										}

										AccountUpdateRecord updateRecord = new AccountUpdateRecord();
										updateRecord.setBeforePrice(profitAvailable);
										updateRecord.setAfterPrice(profitWallter.getAvailableBalance());
										updateRecord.setCode("HC");
										updateRecord.setCreateTime(new Date());
										updateRecord.setPhone(seller.getAccount());
										updateRecord.setSource("挖矿HC");
										updateRecord.setType(AccountUpdateType.SUCCESS_SELL_HC.code());
										updateRecord.setWalletCode(AccountUpdateWallet.PROFIT_WALLET_HC.code());
										updateRecord.setRemark("接单交易");
										updateRecord.setPrice(awardNumber);
										updateRecord.setRoleId(1L);
										updateRecord.setPayMethodType(order.getPayMethodType());
										updateRecord.setAccountId(profitWallter.getSellerId());
										updateRecord.setSerialno(order.getSerialno());
										sellerDao.addAccountUpdateRecord(updateRecord);


										logger.info("交易订单确认收款会员挖矿返利余额，订单号【"+order.getSerialno()+"】，返利后，用户【"
												+profitWallter.getSellerId()+"】,可用余额:"
												+profitWallter.getAvailableBalance()
												+",冻结余额："+profitWallter.getFrozenBalance()+",返利数量:"+awardNumber);

										SellerAccountFlowRecord profitAccountFlowRecord = new SellerAccountFlowRecord();
										profitAccountFlowRecord.setCode("HC");
										profitAccountFlowRecord.setCreateTime(new Date());
										profitAccountFlowRecord.setPrice(awardNumber);
										profitAccountFlowRecord.setSellerId(order.getSellerId());
										profitAccountFlowRecord.setSerialno(order.getSerialno());
										profitAccountFlowRecord.setSource("交易挖矿");
										profitAccountFlowRecord.setWalletType(4);
										profitAccountFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
										sellerDao.addSellerAccountFlowRecord(profitAccountFlowRecord);
									}
								}
							}

							SellerWallter sellerWallter = new SellerWallter();
							sellerWallter.setCode("HC");
							sellerWallter.setSellerId(order.getSellerId());
							sellerWallter.setType(4);
							List<SellerWallter> wallterList = sellerDao.findSellerWallter(sellerWallter);
							if (wallterList != null && wallterList.size() > 0) {
								sellerWallter = wallterList.get(0);
								if (sellerWallter.getFrozenBalance().doubleValue() <= 0) {//订单负数不让过
									throw new WallterException("确认失败");
								}
								if(sellerWallter.getFrozenBalance().doubleValue()<order.getNumber().doubleValue()){//冻结金额小于订单金额的异常单不让通过，异常抛出
									throw new WallterException("冻结金额小于订单金额的异常单");
								}
								logger.info("交易订单确认收款扣除冻结余额,订单号【"+order.getSerialno()+"】，扣除前，用户【"
										+sellerWallter.getSellerId()+"】,可用余额:"
										+sellerWallter.getAvailableBalance()
										+",冻结余额："+sellerWallter.getFrozenBalance()+",扣除数量:"+order.getNumber());


								Double frozen = sellerWallter.getFrozenBalance();
								sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance() - order.getNumber());
								sellerWallter.setUpdateTime(new Date());
								int reslut = sellerDao.updateSellerWallter(sellerWallter);
								if (reslut <= 0) {
									throw new WallterException("确认失败");
								}

								AccountUpdateRecord updateRecord = new AccountUpdateRecord();
								updateRecord.setBeforePrice(frozen);
								updateRecord.setAfterPrice(sellerWallter.getFrozenBalance());
								updateRecord.setCode("HC");
								updateRecord.setCreateTime(new Date());
								updateRecord.setPhone(seller.getAccount());
								updateRecord.setSource("搬砖-HC");
								updateRecord.setType(AccountUpdateType.SUCCESS_SELL_HC.code());
								updateRecord.setWalletCode(AccountUpdateWallet.BANZHUAN_WALLET_HC.code());
								updateRecord.setRemark("接单交易");
								updateRecord.setPrice(-order.getNumber());
								updateRecord.setRoleId(1L);
								updateRecord.setPayMethodType(order.getPayMethodType());
								updateRecord.setAccountId(order.getSellerId());
								updateRecord.setSerialno(order.getSerialno());
								sellerDao.addAccountUpdateRecord(updateRecord);

								logger.info("交易订单确认收款扣除冻结余额,订单号【"+order.getSerialno()+"】，扣除后，用户【"
										+sellerWallter.getSellerId()+"】,可用余额:"
										+sellerWallter.getAvailableBalance()
										+",冻结余额："+sellerWallter.getFrozenBalance());

								SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
								flowRecord.setCode("HC");
								flowRecord.setCreateTime(new Date());
								flowRecord.setPrice(-order.getNumber());
								flowRecord.setSellerId(order.getSellerId());
								flowRecord.setSerialno(order.getSerialno());
								flowRecord.setSource("交易扣除");
								flowRecord.setWalletType(1);
								flowRecord.setRemark("会员确认收款操作");
								flowRecord.setType(FlowRecordConstant.GRAD_HC_COIN);
								sellerDao.addSellerAccountFlowRecord(flowRecord);
							}

							order.setStatus(4);
							order.setIsSuccess(1);
							order.setUpdateTime(new Date());
							order.setCloseTime(new Date());
							order.setIntoNumber(number);
							order.setFeePrice(feeRatio);
							order.setAgentFeePrice(agentFeeRatio);
							order.setBonusNumber(awardNumber < 0 ? 0 : awardNumber);
							order.setUpdateUser(seller.getSellerId());
							order.setUpdateTime(new Date());
							order.setRemark("会员确认");
							otcOrderService.updateSellerBuyerCoinOrder(order);


							SellerPayMethod payMethod = sellerDao.findSellerPayMethodById(order.getPayMethodId());
							payMethod.setFailNumber(0);
							sellerDao.updateSellerPayMethod(payMethod);

							//直推返利
							if (StringUtils.isNotBlank(seller.getReferceIds())){
								String[] pIds =  seller.getReferceIds().split(",");
								String[] ids = new String[pIds.length];
								for(int i=0;i<pIds.length;i++){
									ids[pIds.length-1-i] = pIds[i];
								}
								pIds = ids;
								Integer rateType = 0;
								if(order.getPayMethodType().equals(1)
										|| order.getPayMethodType().equals(5)
										|| order.getPayMethodType().equals(7) ){
									rateType = 1;
								}else if(order.getPayMethodType().equals(2)
										|| order.getPayMethodType().equals(6)
										|| order.getPayMethodType().equals(8)
										|| order.getPayMethodType().equals(9)){
									rateType = 2;
								}else if(order.getPayMethodType().equals(3)){
									rateType=3;
								}else{
									rateType=4;
								}
								List<PromotionRate> promotionList =  promotionRateService.selectListByLeveAsc(rateType);
								if(promotionList != null && promotionList.size()>0){
									int size = promotionList.size()-pIds.length >0?pIds.length:promotionList.size();
									for (int i=0;i<size;i++){
										Double rebateValue =0.0;
										SellerWallter profitWallter = new SellerWallter();
										profitWallter.setCode("HC");
										profitWallter.setType(4);
										profitWallter.setSellerId(Long.valueOf(pIds[i]));
										List<SellerWallter> list = sellerDao.findSellerWallter(profitWallter);
										if (list != null && list.size() > 0) {
											profitWallter = list.get(0);
											rebateValue = order.getNumber() *promotionList.get(i).getBonusRatio() / 100;

											Seller sell = this.sellerDao.findSellerbyId(profitWallter.getSellerId());


											Double profitAvailable = profitWallter.getAvailableBalance();
											profitWallter.setAvailableBalance(profitWallter.getAvailableBalance() + rebateValue);
											profitWallter.setTotalBalance(profitWallter.getTotalBalance() + rebateValue);
											profitWallter.setUpdateTime(new Date());
											int result = sellerDao.updateSellerWallter(profitWallter);
											if (result <= 0) {
												throw new WallterException("确认收款失败");
											}

											AccountUpdateRecord updateRecord3 = new AccountUpdateRecord();
											updateRecord3.setBeforePrice(profitAvailable);
											updateRecord3.setAfterPrice(profitWallter.getAvailableBalance());
											updateRecord3.setCode("HC");
											updateRecord3.setCreateTime(new Date());
											updateRecord3.setPhone(sell.getAccount());
											updateRecord3.setSource("推荐HC");
											updateRecord3.setType(AccountUpdateType.RCCOMEND_AWARD.code());
											updateRecord3.setWalletCode(AccountUpdateWallet.PROFIT_WALLET_HC.code());
											updateRecord3.setRemark("推荐挖矿");
											updateRecord3.setPrice(rebateValue);
											updateRecord3.setRoleId(1l);
											updateRecord3.setSerialno(order.getSerialno());
											updateRecord3.setAccountId(profitWallter.getSellerId());
											sellerDao.addAccountUpdateRecord(updateRecord3);

											// 流水记录
											SellerAccountFlowRecord profitFlowRecord = new SellerAccountFlowRecord();
											profitFlowRecord.setCode(profitWallter.getCode());
											profitFlowRecord.setCreateTime(new Date());
											profitFlowRecord.setPrice(rebateValue);
											profitFlowRecord.setSellerId(sell.getSellerId());
											profitFlowRecord.setSource("推荐返利");
											profitFlowRecord.setWalletType(4);
											profitFlowRecord.setType(FlowRecordConstant.PROFIT_COIN);
											sellerDao.addSellerAccountFlowRecord(profitFlowRecord);
										}
									}
								}
							}

							// 传递给商家那边
							Map<String, String> params = new HashMap<>();
							params.put("cuid", order.getCuid());
							User user = new User();
							user.setUserId(order.getBuyerId());
							user = this.sellerDao.findUserOne(user);
							params.put("uid", user.getAccountCode()+ "");
							params.put("price", order.getNumber() + "");
							params.put("cuid", order.getCuid());
							params.put("user_order_no", order.getUserOrderNo());
							params.put("paytype", order.getPayMethodType() + "");
							params.put("orderno", order.getSerialno());
							params.put("realprice", order.getNumber() + "");
							String sbf = Md5Utils.getSign(params);
							sbf = sbf + "key=" + user.getAppSecret();
							logger.info("sign之前的：" + sbf);
							String sign = DigestUtils.md5DigestAsHex(sbf.getBytes()).toLowerCase();
							params.put("sign", sign);
							logger.info("sign之后：" + sign);
							logger.info(JSONObject.toJSONString(params));
							String notifyResult = null;
							String localhostNotifyUrl = domain+"/app/buyCoin/notifyNotice";
							if (localhostNotifyUrl.equals(order.getNotifyUrl())){
								notifyResult = "success";
							}else{
								for (int i = 0; i < 3; i++) {
									try {
										notifyResult = HttpUtil.postForm(order.getNotifyUrl(), params);
									} catch (IOException e) {
										e.printStackTrace();
									}
									logger.info("result:" + notifyResult);
									notifyResult = notifyResult.replaceAll("\"","");
									if ("success".equals(notifyResult)) {
										break;
									}
								}
							}
							logger.info("最后返回的结果为"+notifyResult);
							if (!"success".equals(notifyResult)){
								throw new WallterException("确认收款失败");
							}
							return ResponseData.success(200, "确认收款成功", null);
						}
					}
				}
			}
		}
		return ResponseData.error("确认失败");
	}


	@Transactional
	public ResponseData closeSellerTrade(String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				SellerOrder order = otcOrderService.findSellerorderBySellerId(seller.getSellerId());
				if (order != null && order.getStatus().intValue() == 0) {// 开启的订单才进行以下操作
					SellerWallter sellerWallter = new SellerWallter();
					sellerWallter.setCode("HC");
					sellerWallter.setType(1);
					sellerWallter.setSellerId(order.getSellerId());
					List<SellerWallter> sellerWallterList = sellerDao.findSellerWallter(sellerWallter);
					if (sellerWallterList != null && sellerWallterList.size() > 0) {
						sellerWallter = sellerWallterList.get(0);
						if (sellerWallter.getFrozenBalance().doubleValue() < order.getNumber().doubleValue()) {
							logger.info("【==========结束挂单出异常，"+order.getSerialNo()+"==用户钱包冻结余额为"+sellerWallter.getFrozenBalance().doubleValue()+"=========挂单数量为："+order.getNumber().doubleValue()+"=====】");
							throw new WallterException("结束失败");
						}

						order.setStatus(1);
						order.setCloseTime(new Date());
						int sellerOrderResult = otcOrderService.updateSellerOrder(order);
						if(sellerOrderResult <=0) {
							throw new WallterException("结束失败");
						}

						logger.info("交易订单未支付退金额,订单号【"+order.getSerialNo()+"】，退回前，用户【"
								+sellerWallter.getSellerId()+"】,可用余额:"
								+sellerWallter.getAvailableBalance()
								+",冻结余额："+sellerWallter.getFrozenBalance());

						SellerCash cash = sellerDao.selectSellerCashBySellerId(seller.getSellerId());
						Double cashPrice = 0.0;
						if (cash != null){
							cashPrice = cash.getCash();
							sellerDao.deleteSellerCashById(cash);
						}
						Double available = sellerWallter.getAvailableBalance();
						sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance() + order.getNumber()+cashPrice);
						sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance() - order.getNumber());
						sellerWallter.setUpdateTime(new Date());
						int result = sellerDao.updateSellerWallter(sellerWallter);
						if (result <= 0) {
							logger.info("【==========结束挂单出异常，更新余额导致=================】");
							throw new WallterException("结束失败");
						}
						AccountUpdateRecord updateRecord = new AccountUpdateRecord();
						updateRecord.setBeforePrice(available);
						updateRecord.setSerialno(order.getSerialNo());
						updateRecord.setAccountId(sellerWallter.getSellerId());
						updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
						updateRecord.setCode("HC");
						updateRecord.setCreateTime(new Date());
						updateRecord.setPhone(seller.getAccount());
						updateRecord.setSource("搬砖-HC");
						updateRecord.setType(AccountUpdateType.CLOSE_SELL_HC.code());
						updateRecord.setWalletCode(AccountUpdateWallet.BANZHUAN_WALLET_HC.code());
						updateRecord.setRemark("接单交易，关闭挂单,变更前可用余额："+available+"变更后可用余额："+sellerWallter.getAvailableBalance()+",退回数量："+order.getNumber()+",押金："+cashPrice);
						updateRecord.setPrice(order.getNumber()+cashPrice);
						updateRecord.setRoleId(1L);
						sellerDao.addAccountUpdateRecord(updateRecord);



						logger.info("交易订单未支付退金额,订单号【"+order.getSerialNo()+"】，退回后，用户【"
								+sellerWallter.getSellerId()+"】,可用余额:"
								+sellerWallter.getAvailableBalance()
								+",冻结余额："+sellerWallter.getFrozenBalance());
						return ResponseData.success(200, "结束成功", null);
					}
				}
			}
		}
		return ResponseData.error("结束失败");
	}

	public ResponseData checkVideo(String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				SellerBuyCoinNotice notice = sellerDao.findSellerBuyCoinNotice(seller.getSellerId());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("flag", false);
				if (notice != null && notice.getIsNotice() == 0) {
					map.put("flag", true);
					notice.setIsNotice(1);
					sellerDao.updateSellerBuyCoinNotice(notice);
				}
				return ResponseData.success(map);
			}
		}
		return ResponseData.error("检查失败");
	}

	public ResponseData checkExceptionMethod(String token) {
		synchronized (this){
			if (StringUtils.isNotBlank(token)) {
				Seller seller = (Seller) redisUtil.get(token);
				if (seller != null) {
					List<SellerPayMethod> list = sellerDao.findSellerPayMethodByException(seller.getSellerId());
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("type", null);
					map.put("account", null);
					map.put("remark", null);
					map.put("flag", false);
					for (SellerPayMethod sellerPayMethod : list) {
						if (sellerPayMethod.getFailNotice() != null && sellerPayMethod.getFailNotice() == 1) {
							map.put("type", sellerPayMethod.getType());
							map.put("account", sellerPayMethod.getAccount());
							map.put("remark", "该收款码已失败"+sellerPayMethod.getFailNumber()+"次，已被系统下架");
							map.put("flag", true);
							sellerPayMethod.setFailNotice(0);
							sellerPayMethod.setUpdateTime(new Date());
							sellerPayMethod.setFailNumber(0);
							sellerDao.updateSellerPayMethod(sellerPayMethod);
							break;
						}
					}
					return ResponseData.success(map);
				}
			}
			return ResponseData.error("检查失败");
		}
	}

	public ResponseData teamInfo(String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				Map<String, Object> map = sellerDao.findMyselfTeamByToday(seller.getSellerId());
				Double totalPrice = Double.valueOf(map.get("teamPrice").toString());
				List<TeamBonusSetting> bonusList = sellerDao.findTeamBonusSettingList();
				Integer level = 0;
				for (TeamBonusSetting teamBonusSetting : bonusList) {
					if (teamBonusSetting.getMinPrice() != null) {
						if (teamBonusSetting.getMinPrice().equals(totalPrice)) {
							level = teamBonusSetting.getLevel();
							break;
						}
						if (teamBonusSetting.getMinPrice() < totalPrice) {
							if (teamBonusSetting.getMaxPrice() == null
									|| teamBonusSetting.getMaxPrice() >= totalPrice) {
								level = teamBonusSetting.getLevel();
								break;
							}
						}
					}
				}
				map.put("level", level);
				return ResponseData.success(map);
			}
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData subordinateInfo(String token, Integer pageSize, Integer pageNumber) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
				Query query = new Query(params);
				List<Map<String, Object>> map = sellerDao.findMyselfSubordinateByToday(query);
				Integer total = sellerDao.findMyselfSubordinateByTodayCount(query);
				total = total == null ? 0 : total;
				PageUtils pageUtils = new PageUtils(total, map);
				return ResponseData.success(pageUtils);
			}
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData subordinateBonusList(String token, Integer pageSize, Integer pageNumber) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
				Query query = new Query(params);
				List<Map<String, Object>> map = sellerDao.subordinateBonusList(query);
				Integer total = sellerDao.subordinateBonusListCount(query);
				total = total == null ? 0 : total;
				PageUtils pageUtils = new PageUtils(total, map);
				return ResponseData.success(pageUtils);
			}
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData teamBonusInfo(String token) {

		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				Map<String, Object> map = sellerDao.teamBonusInfo(seller.getSellerId());
				return ResponseData.success(map);
			}
		}
		return ResponseData.error("获取失败");

	}

	public String getIpAddr(HttpServletRequest request) {  
	    String ip = request.getHeader("x-forwarded-for");  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("Proxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("WL-Proxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getRemoteAddr();  
	    }  
	    return ip;  
	}

    public ResponseData checkSwitchCity(String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				Map<String,Object> map = new HashMap<>();
				map.put("status","N");
				SellerCitySwitchSetting setting = new SellerCitySwitchSetting();
				setting.setSellerId(seller.getSellerId());
				setting = citySwitchSettingService.getOne(new QueryWrapper<>(setting));
				if (setting != null){
					map.put("status","Y");
				}
				return  ResponseData.success(map);
			}
		}
		return  ResponseData.error("检查失败");
    }


	public ResponseData openOrCloseCity(String token,HttpServletRequest request) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				String ip = IpUtil.getIpAddress(request);
				if (StringUtils.isBlank(ip)){
					return ResponseData.error("网络延迟，重新操作");
				}
				SellerCitySwitchSetting setting = new SellerCitySwitchSetting();
				setting.setSellerId(seller.getSellerId());
				setting = citySwitchSettingService.getOne(new QueryWrapper<>(setting));
				if (setting != null){
					citySwitchSettingService.removeById(setting);
					return  ResponseData.success(200,"取消成功",null);
				}
				setting = new SellerCitySwitchSetting();
				setting.setSellerId(seller.getSellerId());
				setting.setIsSwitch(1);
				setting.setCreateTime(new Date());
				setting.setIp(ip);
				citySwitchSettingService.save(setting);
				return ResponseData.success(200,"开启成功",null);
			}
		}
		return  ResponseData.error("操作失败");
	}

	public ResponseData getNoticeList(Integer pageSize, Integer pageNumber, String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			int total = 0;
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (seller != null) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
				Query query = new Query(params);
				List<SellerNotice> noticeList = sellerDao.getSellerNoticeList(query);
				for (SellerNotice notice : noticeList) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("content",notice.getContent());
					map.put("createTime",notice.getCreateTime().getTime()/1000);
					map.put("isSee",notice.getIsSee());
					map.put("id",notice.getId());
					list.add(map);
				}
				total = sellerDao.getSellerNoticeListCount(query);
				PageUtils pageUtils = new PageUtils(total, list);
				return ResponseData.success(pageUtils);
			}
		}
		return ResponseData.success(null);
	}

	public ResponseData getNoticeDetail(String token, Long id) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			if (seller != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				SellerNotice notice = this.sellerDao.getSellerNoticeById(id);
				if (notice != null && notice.getSellerId().equals(seller.getSellerId())){
					map.put("content",notice.getContent());
					map.put("createTime",notice.getCreateTime().getTime()/1000);
					map.put("isSee",1);
					notice.setIsSee(1);
					notice.setUpdateTime(new Date());
					this.sellerDao.updateSellerNotice(notice);
				}
				return ResponseData.success(map);
			}
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData exchangeFeeInfo(String token, Integer type) {
		ExchangeSetting  setting = sellerDao.getExchangeFeeSetting(type);
		if(setting != null) {
			Map<String,Object> param = new HashMap<>();
			param.put("feePrice", setting.getExchangeValue());
			return ResponseData.success(param);
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData exchangeList(Integer pageSize, Integer pageNumber, String token) {
		if (StringUtils.isNotBlank(token)) {
			Seller seller = (Seller) redisUtil.get(token);
			int total = 0;
			List<Map<String, Object>> list = new ArrayList<>();
			if (seller != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("pageNumber", pageNumber);
				params.put("pageSize", pageSize);
				params.put("sellerId", seller.getSellerId());
				Query query = new Query(params);
				List<ExchangeOrderRecord> orderList = sellerDao.getSellerExchangeOrderRecordList(query);
				for (ExchangeOrderRecord order : orderList) {
					Map<String, Object> map = new HashMap<>();
					if (order != null) {
						map.put("number", order.getNumber());//得到的数量
						map.put("source", order.getSource());//类型
						if("USDT".equals(order.getCoin())) {
							map.put("number", "-"+order.getTotalNumber()+"HC");//扣除的数量
							map.put("getNumber", order.getNumber()+"USDT");//得到的数量
						}else {
							map.put("number", "-"+order.getTotalNumber()+"USDT");
							map.put("getNumber", order.getNumber()+"HC");
						}
						map.put("exchagePrice","1:"+ order.getCode());//兑换比例
						map.put("feePrice", order.getFeePrice());//手续费
						map.put("createTime", order.getCreateTime().getTime() / 1000);// 时间为秒
					}
					list.add(map);
				}
				total = sellerDao.getSellerExchangeOrderRecordListCount(query);
			}
			PageUtils pageUtils = new PageUtils(total, list);
			return ResponseData.success(pageUtils);
		}
		return ResponseData.success(null);
	}

	public ResponseData updateEmailOrPhone(String token, String phone, String smsCode, String emailCode, Integer type,String imageCode,String ckToken) {
		if(type == null || type <=0) {
			return ResponseData.error("参数有误");
		}
		if(StringUtils.isBlank(phone)) {
			return ResponseData.error("请输入手机号码");
		}
		if(StringUtils.isBlank(smsCode)) {
			return ResponseData.error("请输入手机验证码");
		}
		if (StringUtils.isBlank(imageCode)){
			return ResponseData.error("请输入图形验证码");
		}

		if (StringUtils.isBlank(ckToken)){
			return ResponseData.error("图形验证码已失效，请重新刷新");
		}
		if (redisUtil.get(Constant.CLIENT_TOKEN+ckToken) == null){
			return ResponseData.error("图形验证码已失效");
		}
		String redisCkToken = (String) redisUtil.get(Constant.CLIENT_TOKEN+ckToken);
		if (!redisCkToken.equals(imageCode)){
			return ResponseData.error("输入的图形验证码有误");
		}

		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
				if (StringUtils.isNotBlank(seller.getPhone())){
					return ResponseData.error("您已绑定手机号码");
				}
				Seller phoneSeller = sellerDao.findSellerByPhone(phone);
				if(phoneSeller != null && !phoneSeller.getSellerId().equals(seller.getSellerId())) {
					return ResponseData.error("该手机号码已被绑定，请重新输入其他手机号");
				}

				if(redisUtil.get(Constant.SMS+phone) == null) {
					return ResponseData.error("手机验证有误");
				}
				String oldSmsCode = (String) redisUtil.get(Constant.SMS+phone);
				if(!oldSmsCode.equals(smsCode)) {
					return ResponseData.error("输入手机验证有误");
				}
				seller.setPhone(phone);
				seller.setUpdateTime(new Date());
				sellerDao.updateSeller(seller);
				//清空保存在redis中的验证码信息
				redisUtil.del(Constant.SMS+phone,Constant.CLIENT_TOKEN+ckToken);
				return ResponseData.success(200, "绑定成功", null);
		}
		return ResponseData.error("绑定失败");
	}

	public ResponseData openGoogleCode(String token) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			String secret = GoogleGenerator.generateSecretKey();
			Map<String,Object> param = new HashMap<String, Object>();
			param.put("key", secret);
			param.put("isPhone", seller.getLoginMethod() ==1 ? true:false);
			redisUtil.set("KEY_"+token, secret);
			return ResponseData.success(200, "开启成功", param);
		}
		return ResponseData.error("开启失败");
	}

	public ResponseData authGoogleCode(String token, String code,String tokenCode) {
		Seller seller = null;
		if(StringUtils.isBlank(token)){
			if (StringUtils.isBlank(tokenCode)){
				return ResponseData.error("认证失败");
			}
			seller = (Seller) redisUtil.get(tokenCode);
		}else{
			seller =  (Seller) redisUtil.get(token);
		}
		if (seller != null) {
			if(StringUtils.isBlank(seller.getGoogleSecret())) {
				return ResponseData.error("还未绑定谷歌验证");
			}
			GoogleGenerator ga = new GoogleGenerator();
			long time = System.currentTimeMillis ();
			boolean result = ga.check_code(seller.getGoogleSecret(), code, time);
			if(result) {
				return ResponseData.success(200, "认证完成", null);
			}
		}
		return ResponseData.error("认证失败");
	}

	public ResponseData sumbitGoogle(String token, String code, String googleCode) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			Boolean loginMethod = seller.getLoginMethod() ==1 ? true:false;
			if(loginMethod) {
				if(redisUtil.get(Constant.SMS+seller.getPhone()) == null) {
					return ResponseData.error("输入手机验证码有误");
				}
				String smsCode = (String) redisUtil.get(Constant.SMS+seller.getPhone());
				if(!smsCode.equals(code)) {
					return ResponseData.error("输入手机验证码有误");
				}
			}else {
				if(redisUtil.get(Constant.SMS+seller.getEmail()) == null) {
					return ResponseData.error("输入邮箱验证码有误");
				}
				String smsCode = (String) redisUtil.get(Constant.SMS+seller.getEmail());
				if(!smsCode.equals(code)) {
					return ResponseData.error("输入邮箱验证码有误");
				}
			}
			String key = (String) redisUtil.get("KEY_"+token);
			GoogleGenerator ga = new GoogleGenerator();
			long time = System.currentTimeMillis();
			boolean result = ga.check_code(key, googleCode, time);
			if(!result) {
				return ResponseData.error("输入谷歌验证码有误");
			}
			seller.setBingGoogle(1);
			seller.setGoogleSecret(key);
			sellerDao.updateSeller(seller);
			return ResponseData.success(200, "开启成功", null);
		}
		return ResponseData.error("开启失败");
	}

	public ResponseData flowRecordInfo(Integer type, Long id, String token, String starTime, String endTime) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sellerId", seller.getSellerId());
			if (StringUtils.isNotBlank(starTime)){
				params.put("starTime",starTime);
			}
			if (StringUtils.isNotBlank(endTime)){
				params.put("endTime",endTime);
			}
			if (type != null && type.equals(1) ){
				SellerWallter sellerWallter = new SellerWallter();
				sellerWallter.setSellerWallterId(id);
				List<SellerWallter> sellerWallterList = sellerDao.findSellerWallter(sellerWallter);
				if (sellerWallterList == null || sellerWallterList.size() < 0) {
					return ResponseData.error("获取失败");
				}
				sellerWallter = sellerWallterList.get(0);
				params.put("code", sellerWallter.getCode());
				Map<String,Object> returnParam = sellerDao.getFlowRecordStatistics(params);
				return ResponseData.success(returnParam);
			}else if(type != null && type.equals(2) ){
				params.put("code", "HC");
				Map<String,Object> returnParam = sellerDao.getUserAccountFlowRecordStatistics(params);
				return ResponseData.success(returnParam);
			}else if(type != null && type.equals(3) ){
				SellerProfitWallter sellerProfitWallter = new SellerProfitWallter();
				sellerProfitWallter.setProfitWallterId(id);
				sellerProfitWallter.setSellerId(seller.getSellerId());
				List<SellerProfitWallter> profitWallterList = sellerDao
						.findSellerProfitWallterList(sellerProfitWallter);
				if (profitWallterList == null || profitWallterList.size() < 0) {
					return ResponseData.error("获取失败");
				}
				sellerProfitWallter = profitWallterList.get(0);
				params.put("code", sellerProfitWallter.getCode());
				Map<String,Object> returnParam = sellerDao.getSellerProfitAccountFlowRecordStatistics(params);
				return ResponseData.success(returnParam);
			}
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData buyCoinTime(String token) {
		SellerOrderTimeSetting orderTimeSetting = orderTimeSettingService.getOne(null);
		if (orderTimeSetting != null){
			Map<String,Object> map = new HashMap<>();
			map.put("starTime",orderTimeSetting.getStarTime());
			map.put("endTime",orderTimeSetting.getEndTime());
			return  ResponseData.success(map);
		}
		return ResponseData.success();
	}

	public ResponseData otcCoinTime(String token) {
		OtcpCannelNumberSetting setting = sellerDao.findOtcpCannelNumberSetting();
		if (setting != null){
			Map<String,Object> map = new HashMap<>();
			map.put("number",setting.getNumber());
			map.put("endTime",setting.getMinTime());
			return  ResponseData.success(map);
		}
		return ResponseData.success();

	}

	public ResponseData isHaveNotice(String token) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			Map<String,Object> map = new HashMap<>();
			int count = sellerDao.findSellerNoticeCount(seller.getSellerId());
			map.put("isHave",count >0 ? true :false);
			return ResponseData.success(map);
		}
		return ResponseData.error("获取失败");
	}

	public ResponseData loginAuthGoogle(String phone, String password) {
		logger.info("判断是否需要谷歌验证+"+phone);
		if (StringUtils.isBlank(phone)){
			return ResponseData.error("手机号码不能为空");
		}
		if (StringUtils.isBlank(password)){
			return ResponseData.error("密码不能为空");
		}
		Seller seller = sellerDao.findSellerByPhone(phone);
		if (seller == null) {
			return ResponseData.error("该手机号不存在，请注册后再登录");
		}
		if (!seller.getPassword().equals(Md5Utils.GetMD5Code(password))) {
			return ResponseData.error("输入的密码有误");
		}
		if (seller.getEnabled() == 1) {
			return ResponseData.error("该手机号已被禁用");
		}
		Map<String,Object> map = new HashMap<>();
		map.put("isNeed",0);
		logger.info("判断是否需要谷歌验证");
		if(seller.getBingGoogle() != null && seller.getBingGoogle().equals(1)) {
			map.put("isNeed",1);
		}
		logger.info("判断是否需要谷歌验证"+map);
		return ResponseData.success(map);
	}

    public ResponseData getTotalMoney(String token) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			SellerWallter sellerWallter = new SellerWallter();
			sellerWallter.setSellerId(seller.getSellerId());
			sellerWallter.setCode("USDT");
			Double usdtTotal = 0.0;
			List<SellerWallter> sellerWallterList = sellerDao.findSellerWallter(sellerWallter);
			for (SellerWallter sellerWallter1 : sellerWallterList){
				usdtTotal = usdtTotal+(sellerWallter1.getAvailableBalance() == null  ? 0.0 : sellerWallter1.getAvailableBalance())
								+(sellerWallter1.getFrozenBalance() == null ?0.0 : sellerWallter1.getFrozenBalance());
			}
			if (seller.getIsAccepter() != null && seller.getIsAccepter().equals(1) ){
				UserWallter userWallter = new UserWallter();
				userWallter.setType(1);
				userWallter.setUserId(seller.getUserId());
				List<UserWallter> list = sellerDao.findUserWallterList(userWallter);
				for (UserWallter userWallter1 : list){
					usdtTotal = usdtTotal+(userWallter1.getAvailableBalance() == null  ? 0.0 : userWallter1.getAvailableBalance())
							+(userWallter1.getFrozenBalance() == null ?0.0 : userWallter1.getFrozenBalance());
				}
			}

			USDTOtcpExchange usdtOtcpExchange = this.sellerDao.getOTCMarkInfo();
			Double hcTotal = usdtTotal*usdtOtcpExchange.getValue();
			Map<String,Object> map = new HashMap<>();
			map.put("usdtTotal",usdtTotal);
			map.put("hcTotal",hcTotal);
			return ResponseData.success(map);
		}
		return ResponseData.error("获取失败");
    }

	public ResponseData checkBindPhone(String token) {
		Seller seller = (Seller) redisUtil.get(token);
		if (seller != null) {
			Map<String,Object> map = new HashMap<>();
			map.put("isBind",false);
			if (StringUtils.isNotBlank(seller.getPhone())){
				map.put("isBind",true);
			}
			return  ResponseData.success(map);
		}
		return ResponseData.error("获取失败");
	}
}
