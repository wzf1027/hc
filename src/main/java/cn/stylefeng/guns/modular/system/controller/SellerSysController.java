package cn.stylefeng.guns.modular.system.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.stylefeng.guns.core.aop.WallterException;
import cn.stylefeng.guns.core.common.page.LayuiPageInfo;
import cn.stylefeng.guns.core.util.FlowRecordConstant;
import cn.stylefeng.guns.modular.app.service.OtcOrderService;
import cn.stylefeng.guns.modular.system.entity.*;
import cn.stylefeng.guns.modular.system.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.Const;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.util.Md5Utils;
import cn.stylefeng.guns.modular.app.mapper.SellerMapper;
import cn.stylefeng.guns.modular.system.warpper.SellerWallterListWrapper;
import cn.stylefeng.guns.modular.system.warpper.SellerWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

@Controller
@RequestMapping("/sellerMgr")
public class SellerSysController extends BaseController {

	   private String PREFIX = "/modular/system/seller/";
	   
	   @Autowired
	   private SellerSysService sellerSysService;
	   
	   @Autowired
	   private UserService userService;
	   
	   @Autowired
		private SellerMapper sellerDao;
	   
	   @Autowired
	   private MoneyPasswordSettingMgrService settingService;
	   
	   @Autowired
	   private WallterLogService wallterLogService;

	   @Autowired
	   private OtcOrderService otcOrderService;

	   @RequestMapping("")
	    public String index() {
	        return PREFIX + "seller.html";
	    }

		@RequestMapping("/relation")
		public String relation() {
			return PREFIX + "relation.html";
		}

	@RequestMapping("/listRelationTree")
	@ResponseBody
	public Object listRelationTree(@RequestParam(required = false) String condition) {
		//根据条件查询
		List<Map<String, Object>> relation = sellerSysService.selectListTreeByCondition(condition);
		LayuiPageInfo result = new LayuiPageInfo();
		result.setData(relation);
		return result;
	}

	   @RequestMapping(value = "/list")
	   @ResponseBody
	    public Object list(@RequestParam(required = false)String condition,
				@RequestParam(required = false) String phone,
				@RequestParam(required = false) Integer isAccepter,
				@RequestParam(required = false) String recommend,
				@RequestParam(required = false) Integer isAuth,
				@RequestParam(required = false) Integer enabled,
	            @RequestParam(required = false) String timeLimit) {
		   //拼接查询条件
	        String beginTime = "";
	        String endTime = "";

	        if (ToolUtil.isNotEmpty(timeLimit)) {
	            String[] split = timeLimit.split(" - ");
	            beginTime = split[0];
	            endTime = split[1];
	        }
	        Page<Map<String, Object>> list = this.sellerSysService.list(condition,phone,beginTime,endTime,isAccepter,recommend,isAuth,enabled);
	        Page<Map<String, Object>> wrap = new SellerWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }

	@RequestMapping("/seller_time")
	public String seller_time(Model model) {
		SellerTimeSetting setting =  this.sellerSysService.getSellerTimeSetting();
		model.addAttribute("time",setting.getValue());
		model.addAttribute("id",setting.getId());
	   	return PREFIX + "seller_time.html";
	}

	@RequestMapping(value = "/updateSellerTime")
	@ResponseBody
	public Object update(SellerTimeSetting setting) {
		if (ToolUtil.isOneEmpty(setting.getId())) {
			throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
		}
		if (setting.getValue() == null ){
			return ResponseData.error("时间不能为空");
		}
		this.sellerSysService.updateSellerTimeSetting(setting);
		return SUCCESS_TIP;
	}

	   
	   @RequestMapping(value = "/wallterList")
	   @ResponseBody
	    public Object wallterList(Long sellerId,Integer type) {
	        Page<Map<String, Object>> list = this.sellerSysService.wallterList(sellerId,type);
	        Page<Map<String, Object>> wrap = new SellerWallterListWrapper(list).wrap();
	        return LayuiPageFactory.createPageInfo(wrap);
	    }
	   
		@RequestMapping("/authPage/{sellerId}")
	    public String authPage(@PathVariable Long sellerId, Model model) {
	        Seller seller = this.sellerSysService.getById(sellerId);
	        model.addAllAttributes(BeanUtil.beanToMap(seller));
	        LogObjectHolder.me().set(seller);
	        return PREFIX + "authPage.html";
	    }


	@RequestMapping("/authDetailPage/{sellerId}")
	public String authDetailPage(@PathVariable Long sellerId, Model model) {
		Seller seller = this.sellerSysService.getById(sellerId);
		model.addAllAttributes(BeanUtil.beanToMap(seller));
		LogObjectHolder.me().set(seller);
		return PREFIX + "authDetailPage.html";
	}

		@RequestMapping("/updteWallter_Number/{sellerWallterId}")
	    public String updteWallter_Number(@PathVariable Long sellerWallterId, Model model) {
	        SellerWallter sellerWallter = this.sellerSysService.findSellerWallter(sellerWallterId);
	        model.addAllAttributes(BeanUtil.beanToMap(sellerWallter));
	        LogObjectHolder.me().set(sellerWallter);
	        return PREFIX + "seller_wallter_number.html";
	    }
		
		@RequestMapping(value = "/updateWallterNumber")
	    @ResponseBody
	    @Transactional
	    @Permission(Const.ADMIN_NAME)
	    public ResponseData updateWallterNumber(SellerWallter sellerWallter) {
			  SellerWallter old = this.sellerSysService.findSellerWallter(sellerWallter.getSellerWallterId());
			 if(old == null) {
				 return ResponseData.error("更改失败");
			 }		
			Seller seller = sellerDao.findSellerbyId(old.getSellerId());
			if(seller == null) {
				 return ResponseData.error("更改失败");
			}
			
			if(StringUtils.isBlank(sellerWallter.getPassword())) {
				return ResponseData.error("请输入资产密码");
			}
			if (StringUtils.isBlank(sellerWallter.getRemark())){
				return ResponseData.error("请输入备注信息");
			}
			MoneyPasswordSetting setting = settingService.getOne(null);
			if(!setting.getPassword().equals(Md5Utils.GetMD5Code(sellerWallter.getPassword()))) {
				return ResponseData.error("输入资产密码有误");
			}
			if (old.getAvailableBalance()+sellerWallter.getAvailableBalance()<0){
				return ResponseData.error("输入的要减的可用余额过大");
			}
			if (old.getFrozenBalance()+sellerWallter.getFrozenBalance()<0){
				return ResponseData.error("输入的要减的冻结余额过大");
			}
			if(sellerWallter.getAvailableBalance() >0 || sellerWallter.getAvailableBalance() <0){
				AccountUpdateRecord updateRecord = new AccountUpdateRecord();
				updateRecord.setBeforePrice(old.getAvailableBalance());
				updateRecord.setAfterPrice(sellerWallter.getAvailableBalance()+old.getAvailableBalance());
				updateRecord.setCode(sellerWallter.getCode());
				updateRecord.setCreateTime(new Date());
				updateRecord.setPhone(seller.getAccount());
				updateRecord.setSource("会员"+sellerWallter.getCode());
				updateRecord.setType("后台修改");
				updateRecord.setRemark(sellerWallter.getRemark());
				updateRecord.setPrice(sellerWallter.getAvailableBalance());
				updateRecord.setRoleId(1L);
				updateRecord.setAccountId(old.getSellerId());
				sellerDao.addAccountUpdateRecord(updateRecord);

				SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
				flowRecord.setCode(sellerWallter.getCode());
				flowRecord.setCreateTime(new Date());
				flowRecord.setPrice(sellerWallter.getAvailableBalance());
				flowRecord.setSellerId(seller.getSellerId());
				flowRecord.setSource("系统变更可用余额");
				flowRecord.setType(FlowRecordConstant.SYSTEM_UPDATE_MONEY);
				sellerDao.addSellerAccountFlowRecord(flowRecord);

			}

			if(sellerWallter.getFrozenBalance() >0 || sellerWallter.getFrozenBalance() <0){
				AccountUpdateRecord updateRecord1 = new AccountUpdateRecord();
				updateRecord1.setBeforePrice(old.getFrozenBalance());
				updateRecord1.setAfterPrice(sellerWallter.getFrozenBalance()+old.getFrozenBalance());
				updateRecord1.setCode(sellerWallter.getCode());
				updateRecord1.setCreateTime(new Date());
				updateRecord1.setPhone(seller.getPhone());
				updateRecord1.setSource("会员"+sellerWallter.getCode());
				updateRecord1.setType("后台修改");
				updateRecord1.setRemark(sellerWallter.getRemark());
				updateRecord1.setPrice(sellerWallter.getFrozenBalance());
				updateRecord1.setRoleId(1L);
				updateRecord1.setAccountId(old.getSellerId());
				sellerDao.addAccountUpdateRecord(updateRecord1);
				SellerAccountFlowRecord flowRecord = new SellerAccountFlowRecord();
				flowRecord.setCode(sellerWallter.getCode());
				flowRecord.setCreateTime(new Date());
				flowRecord.setPrice(sellerWallter.getFrozenBalance());
				flowRecord.setSellerId(seller.getSellerId());
				flowRecord.setSource("系统变更冻结余额");
				flowRecord.setType(FlowRecordConstant.SYSTEM_UPDATE_MONEY);
				sellerDao.addSellerAccountFlowRecord(flowRecord);
			}

			
			 old.setAvailableBalance(sellerWallter.getAvailableBalance()+old.getAvailableBalance());
			 old.setFrozenBalance(sellerWallter.getFrozenBalance()+old.getFrozenBalance());
			 old.setUpdateTime(new Date());
	 		 this.sellerSysService.updateSellerWallter(old);
	 		 



	 		 //更新余额日志操作
	 		 WallterLog log = new WallterLog();
	 		 log.setContent("修改"+old.getCode()+"可用余额增加或减少："+sellerWallter.getAvailableBalance()+","+old.getCode()+"冻结余额增加或减少："+sellerWallter.getFrozenBalance());
	 		 log.setCreateTime(new Date());
	 		 log.setLogUser(ShiroKit.getUser().getName());
	 		 log.setSellerId(old.getSellerId());
	 		 wallterLogService.save(log);
	 		 return ResponseData.success();
	    }
		
		@RequestMapping(value = "/updateAuth")
	    @ResponseBody
	    public ResponseData updateAuth(@RequestParam Long sellerId,@RequestParam Integer status) {
	 		return this.sellerSysService.updateAuth(sellerId,status);
	    }

	/**
	 * 启用禁用状态
	 * @param sellerId
	 * @return
	 */
	@RequestMapping("/freeze")
	    @ResponseBody
	    public ResponseData freeze(@RequestParam Long sellerId) {
	        if (ToolUtil.isEmpty(sellerId)) {
	            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
	        }
	        Seller seller =  this.sellerSysService.getById(sellerId);
	        if(seller.getEnabled()==0) {//禁用
	        	seller.setEnabled(1);
	        	seller.setUpdateTime(new Date());
	        	if(seller.getIsAccepter() ==1) {
	        		User user = userService.getById(seller.getUserId());
	        		if(user != null) {
	        			user.setStatus("DISABLE");
	        			user.setUpdateUser(1l);
	        			user.setUpdateTime(new Date());
	        			userService.updateById(user);
	        		}
	        	}

	        	//判断是否开启的挂单
				SellerOrder sellerOrder =otcOrderService.findSellerorderBySellerId(seller.getSellerId());
	        	if (sellerOrder != null ){
	        		sellerOrder.setStatus(1);
	        		sellerOrder.setUpdateTime(new Date());
	        		sellerOrder.setCloseTime(new Date());
	        		otcOrderService.updateSellerOrder(sellerOrder);
					SellerWallter sellerWallter = new SellerWallter();
					sellerWallter.setSellerId(seller.getSellerId());
					sellerWallter.setCode("HC");
					List<SellerWallter> sellerWallterList = this.sellerDao.findSellerWallter(sellerWallter);
					if (sellerWallter == null || sellerWallterList.size() <=0){
						throw new WallterException("更新失败");
					}
						sellerWallter = sellerWallterList.get(0);
						AccountUpdateRecord updateRecord = new AccountUpdateRecord();
						updateRecord.setBeforePrice(sellerWallter.getAvailableBalance());

						SellerCash cash = this.sellerDao.selectSellerCashBySellerId(seller.getSellerId());
						Double cashPrice = 0.0;
						if (cash != null){
							cashPrice = cash.getCash();
							sellerDao.deleteSellerCashById(cash);
						}

						sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()+sellerOrder.getNumber()+cashPrice);
						sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-sellerOrder.getNumber());
						sellerWallter.setUpdateTime(new Date());
						int result = this.sellerDao.updateSellerWallter(sellerWallter);
						if(result <=0){
							throw new WallterException("更新失败");
						}
						updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
						updateRecord.setCode("HC");
						updateRecord.setCreateTime(new Date());
						updateRecord.setPhone(seller.getAccount());
						updateRecord.setSource("会员HC");
						updateRecord.setType("接单交易");
						updateRecord.setRemark("接单交易停止，退回");
						updateRecord.setPrice(sellerOrder.getNumber());
						updateRecord.setRoleId(1L);
						updateRecord.setSerialno(sellerOrder.getSerialNo());
						updateRecord.setAccountId(seller.getSellerId());
						sellerDao.addAccountUpdateRecord(updateRecord);


						List<SellerPayMethod> sellerPayMethodList = sellerDao.findSellerPayMethodByIsCheck(seller.getSellerId(),null);
						for (SellerPayMethod sellerPayMethod : sellerPayMethodList){
							sellerPayMethod.setIsCheck(0);
							sellerPayMethod.setIsSoldOut(1);
							sellerPayMethod.setSoldOutTime(new Date());
							this.sellerDao.updateSellerPayMethod(sellerPayMethod);
						}
				}

	        }else {//启用
	        	seller.setEnabled(0);
	        	seller.setUpdateTime(new Date());
	        	if(seller.getIsAccepter() ==1) {
	        		User user = userService.getById(seller.getUserId());
	        		if(user != null) {
	        			user.setStatus("ENABLE");
	        			user.setUpdateUser(1l);
	        			user.setUpdateTime(new Date());
	        			userService.updateById(user);
	        		}
	        	}
	        	SellerPayMethod payMethod = new SellerPayMethod();
	        	payMethod.setIsSoldOut(1);
	        	payMethod.setSellerId(seller.getSellerId());
	        	List<SellerPayMethod> list = sellerDao.findSellerPayMethodList(payMethod);
	        	for (SellerPayMethod sellerPayMethod : list){
	        		sellerPayMethod.setIsSoldOut(0);
	        		sellerPayMethod.setUpdateTime(null);
	        		sellerPayMethod.setFailNumber(0);
	        		sellerPayMethod.setFailNotice(0);
	        		sellerDao.updateSellerPayMethod(sellerPayMethod);
				}
	        }
	    	this.sellerSysService.updateById(seller);
	        return SUCCESS_TIP;
	    }


	/**
	 * 启用禁用接单
	 * @param sellerId
	 * @return
	 */
	@RequestMapping("/gradEnabledFreeze")
	@ResponseBody
	public ResponseData gradEnabledFreeze(@RequestParam Long sellerId) {
		if (ToolUtil.isEmpty(sellerId)) {
			throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
		}
		Seller seller =  this.sellerSysService.getById(sellerId);
		if(seller.getGradEnabled()==0) {//禁用
			seller.setGradEnabled(1);
			//判断是否开启的挂单
			SellerOrder sellerOrder =otcOrderService.findSellerorderBySellerId(seller.getSellerId());
			if (sellerOrder != null ){
				sellerOrder.setStatus(1);
				sellerOrder.setUpdateTime(new Date());
				sellerOrder.setCloseTime(new Date());
				otcOrderService.updateSellerOrder(sellerOrder);
				SellerWallter sellerWallter = new SellerWallter();
				sellerWallter.setSellerId(seller.getSellerId());
				sellerWallter.setCode("HC");
				List<SellerWallter> sellerWallterList = this.sellerDao.findSellerWallter(sellerWallter);
				if (sellerWallter == null || sellerWallterList.size() <=0){
					throw new WallterException("更新失败");
				}
					sellerWallter = sellerWallterList.get(0);
					AccountUpdateRecord updateRecord = new AccountUpdateRecord();
					updateRecord.setBeforePrice(sellerWallter.getAvailableBalance());

					SellerCash cash = this.sellerDao.selectSellerCashBySellerId(seller.getSellerId());
					Double cashPrice = 0.0;
					if (cash != null){
						cashPrice = cash.getCash();
						sellerDao.deleteSellerCashById(cash);
					}

					sellerWallter.setAvailableBalance(sellerWallter.getAvailableBalance()+sellerOrder.getNumber()+cashPrice);
					sellerWallter.setFrozenBalance(sellerWallter.getFrozenBalance()-sellerOrder.getNumber());
					sellerWallter.setUpdateTime(new Date());
					int result = this.sellerDao.updateSellerWallter(sellerWallter);
					if(result <=0){
						throw new WallterException("更新失败");
					}
					updateRecord.setAfterPrice(sellerWallter.getAvailableBalance());
					updateRecord.setCode("HC");
					updateRecord.setCreateTime(new Date());
					updateRecord.setPhone(seller.getAccount());
					updateRecord.setSource("会员HC");
					updateRecord.setType("接单交易");
					updateRecord.setRemark("接单交易停止，退回");
					updateRecord.setPrice(sellerOrder.getNumber());
					updateRecord.setRoleId(1L);
					updateRecord.setSerialno(sellerOrder.getSerialNo());
					updateRecord.setAccountId(seller.getSellerId());
					sellerDao.addAccountUpdateRecord(updateRecord);


					List<SellerPayMethod> sellerPayMethodList = sellerDao.findSellerPayMethodByIsCheck(seller.getSellerId(),null);
					for (SellerPayMethod sellerPayMethod : sellerPayMethodList){
						sellerPayMethod.setIsCheck(0);
						sellerPayMethod.setIsSoldOut(1);
						sellerPayMethod.setSoldOutTime(new Date());
						this.sellerDao.updateSellerPayMethod(sellerPayMethod);
					}
			}
		}else {//启用
			seller.setGradEnabled(0);
			SellerPayMethod payMethod = new SellerPayMethod();
			payMethod.setIsSoldOut(1);
			payMethod.setSellerId(seller.getSellerId());
			List<SellerPayMethod> list = sellerDao.findSellerPayMethodList(payMethod);
			for (SellerPayMethod sellerPayMethod : list){
				sellerPayMethod.setIsSoldOut(0);
				sellerPayMethod.setUpdateTime(null);
				sellerPayMethod.setFailNumber(0);
				sellerPayMethod.setFailNotice(0);
				sellerDao.updateSellerPayMethod(sellerPayMethod);
			}
		}
		seller.setUpdateTime(new Date());
		this.sellerSysService.updateById(seller);
		return SUCCESS_TIP;
	}

	/**
	 * 启用禁用出售
	 * @param sellerId
	 * @return
	 */
	@RequestMapping("/sellEnabledFreeze")
	@ResponseBody
	public ResponseData sellEnabledFreeze(@RequestParam Long sellerId) {
		if (ToolUtil.isEmpty(sellerId)) {
			throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
		}
		Seller seller =  this.sellerSysService.getById(sellerId);
		if(seller.getSellEnabled()==0) {//禁用
			seller.setSellEnabled(1);
		}else {//启用
			seller.setSellEnabled(0);
			SellerPayMethod payMethod = new SellerPayMethod();
			payMethod.setIsSoldOut(1);
			payMethod.setSellerId(seller.getSellerId());
			List<SellerPayMethod> list = sellerDao.findSellerPayMethodList(payMethod);
			for (SellerPayMethod sellerPayMethod : list){
				sellerPayMethod.setIsSoldOut(0);
				sellerPayMethod.setUpdateTime(null);
				sellerPayMethod.setFailNumber(0);
				sellerPayMethod.setFailNotice(0);
				sellerDao.updateSellerPayMethod(sellerPayMethod);
			}
		}
		seller.setUpdateTime(new Date());
		this.sellerSysService.updateById(seller);
		return SUCCESS_TIP;
	}




	/**
	 * 重置密码
	 * @param sellerId
	 * @return
	 */
	@RequestMapping("/updatePassword")
	@ResponseBody
	public ResponseData updatePassword(@RequestParam Long sellerId) {
		if (ToolUtil.isEmpty(sellerId)) {
			throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
		}
		Seller seller =  this.sellerSysService.getById(sellerId);
		if (StringUtils.isNotBlank(seller.getTraderPassword())){
			seller.setTraderPassword(Md5Utils.GetMD5Code("123456"));
		}
		seller.setPassword(Md5Utils.GetMD5Code("123456"));
		seller.setUpdateTime(new Date());
		this.sellerSysService.updateById(seller);
		return SUCCESS_TIP;
	}

	/**
	 * 启用禁用划转
	 * @param sellerId
	 * @return
	 */
	@RequestMapping("/tranferEnabledFreeze")
	@ResponseBody
	public ResponseData tranferEnabledFreeze(@RequestParam Long sellerId) {
		if (ToolUtil.isEmpty(sellerId)) {
			throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
		}
		Seller seller =  this.sellerSysService.getById(sellerId);
		if(seller.getTranferEnabled()==0) {//禁用
			seller.setTranferEnabled(1);
		}else {//启用
			seller.setTranferEnabled(0);
		}
		seller.setUpdateTime(new Date());
		this.sellerSysService.updateById(seller);
		return SUCCESS_TIP;
	}

	/**
	 * 启用禁用购买
	 * @param sellerId
	 * @return
	 */
	@RequestMapping("/buyEnabledFreeze")
	@ResponseBody
	public ResponseData buyEnabledFreeze(@RequestParam Long sellerId) {
		if (ToolUtil.isEmpty(sellerId)) {
			throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
		}
		Seller seller =  this.sellerSysService.getById(sellerId);
		if(seller.getBuyEnabled()==0) {//禁用
			seller.setBuyEnabled(1);
		}else {//启用
			seller.setBuyEnabled(0);
		}
		seller.setUpdateTime(new Date());
		this.sellerSysService.updateById(seller);
		return SUCCESS_TIP;
	}



	@RequestMapping("/bingGoogleUnFreeze")
	@ResponseBody
	public ResponseData bingGoogleUnFreeze(@RequestParam Long sellerId) {
		if (ToolUtil.isEmpty(sellerId)) {
			throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
		}
		Seller seller =  this.sellerSysService.getById(sellerId);
		seller.setBingGoogle(0);
		seller.setUpdateTime(new Date());
		seller.setGoogleSecret(null);
		this.sellerSysService.updateById(seller);
		return SUCCESS_TIP;
	}

}
