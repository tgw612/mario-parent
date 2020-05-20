package com.mario;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MqEnvProperties.MQ_PREFIX)
@Data
@ToString
public class MqEnvProperties {

  public static final String MQ_PREFIX = "mq";
  public static final String MQ_PRODUCERS_PREFIX = MQ_PREFIX + ".producers";
  public static final String MQ_ORDER_PRODUCERS_PREFIX = MQ_PREFIX + ".order-producers";
  public static final String MQ_TRANSACTION_PRODUCERS_PREFIX = MQ_PREFIX + ".transaction-producers";

  static final String BASE_PACKAGES_PROPERTY_NAME = MQ_PREFIX + ".consumer-scan-base-packages";


  /**
   * 环境
   *
   * @see ITopicEnv 如：com.doubo.ali.mq.autoconfigure.TopicEnv.TEST
   */
  private String topicEnv;

  /**
   * 说明：
   * 单个日志文件大小：64MB
   * 保存历史日志文件的最大个数：100个（默认为10）
   * 日志级别：默认为 INFO
   */
  /**
   * mq log 目录
   */
  private String logRoot;

  /**
   * 级别 "ERROR", "WARN", "INFO", "DEBUG"
   */
  private String logLevel;

  /**
   * Log max index
   */
  private int logMaxIndex = 10;

  /**
   * 消费者
   */
  private Map<String, Properties> consumers = new LinkedHashMap<>(3);

  /**
   * 普通消息生产者
   * <p>
   * 注意事项 定时和延时消息的 msg.setStartDeliverTime 参数需要设置成当前时间戳之后的某个时刻（单位毫秒）。如果被设置成当前时间戳之前的某个时刻，消息将立刻投递给消费者。
   * 定时和延时消息的 msg.setStartDeliverTime 参数可设置40天内的任何时刻（单位毫秒），超过40天消息发送将失败。 StartDeliverTime
   * 是服务端开始向消费端投递的时间。 如果消费者当前有消息堆积，那么定时和延时消息会排在堆积消息后面，将不能严格按照配置的时间进行投递。
   * 由于客户端和服务端可能存在时间差，消息的实际投递时间与客户端设置的投递时间之间可能存在偏差。 设置定时和延时消息的投递时间后，依然受 3 天的消息保存时长限制。例如，设置定时消息 5
   * 天后才能被消费，如果第 5 天后一直没被消费，那么这条消息将在第8天被删除。 除 Java 语言支持延时消息外，其他语言都不支持延时消息
   *
   * @see MqEnvProperties.MQ_PRODUCERS_PREFIX
   */
  private Map<String, Properties> producers = new LinkedHashMap<>(3);

  /**
   * 顺序消息生产者
   * <p>
   * 注意事项： 顺序消息暂不支持广播模式 同一个 Producer ID 或者 Consumer ID 只能对应一种类型的 Topic，即不能同时用于顺序消息和无序消息的收发。
   * 顺序消息不支持异步发送方式，否则将无法严格保证顺序。 对于全局顺序消息，建议创建实例个数 >=2。 同时运行多个实例的作用是为了防止工作实例意外退出时，业务中断。
   * 当工作实例退出时，其他实例可以立即接手工作，不会导致业务中断，实际同时工作的只会有一个实例。
   *
   * @see MqEnvProperties.MQ_ORDER_PRODUCERS_PREFIX
   */
  private Map<String, Properties> orderProducers = new LinkedHashMap<>(3);
  /**
   * 事务消息生产者
   * <p>
   * 注意事项 事务消息的 Producer ID 不能与其他类型消息的 Producer ID 共用。与其他类型的消息不同，事务消息有回查机制，回查时MQ Server会根据Producer
   * ID去查询客户端。
   * <p>
   * 通过 ONSFactory.createTransactionProducer 创建事务消息的 Producer 时必须指定 LocalTransactionChecker
   * 的实现类，处理异常情况下事务消息的回查。
   * <p>
   * 事务消息发送完成本地事务后，可在 execute 方法中返回以下三种状态：
   * <p>
   * TransactionStatus.CommitTransaction 提交事务，允许订阅方消费该消息。 TransactionStatus.RollbackTransaction
   * 回滚事务，消息将被丢弃不允许消费。 TransactionStatus.Unknow 暂时无法判断状态，期待固定时间以后 MQ Server 向发送方进行消息回查。
   * 可通过以下方式给每条消息设定第一次消息回查的最快时间：
   * <p>
   * Message message = new Message(); // 在消息属性中添加第一次消息回查的最快时间，单位秒。例如，以下设置实际第一次回查时间为 120 ~ 125 秒之间
   * message.putUserProperties(PropertyKeyConst.CheckImmunityTimeInSeconds,"120"); //
   * 以上方式只确定事务消息的第一次回查的最快时间，实际回查时间向后浮动0~5秒；如第一次回查后事务仍未提交，后续每隔5秒回查一次。
   *
   * @see MqEnvProperties.MQ_TRANSACTION_PRODUCERS_PREFIX
   */
  private Map<String, TransactionProducerProperties> transactionProducers = new LinkedHashMap<>(3);
}
