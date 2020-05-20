package com.mario.ali.mq.producer;

import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.mario.ali.mq.model.RockMqMessage;
import com.mario.common.threadlocal.SerialNo;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RocketMqProducerAbstract {

  private static final Logger log = LoggerFactory.getLogger(RocketMqProducerAbstract.class);
  static final String SendMsgBodyMaxBytes = "SendMsgBodyMaxBytes";
  static final int Default_Warning_Send_Msg_Body_MaxBytes = 1048576;
  static final int Default_Send_Msg_Body_MaxBytes = 2097152;
  static final int MQ_Default_Send_Msg_Body_MaxBytes = 4193280;
  private int sendMsgBodyMaxBytes = 2097152;

  public RocketMqProducerAbstract() {
  }

  public void init(Properties properties) {
    String bodyMaxBytesStr = properties.getProperty("SendMsgBodyMaxBytes");
    if (bodyMaxBytesStr != null) {
      try {
        this.sendMsgBodyMaxBytes = Integer.parseInt(bodyMaxBytesStr);
      } catch (Exception var4) {
        throw new ONSClientException("Mq SendMsgBodyMaxBytes must be number!");
      }
    }

    if (this.sendMsgBodyMaxBytes <= 0) {
      this.sendMsgBodyMaxBytes = 2097152;
    }

  }

  protected boolean checkBeforeSendMsg(RockMqMessage<?> message) {
    int currentLen = message.getBody().length;
    Object[] params;
    if (currentLen > this.sendMsgBodyMaxBytes) {
      params = new Object[]{SerialNo.getSerialNo(), currentLen, this.sendMsgBodyMaxBytes,
          message.getTopic(), message.getTag(), message.getKey()};
      log.error(
          "[{}] Mq send Failure, Because Message size is {} greater than {}, topic:[{}], tag:[{}], key:[{}]",
          params);
      return false;
    } else if (currentLen > 4193280) {
      params = new Object[]{SerialNo.getSerialNo(), currentLen, 4193280, message.getTopic(),
          message.getTag(), message.getKey()};
      log.error(
          "[{}] Mq send Failure, Because Message size is {} greater than {}, topic:[{}], tag:[{}], key:[{}]",
          params);
      return false;
    } else {
      if (currentLen > 1048576) {
        params = new Object[]{SerialNo.getSerialNo(), currentLen, message.getTopic(),
            message.getTag(), message.getKey()};
        log.warn("[{}] Mq send big Message size is {}, topic:[{}], tag:[{}], key:[{}]", params);
      }

      return true;
    }
  }
}
