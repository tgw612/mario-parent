package com.sibat.controller;

import com.sibat.domain.VerifyCode;
import com.sibat.domain.VerifyCodeRepository;
import com.sibat.util.captcha.Captcha;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tgw61 on 2017/7/18.
 */
@RestController
@RequestMapping("api")
public class Api {
    @Autowired
    VerifyCodeRepository verifyCodeRepository;
    @RequestMapping(value = "captcha", method = RequestMethod.GET)
    public void generate(HttpServletRequest request, HttpServletResponse response) {
        // 设置响应的类型格式为图片格式
        response.setContentType("image/jpeg");
        //禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
//        String cookie =HashUtil.getRandomId();
//        response.setHeader("cookie",cookie);
        Captcha captcha = new Captcha();
//        String sessionId = request.getSession().getId();
        VerifyCode vc = new VerifyCode(captcha.getCode(), System.currentTimeMillis());
        verifyCodeRepository.save(vc);
        try {
            captcha.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
