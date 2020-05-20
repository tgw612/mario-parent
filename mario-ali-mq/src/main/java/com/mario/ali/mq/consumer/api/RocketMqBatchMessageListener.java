package com.mario.ali.mq.consumer.api;

import com.aliyun.openservices.ons.api.Action;
import com.mario.ali.mq.model.MessageContext;
import com.mario.ali.mq.model.MqBaseMessageBody;
import com.mario.ali.mq.serializer.MqDeserializer;
import com.mario.ali.mq.topic.MqTopic;
import java.util.List;

public interface RocketMqBatchMessageListener<T> extends MqDeserializer<T> {

  default Action call(MqBaseMessageBody<T> record, MessageContext messageContext) throws Exception {
    return Action.CommitMessage;
  }

  default Action call(List<MqBaseMessageBody<T>> records, List<MessageContext> messageContexts)
      throws Exception {
    for (int i = 0; i < records.size(); ++i) {
      Action action = this
          .call((MqBaseMessageBody) records.get(i), (MessageContext) messageContexts.get(i));
      if (action == null || action != Action.CommitMessage) {
        return action;
      }
    }

    return Action.CommitMessage;
  }

  default MqTopic subscriTopic() {
    return null;
  }
}