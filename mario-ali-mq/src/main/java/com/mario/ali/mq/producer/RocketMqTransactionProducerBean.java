package com.mario.ali.mq.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.mario.ali.mq.model.MessageContext;
import com.mario.ali.mq.model.MqBaseMessageBody;
import com.mario.ali.mq.model.RockMqSendResult;
import com.mario.ali.mq.model.transaction.RockMqTransactionMessage;
import com.mario.ali.mq.model.transaction.TransactionMessageContext;
import com.mario.ali.mq.producer.api.RocketMqLocalTransactionExecuter;
import com.mario.ali.mq.producer.api.RocketMqTransactionMessageChecker;
import com.mario.ali.mq.producer.api.transaction.RocketMqTransactionProducer;
import com.mario.ali.mq.serializer.MqSerializer;
import com.mario.ali.mq.util.MqContextUtil;
import com.mario.ali.mq.util.TransactionMessageContextHolder;
import com.mario.common.constants.CommonConstants;
import com.mario.common.enums.AppName;
import com.mario.common.threadlocal.SerialNo;
import com.mario.common.util.ExceptionUtil;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocketMqTransactionProducerBean extends RocketMqProducerAbstract implements
    RocketMqTransactionProducer {

  private static final Logger log = LoggerFactory.getLogger(RocketMqTransactionProducerBean.class);
  private Properties properties;
  private RocketMqTransactionMessageChecker rocketMqTransactionMessageChecker;
  private TransactionProducer transactionproducer;

  public RocketMqTransactionProducerBean() {
  }

  public void start() {
    if (null == this.properties) {
      throw new ONSClientException("properties not set");
    } else if (null == this.rocketMqTransactionMessageChecker) {
      throw new ONSClientException("Transaction MQ localTransactionChecker must not null");
    } else {
      super.init(this.properties);
      this.transactionproducer = ONSFactory.createTransactionProducer(this.properties,
          this.getLocalTransactionChecker(this.rocketMqTransactionMessageChecker));
      this.transactionproducer.start();
    }
  }

  public void updateCredential(Properties credentialProperties) {
    if (this.transactionproducer != null) {
      this.transactionproducer.updateCredential(credentialProperties);
    }

  }

  @Override
  public void shutdown() {
    if (this.transactionproducer != null) {
      this.transactionproducer.shutdown();
    }

  }

  @Override
  public <T> boolean send(RockMqTransactionMessage<T> message,
      RocketMqLocalTransactionExecuter executer, Object arg, MqSerializer<T> serializer) {
    return this.sendBackResult(message, executer, arg, serializer).isSuccess();
  }

  @Override
  public <T> RockMqSendResult sendBackResult(final RockMqTransactionMessage<T> message,
      final RocketMqLocalTransactionExecuter executer, Object arg, MqSerializer<T> serializer) {
    if (message.getContent() == null) {
      Object[] params = new Object[]{SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
          message.getKey()};
      log.error(
          "[{}] Transaction Mq send Failure, Because Message content is null , topic:[{}], tag:[{}], key:[{}]",
          params);
      return RockMqSendResult.fail();
    } else {
      message.putUserProperties("CheckImmunityTimeInSeconds",
          Integer.toString(message.getCheckImmunityTimeInSeconds()));
      SendResult sendResult = null;
      long start = System.currentTimeMillis();

      try {
        byte[] bytes = serializer.serialize(message.getContent());
        message.setBody(bytes);
        if (!this.checkBeforeSendMsg(message)) {
          return RockMqSendResult.fail();
        }

        sendResult = this.transactionproducer.send(message, new LocalTransactionExecuter() {
          public TransactionStatus execute(Message msg, Object arg) {
            TransactionMessageContext transactionMessageContext = MqContextUtil
                .getTransactionMessageContext(msg);
            TransactionStatus transactionStatus = TransactionStatus.Unknow;

            try {
              TransactionMessageContextHolder
                  .setTransactionMessageContext(transactionMessageContext);
              transactionStatus = executer.execute(message, arg, transactionMessageContext);
            } finally {
              TransactionMessageContextHolder.clearTransactionMessageContext();
            }

            return transactionStatus;
          }
        }, arg);
      } catch (Throwable var10) {
        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
            message.getKey(), message.getContent(), System.currentTimeMillis() - start,
            ExceptionUtil.getAsString(var10)};
        log.error(
            "[{}] Transaction Mq send Exception, topic:[{}], tag:[{}], key:[{}], message:[{}], costTime:{}ms, Some Exception Occur:[{}]",
            params);
      }

      return RockMqSendResult.successIfNotNull(sendResult);
    }
  }

  @Override
  public <T> boolean send(RockMqTransactionMessage<T> message,
      RocketMqLocalTransactionExecuter executer, MqSerializer<T> serializer) {
    return this.sendBackResult(message, executer, serializer).isSuccess();
  }

  @Override
  public <T> RockMqSendResult sendBackResult(RockMqTransactionMessage<T> message,
      RocketMqLocalTransactionExecuter executer, MqSerializer<T> serializer) {
    return this.sendBackResult(message, executer, CommonConstants.EMPTY_OBJECT, serializer);
  }

  public Properties getProperties() {
    return this.properties;
  }

  public RocketMqTransactionMessageChecker getRocketMqTransactionMessageChecker() {
    return this.rocketMqTransactionMessageChecker;
  }

  public void setRocketMqTransactionMessageChecker(
      RocketMqTransactionMessageChecker rocketMqTransactionMessageChecker) {
    this.rocketMqTransactionMessageChecker = rocketMqTransactionMessageChecker;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  @Override
  public boolean isStarted() {
    return this.transactionproducer.isStarted();
  }

  @Override
  public boolean isClosed() {
    return this.transactionproducer.isClosed();
  }

  private LocalTransactionChecker getLocalTransactionChecker(
      final RocketMqTransactionMessageChecker checker) {
    return new LocalTransactionChecker() {
      public TransactionStatus check(Message message) {
        MqBaseMessageBody record = null;

        TransactionStatus var10;
        try {
          Object[] params;
          try {
            SerialNo.init(AppName.DOUBO_MQ);
            MessageContext messageContext = MqContextUtil.getMessageContext(message);
            record = checker.deserialize(message.getBody(), messageContext);
            if (record == null) {
              params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), message.getTopic(),
                  message.getTag(), message.getKey(),
                  RocketMqTransactionProducerBean.this.properties.get("GROUP_ID"),
                  message.getReconsumeTimes()};
              RocketMqTransactionProducerBean.log.error(
                  "[{}]Transaction Mq consume message Exception: record is null. msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}]",
                  params);
            } else if (RocketMqTransactionProducerBean.log.isDebugEnabled()) {
              params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), message.getTopic(),
                  message.getTag(), message.getKey(),
                  RocketMqTransactionProducerBean.this.properties.get("GROUP_ID"),
                  message.getReconsumeTimes(), record};
              RocketMqTransactionProducerBean.log.debug(
                  "[{}]Transaction Mq Received message: msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}]",
                  params);
            } else {
              params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), message.getTopic(),
                  message.getTag(), message.getKey(), message.getReconsumeTimes()};
              RocketMqTransactionProducerBean.log.info(
                  "[{}]Transaction Mq Received message: msgId:[{}], topic[{}], tag[{}], key[{}], retryTimes:[{}]",
                  params);
            }

            var10 = checker.check(record, messageContext);
            return var10;
          } catch (Throwable var8) {
            if (record != null) {
              params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), message.getTopic(),
                  message.getTag(), message.getKey(),
                  RocketMqTransactionProducerBean.this.properties.get("GROUP_ID"),
                  message.getReconsumeTimes(), record, ExceptionUtil.getAsString(var8)};
              RocketMqTransactionProducerBean.log.error(
                  "[{}]Transaction Mq consume message Exception: msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}], Exception:{}",
                  params);
            } else {
              params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), message.getTopic(),
                  message.getTag(), message.getKey(),
                  RocketMqTransactionProducerBean.this.properties.get("GROUP_ID"),
                  message.getReconsumeTimes(), record, ExceptionUtil.getAsString(var8)};
              RocketMqTransactionProducerBean.log.error(
                  "[{}]Transaction Mq consume message Exception: msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record is null, message:[{}], Exception:{}",
                  params);
            }

            var10 = TransactionStatus.Unknow;
          }
        } finally {
          SerialNo.clear();
        }

        return var10;
      }
    };
  }
}
