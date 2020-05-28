package com.sibat;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestEmailVerifyCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tgw61 on 2017/4/24.
 */
@RestController
public class UserController {
    @RequestMapping(value = "/addUser", produces = "application/json", method = RequestMethod.POST)
    public Response addUser(@RequestParam("userName") String userName
            , @RequestParam("password") String password) throws AVException {
        AVUser user = new AVUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.setEmail("453424815@qq.com");
        user.setMobilePhoneNumber("18858739630");
        user.signUp();
        AVUser.requestEmailVerify("453424815@qq.com");
        return new Response("200", "success");
    }


    @RequestMapping(value = "/login", produces = "application/json", method = RequestMethod.POST)
    public Response login(@RequestParam("userName") String userName
            , @RequestParam("password") String password) throws AVException {
        AVUser avUser = AVUser.logIn(userName, password);
        return new Response("200", avUser);
    }

    @RequestMapping(value = "/resetPassword", produces = "application/json", method = RequestMethod.POST)
    public Response resetPassword() throws AVException {
        AVUser.requestPasswordReset("453424815@qq.com");
        return new Response("200","success");
    }

    @RequestMapping(value = "/logout", produces = "application/json", method = RequestMethod.POST)
    public Response logout() throws AVException {
        AVUser.logOut();// 清除缓存用户对象
        AVUser currentUser = AVUser.getCurrentUser();// 现在的 currentUser 是 null 了
        return new Response("200","success");
    }
}
