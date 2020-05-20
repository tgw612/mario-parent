package com.mario.ali.mq.model.transaction;

import com.mario.ali.mq.model.MessageContext;
import java.util.Properties;

public class TransactionMessageContext extends MessageContext {

  private long currentSysTimeMil = System.currentTimeMillis();

  public TransactionMessageContext() {
  }

  public TransactionMessageContext(String topic, Properties userProperties,
      Properties systemProperties) {
    super(topic, userProperties, systemProperties);
  }

  public long getCurrentSysTimeMil() {
    return this.currentSysTimeMil;
  }
}
