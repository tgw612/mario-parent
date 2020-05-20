package com.mario.model;

import com.mario.common.model.response.BaseMessage;

/**
 * @Description: 消息统一载体
 */
public interface MqBaseMessageBody<T> extends BaseMessage<T> {

  /**
   * 业务ID(rockmq 可以根据当前业务ID和主题查询消息) --注意：不设置也不会影响消息正常收发
   *
   * @return
   */
  String getBusinessId();
}


