package cn.stylefeng.guns.core.interceptor;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
public class InterceptorConfiguration implements WebMvcConfigurer {

	 @Override
	    public void addInterceptors(InterceptorRegistry registry) {
	        // 注册拦截器
	        InterceptorRegistration ir = registry.addInterceptor(loginInterceptor());
	        // 配置拦截的路径
	        ir.addPathPatterns("/app/**");
	        // 配置不拦截的路径
	        ir.excludePathPatterns("/app/login");//登录
	        ir.excludePathPatterns("/app/register");//注册
	        ir.excludePathPatterns("/app/forgetPwd");//忘记密码
	        ir.excludePathPatterns("/app/sms/**");//短信验证码
	        ir.excludePathPatterns("/app/caursel/**");
	        ir.excludePathPatterns("/app/file/**");
	        ir.excludePathPatterns("/api/file/**");
	        ir.excludePathPatterns("/app/image/**");
	        ir.excludePathPatterns("/app/getAppVersion");
	        ir.excludePathPatterns("/app/getCustomerData");
	        ir.excludePathPatterns("/app/getOTCMarkInfo");
	        ir.excludePathPatterns("/app/helpList");
	        ir.excludePathPatterns("/app/buyCoin/**");
	        ir.excludePathPatterns("/app/proclamation/**");
	        ir.excludePathPatterns("/app/helpDetail");
	        ir.excludePathPatterns("/app/agreementPage");
	        ir.excludePathPatterns("/app/pay");
	        ir.excludePathPatterns("/app/registerPage");
	        ir.excludePathPatterns("/app/agreement/**");
	        ir.excludePathPatterns("/app/agreementPage");
	        ir.excludePathPatterns("/app/websocket");
		 	ir.excludePathPatterns("/app/seller/authGoogleCode");
		 	ir.excludePathPatterns("/app/authGoogle");
		 ir.excludePathPatterns("/app/captcha/**");

		 ir.excludePathPatterns("/app/paycheck");
	    }
	
	 @Bean
	 public LoginInterceptor  loginInterceptor() {
		return new LoginInterceptor();
	 }
	 
}
