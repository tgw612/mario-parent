package com.sibat.controller;


import com.sibat.domain.User;
import com.sibat.domain.UserRepository;
import com.sibat.domain.VerifyCode;
import com.sibat.domain.VerifyCodeRepository;
import com.sibat.util.Response;
import com.sibat.util.captcha.Captcha;
import com.sibat.util.captcha.HashUtil;
import com.sibat.util.jwt.Jwt;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tgw61 on 2016/10/31.
 */
@RestController
public class UserController {
    public static final int TOKEN_TIME = 1000 * 60 * 60 * 3;
    public static final String SALT = "gop666safe";
    Logger logger = Logger.getLogger(UserController.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    VerifyCodeRepository verifyCodeRepository;

    /**
     * 只需要帐号密码就可登录,不返回token
     *
     * @param account
     * @param password
     * @return
     */
    @RequestMapping(value = "/login_simple", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Response login_simple(@RequestParam("user_name") String account, @RequestParam("password") String password) {
        String md5Password;
        User user;
        md5Password = HashUtil.createPassword(account, password);
        user = userRepository.findByUserNameAndPassword(account, md5Password);
        if (user != null) {
            return new Response("200", "success");
        } else {
            return new Response("404", "sorry for login failed");
        }
    }

    /**
     * 登录界面 返回token 不需要验证码
     *
     * @param account
     * @param password
     * @return
     */
    @RequestMapping(value = "login", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Response login(@RequestParam("user_name") String account, @RequestParam("password") String password) {
        String md5Password = HashUtil.createPassword(account, password);
        User user = userRepository.findByUserNameAndPassword(account, md5Password);
        if (user != null) {
            String token = getToken(user);
            return new Response("200", token);
        } else {
            return new Response("403", "error");
        }
    }

    public String getToken(User user) {
        Map<String, Object> payload = new HashMap<>();
        Date date = new Date();
        Long currentTime = date.getTime();
        payload.put("uName", user.getUserName());
        payload.put("begin", currentTime);
        payload.put("role", user.getRole());
        user.setLastLoginTime(currentTime.toString());
        userRepository.saveAndFlush(user);
        payload.put("end", date.getTime() + TOKEN_TIME);
        return Jwt.createToken(payload);
    }

    @RequestMapping(value = "/validuser", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public Response validuser(@RequestParam("user_name") String account) {
        User user = userRepository.findByUserName(account);
        if (user != null) {
            return new Response("200", "vaild");
        } else {
            return new Response("403", "invalid");
        }
    }

    /**
     * 需要验证码,返回token的登录api
     * 验证码可以放入redis中
     *
     * @param account
     * @param password
     * @param verify_code
     * @return
     */
    @RequestMapping(value = "/login_verify", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Response login_verify(@RequestParam("user_name") String account, @RequestParam("password") String password,
                                 @RequestParam("verify_code") String verify_code) {
        Response res = new Response();
        String md5Password = null;
        try {
            md5Password = Jwt.getMD5(new StringBuffer(account).reverse().toString().concat(password));
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getCause());
        }
        User user = userRepository.findByUserNameAndPassword(account, md5Password);
        VerifyCode vc = verifyCodeRepository.findByCode(verify_code.toUpperCase());
        if (user != null && vc != null) {
            String token = getToken(user);
            res.setData(token);
            verifyCodeRepository.delete(vc);
            return res.success("200");
        } else if (vc != null) {
            res.setMessage("账户/密码错误");
            verifyCodeRepository.delete(vc);
            return res.failure("404");
        } else {
            res.setMessage("验证码错误");
            return res.failure("404");
        }
    }

    /**
     * token验证
     * 多终端登录
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/login_auth_more", method = RequestMethod.POST)
    public Response login_auth(@RequestParam("token") String token) {
        Response res = new Response();
        if (token != null) {
            Response temp = Jwt.validTokenMore(token);
            if (temp.getStatus().equals("200")) {
                Long user_id = Long.parseLong(temp.getData().toString());
                User user = userRepository.findByUserId(user_id);
                user.setPassword("*******");
                res.setMessage("success");
                res.setData(user);
                return res.success("200");
            } else {
                res.setMessage("error");
                return res.failure("200");
            }
        }
        res.setMessage("error");
        return res.failure("200");
    }

    /**
     * token验证
     * 单终端登录
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/login_auth_one", method = RequestMethod.POST)
    public Response login_auth_one(@RequestParam("token") String token) {
        Response res = new Response();
        Jwt jwt = new Jwt();
        if (token != null) {
            Response temp = jwt.validTokenOne(token, userRepository);
            if (temp.getStatus().equals("200")) {
                User user = userRepository.findByUserName(temp.getData().toString());
                user.setPassword("*******");
                res.setMessage("success");
                res.setData(user);
                return res.success("200");
            } else {
                res.setMessage("error");
                return res.failure("200");
            }
        }
        res.setMessage("error");
        return res.failure("200");
    }

    /**
     * 生成验证码图片
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
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


    @RequestMapping(value = "/test", produces = "application/json", method = RequestMethod.POST)
    public void test() {

    }
}
