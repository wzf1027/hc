package cn.stylefeng.guns.modular.system.controller;

import cn.stylefeng.guns.core.aop.GoogleCodeException;
import cn.stylefeng.guns.core.common.node.MenuNode;
import cn.stylefeng.guns.core.log.LogManager;
import cn.stylefeng.guns.core.log.factory.LogTaskFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.shiro.ShiroUser;
import cn.stylefeng.guns.core.util.GoogleGenerator;
import cn.stylefeng.guns.core.util.RedisUtil;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.List;

import static cn.stylefeng.roses.core.util.HttpContext.getIp;

/**
 * 登录控制器
 */
@Controller
public class LoginController extends BaseController {
	
	private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    SessionDAO sessionDAO;

    @Autowired
    RedisUtil redisUtil;

    /**
     * 跳转到主页
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {

        //获取当前用户角色列表
        ShiroUser user = ShiroKit.getUserNotNull();
        List<Long> roleList = user.getRoleList();

        if (roleList == null || roleList.size() == 0) {
            ShiroKit.getSubject().logout();
            model.addAttribute("tips", "该用户没有角色，无法登陆");
            return "/login.html";
        }

        model.addAttribute("isAuth", 0);
        for (Long roleId : user.getRoleList()) {
                if(roleId.equals(2L)) {
	                if(user.getIsAuth() ==0) {
	                      model.addAttribute("isAuth", 1);
	                }else if(user.getIsAuth()==1){
	                      model.addAttribute("isAuth", 1);
	                }
                }
        }          
        List<MenuNode> menus = userService.getUserMenuNodes(roleList);
        model.addAttribute("menus", menus);
        return "/index.html";
    }

    /**
     * 跳转到登录页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        if (ShiroKit.isAuthenticated() || ShiroKit.getUser() != null) {
            return REDIRECT + "/";
        } else {
            return "/login.html";
        }
    }

    /**
     * 点击登录执行的动作
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginVali() {

        String username = super.getPara("username").trim();
        String password = super.getPara("password").trim();
        String remember = super.getPara("remember");
        String code = super.getPara("code").trim();
        Subject currentUser = ShiroKit.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray());

        //如果开启了记住我功能
        if ("on".equals(remember)) {
            token.setRememberMe(true);
        } else {
            token.setRememberMe(false);
        }
        //执行shiro登录操作
        currentUser.login(token);

//        Collection<Session> sessions =  sessionDAO.getActiveSessions();
//        if (currentUser.isAuthenticated()){
//            for (Session session:sessions){
//                if(username.equals(session.getAttribute("loginedUser"))){
//                    currentUser.logout();
//                    throw  new CredentialsException("用户已登录");
//                }
//            }
//        }

        //登录成功，记录登录日志
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        User user = userService.getById(shiroUser.getId());
        if(StringUtils.isNotBlank(user.getGoogleSecret())){
            if ( StringUtils.isBlank(code)){
                currentUser.logout();
                throw  new GoogleCodeException("请输入谷歌验证");
            }
            GoogleGenerator ga = new GoogleGenerator();
            long time = System.currentTimeMillis ();
            boolean result = ga.check_code(user.getGoogleSecret(), code, time);
            if (!result){
                currentUser.logout();
                throw  new GoogleCodeException("输入的谷歌验证有误");
            }
        }

        LogManager.me().executeLog(LogTaskFactory.loginLog(shiroUser.getId(), getIp()));
        ShiroKit.getSession().setAttribute("sessionFlag", true);
//        ShiroKit.getSession().setAttribute("loginedUser",username);
        return REDIRECT + "/";
    }

    @RequestMapping(value = "/examine", method = RequestMethod.GET)
    public String examine(Model model) {
    	 if (!ShiroKit.isAuthenticated() || ShiroKit.getUser() == null) {
             return REDIRECT + "/";
         }
        //获取当前用户角色列表
        ShiroUser user = ShiroKit.getUserNotNull();
        List<Long> roleList = user.getRoleList();
        if (roleList == null || roleList.size() == 0) {
            ShiroKit.getSubject().logout();
            model.addAttribute("tips", "该用户没有角色，无法登陆");
            return "/login.html";
        }
        for (Long roleId : roleList) {
			if(roleId.equals(2L)) {
				 return "/examine.html";
			}
		} 
        return REDIRECT + "/";
    }
    
    @RequestMapping(value="/examineSuccessPage", method = RequestMethod.GET)
    public String examineSuccessPage(Model model) {
    	if (!ShiroKit.isAuthenticated() || ShiroKit.getUser() == null) {
            return REDIRECT + "/";
        }
    	  //获取当前用户角色列表
        ShiroUser user = ShiroKit.getUserNotNull();
        User merchantUser = userService.getById(user.getId());
        if(merchantUser.getIsAuth().equals(2) || merchantUser.getIsAuth().equals(0)) {
        	  ShiroKit.getSubject().logout();
              deleteAllCookie();
              logger.info("examineSuccessPage-login");
              return REDIRECT + "/login";
        }
        return "/examine_success.html";
    }
    
    
    @RequestMapping("/examineSuccess")
    public String examineSuccess(Model model) {
    	 if (!ShiroKit.isAuthenticated() || ShiroKit.getUser() == null) {
             return REDIRECT + "/";
         }
    	 //获取当前用户角色列表
        ShiroUser user = ShiroKit.getUserNotNull();
        List<Long> roleList = user.getRoleList();

        if (roleList == null || roleList.size() == 0) {
            ShiroKit.getSubject().logout();
            deleteAllCookie();
            model.addAttribute("tips", "该用户没有角色，无法登陆");
            return "/login.html";
        }
        if(user.getIsAuth()==2) {
        	 return REDIRECT + "/"; 
        }
        for (Long roleId : roleList) {
			if(roleId.equals(2L)) {
				return REDIRECT + "/examineSuccessPage"; 
			}
		} 
        return REDIRECT + "/";  
    }
    
    
   
    
    
    /**
     * 退出登录
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOut() {
        LogManager.me().executeLog(LogTaskFactory.exitLog(ShiroKit.getUserNotNull().getId(), getIp()));
        ShiroKit.getSubject().logout();
        deleteAllCookie();
        return REDIRECT + "/login";
    }
    
    /**
     * 注册页面
     * @param code
     * @param model
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(@RequestParam(value="code",required=false)String code,Model model) {
    	 model.addAttribute("code",code);
    	  return "/register.html";
    }
    
    /**
     * 忘记密码页面
     * @param model
     * @return
     */
    @RequestMapping(value = "/forgetPwd", method = RequestMethod.GET)
    public String register(Model model) {
    	  return "/forget_pwd.html";
    }
    
    /**
     	* 商户注册
     * @param phone 手机号码
     * @param password 密码
     * @param refenceCode 推荐码
     * @param code 短信验证码
     * @return
     */
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData  userRegister(String phone,String password,String refenceCode,String code) {
    	return userService.userRegister(phone,password,refenceCode,code);
    }


    /**
            * 忘记密码
     * @param phone 手机号码
     * @param password 密码
     * @param code 短信验证码
     * @return
     */
    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData  updatePwd(String phone,String password,String code) {
    	return userService.updatePwd(phone,password,code);
    }
    
}
