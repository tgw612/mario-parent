package com.mario.ali.mq.util;

import com.mario.ali.mq.model.transaction.TransactionMessageContext;

public class TransactionMessageContextHolder {

  private static final ThreadLocal<TransactionMessageContext> LOCAL_TRANSACTION_CONTEXT_HOLDER = new ThreadLocal();

  public static TransactionMessageContext getTransactionMessageContext() {
    if (LOCAL_TRANSACTION_CONTEXT_HOLDER.get() != null) {
      throw new IllegalStateException(
          "MqUtil.LOCAL_TRANSACTION_CONTEXT_HOLDER has previous value, please clear first.");
    } else {
      return (TransactionMessageContext) LOCAL_TRANSACTION_CONTEXT_HOLDER.get();
    }
  }

  public static void clearTransactionMessageContext() {
    LOCAL_TRANSACTION_CONTEXT_HOLDER.remove();
  }

  public static void setTransactionMessageContext(
      TransactionMessageContext transactionMessageContext) {
    LOCAL_TRANSACTION_CONTEXT_HOLDER.set(transactionMessageContext);
  }

  private TransactionMessageContextHolder() {
  }
}
