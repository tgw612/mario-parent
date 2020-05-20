package com.mario.producer.api;

import com.aliyun.openservices.ons.api.Admin;
import com.aliyun.openservices.ons.api.SendCallback;
import com.mario.model.RockMqMessage;

/**
 * Created with IntelliJ IDEA. User: qiujingwang Date: 2016/11/24 Description: 无序消息生产者接口
 *
 * @see com.aliyun.openservices.ons.api.Producer
 */
public interface RocketMqProducer extends Admin {

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
   * 同步发送消息，只要不抛异常就表示成功
   *
   * @param message
   * @return 发送结果，true 表示发送成功，否则发送失败
   */
  boolean send(final RockMqMessage message);


  /**
   * 发送消息，Oneway形式，服务器不应答，无法保证消息是否成功到达服务器
   *
   * @param message
   * @return 发送结果，true 表示发送成功，否则发送失败
   */
  boolean sendOneway(final RockMqMessage message);

  /**
   * 发送消息，异步Callback形式
   *
   * @param message
   * @return 发送结果，true 表示发送成功，否则发送失败
   */
  boolean sendAsync(final RockMqMessage message, final SendCallback sendCallback);
}