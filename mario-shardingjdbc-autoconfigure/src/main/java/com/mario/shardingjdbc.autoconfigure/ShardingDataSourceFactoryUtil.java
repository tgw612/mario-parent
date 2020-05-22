package com.mario.shardingjdbc.autoconfigure;

import com.mario.shardingjdbc.autoconfigure.core.properties.CustomShardingProperties;
import com.mario.shardingjdbc.autoconfigure.core.properties.CustomShardingPropertiesConstant;
import com.mario.shardingjdbc.autoconfigure.core.properties.SpringBootShardingRuleConfigurationProperties;
import io.shardingsphere.api.config.MasterSlaveRuleConfiguration;
import io.shardingsphere.api.config.ShardingRuleConfiguration;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public class ShardingDataSourceFactoryUtil {

    public static DataSource createShardingDataSource(
        SpringBootShardingRuleConfigurationProperties shardingProperties, List<MasterSlaveRuleConfiguration> masterSlaveRuleConfigurationList, Map<String, DataSource> dataSourceMap) throws SQLException {
        //数据分片 加 读写分离
        //分配规则配置
        ShardingRuleConfiguration shardingRuleConfig = shardingProperties.getShardingRuleConfiguration();
        //主从规则配置
        shardingRuleConfig.setMasterSlaveRuleConfigs(masterSlaveRuleConfigurationList);

        CustomShardingProperties customShardingProperties = new CustomShardingProperties(shardingProperties.getProps());

        int executorSize = customShardingProperties.getValue(CustomShardingPropertiesConstant.EXECUTOR_SIZE);
        if (executorSize > 0) {//如果大于0
            //ShardingDataSourceFactory 增加了队列大小
            return CustomShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, shardingProperties.getConfigMap(), shardingProperties.getProps());
        } else {
            //原逻辑
            return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, shardingProperties.getConfigMap(), shardingProperties.getProps());
        }
    }
}
