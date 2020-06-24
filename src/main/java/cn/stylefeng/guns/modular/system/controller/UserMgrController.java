package cn.stylefeng.guns.modular.system.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import cn.stylefeng.guns.core.util.GoogleGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.collection.CollectionUtil;
import cn.stylefeng.guns.config.properties.GunsProperties;
import cn.stylefeng.guns.core.common.annotion.BussinessLog;
import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.Const;
import cn.stylefeng.guns.core.common.constant.dictmap.UserDict;
import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.guns.core.common.constant.state.ManagerStatus;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.entity.USDTOtcpExchange;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.factory.UserFactory;
import cn.stylefeng.guns.modular.system.model.UserDto;
import cn.stylefeng.guns.modular.system.service.USDTOtcpExchangeService;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.guns.modular.system.warpper.UserWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.datascope.DataScope;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.RequestEmptyException;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

/**
 * 系统管理员控制器
 */
@Controller
@RequestMapping("/mgr")
public class UserMgrController extends BaseController {

    private static String PREFIX = "/modular/system/user/";

    @Autowired
    private GunsProperties gunsProperties;

    @Autowired
    private UserService userService;
    
    @Autowired
    private USDTOtcpExchangeService exchageService;
    

    /**
     * 跳转到查看管理员列表的页面
     *
     * 
     * 
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "user.html";
    }

    @RequestMapping("/google")
    public String google(Model model) {
        User user = this.userService.getById(ShiroKit.getUser().getId());
        if (user.getBingGoogle() != null && user.getBingGoogle().equals(1)){
            model.addAttribute("code",user.getGoogleSecret());
            model.addAttribute("title","解绑更新");
        }else{
            model.addAttribute("code",null);
            model.addAttribute("title","绑定更新");
        }
        return PREFIX + "googleInfo.html";
    }

    @RequestMapping("/bingGoogle")
    @ResponseBody
    public Object bingGoogle() {
        User user = this.userService.getById(ShiroKit.getUser().getId());
        if (user.getBingGoogle() == null || user.getBingGoogle().equals(0)){
            String secret = GoogleGenerator.generateSecretKey();
            user.setGoogleSecret(secret);
            user.setBingGoogle(1);
            this.userService.updateById(user);
        }else{
            user.setGoogleSecret(null);
            user.setBingGoogle(0);
            this.userService.updateByGoogle(user);
        }
        return SUCCESS_TIP;
    }

    /**
     * 跳转到查看管理员列表的页面
     *
     * 
     * 
     */
    @RequestMapping("/user_add")
    public String addView() {
        return PREFIX + "user_add.html";
    }
    
    @RequestMapping("/exchange")
    public String exchange(Model model) {
    	USDTOtcpExchange exchange = exchageService.getOne(null);
    	if(exchange != null) {
    		model.addAttribute("usdt", exchange.getValue());
    	}
        return PREFIX + "exchange.html";
    }
    
    @RequestMapping("/coinExchange")
    @ResponseBody
    public Object coinExchange(@RequestParam("number") Double number, @RequestParam("place")String place) {
    	USDTOtcpExchange exchange = exchageService.getOne(null);
    	return this.userService.coinExchange(number, place,exchange);
    }

    /**
     * 跳转到角色分配页面
     *
     * 
     * 
     */
    @Permission
    @RequestMapping("/role_assign")
    public String roleAssign(@RequestParam Long userId, Model model) {
        if (ToolUtil.isEmpty(userId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        model.addAttribute("userId", userId);
        return PREFIX + "user_roleassign.html";
    }

    /**
     * 跳转到编辑管理员页面
     *
     * 
     * 
     */
    @Permission
    @RequestMapping("/user_edit")
    public String userEdit(@RequestParam Long userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        User user = this.userService.getById(userId);
        LogObjectHolder.me().set(user);
        return PREFIX + "user_edit.html";
    }

    /**
     * 获取用户详情
     *
     * 
     * 
     */
    @RequestMapping("/getUserInfo")
    @ResponseBody
    public Object getUserInfo(@RequestParam Long userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new RequestEmptyException();
        }

        this.userService.assertAuth(userId);
        User user = this.userService.getById(userId);
        Map<String, Object> map = UserFactory.removeUnSafeFields(user);

        HashMap<Object, Object> hashMap = CollectionUtil.newHashMap();
        hashMap.putAll(map);
        hashMap.put("roleName", ConstantFactory.me().getRoleName(user.getRoleId()));
        hashMap.put("deptName", ConstantFactory.me().getDeptName(user.getDeptId()));

        return ResponseData.success(hashMap);
    }

    /**
     * 修改当前用户的密码
     *
     * 
     * 
     */
    @RequestMapping("/changePwd")
    @ResponseBody
    public Object changePwd(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        if (ToolUtil.isOneEmpty(oldPassword, newPassword)) {
            throw new RequestEmptyException();
        }
        this.userService.changePwd(oldPassword, newPassword);
        ShiroKit.getSubject().logout();
        deleteAllCookie();
        return SUCCESS_TIP;
    }

    /**
     * 查询管理员列表
     *
     * 
     * 
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping("/list")
    @Permission
    @ResponseBody
    public Object list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String timeLimit,
                       @RequestParam(required = false) Long deptId) {

        //拼接查询条件
        String beginTime = "";
        String endTime = "";

        if (ToolUtil.isNotEmpty(timeLimit)) {
            String[] split = timeLimit.split(" - ");
            beginTime = split[0];
            endTime = split[1];
        }

        if (ShiroKit.isAdmin()) {
            Page<Map<String, Object>> users = userService.selectUsers(null, name, beginTime, endTime, deptId);
            Page wrapped = new UserWrapper(users).wrap();
            return LayuiPageFactory.createPageInfo(wrapped);
        } else {
            DataScope dataScope = new DataScope(ShiroKit.getDeptDataScope());
            Page<Map<String, Object>> users = userService.selectUsers(dataScope, name, beginTime, endTime, deptId);
            Page wrapped = new UserWrapper(users).wrap();
            return LayuiPageFactory.createPageInfo(wrapped);
        }
    }

    /**
     * 添加管理员
     *
     * 
     * 
     */
    @RequestMapping("/add")
    @BussinessLog(value = "添加管理员", key = "account", dict = UserDict.class)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public ResponseData add(@Valid UserDto user, BindingResult result) {
        if (result.hasErrors()) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        this.userService.addUser(user);
        return SUCCESS_TIP;
    }

    /**
     * 修改管理员
     *
     * 
     * 
     */
    @RequestMapping("/edit")
    @BussinessLog(value = "修改管理员", key = "account", dict = UserDict.class)
    @ResponseBody
    public ResponseData edit(@Valid UserDto user, BindingResult result) {
        if (result.hasErrors()) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        if(StringUtils.isNotBlank(user.getPhone())) {
        	User loginUser = userService.getById(ShiroKit.getUser().getId());
        	Map<String, Object> columnMap = new HashMap<String, Object>();
        	columnMap.put("PHONE", user.getPhone());
        	columnMap.put("ROLE_ID", loginUser.getRoleId());
        	if(!loginUser.getPhone().equals(user.getPhone()) && userService.listByMap(columnMap) != null && userService.listByMap(columnMap).size()>0)
        	{
        		return ResponseData.error("该手机号码已存在");
        	}
        }
        this.userService.editUser(user);
        return SUCCESS_TIP;
    }

    /**
     * 删除管理员（逻辑删除）
     *
     * 
     * 
     */
    @RequestMapping("/delete")
    @BussinessLog(value = "删除管理员", key = "userId", dict = UserDict.class)
    @Permission
    @ResponseBody
    public ResponseData delete(@RequestParam Long userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        this.userService.deleteUser(userId);
        return SUCCESS_TIP;
    }

    /**
     * 查看管理员详情
     *
     * 
     * 
     */
    @RequestMapping("/view/{userId}")
    @ResponseBody
    public User view(@PathVariable Long userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        this.userService.assertAuth(userId);
        return this.userService.getById(userId);
    }

    /**
     * 重置管理员的密码
     *
     * 
     * 
     */
    @RequestMapping("/reset")
    @BussinessLog(value = "重置管理员密码", key = "userId", dict = UserDict.class)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public ResponseData reset(@RequestParam Long userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        this.userService.assertAuth(userId);
        User user = this.userService.getById(userId);
        user.setSalt(ShiroKit.getRandomSalt(5));
        user.setPassword(ShiroKit.md5(Const.DEFAULT_PWD, user.getSalt()));
        this.userService.updateById(user);
        return SUCCESS_TIP;
    }

    /**
     * 冻结用户
     *
     * 
     * 
     */
    @RequestMapping("/freeze")
    @BussinessLog(value = "冻结用户", key = "userId", dict = UserDict.class)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public ResponseData freeze(@RequestParam Long userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        //不能冻结超级管理员
        if (userId.equals(Const.ADMIN_ID)) {
            throw new ServiceException(BizExceptionEnum.CANT_FREEZE_ADMIN);
        }
        this.userService.assertAuth(userId);
        this.userService.setStatus(userId, ManagerStatus.FREEZED.getCode());
        return SUCCESS_TIP;
    }

    /**
     * 解除冻结用户
     *
     * 
     * 
     */
    @RequestMapping("/unfreeze")
    @BussinessLog(value = "解除冻结用户", key = "userId", dict = UserDict.class)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public ResponseData unfreeze(@RequestParam Long userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        this.userService.assertAuth(userId);
        this.userService.setStatus(userId, ManagerStatus.OK.getCode());
        return SUCCESS_TIP;
    }

    /**
     * 分配角色
     *
     * 
     * 
     */
    @RequestMapping("/setRole")
    @BussinessLog(value = "分配角色", key = "userId,roleIds", dict = UserDict.class)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public ResponseData setRole(@RequestParam("userId") Long userId, @RequestParam("roleIds") String roleIds) {
        if (ToolUtil.isOneEmpty(userId, roleIds)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        //不能修改超级管理员
        if (userId.equals(Const.ADMIN_ID)) {
            throw new ServiceException(BizExceptionEnum.CANT_CHANGE_ADMIN);
        }
        this.userService.assertAuth(userId);
        this.userService.setRoles(userId, roleIds);
        return SUCCESS_TIP;
    }

    /**
           * 上传图片
     *
     * 
     * 
     */
    @RequestMapping(method = RequestMethod.POST, path = "/upload")
    @ResponseBody
    public String upload(@RequestPart("file") MultipartFile picture) {

        String pictureName = UUID.randomUUID().toString() + "." + ToolUtil.getFileSuffix(picture.getOriginalFilename());
        try {
            String fileSavePath = gunsProperties.getFileUploadPath();
            picture.transferTo(new File(fileSavePath + pictureName));
        } catch (Exception e) {
            throw new ServiceException(BizExceptionEnum.UPLOAD_ERROR);
        }
        return pictureName;
    }
    
    /**
     * 提交审核信息
     *
     * @param name
     * @param idCardNo
     * @param idCardFront
     * @param idCardReverse
     * @param idCardImage
     * @return
     */
    @RequestMapping(value = "/submitAuth")
    @ResponseBody
    public ResponseData submitAuth(String realName, String idCardNo, String idCardFront, String idCardReverse, String idCardImage,String contactWay) {
        int result =  userService.submitAuth(realName, idCardNo, idCardFront, idCardReverse, idCardImage,contactWay);
        if(result <=0) {
        	return ResponseData.error("提交失败");
        }
        userService.refreshCurrentUser();
        return SUCCESS_TIP;
    }
    
    @RequestMapping(value = "/getPayMethodList")
    @ResponseBody
    public ResponseData getPayMethodList() {
       return  userService.getPayMethodList(ShiroKit.getUser().getId());
    }
    
    
}
