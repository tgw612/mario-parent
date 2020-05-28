package com.sibat.controller;

import com.sibat.constant.RoleConstant;
import com.sibat.domain.User;
import com.sibat.domain.UserRepository;
import com.sibat.util.Response;
import com.sibat.util.captcha.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tgw61 on 2017/7/17.
 */

@RestController
@RequestMapping("/user")
public class UserApi {
    @Autowired
    UserRepository userRepository;


    /**
     * 添加用户
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/add_user", produces = "application/json", method = RequestMethod.POST)
    public Response addUser(User user) {
        Response res = new Response();
        try {
            if (userRepository.findByUserName(user.getUserName()) != null) {
                res.setMessage("此帐号被注册");
                return res.failure("403");
            } else {
                //可将密码进行不可逆加密
                String password = HashUtil.createPassword(user.getUserName(),user.getPassword());
                user.setPassword(password);
                user.setRole(RoleConstant.ROLE_USER);
                userRepository.saveAndFlush(user);
                res.setMessage("success");
                return res.success("200");
            }
        } catch (Exception e) {
            res.setMessage("发生了一些错误...");
            return res.failure("500");
        }
    }

    /**
     * 删除用户
     *
     * @param user_id
     * @return
     */
    @RequestMapping(value = "/delete_user", produces = "application/json", method = RequestMethod.POST)
    public Response delete_user(@RequestParam("user_id") long user_id) {
        Response response = new Response();
        try {
            User user = userRepository.findByUserId(user_id);
            if (user != null) {
                userRepository.delete(user);
                response.setMessage("删除成功");
                return response.success("200");
            } else {
                response.setMessage("删除失败");
                return response.success("500");
            }
        } catch (Exception e) {
            response.setMessage("删除失败");
            return response.success("500");
        }
    }



    /**
     * 修改密码
     *
     * @param user_id
     * @param old_password
     * @param new_password
     * @return
     */
//    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/change_password", produces = "application/json", method = RequestMethod.POST)
    public Response change_password(//@AuthenticationPrincipal Principal principal,
                                @RequestParam("user_id") long user_id,
                                @RequestParam("old_password") String old_password,
                                @RequestParam("new_password") String new_password) {
        Response res = new Response();
        User user = userRepository.findByUserId(user_id);
        String old_passwordMd5 = HashUtil.createPassword(user.getUserName(), old_password);
        String new_passwordMd5 = HashUtil.createPassword(user.getUserName(), new_password);
        if (user != null && user.getPassword().equals(old_passwordMd5)) {
            user.setPassword(new_passwordMd5);
            userRepository.saveAndFlush(user);
            //跳转到登录页面
            res.setMessage("修改密码成功");
            return res.success("200");
        } else {
            res.setMessage("未找到该用户");
            return res.failure("404");
        }
    }

}
