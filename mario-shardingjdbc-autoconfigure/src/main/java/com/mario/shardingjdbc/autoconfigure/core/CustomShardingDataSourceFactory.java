package com.mario.shardingjdbc.autoconfigure.core;

import com.mario.shardingjdbc.autoconfigure.core.datasource.CustomShardingDataSource;
import io.shardingsphere.api.config.ShardingRuleConfiguration;
import io.shardingsphere.core.rule.ShardingRule;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;

public final class CustomShardingDataSourceFactory {

  public static DataSource createDataSource(Map<String, DataSource> dataSourceMap,
      ShardingRuleConfiguration shardingRuleConfig,
      Map<String, Object> configMap, Properties props) throws SQLException {
    return new CustomShardingDataSource(dataSourceMap,
        new ShardingRule(shardingRuleConfig, dataSourceMap.keySet()),
        configMap, props);
  }

  private CustomShardingDataSourceFactory() {
  }
}
