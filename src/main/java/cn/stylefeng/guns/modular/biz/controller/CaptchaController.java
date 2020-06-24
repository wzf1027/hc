package cn.stylefeng.guns.modular.biz.controller;

import cn.stylefeng.guns.core.util.ResponseData;
import cn.stylefeng.guns.modular.biz.service.CaptchaService;
import com.google.code.kaptcha.Producer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;


/**
 * 图片验证器
 */
@RestController
@RequestMapping("/app/captcha")
public class CaptchaController {

    @Resource
    private Producer producer;

    @Resource
    private CaptchaService captchaService;


    /**
     * 获取验证码
     * @param response HttpServletResponse
     * @return ResponseData
     * @throws ServletException
     * @throws IOException
     */
    @PostMapping("/code")
    public ResponseData captcha(HttpServletResponse response) throws  IOException {
        // 生成文字验证码
        String text = producer.createText();
        // 生成图片验证码
        ByteArrayOutputStream outputStream = null;
        BufferedImage image = producer.createImage(text);
        outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        // 生成captcha的token
        Map<String, Object> map = captchaService.createToken(text);
        map.put("img", encoder.encode(outputStream.toByteArray()).replace("\r","").replace("\n",""));
        return ResponseData.success(map);
    }

}
