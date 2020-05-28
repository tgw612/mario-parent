//package com.sibat;
//
//import com.sibat.domain.User;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * Created by tgw61 on 2017/7/8.
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class RedisTest {
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//    @Autowired
//    private RedisTemplate<String, User> redisTemplate;
//
//    @Test
//    public void test() throws Exception {
//        // 保存字符串
//        stringRedisTemplate.opsForValue().set("aaa", "111");
//        Assert.assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
//
//        // 保存对象
//        User user = new User("admin", "admin");
//        redisTemplate.opsForValue().set(user.getUserName(), user);
//        user = new User("user1", "user1");
//        redisTemplate.opsForValue().set(user.getUserName(), user);
//        user = new User("user2", "user2");
//        redisTemplate.opsForValue().set(user.getUserName(), user);
//        Assert.assertEquals("user1", redisTemplate.opsForValue().get("user1").getPassword());
//        Assert.assertEquals("admin", redisTemplate.opsForValue().get("admin").getPassword());
//        Assert.assertEquals("user2", redisTemplate.opsForValue().get("user2").getPassword());
//    }
//}
//
