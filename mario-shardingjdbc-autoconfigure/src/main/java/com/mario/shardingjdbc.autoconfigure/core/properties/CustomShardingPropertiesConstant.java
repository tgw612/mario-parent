package com.mario.shardingjdbc.autoconfigure.core.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * //TODO 增加 EXECUTOR_QUEUE_SIZE 线程池队列大小 executor.queue.size（默认值为当前线程数的两倍），设置了线程池大小才会生效
 */
@RequiredArgsConstructor
@Getter
public enum CustomShardingPropertiesConstant {

    /**
     * Enable or Disable to show SQL details.
     *
     * <p>
     * Print SQL details can help developers debug easier.
     * The details includes: logic SQL, parse context and rewrote actual SQL list.
     * Enable this property will log into log topic: {@code Sharding-Sphere-SQL}, log level is {@code INFO}.
     * Default: false
     * </p>
     */
    SQL_SHOW("sql.show", String.valueOf(Boolean.FALSE), boolean.class),

    /**
     * Worker group or user group thread max size.
     *
     * <p>
     * Worker group accept tcp connection.
     * User group accept MySQL command.
     * Default: CPU cores * 2.
     * </p>
     */
    ACCEPTOR_SIZE("acceptor.size", String.valueOf(Runtime.getRuntime().availableProcessors() * 2), int.class),

    /**
     * Worker thread max size.
     *
     * <p>
     * Execute SQL Statement and PrepareStatement will use this thread pool.
     * One sharding data source will use a independent thread pool, it does not share thread pool even different data source in same JVM.
     * Default: infinite.
     * </p>
     */
    EXECUTOR_SIZE("executor.size", String.valueOf(0), int.class),

    /**
     *
     * //TODO 线程池队列大小
     * Worker thread max queue size.
     *
     * <p>
     * Execute SQL Statement and PrepareStatement will use this thread pool queue.
     * One sharding data source will use a independent thread pool, it does not share thread pool even different data source in same JVM.
     * Default: infinite.
     * </p>
     */
    EXECUTOR_QUEUE_SIZE("executor.queue.size", String.valueOf(Runtime.getRuntime().availableProcessors() * 2), int.class),

    MAX_CONNECTIONS_SIZE_PER_QUERY("max.connections.size.per.query", String.valueOf(1), int.class),

    PROXY_TRANSACTION_ENABLED("proxy.transaction.enabled", String.valueOf(Boolean.FALSE), boolean.class),

    PROXY_OPENTRACING_ENABLED("proxy.opentracing.enabled", String.valueOf(Boolean.FALSE), boolean.class),

    PROXY_BACKEND_USE_NIO("proxy.backend.use.nio", String.valueOf(Boolean.FALSE), boolean.class),

    PROXY_BACKEND_MAX_CONNECTIONS("proxy.backend.max.connections", String.valueOf(8), int.class),

    PROXY_BACKEND_CONNECTION_TIMEOUT_SECONDS("proxy.backend.connection.timeout.seconds", String.valueOf(60), int.class);

    private final String key;

    private final String defaultValue;

    private final Class<?> type;

    /**
     * Find value via property key.
     *
     * @param key property key
     * @return value enum, return {@code null} if not found
     */
    public static CustomShardingPropertiesConstant findByKey(final String key) {
        for (CustomShardingPropertiesConstant each : CustomShardingPropertiesConstant.values()) {
            if (each.getKey().equals(key)) {
                return each;
            }
        }
        return null;
    }
}