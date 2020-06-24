package cn.stylefeng.guns.modular.biz.service;

import cn.stylefeng.guns.core.util.Constant;
import cn.stylefeng.guns.core.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CaptchaService {

    @Value("${captcha.timeout}")
    private Integer timeout;

    @Resource
    private RedisUtil redisUtil;

    public Map<String,Object> createToken(String captcha){
        //生成一个token
        String cToken = UUID.randomUUID().toString();
        //生成验证码对应的token  以token为key  验证码为value存在redis中
        redisUtil.set(Constant.CLIENT_TOKEN +cToken,captcha,timeout);
        Map<String, Object> map = new HashMap<>();
        map.put("cToken", cToken);
        map.put("expire", timeout);
        return map;
    }


}
