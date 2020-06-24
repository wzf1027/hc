package cn.stylefeng.guns.modular.biz.controller;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.stylefeng.guns.core.aop.MemberLoginException;
import cn.stylefeng.guns.core.util.Constant;
import cn.stylefeng.guns.core.util.RedisUtil;
import cn.stylefeng.guns.modular.biz.service.SendSMSExtService;
import cn.stylefeng.guns.modular.system.entity.Seller;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@RestController
@RequestMapping("/app/sms")
public class SMSController {
	
	@Resource
	private RedisUtil redisUtil;

    @Resource
    private SendSMSExtService sendSMSExtService;
    
	@Value("${sms.switch}")
	private String isSwitch;

    /**
     * 发短信验证码
     * @param phone 手机号码
     * @param token 登录后需要发送短信的，需传递
     * @param type 类型：1表示国内，2表示国际
     * @param code 区号
     * @return
     */
    @RequestMapping(value="/getMsg",method = RequestMethod.POST)
    @ResponseBody
    public ResponseData getMsg(@RequestParam(value ="phone",required = false) String phone
    		,@RequestHeader(value="token",required=false)String token
    		,@RequestParam(value ="type",required = false,defaultValue="1")Long  type
    		,@RequestParam(value ="code",required = false)String  code
    		){

        if(StringUtils.isNotBlank(token)) {
        	Seller seller = (Seller) redisUtil.get(token);
			if(seller == null) {
				//未登录，抛出异常
				throw new MemberLoginException("用户未登录");
			}
			phone = seller.getPhone();
		}
        if(StringUtils.isNotBlank(phone)) {
      	 int number =123456;
        	if("1".equals(isSwitch)) {
            	redisUtil.set(Constant.SMS+phone,String.valueOf(number),120);
              return ResponseData.success(200, "发送成功", null);
        	}
       	 	number = (int) ((Math.random()*9+1)*100000);
        	String content = "【码力】您的短信验证码为%s,该验证码2分钟内有效，请勿泄露于他人。";
     		content = String.format(content,number);
            if(sendSMSExtService.sendSms(content,phone,type)){
             	redisUtil.set(Constant.SMS+phone,String.valueOf(number),120);
             	return ResponseData.success(200, "发送成功", null);
             }
             return ResponseData.error("发送失败，请重新发送");   
        }
        return ResponseData.error("发送失败，手机号码有误");   
    }
    
}
