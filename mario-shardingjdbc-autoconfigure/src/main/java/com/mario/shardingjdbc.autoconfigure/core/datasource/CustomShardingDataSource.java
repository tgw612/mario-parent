package com.mario.shardingjdbc.autoconfigure.core.datasource;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mario.common.util.ReflectionUtil;
import com.mario.shardingjdbc.autoconfigure.core.properties.CustomShardingProperties;
import com.mario.shardingjdbc.autoconfigure.core.properties.CustomShardingPropertiesConstant;
import io.shardingsphere.api.ConfigMapContext;
import io.shardingsphere.core.executor.ShardingExecuteEngine;
import io.shardingsphere.core.executor.ShardingThreadFactoryBuilder;
import io.shardingsphere.core.rule.ShardingRule;
import io.shardingsphere.shardingjdbc.jdbc.adapter.AbstractDataSourceAdapter;
import io.shardingsphere.shardingjdbc.jdbc.core.ShardingContext;
import io.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection;
import io.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;

public class CustomShardingDataSource extends AbstractDataSourceAdapter implements AutoCloseable {
    private final Map<String, DataSource> dataSourceMap;
    private final ShardingContext shardingContext;
    private final CustomShardingProperties shardingProperties;

    public CustomShardingDataSource(Map<String, DataSource> dataSourceMap, ShardingRule shardingRule) throws SQLException {
        this(dataSourceMap, shardingRule, new ConcurrentHashMap(), new Properties());
    }

    public CustomShardingDataSource(Map<String, DataSource> dataSourceMap, ShardingRule shardingRule, Map<String, Object> configMap, Properties props) throws SQLException {
        super(dataSourceMap.values());
        this.checkDataSourceType(dataSourceMap);
        if (!configMap.isEmpty()) {
            ConfigMapContext.getInstance().getShardingConfig().putAll(configMap);
        }

        this.dataSourceMap = dataSourceMap;
        this.shardingProperties = new CustomShardingProperties(null == props ? new Properties() : props);
        this.shardingContext = this.getShardingContext(shardingRule);
    }

    private void checkDataSourceType(Map<String, DataSource> dataSourceMap) {
        Iterator var2 = dataSourceMap.values().iterator();

        while(var2.hasNext()) {
            DataSource each = (DataSource)var2.next();
            Preconditions.checkArgument(!(each instanceof MasterSlaveDataSource), "Initialized data sources can not be master-slave data sources.");
        }

    }

    private ShardingContext getShardingContext(ShardingRule shardingRule) throws SQLException {
        int executorSize = (Integer)this.shardingProperties.getValue(
            CustomShardingPropertiesConstant.EXECUTOR_SIZE);
        int maxConnectionsSizePerQuery = (Integer)this.shardingProperties.getValue(CustomShardingPropertiesConstant.MAX_CONNECTIONS_SIZE_PER_QUERY);
        boolean showSQL = (Boolean)this.shardingProperties.getValue(CustomShardingPropertiesConstant.SQL_SHOW);
        ShardingExecuteEngine executeEngine = new ShardingExecuteEngine(executorSize);
        if (executorSize > 0) {
            int queueSize = (Integer)this.shardingProperties.getValue(CustomShardingPropertiesConstant.EXECUTOR_QUEUE_SIZE);
            ListeningExecutorService newExecuteEngine = this.getNewListeningExecutorService(executorSize, queueSize);
            this.resetListeningExecutorService(executeEngine, newExecuteEngine);
        }

        return new ShardingContext(this.dataSourceMap, shardingRule, this.getDatabaseType(), executeEngine, maxConnectionsSizePerQuery, showSQL);
    }

    @Override
    public final ShardingConnection getConnection() {
        return new ShardingConnection(this.dataSourceMap, this.shardingContext);
    }

    @Override
    public final void close() {
        this.closeOriginalDataSources();
        this.shardingContext.close();
    }

    private void closeOriginalDataSources() {
        Iterator var1 = this.dataSourceMap.values().iterator();

        while(var1.hasNext()) {
            DataSource each = (DataSource)var1.next();

            try {
                each.getClass().getDeclaredMethod("close").invoke(each);
            } catch (ReflectiveOperationException var4) {
            }
        }

    }

    private void resetListeningExecutorService(ShardingExecuteEngine executeEngine, ListeningExecutorService newExecutorService) {
        String listeningExecutorServiceFieldName = "executorService";
        Field listeningExecutorServiceField = ReflectionUtil
            .getAccessibleField(executeEngine, listeningExecutorServiceFieldName);
        if (listeningExecutorServiceField == null) {
            throw new RuntimeException("Can not found field[executorService] in ShardingExecuteEngine.");
        } else {
            try {
                listeningExecutorServiceField.set(executeEngine, newExecutorService);
            } catch (IllegalAccessException var6) {
                throw new RuntimeException("Can not set field[executorService] in ShardingExecuteEngine.", var6);
            }
        }
    }

    private ListeningExecutorService getNewListeningExecutorService(int executorSize, int queueSize) {
        if (queueSize <= 0) {
            queueSize = 0;
        }

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(executorSize, executorSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(queueSize), ShardingThreadFactoryBuilder.build());
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(threadPoolExecutor);
        MoreExecutors.addDelayedShutdownHook(executorService, 60L, TimeUnit.SECONDS);
        return executorService;
    }
}