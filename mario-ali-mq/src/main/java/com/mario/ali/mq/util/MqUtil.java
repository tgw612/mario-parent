package com.mario.ali.mq.util;

import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.mario.ali.mq.model.MessageContext;
import com.mario.ali.mq.model.transaction.TransactionMessageContext;
import com.mario.common.constants.CommonConstants;
import com.mario.common.threadlocal.SerialNo;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqUtil {

  private static final Logger log = LoggerFactory.getLogger(MqUtil.class);
  static ConcurrentHashMap<String, Object> consumerIds = new ConcurrentHashMap();
  private static final ThreadLocal<TransactionMessageContext> LOCAL_TRANSACTION_CONTEXT_HOLDER = new ThreadLocal();
  public static int DEFAULT_TRANSACTION_MAX_CHECK_TIME_MILS = 300000;
  public static int DEFAULT_LOCAL_TRANSACTION_EXECUTE_MAX_TIME_MILS = 65000;

  public MqUtil() {
  }

  public static void check(String consumerId) throws IllegalArgumentException {
    if (consumerIds.putIfAbsent(consumerId, CommonConstants.EMPTY_OBJECT) != null) {
      throw new IllegalArgumentException("GroupId(consumerId):" + consumerId + " 订阅关系不一致");
    }
  }

  public static String getConsumerId(Properties properties) {
    return properties.getProperty("GROUP_ID", properties.getProperty("ConsumerId"));
  }

  public static String getProductId(Properties properties) {
    return properties.getProperty("GROUP_ID", properties.getProperty("ProducerId"));
  }

  public static TransactionStatus retryOrRollbackTransactionByCompareBornTimestamp(
      MessageContext messageContext) {
    return retryOrRollbackTransactionByCompareBornTimestamp(System.currentTimeMillis(),
        messageContext);
  }

  public static TransactionStatus retryOrRollbackTransactionByCompareBornTimestamp(long currentMils,
      MessageContext messageContext) {
    Object[] params;
    if (compareTimeWithTransationMaxTime(currentMils, messageContext)) {
      params = new Object[]{SerialNo.getSerialNo(), messageContext.getMsgID(),
          messageContext.getUniqMsgId(), messageContext.getTopic(), messageContext.getTag(),
          messageContext.getKey(), messageContext.getBornTimestamp()};
      log.warn(
          "[{}]Mq transation message, born time is less then transation max time, return unknow(retry). msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], bornTimestamp:[{}]",
          params);
      return TransactionStatus.Unknow;
    } else {
      params = new Object[]{SerialNo.getSerialNo(), messageContext.getMsgID(),
          messageContext.getUniqMsgId(), messageContext.getTopic(), messageContext.getTag(),
          messageContext.getKey(), messageContext.getBornTimestamp()};
      log.warn(
          "[{}]Mq transation message, born time is greater then transation max time, return rollback(delete). msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], bornTimestamp:[{}]",
          params);
      return TransactionStatus.RollbackTransaction;
    }
  }

  public static boolean compareTimeWithTransationMaxTime(long currentSysMils,
      long msgBornTimestamp) {
    return currentSysMils - msgBornTimestamp <= (long) DEFAULT_TRANSACTION_MAX_CHECK_TIME_MILS;
  }

  public static boolean compareTimeWithTransationMaxTime(long currentSysMils,
      MessageContext messageContext) {
    return compareTimeWithTransationMaxTime(currentSysMils, messageContext.getBornTimestamp());
  }

  public static boolean isNeedToRollbackLocalTransactionByDefaultMaxTransactionTimestamp(
      TransactionMessageContext messageContext) {
    if (!compareTimeWithLocalTransationMaxTime(messageContext)) {
      Object[] params = new Object[]{SerialNo.getSerialNo(), messageContext.getMsgID(),
          messageContext.getUniqMsgId(), messageContext.getTopic(), messageContext.getTag(),
          messageContext.getKey(), messageContext.getBornTimestamp()};
      log.warn(
          "[{}]Mq transation message, local transaction execute time is greater then transation max time, need to rollback(transaction). msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], bornTimestamp:[{}]",
          params);
      return true;
    } else {
      return false;
    }
  }

  public static boolean isNeedToRollbackLocalTransactionByTransactionMessageContextHolder() {
    TransactionMessageContext transactionMessageContext = TransactionMessageContextHolder
        .getTransactionMessageContext();
    return isNeedToRollbackLocalTransactionByDefaultMaxTransactionTimestamp(
        transactionMessageContext);
  }

  public static boolean compareTimeWithLocalTransationMaxTime(
      TransactionMessageContext transactionMessageContext) {
    return compareTimeWithLocalTransationMaxTime(System.currentTimeMillis(),
        transactionMessageContext.getCurrentSysTimeMil());
  }

  public static boolean compareTimeWithLocalTransationMaxTime(long currentSysMils,
      long startExecuteTime) {
    return currentSysMils - startExecuteTime
        <= (long) DEFAULT_LOCAL_TRANSACTION_EXECUTE_MAX_TIME_MILS;
  }
}