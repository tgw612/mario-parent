package com.mario;

import java.util.Properties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionProducerProperties extends Properties {

  /**
   * 事务消息回调检查器clazz
   * @see RocketMqTransactionMessageChecker
   */
//    private String transactionMessageCheckerClazz;

  /**
   * 事务消息回调检查器beanName
   */
  private String transactionMessageCheckerBeanName;

}
