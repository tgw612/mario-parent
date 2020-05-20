package com.mario.ali.mq.serializer;

import com.mario.ali.mq.exception.SerializationException;
import com.mario.ali.mq.model.MqBaseMessageBody;

@FunctionalInterface
public interface MqSerializer<T> {

  byte[] serialize(MqBaseMessageBody<T> var1) throws SerializationException;
}
