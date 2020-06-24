package cn.stylefeng.guns.modular.app.controller;



import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.util.ResponseData;
import cn.stylefeng.guns.modular.app.dto.SellerAuthenticationDto;
import cn.stylefeng.guns.modular.app.dto.SellerPayMethodDto;
import cn.stylefeng.guns.modular.app.service.SellerService;

/**
 * 码商控制器
 */
@RestController
@RequestMapping(value = "/app/seller")
public class SellerController {

	@Resource
	private SellerService sellerService;
	
    /**
     * 获取用户基本信息
     */
    @RequestMapping(value = "/getSellerDetail")
    public ResponseData getSellerDetail(@RequestHeader("token") String token) {
        return sellerService.getSellerInfo(token);
    }


	/**
	 * 获取我的总资产
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping(value = "/getTotalMoney")
	public ResponseData getTotalMoney(@RequestHeader("token") String token) {
		return sellerService.getTotalMoney(token);
	}
    
    /**
     * 更改头像
     * @param token 用户唯一标识
     * @param icon 头像地址
     * @return  ResponseData
     */
    @RequestMapping(value = "/updateIcon")
    public ResponseData updateIcon(@RequestHeader("token") String token, String icon) {
        return sellerService.updateIcon(token, icon);
    }

	/**
	 * 检查是否绑定手机号码
	 * @param token 用户唯一标识
	 * @return  ResponseData
	 */
	@RequestMapping(value = "/checkBindPhone")
	public ResponseData checkBindPhone(@RequestHeader("token") String token) {
		return sellerService.checkBindPhone(token);
	}


	/**
	 * 绑定手机
	 * @param token 用户唯一标识
	 * @param phone 手机号码
	 * @param smsCode 手机验证码
	 * @param imageCode 图形验证码
	 * @param ckToken  图形验证码的key
	 * @return ResponseData
	 */
	@RequestMapping(value = "/updateEmailOrPhone")
	public ResponseData updateEmailOrPhone(@RequestHeader("token") String token
			, String phone,String smsCode,String imageCode,String ckToken) {
		return sellerService.updateEmailOrPhone(token, phone,smsCode,null,1,imageCode,ckToken);
	}

	/**
	 * 开启谷歌验证
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping(value = "/openGoogleCode")
	public ResponseData openGoogleCode(@RequestHeader("token") String token) {
		return sellerService.openGoogleCode(token);
	}

	/**
	 * 谷歌验证码校验
	 * @param token 用户唯一标识
	 * @param code 验证码
	 * @return ResponseData
	 */
	@RequestMapping(value = "/authGoogleCode")
	public ResponseData authGoogleCode(@RequestHeader("token") String token,String code
			,@RequestParam(value = "tokenCode",required = false) String tokenCode) {
		return sellerService.authGoogleCode(token,code,tokenCode);
	}

	/**
	 * 绑定谷歌验证
	 * @param token 用户唯一标识
	 * @param code 手机/邮箱验证码
	 * @param googleCode 谷歌验证码
	 * @return ResponseData
	 */
	@RequestMapping(value = "/sumbitGoogle")
	public ResponseData sumbitGoogle(@RequestHeader("token") String token,String code,String googleCode) {
		return sellerService.sumbitGoogle(token,code,googleCode);
	}

    /**
     * 更改昵称
     * @param token 用户唯一标识
     * @param nickName 名称
     * @return ResponseData
     */
    @RequestMapping(value = "/updateNickName")
    public ResponseData updateNickName(@RequestHeader("token") String token, String nickName) {
        return sellerService.updateNickName(token, nickName);
    }



	/**
	 * 提币手续费
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
    @RequestMapping("withdrawFee")
    public ResponseData withdrawFee( @RequestHeader(value = "token") String token) {
        return sellerService.withdrawFee(token);
    }
	
	/**
     * 实名认证
     * @param token 用户唯一标识
     * @param authenVo 实体类
     * @return ResponseData
     */
    @RequestMapping(value = "/authentication")
    public ResponseData authentication(@RequestHeader(value = "token") String token
            , SellerAuthenticationDto authenVo) {
        return sellerService.authentication(token, authenVo);
    }
    

    /**
     * 用户退出登录
     * @param token  用户唯一标识
     * @return ResponseData
     */
    @RequestMapping(value = "/logout")
    public ResponseData logout(@RequestHeader(value = "token") String token) {
        return sellerService.logout(token);
    }

	/**
	 * 设置交易密码
	 * @param code 手机验证码
	 * @param password 新密码
	 * @param againPassword 确认新密码
	 * @param imageCode 图形验证码
	 * @param ckToken 图形验证码ckToken
	 * @param token 用户唯一标识
	 * @return  ResponseData
	 */
    @RequestMapping(value = "/tradersPwd")
    public ResponseData tradersPwd(String code, String password,String againPassword,String imageCode,String ckToken, @RequestHeader(value = "token") String token) {
        return sellerService.tradersPwd(code, password,againPassword,imageCode,ckToken, token);
    }

	/**
	 * 修改登录密码
	 * @param token token
	 * @param password 新密码
	 * @param againPassword 确认新密码
	 * @param code 手机验证码
	 * @param imageCode 图形验证码
	 * @param ckToken 图形验证码的token
	 * @return ResponseData
	 */
    @RequestMapping("/updatePwd")
    public ResponseData updatePwd(@RequestHeader(value = "token") String token, String password,
								  String againPassword, String code,String imageCode,String ckToken) {
        return sellerService.updatePwd(token, password,againPassword, code,imageCode,ckToken);
    }
    
    
    /**
     * 添加支付方式
     * @param payMethod payMethod
     * @param token 用户唯一标识
     * @return ResponseData
     */
    @RequestMapping("/addEditPayMethod")
    public ResponseData addEditPayMethod(SellerPayMethodDto payMethod, @RequestHeader(value = "token") String token) {
        return sellerService.addEditPayMethod(payMethod, token);
    }
    
    /**
     * 获取某个支付类型的支付列表数据
     * @param pageSize 当前页显示的条数
     * @param pageNumber 当前页的页数
     * @param token 用户唯一标识
     * @param type 支付类型 1：表示支付宝，2：表示微信，3：表示银行卡 4:支付宝固码,5：支付宝转银行
     * @return ResponseData
     */
    @RequestMapping("getPayMethodList")
    public ResponseData getPayMethodList(@RequestParam(defaultValue="20",value="pageSize")Integer pageSize,
    		@RequestParam(defaultValue="1",value="pageNumber")Integer pageNumber, @RequestHeader(value = "token") String token,
    		@RequestParam(defaultValue="1",value="type")Integer type) {
        return sellerService.getPayMethodList(pageSize, pageNumber, token, type);
    }
    
    /**
     * 删除某个收款方式
     * @param token 用户唯一标识
     * @param id 收款方式id
     * @return ResponseData
     */
    @RequestMapping("deletePayMethod")
    public ResponseData deletePayMethod(@RequestHeader(value = "token") String token, Long id) {
        return sellerService.deletePayMethod(token, id);
    }
    
    /**
     * 获取USDT地址列表数据
     * @param pageSize 当前页显示的条数
     * @param pageNumber 当前页的页数
     * @param token 用户的唯一表示
     * @return ResponseData
     */
    @RequestMapping("getAddressList")
    public ResponseData getAddressList(@RequestParam(defaultValue="20",value="pageSize")Integer pageSize,
									   @RequestParam(defaultValue="1",value="pageNumber")Integer pageNumber,
									   @RequestHeader(value = "token") String token) {
        return sellerService.getAddressList(pageSize, pageNumber, token);
    }

    
    /**
     * 添加USDT地址
     * @param remark 备注
     * @param address 钱包地址
     * @param token 用户的唯一表示
     * @return ResponseData
     */
    @RequestMapping("/addAddress")
    public ResponseData addAddress(String remark ,String address, @RequestHeader(value = "token") String token) {
        return sellerService.addAddress(remark,address, token);
    }
    /**
     * 删除USDT地址
     * @param token 用户的唯一表示
     * @param id 地址id
     * @return ResponseData
     */
    @RequestMapping("deleteAddress")
    public ResponseData deleteAddress(@RequestHeader(value = "token") String token, Long id) {
        return sellerService.deleteAddress(token, id);
    }
    /**
     * 一键兑换
     * @param number 兑换数量
	 * @param type 类型 1表示HC兑换成USDT ,2表示usdt兑换成HC
     * @param token 用户唯一表示
     * @return  ResponseData
     */
    @RequestMapping("/usdtExchange")
    public ResponseData usdtExchange(Double number, @RequestHeader(value = "token") String token
    		,@RequestParam(defaultValue="1",value="type")Integer type) {
        return sellerService.usdtExchange(number, token,type);
    }

	/**
	 * 兑换手续费
	 * @param token 用户唯一标识
	 * @param type 类型：1表示USDT兑换成HC，2表示HC兑换成USDT
	 * @return ResponseData
	 */
	@RequestMapping("/exchangeFeeInfo")
	public ResponseData exchangeFeeInfo( @RequestHeader(value = "token") String token
			,@RequestParam(defaultValue="1",value="type")Integer type) {
		return sellerService.exchangeFeeInfo(token,type);
	}

	/**
	 * 兑换列表
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页的页数
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/exchangeList")
	public ResponseData exchangeList(@RequestParam(defaultValue="20",value="pageSize")Integer pageSize,
									 @RequestParam(defaultValue="1",value="pageNumber")Integer pageNumber,
									 @RequestHeader(value = "token") String token) {
		return sellerService.exchangeList(pageSize, pageNumber, token);
	}


    /**
     * 获取我的钱包信息
     * @param type 账号类型：1表示搬砖账户，2表示代付账户，3：表示法币账户，4表示挖矿账号
     * @param token 用户唯一标识
     * @return ResponseData
     */
    @RequestMapping("/wallterInfo")
    public ResponseData wallterInfo(@RequestParam(defaultValue="1",value="type")Integer type
    		, @RequestHeader(value = "token") String token) {
        return sellerService.wallterInfo(type, token);
    }
    
    /**
     * 获取账户下的钱包列表数据
     * @param type 账号类型：1表示搬砖账户，2表示代付账户，3：表示法币账户，4表示挖矿账号
     * @param token 用户唯一标识
     * @return ResponseData
     */
    @RequestMapping("/wallterList")
    public ResponseData wallterList(@RequestParam(defaultValue="1",value="type")Integer type
    		, @RequestHeader(value = "token") String token) {
        return sellerService.wallterList(type, token);
    }
    
    /**
     * 获取某个账户下的某个币种的钱包余额信息
     * @param type 账号类型：1表示搬砖账户，2表示代付账户，3：表示法币账户，4表示挖矿账号
     * @param token 用户唯一标识
	 * @param id 某个钱包id
	 * @return ResponseData
     */
    @RequestMapping("/wallterMoney")
    public ResponseData wallterMoney(@RequestParam(defaultValue="1",value="type")Integer type,Long id
    		, @RequestHeader(value = "token") String token) {
        return sellerService.wallterMoney(type, token,id);
    }

	/**
	 * 获取某个账户某个币种的可用余额
	 * @param type 账号类型：1表示搬砖账户，2表示代付账户，3：表示法币账户，4表示挖矿账号
	 * @param symbols 钱包币种
	 * @param token 用户唯一标识
	 * @return  ResponseData
	 */
    @RequestMapping("/availableBalance")
    public ResponseData availableBalance(@RequestParam(defaultValue="1",value="type")Integer type,String symbols
    		, @RequestHeader(value = "token") String token) {
        return sellerService.availableBalance(type, token,symbols);
    }
    
    /**
	 * 获取平台的充币地址
	 * @param token 用户唯一标识
	 * @return  ResponseData
	 */
	@RequestMapping("/chargeCoinAddress")
	public ResponseData  chargeCoinAddress(@RequestHeader(value="token")String token) {
		return sellerService.chargeCoinAddress(token);
	}


	/**
	 * 提交充币的信息
	 * @param token 用户唯一标识
	 * @param hashValue 交易哈希值
	 * @param number 充值金额
	 * @return ResponseData
	 */
	@RequestMapping("/submitChargeCoin")
	public ResponseData  submitChargeCoin(@RequestHeader(value="token")String token,String hashValue,Double number) {
		return sellerService.submitChargeCoin(token,hashValue,number);
	}
	
	/**
	 * 充币记录列表
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页的页数
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/chargeCoinList")
    public ResponseData chargeCoinList(@RequestParam(defaultValue="20",value="pageSize")Integer pageSize,
									   @RequestParam(defaultValue="1",value="pageNumber")Integer pageNumber
			, @RequestHeader(value = "token") String token) {
        return sellerService.ChargeCoinList(pageSize, pageNumber, token);
    }


	/**
	 * 提交提币
	 * @param token 用户唯一标识
	 * @param number 提币数量
	 * @param address 提币地址
	 * @param tradePwd 交易密码
	 * @param symbol 币种
	 * @return ResponseData
	 */
	@RequestMapping(value = "/sellerWithdraw")
    public ResponseData sellerWithdraw(@RequestHeader(value = "token") String token,
									   Double number,
									   String address,
									   String tradePwd,
									   String symbol) {
        return sellerService.addSellerWithdrawOrder(number,address,tradePwd, token,symbol);
    }
	
	/**
	 * 提币记录
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页的页数
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/withdrawCoinList")
    public ResponseData withdrawCoinList(@RequestParam(defaultValue="20",value="pageSize")Integer pageSize,
										 @RequestParam(defaultValue="1",value="pageNumber")Integer pageNumber
										, @RequestHeader(value = "token") String token) {
        return sellerService.withdrawCoinList(pageSize, pageNumber, token);
    }
    
	/**
	 * 提交划转
	 * @param type：1表示从搬砖账户转到法币账户，2表示法币账户转到搬砖账户中，3表示代付转到搬砖账户，4表示代付转到法币账户，5表示挖矿账号转到搬砖，6表示挖矿到法币
	 * @param token： 用户唯一标识
	 * @param number 划转数量
	 * @param symbols 币种：USDT或HC
	 * @return ResponseData
	 */
	@RequestMapping("/transferCoin")
    public ResponseData transferCoin(@RequestParam(defaultValue="1",required=false,value="type")Integer type
    		, @RequestHeader(value = "token") String token,Double number,String symbols) {
        return sellerService.transferCoin(type, number, symbols,token);
    }
	
	/**
	 * 划转记录列表
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页的页数
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/transferCoinList")
    public ResponseData transferCoinList(Integer pageSize, Integer pageNumber, @RequestHeader(value = "token") String token) {
        return sellerService.transferCoinList(pageSize, pageNumber, token);
    }
	
	/**
	 * 流水记录
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页的页数
	 * @param type 流水类型
	 * @param id 钱包id
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/flowRecordList")
    public ResponseData flowRecordList(
    		@RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize,
			@RequestParam(value = "pageNumber",defaultValue = "1")Integer pageNumber,
			@RequestParam(value = "type",required = false)String type,
			@RequestParam(value = "id")Long id,
			@RequestParam(value = "starTime",required = false)String starTime,
			@RequestParam(value = "endTime",required = false)String endTime,
			@RequestHeader(value = "token") String token) {
        return sellerService.flowRecordList(pageSize, pageNumber,type,id, token,starTime,endTime);
    }

	/**
	 * 获取流水记录的统计信息
	 * @param type 类型
	 * @param id 钱包id
	 * @param starTime 开始时间
	 * @param endTime 结束时间
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/flowRecordInfo")
	public ResponseData flowRecordInfo(
			@RequestParam(value = "type",defaultValue = "1")Integer type,
			@RequestParam(value = "id")Long id,
			@RequestParam(value = "starTime",required = false)String starTime,
			@RequestParam(value = "endTime",required = false)String endTime,
			@RequestHeader(value = "token") String token) {
		return sellerService.flowRecordInfo(type,id, token,starTime,endTime);
	}


	/**
	 *  提交承兑商的申请信息
	 * @param token 用户唯一标识
	 * @param name 真实姓名
	 * @param phone 手机号码
	 * @param idcardNo 身份证号码
	 * @return  ResponseData
	 */
	@RequestMapping("/submitAccpterAppeal")
	public ResponseData  submitAccpterAppeal(@RequestHeader(value="token")String token,String name,String phone,String idcardNo) {
		return sellerService.submitAccpterAppeal(token,name,phone,idcardNo);
	}


	/**
	 *
	 * otc交易列表数据
	 * @param token 用户唯一标识
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页显示的页数
	 * @param symbols 币种
	 * @param type 类型：1会员区，2承兑商
	 * @param payMethod 支付方式：0表示全部，1表示支付宝，2表示微信，3表示银行卡
	 * @param price 价格
	 * @return ResponseData
	 */
	@RequestMapping(value="/otcList")
    public ResponseData otcList(@RequestHeader(value = "token") String token,
			@RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize,
			@RequestParam(value = "pageNumber",defaultValue = "1")Integer pageNumber,
			@RequestParam(value = "symbols",defaultValue = "HC")String symbols,
			@RequestParam(value = "type",defaultValue = "1")Integer type,
			@RequestParam(value = "payMethod",required = false)Integer payMethod,
			@RequestParam(value = "price",required = false)Double price
			) {
        return sellerService.otcList(pageSize, pageNumber,type,symbols,payMethod,price, token);
    }


	/**
	 * 购买otcp
	 * @param token 用户唯一标识
	 * @param buyType 1表示按金额购买，2表示按数量购买
	 * @param type 1：表示会员区，2表示承兑商区
	 * @param id：表示购买那个订单
	 * @param numberPrice：金额或数量
	 * @return ResponseData
	 */
	@RequestMapping("/submitBuyOtc")
	public ResponseData  submitBuyOtc(@RequestHeader(value="token")String token,
									  @RequestParam(value="buyType",defaultValue="1")Integer buyType,
									  @RequestParam(value="type",defaultValue="1")Integer type,
									  Long id,Double numberPrice) {
		return sellerService.submitBuyOtc(token,buyType,type,numberPrice,id);
	}

	/**
	 * 我的某个otc订单详情信息
	 * @param token 用户唯一标识
	 * @param id 订单id
	 * @return  ResponseData
	 */
	@RequestMapping("/buyOtcDetail")
	public ResponseData buyOtcDetail(@RequestHeader(value = "token") String token,
									 @RequestParam(value="id")Long id) {
		return sellerService.buyOtcDetail(token,id);
	}

	/**
	 *  我购买的otc或出售的订单订单记录列表
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页的页数
	 * @param type ：1表示会员区，2表示承兑商区
	 * @param token 用户唯一标识
	 * @param status 1:表示未支付，2表示已付款，3已完成，4表示已取消，5表示申诉中
	 * @param tradeType 1:表示购买，2表示出售
	 * @param time 时间
	 * @return ResponseData
	 */
	@RequestMapping("/buyOtcList")
    public ResponseData buyOtcList(@RequestParam(value="pageSize",defaultValue="20")Integer pageSize,
    		@RequestParam(value="pageNumber",defaultValue="1")Integer pageNumber,
    		@RequestParam(value="type",defaultValue="1")Integer type,
    		@RequestHeader(value = "token") String token,
    		@RequestParam(value="status",defaultValue="0",required = false)Integer status,
			@RequestParam(value="tradeType",defaultValue="0",required = false)Integer tradeType,
			@RequestParam(value="time",required = false)String time) {
        return sellerService.buyOtcList(pageSize, pageNumber,type, token,status,tradeType,time);
    }
	


	/**
	 * 	我已付款
	 * @param token 用户唯一标识
	 * @param id 订单id
	 * @return ResponseData
	 */
	@RequestMapping("/alreayPay")
    public ResponseData alreayPay(@RequestHeader(value = "token") String token,
    		@RequestParam(value="id")Long id,Long payMethodId,String credentials) {
        return sellerService.alreayPay(token,id,payMethodId,credentials);
    }


	/**
	 * 取消交易
	 * @param token 用户唯一标识
	 * @param id 订单id
	 * @return ResponseData
	 */
	@RequestMapping("/cannelOtcOrder")
	public ResponseData cannelOtcOrder(@RequestHeader(value = "token") String token,
									   @RequestParam(value="id")Long id) {
		return sellerService.cannelOtcOrder(token,id);
	}

	/**
	 *  出售的订单,确认收款
	 * @param token 用户唯一标识
	 * @param id 订单id
	 * @return ResponseData
	 */
	@RequestMapping("/ConfirmPayOtcp")
    public ResponseData ConfirmPayOtcp(@RequestHeader(value = "token") String token,
    		@RequestParam(value="id")Long id) {
        return sellerService.ConfirmPayOtcp(token,id);
    }


	/**
	 * 确认完成
	 * @param token 用户唯一标识
	 * @param id otc订单id
	 * @return  ResponseData
	 */
	@RequestMapping("/finishPayOtcp")
	public ResponseData finishPayOtcp(@RequestHeader(value = "token") String token,
									  @RequestParam(value="id")Long id) {
		return sellerService.finishPayOtcp(token,id);
	}



	/**
	 * 提交申诉
	 * @param token 用户唯一标识
	 * @param id 购买otc的订单id
	 * @return ResponseData
	 */
	@RequestMapping("/submitAppealOtc")
	public ResponseData  submitAppealOtc(@RequestHeader(value="token")String token,Long id,String content,String certificate) {
		return sellerService.submitAppealOtc(token,id,content,certificate);
	}


	/**
	 * 出售otc余额跟手续费的信息
	 * @param type 类型：1表示币币去，2表示承兑区
	 * @param token 用户唯一标识
	 * @param symbols 币种
	 * @return ResponseData
	 */
	@RequestMapping("/octSellerFeeAndMoney")
    public ResponseData octSellerFeeAndMoney(Integer type,String symbols, @RequestHeader(value = "token") String token) {
        return sellerService.octSellerFeeAndMoney(type,symbols, token);
    }


	
	/**
	 * 出售otc
	 * @param token 用户唯一标识
	 * @param minNumber 最小数量
	 * @param maxNumber 最大数量
	 * @param type 类型：1表示会员出售，2表示承兑商出售
	 * @param number 出售数量
	 * @param  price 出售价格
	 * @param tradePwd 交易密码
	 * @param payMethodIds 收款方式
	 * @param symbols 币种
	 * @return ResponseData
	 */
	@RequestMapping("/submitSellOtc")
	public ResponseData  submitSellOtc(@RequestHeader(value="token")String token,
			@RequestParam(value="minNumber",required=false)Double minNumber,
			@RequestParam(value="maxNumber",required=false)Double maxNumber,
			@RequestParam(value="type",defaultValue="1")Integer type,
			String symbols
			,Double price
			,Double number,String tradePwd,String payMethodIds) {
		return sellerService.submitSellOtc(token,type,minNumber,maxNumber,number,tradePwd,payMethodIds,symbols,price);
	}
	
	

	
	/**
	 * 我当前委托的订单列表数据
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页的页数
	 * @param type 类型 1：表示币币区，2表示承兑区
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/sellOtcList")
    public ResponseData sellOtcList(@RequestParam(value="pageSize",defaultValue="20")Integer pageSize,
    		@RequestParam(value="pageNumber",defaultValue="1")Integer pageNumber,
    		@RequestParam(value="type",defaultValue="1")Integer type,
    		@RequestHeader(value = "token") String token) {
        return sellerService.sellOtcList(pageSize, pageNumber,type, token);
    }
	
	/**
	 * 撤销出售订单
	 * @param token 用户唯一标识
	 * @param id 出售订单id
	 * @return  ResponseData
	 */
	@RequestMapping("/revocation")
	public ResponseData revocation( @RequestHeader(value = "token") String token,Long id) {
		  return sellerService.revocation(token,id);
	}
	

	
	
	/**
	 * 交易信息
	 * @param token 用户唯一标识
	 * @param type 类型：1表示出售，2表示购买
	 * @return ResponseData
	 */
	@RequestMapping("/buyCoinInfo")
    public ResponseData buyCoinInfo(@RequestHeader(value = "token") String token,@RequestParam(value="type",defaultValue="1")Integer type ) {
        return sellerService.buyCoinInfo(token,type);
    }
	
	
	/**
	 * 码设置
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/getPaymethod")
    public ResponseData getPaymethod(@RequestHeader(value = "token") String token) {
        return sellerService.getPaymethod(token);
    }
	
	
	/**
	 * 更新码设置
	 * @param token 用户唯一标识
	 * @param payMethodList  收款方式集合
	 * @return ResponseData
	 */
	@RequestMapping("/updatePaymethod")
    public ResponseData updatePaymethod(@RequestHeader(value = "token") String token,String payMethodList) {
        return sellerService.updatePaymethod(token,payMethodList);
    }


	/**
	 * 挂单出售
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/sellerNumber")
	public ResponseData  sellerNumber(@RequestHeader(value="token")String token,HttpServletRequest request) {
		return sellerService.sellerNumber(token,request);
	}

	/**
	 * 检查是否开启接单
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/checkStart")
    public ResponseData checkStart(@RequestHeader(value = "token") String token) {
        return sellerService.checkStart(token);
    }
	
	/**
	 * 检查用户的收款码是否出异常
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/checkExceptionMethod")
    public ResponseData checkExceptionMethod(@RequestHeader(value = "token") String token) {
        return sellerService.checkExceptionMethod(token);
    }
	
	/**
	 * 结束挂单
	 * @param token 用户唯一标识
	 * @return  ResponseData
	 */
	@RequestMapping("/closeSellerTrade")
    public ResponseData closeSellerTrade(@RequestHeader(value = "token") String token) {
        return sellerService.closeSellerTrade(token);
    }
	
	/**
	 * 检查是否发送语言通知
	 * @param token 用户唯一标识
	 * @return  ResponseData
	 */
	@RequestMapping("/checkVideo")
    public ResponseData checkVideo(@RequestHeader(value = "token") String token) {
        return sellerService.checkVideo(token);
    }
	
	/**
	 * 获取匹配成功的订单列表
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页的页数
	 * @param token 用户唯一标识
	 * @param type ：类型：1表示首页显示的数据，2表示交易记录里显示的数据
	 * @param status ：状态：0表示全部，1表示已完成，2表示已取消,3待冻结,4:待确认
	 * @param serialno 订单号
	 * @param startTime 开启时间
	 * @param endTime 结束时间
	 * @return ResponseData
	 */
	 @RequestMapping("/getSellerOrderList")
	 public ResponseData getSellerOrderList(Integer pageSize, Integer pageNumber, @RequestHeader(value = "token") String token,
	    		@RequestParam(value="type",defaultValue="0")Integer type
	    		,@RequestParam(value="status",defaultValue="0")Integer status,
				@RequestParam(value="serialno",required=false)String serialno,
				@RequestParam(value="startTime",required=false)String startTime,
				@RequestParam(value="endTime",required=false)String endTime) {
	        return sellerService.getSellerOrderList(pageSize, pageNumber, token,type,status,serialno,startTime,endTime);
	 }
	 
	 /**
	  * 订单详情
	  * @param serialno 订单流水号
	  * @param token 用户唯一标识
	  * @return ResponseData
	  */
	@RequestMapping("/getSellerOrderDetail")
    public ResponseData getSellerOrderDetail(String serialno, @RequestHeader(value = "token") String token) {
        return sellerService.getSellerOrderDetail(serialno, token);
    }
	
	/**
	 * 确认收款
	 * @param token 用户唯一标识
	 * @param serialno 订单流水号
	 * @return ResponseData
	 */
	@RequestMapping("/ConfirmFinishTrader")
    public ResponseData ConfirmFinishTrader(@RequestHeader(value = "token") String token,
											Double price,
											String serialno,HttpServletRequest request) throws Exception {
        return sellerService.ConfirmFinishTrader(token,serialno,request,price);
    }


	/**
	 * 检查是否开启同城匹配
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/checkSwitchCity")
	public ResponseData checkSwitchCity(@RequestHeader(value = "token") String token) {
		return sellerService.checkSwitchCity(token);
	}

	/**
	 * 开启或关闭同城匹配
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/openOrCloseCity")
	public ResponseData openOrCloseCity(@RequestHeader(value = "token") String token,HttpServletRequest request) {
		return sellerService.openOrCloseCity(token,request);
	}


	/**
	 * 通知栏列表数据
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页的页数
	 * @param token 用户唯一标识
	 * @return getNoticeList
	 */
	@RequestMapping("/getNoticeList")
	public ResponseData getNoticeList(@RequestParam(value="pageSize",defaultValue="20")Integer pageSize,
										   @RequestParam(value="pageNumber",defaultValue="1") Integer pageNumber,
										   @RequestHeader(value = "token") String token) {
		return sellerService.getNoticeList(pageSize, pageNumber, token);
	}

	/**
	 * 查看通知详情
	 * @param token 用户唯一标识
	 * @param id 通知id
	 * @return ResponseData
	 */
	@RequestMapping("/getNoticeDetail")
	public ResponseData getNoticeDetail(@RequestHeader(value = "token") String token,Long id) {
		return sellerService.getNoticeDetail(token,id);
	}



	/**
	 * 我的团队信息
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/teamInfo")
    public ResponseData teamInfo(@RequestHeader(value = "token") String token) {
        return sellerService.teamInfo(token);
    }
	
	/**
	 * 我下级列表信息
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页的页数
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/subordinateInfo")
    public ResponseData subordinateInfo(Integer pageSize, Integer pageNumber,@RequestHeader(value = "token") String token) {
        return sellerService.subordinateInfo(token,pageSize,pageNumber);
    }
	
	/**
	 * 直属下级推荐挖矿列表信息
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页的页数
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/subordinateBonusList")
    public ResponseData subordinateBonusList(Integer pageSize, Integer pageNumber,@RequestHeader(value = "token") String token) {
        return sellerService.subordinateBonusList(token,pageSize,pageNumber);
    }
	
	/**
	 * 我的推荐挖矿信息
	 * @param token 用户唯一标识
	 * @return ResponseData
	 */
	@RequestMapping("/teamBonusInfo")
    public ResponseData teamBonusInfo(@RequestHeader(value = "token") String token) {
        return sellerService.teamBonusInfo(token);
    }

	/**
	 * 检查是否有最新的通知
	 * @param token 用户唯一标识
	 * @return  ResponseData
	 */
	@RequestMapping("/isHaveNotice")
	public ResponseData isHaveNotice(@RequestHeader(value = "token") String token) {
		return sellerService.isHaveNotice(token);
	}
	
	public static void main(String[] args) {
		System.out.println(ShiroKit.md5("123456", "vs5sr"));



	}


	@RequestMapping("/buyCoinTime")
	public ResponseData buyCoinTime(@RequestHeader(value = "token") String token) {
		return sellerService.buyCoinTime(token);
	}

	@RequestMapping("/otcCoinTime")
	public ResponseData otcCoinTime(@RequestHeader(value = "token") String token) {
		return sellerService.otcCoinTime(token);
	}


}
