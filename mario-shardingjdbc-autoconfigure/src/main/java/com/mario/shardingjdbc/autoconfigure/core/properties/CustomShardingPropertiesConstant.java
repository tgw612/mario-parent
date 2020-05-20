package com.mario.shardingjdbc.autoconfigure.core.properties;

import java.beans.ConstructorProperties;

public enum CustomShardingPropertiesConstant {
  SQL_SHOW("sql.show", String.valueOf(Boolean.FALSE), Boolean.TYPE),
  ACCEPTOR_SIZE("acceptor.size", String.valueOf(Runtime.getRuntime().availableProcessors() * 2),
      Integer.TYPE),
  EXECUTOR_SIZE("executor.size", String.valueOf(0), Integer.TYPE),
  EXECUTOR_QUEUE_SIZE("executor.queue.size",
      String.valueOf(Runtime.getRuntime().availableProcessors() * 2), Integer.TYPE),
  MAX_CONNECTIONS_SIZE_PER_QUERY("max.connections.size.per.query", String.valueOf(1), Integer.TYPE),
  PROXY_TRANSACTION_ENABLED("proxy.transaction.enabled", String.valueOf(Boolean.FALSE),
      Boolean.TYPE),
  PROXY_OPENTRACING_ENABLED("proxy.opentracing.enabled", String.valueOf(Boolean.FALSE),
      Boolean.TYPE),
  PROXY_BACKEND_USE_NIO("proxy.backend.use.nio", String.valueOf(Boolean.FALSE), Boolean.TYPE),
  PROXY_BACKEND_MAX_CONNECTIONS("proxy.backend.max.connections", String.valueOf(8), Integer.TYPE),
  PROXY_BACKEND_CONNECTION_TIMEOUT_SECONDS("proxy.backend.connection.timeout.seconds",
      String.valueOf(60), Integer.TYPE);

  private final String key;
  private final String defaultValue;
  private final Class<?> type;

  public static CustomShardingPropertiesConstant findByKey(String key) {
    CustomShardingPropertiesConstant[] var1 = values();
    int var2 = var1.length;

    for (int var3 = 0; var3 < var2; ++var3) {
      CustomShardingPropertiesConstant each = var1[var3];
      if (each.getKey().equals(key)) {
        return each;
      }
    }

    return null;
  }

  @ConstructorProperties({"key", "defaultValue", "type"})
  private CustomShardingPropertiesConstant(String key, String defaultValue, Class<?> type) {
    this.key = key;
    this.defaultValue = defaultValue;
    this.type = type;
  }

  public String getKey() {
    return this.key;
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public Class<?> getType() {
    return this.type;
  }
}