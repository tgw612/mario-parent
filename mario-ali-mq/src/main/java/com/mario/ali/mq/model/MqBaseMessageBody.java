package com.mario.ali.mq.model;

import com.mario.common.model.response.BaseMessage;

public interface MqBaseMessageBody<T> extends BaseMessage<T> {

  String getBusinessId();
}
