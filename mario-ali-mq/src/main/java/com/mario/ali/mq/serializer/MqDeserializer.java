package com.mario.ali.mq.serializer;


import com.mario.ali.mq.exception.SerializationException;
import com.mario.ali.mq.model.MessageContext;
import com.mario.ali.mq.model.MqBaseMessageBody;

@FunctionalInterface
public interface MqDeserializer<T> {

  MqBaseMessageBody<T> deserialize(byte[] var1, MessageContext var2) throws SerializationException;
}
