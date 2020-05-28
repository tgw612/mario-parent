package com.sibat;

import com.avos.avoscloud.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tgw61 on 2017/4/24.
 */
@RestController
public class Controller {
    @RequestMapping(value = "/test1", produces = "application/json", method = RequestMethod.GET)
    public void test1() {
        AVObject testObject = new AVObject("TestObject");
        testObject.put("words","Hello World!");
        try {
            testObject.save();
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/test2", produces = "application/json", method = RequestMethod.GET)
    public void test2() throws AVException {
        AVObject todoFolder = new AVObject("TodoFolder");// 构建对象
        todoFolder.put("name", "工作");// 设置名称
        todoFolder.put("priority", 1);// 设置优先级
        todoFolder.save();// 保存到服务端
    }

    @RequestMapping(value = "/test3", produces = "application/json", method = RequestMethod.GET)
    public void test3() throws AVException {
        try {
            AVCloudQueryResult result = AVQuery.doCloudQuery("insert into TodoFolder(name, priority) values('工作', 1)");
            // 保存成功
            AVObject todoFolder = result.getResults().get(0);
        } catch (Exception e) {
            // 失败的话，请检查网络环境以及 SDK 配置是否正确
            e.printStackTrace();
        }
    }

    /**
     * query 选项举例】用户的账务账户表 Account 有一个余额字段 balance
     * ，同时有多个请求要修改该字段值，为避免余额出现负值
     * ，只有满足 balance >= 当前请求的数值 这个条件才允许修改，否则提示「余额不足，操作失败！」。
     * @throws AVException
     */
    @RequestMapping(value = "/test4", produces = "application/json", method = RequestMethod.GET)
    public void test4() throws AVException {
        final int amount = -100;
        AVQuery query = new AVQuery("Account");
        AVObject account = query.getFirst();

        account.increment("balance", -amount);

        AVSaveOption option = new AVSaveOption();
        option.query(new AVQuery("Account").whereGreaterThanOrEqualTo("balance",-amount));
        option.setFetchWhenSave(true);
        try {
            account.save(option);
            System.out.println("当前余额为：" + account.getInt("balance"));
        } catch (AVException e){
            if (e != null){
                if (e.getCode() == 305){
                    System.out.println("余额不足，操作失败！");
                }
            }
        }
    }

    /**
     * 同步对象
     多终端共享一个数据时，为了确保当前客户端拿到的对象数据是最新的，可以调用刷新接口来确保本地数据与云端的同步：
     在更新对象操作后，对象本地的 updatedAt 字段（最后更新时间）会被刷新，直到下一次 save 或 fetch 操作
     ，updatedAt 的最新值才会被同步到云端，这样做是为了减少网络流量传输。
     * @throws AVException
     */
    @RequestMapping(value = "/test5", produces = "application/json", method = RequestMethod.GET)
    public void test5() throws AVException {
        String objectId = "5656e37660b2febec4b35ed7";
        // 假如已知了 objectId 可以用如下的方式构建一个 AVObject
        AVObject object = AVObject.createWithoutData("Todo", objectId);
        // 然后调用刷新的方法，将数据从服务端拉到本地
        object.fetch();
        String title = object.getString("title");// 读取 title
        String content = object.getString("content");// 读取 content
    }

    /**
     * 目前 Todo 这个类已有四个自定义属性：priority、content、location 和 title。为了节省流量
     * ，现在只想刷新 priority 和 location 可以使用如下方式：
     * @throws AVException
     */
    @RequestMapping(value = "/test6", produces = "application/json", method = RequestMethod.GET)
    public void test6() throws AVException {
        String objectId = "5656e37660b2febec4b35ed7";
        // 假如已知了 objectId 可以用如下的方式构建一个 AVObject
        AVObject avObject = AVObject.createWithoutData("Todo", objectId);
        String keys = "priority,location";// 指定刷新的 key 字符串
        // 然后调用刷新的方法，将数据从服务端拉到本地
        avObject.fetch(keys);
        // avObject 的 location 和 content 属性的值就是与服务端一致的
        int priority = avObject.getInt("priority");
        String location = avObject.getString("location");
    }
    @RequestMapping(value = "/test7", produces = "application/json", method = RequestMethod.GET)
    public void test7() throws AVException {

    }

    @RequestMapping(value = "/test8", produces = "application/json", method = RequestMethod.GET)
    public void test8() throws AVException {

    }

    @RequestMapping(value = "/test9", produces = "application/json", method = RequestMethod.GET)
    public void test9() throws AVException {

    }

    @RequestMapping(value = "/test11", produces = "application/json", method = RequestMethod.GET)
    public void test11() throws AVException {

    }

    @RequestMapping(value = "/test12", produces = "application/json", method = RequestMethod.GET)
    public void test12() throws AVException {

    }
    @RequestMapping(value = "/test13", produces = "application/json", method = RequestMethod.GET)
    public void test13() throws AVException {

    }

    @RequestMapping(value = "/test14", produces = "application/json", method = RequestMethod.GET)
    public void test14() throws AVException {

    }

    @RequestMapping(value = "/test15", produces = "application/json", method = RequestMethod.GET)
    public void test15() throws AVException {

    }

    @RequestMapping(value = "/test16", produces = "application/json", method = RequestMethod.GET)
    public void test16() throws AVException {

    }

    @RequestMapping(value = "/test17", produces = "application/json", method = RequestMethod.GET)
    public void test17() throws AVException {

    }




}
