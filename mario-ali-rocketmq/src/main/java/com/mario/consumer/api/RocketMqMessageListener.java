package com.mario.consumer.api;

import com.aliyun.openservices.ons.api.Action;
import com.mario.model.MessageContext;
import com.mario.model.MqBaseMessageBody;

/**
 * 无序消息消费接口 Created with IntelliJ IDEA. User: qiujingwang Date: 2016/7/14
 */
@FunctionalInterface
public interface RocketMqMessageListener {

  /**
   * 执行业务 返回Action.CommitMessage表示消费成功，或 抛异常也是表示消费成功（只有返回Action.ReconsumeLater才会告知服务器稍后再投递这条消息,消费失败）
   *
   * @param record         消息记录
   * @param messageContext 消息上下文
   * @return Action
   */
  Action call(MqBaseMessageBody record, MessageContext messageContext) throws Exception;
}
