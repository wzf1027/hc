package cn.stylefeng.guns.core.common.constant;

import cn.hutool.core.collection.CollectionUtil;

import java.util.List;

/**
 * 系统常量
 */
public interface Const {

    /**
     * 默认管理系统的名称
     */
    String DEFAULT_SYSTEM_NAME = "HC管理系统";

    /**
     * 默认欢迎界面的提示
     */
    String DEFAULT_WELCOME_TIP = "欢迎使用HC管理系统";

    /**
     * 系统默认的管理员密码
     */
    String DEFAULT_PWD = "111111";

    /**
     * 管理员角色的名字
     */
    String ADMIN_NAME = "administrator";
    
    String MERCHANT_NAME ="merchant";

    /**
     * 管理员id
     */
    Long ADMIN_ID = 1L;

    /**
     * 超级管理员角色id
     */
    Long ADMIN_ROLE_ID = 1L;
    /**
     *  商户角色id
     */
    Long MERCHANT_ROLE_ID = 2L;
    
    /**
     * 承兑商角色id
     */
    Long ACCPERT_ROLE_ID = 3L;
    
    /**
     * 代理商角色
     */
    Long AGENT_ROLE_ID = 4L;

    /**
     * 接口文档的菜单名
     */
    String API_MENU_NAME = "接口文档";

    /**
     * 不需要权限验证的资源表达式
     */
    List<String> NONE_PERMISSION_RES = CollectionUtil.newLinkedList("/assets/**", "/gunsApi/**", "/login", "/global/sessionError", "/kaptcha", "/error", "/global/error","/register","/app/**"
    		,"/userRegister","/mgr/submitAuth","/image/**","/examineSuccess","/system/examineSuccessPage","/examineSuccessPage","/forgetPwd"
    		,"/updatePwd");

}
