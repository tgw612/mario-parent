package com.mario.ali.mq.model.order;

import com.mario.ali.mq.model.MessageContext;
import java.util.Properties;

public class OrderMessageContext extends MessageContext {

  public OrderMessageContext() {
  }

  public OrderMessageContext(String topic, Properties userProperties, Properties systemProperties) {
    super(topic, userProperties, systemProperties);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof OrderMessageContext)) {
      return false;
    } else {
      OrderMessageContext other = (OrderMessageContext) o;
      return other.canEqual(this);
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof OrderMessageContext;
  }

  @Override
  public int hashCode() {
    int result = 1;
    return result;
  }

  @Override
  public String toString() {
    return "OrderMessageContext()";
  }
}
