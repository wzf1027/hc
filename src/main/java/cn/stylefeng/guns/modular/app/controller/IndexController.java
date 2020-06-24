package cn.stylefeng.guns.modular.app.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.util.IpUtil;
import cn.stylefeng.guns.modular.system.entity.AppVersion;
import cn.stylefeng.guns.modular.system.service.VersionMgrService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import cn.stylefeng.guns.core.util.ResponseData;
import cn.stylefeng.guns.modular.app.service.SellerService;
import cn.stylefeng.guns.modular.system.entity.Help;
import java.util.List;

/**
 * 首页控制器
 */
@Controller
@RequestMapping("/app")
public class IndexController {
	
	
	@Resource
	private SellerService sellerService;

	@Resource
	private VersionMgrService versionMgrService;



	@RequestMapping(value="/paycheck")
	public String registerPage() {
		return "/modular/frame/paycheck.html";
	}

    @RequestMapping(value="/registerPage")
	public String registerPage(Model model,String code) {
		model.addAttribute("code", code);
		List<AppVersion> list = versionMgrService.list();
		for (AppVersion appVersion:list){
			if (appVersion.getType().equals(1)){
				model.addAttribute("androidUrl", appVersion.getAddress());
			}
			if (appVersion.getType().equals(2)){
				model.addAttribute("iosUrl", appVersion.getAddress());
			}
		}
		return "/modular/frame/register.html";
	}

    @RequestMapping(value="/pay")
  	public String pay(HttpServletRequest request) {
		String ip = IpUtil.getIpAddress(request);
		System.out.println("======真实ip："+ip);
    	return "/modular/frame/testPay.html";
  	}

    
    @RequestMapping(value="/agreementPage")
	public String agreementPage() {
		return "/modular/frame/agreement.html";
	}



	/**
	 *
	 * 用户注册
	 * @param account 用户名
	 * @param password 密码
	 * @param againPassword  确认密码
	 * @param code 验证码
	 * @param recommendCode 邀请码
	 * @return ResponseData
	 */
	@PostMapping("/register")
	@ResponseBody
	public ResponseData register(String account,
								 String password,
								 String againPassword,
								 String code,
								 String recommendCode,
								 String ckToken
								 ) {
		return sellerService.register(account, password,againPassword, code, recommendCode,ckToken);
	}



	/***
	 * 用户登录
	 * @param account 用户名
	 * @param password 密码
	 * @param code 验证码
	 * @return ResponseData
	 */
    @PostMapping("/login")
    @ResponseBody
    public ResponseData login(String account,
							  String password
						,String code,String ckToken) {
        return sellerService.login(account, password,code,ckToken);
    }
    
    /**
     * 忘记密码
     * @param phone 手机号码
     * @param code 短信验证码
     * @param password 新密码
     * @return ResponseData
     */
    @RequestMapping(value = "/forgetPwd")
    @ResponseBody
    public ResponseData forgetPwd(String account,String phone, String code,
								  String password,String againPassword,
								  String imageCode,String ckToken) {
        return sellerService.forgetPwd(account,phone, code, password,againPassword,imageCode,ckToken);
    }
    
    
    /**
     * 获取版本号
     * @param type 类型：1：表示安卓，2表示苹果
     * @return ResponseData
     */
    @RequestMapping("/getAppVersion")
    @ResponseBody
    public ResponseData getAppVersion(@RequestParam(value = "type", required = false, defaultValue = "1") Integer type) {
        return sellerService.getAppVersion(type);
    }

    /**
     * 获取客服信息
     * @return ResponseData
     */
    @RequestMapping("/getCustomerData")
    @ResponseBody
    public ResponseData getCustomerData() {
        return sellerService.getCustomerData();
    }
    
    	
	/**
	 * 获取HC与USDT的兑换比例数据
	 * @return ResponseData
	 */
	@RequestMapping(value = "/getOTCMarkInfo")
	@ResponseBody
	public ResponseData getOTCMarkInfo() {
		return sellerService.getOTCMarkInfo();
	}

	/**
	 * 帮助中心
	 * @param pageSize 当前页显示的条数
	 * @param pageNumber 当前页显示的页数
	 * @return ResponseData
	 */
	@RequestMapping("/helpList")
	@ResponseBody
    public ResponseData helpList(@RequestParam(defaultValue="20",value="pageSize")Integer pageSize,
    		@RequestParam(defaultValue="1",value="pageNumber")Integer pageNumber) {
        return sellerService.helpList(pageSize, pageNumber);
    }
	
	
	
	/**
	 * 某个帮助中心的详情内容
	 * @param id 获取某条id的数据
	 * @param model  model
	 * @return String
	 */
	@RequestMapping(value="/helpDetail")
	public String helpDetail(Long id,Model model) {
		Help help = sellerService.getHelpDetail(id);
		if(help != null) {
			model.addAttribute("title", help.getTitle());
			model.addAttribute("createTime",help.getCreateTime());
			model.addAttribute("content",help.getContent());
		}else {
			model.addAttribute("createTime",null);
			model.addAttribute("title", null);
			model.addAttribute("content",null);
		}
		return "/modular/frame/helpDetail.html";
	}

	/**
	 * 谷歌验证
	 * @param phone 手机号码
	 * @param password 密码
	 * @return ResponseData
	 */
	@RequestMapping(value = "/authGoogle")
	@ResponseBody
	public ResponseData authGoogle(String phone, String password) {
		return sellerService.loginAuthGoogle(phone,password);
	}



	public static void main(String[] args) {
		System.out.println(ShiroKit.md5("abc123","g57yc"));
	}


}
