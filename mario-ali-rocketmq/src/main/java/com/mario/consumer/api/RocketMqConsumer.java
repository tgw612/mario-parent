package com.mario.consumer.api;

import com.aliyun.openservices.ons.api.Admin;

/**
 * Created with IntelliJ IDEA. User: qiujingwang Date: 2016/11/24 Description: 无序消息消费者，用来订阅消息
 *
 * @see com.aliyun.openservices.ons.api.Consumer
 */
public interface RocketMqConsumer extends Admin {

  /**
   * 启动服务
   */
  @Override
  void start();

  /**
   * 关闭服务
   */
  @Override
  void shutdown();

  /**
   * 订阅消息
   *
   * @param topic         消息主题
   * @param subExpression 订阅过滤表达式字符串，ONS服务器依据此表达式进行过滤。只支持或运算<br> eg: "tag1 || tag2 || tag3"<br>
   *                      如果subExpression等于null或者*，则表示全部订阅
   * @param listener      消息回调监听器
   */
  void subscribe(final String topic, final String subExpression,
      final RocketMqMessageListener listener);

  /**
   * 取消某个topic订阅
   *
   * @param topic
   */
  void unsubscribe(final String topic);
}

