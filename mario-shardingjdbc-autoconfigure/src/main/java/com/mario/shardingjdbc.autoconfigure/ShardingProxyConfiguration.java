package com.mario.shardingjdbc.autoconfigure;

import com.google.common.base.Preconditions;
import com.mario.common.util.CollectionUtil;
import com.mario.common.util.SpringBootPropertyUtil;
import com.mario.common.util.StringUtil;
import com.mario.shardingjdbc.autoconfigure.core.properties.SpringBootShardingRuleConfigurationProperties;
import io.shardingsphere.api.algorithm.masterslave.RoundRobinMasterSlaveLoadBalanceAlgorithm;
import io.shardingsphere.api.config.MasterSlaveRuleConfiguration;
import io.shardingsphere.api.config.ShardingRuleConfiguration;
import io.shardingsphere.core.exception.ShardingException;
import io.shardingsphere.core.rule.DataSourceParameter;
import io.shardingsphere.core.util.InlineExpressionParser;
import io.shardingsphere.core.yaml.sharding.YamlShardingRuleConfiguration;
import io.shardingsphere.shardingproxy.config.yaml.ProxyYamlRuleConfiguration;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@AutoConfigureOrder(-2147483648)
@ConditionalOnProperty(
        value = {"sharding.jdbc.bootstrap.enabled"},
        havingValue = "true",
        matchIfMissing = false
)
@EnableConfigurationProperties({SpringBootShardingRuleConfigurationProperties.class})
public class ShardingProxyConfiguration implements EnvironmentAware {
    private static final Logger log = LoggerFactory.getLogger(ShardingProxyConfiguration.class);
    static final String SHARDING_BOOTSTRAP_ENABLED = "sharding.jdbc.bootstrap.enabled";
    private static final String DATASOURCE_PREFIX = "sharding.jdbc.datasource.";
    private static final String PREFIX = "sharding.jdbc.bootstrap.";
    private static final String MASTER_INLINE_NAMES = "sharding.jdbc.bootstrap.master.names";
    private static final String SLAVE_INLINE_NAMES = "sharding.jdbc.bootstrap.slave.names";
    private static final String SLAVE_KEY = "sharding.jdbc.bootstrap.slave.key";
    @Autowired
    private SpringBootShardingRuleConfigurationProperties shardingProperties;
    private final Map<String, DataSourceParameter> dataSourceMap = new LinkedHashMap();
    private final List<MasterSlaveRuleConfiguration> masterSlaveRuleConfigurationList = new ArrayList();

    public ShardingProxyConfiguration() {
    }

    public ProxyYamlRuleConfiguration getConfig() throws Exception {
        ShardingRuleConfiguration shardingRuleConfig = this.shardingProperties.getShardingRuleConfiguration();
        shardingRuleConfig.setMasterSlaveRuleConfigs(this.masterSlaveRuleConfigurationList);
        ProxyYamlRuleConfiguration proxyConfig = new ProxyYamlRuleConfiguration();
        proxyConfig.setDataSources(this.dataSourceMap);
        proxyConfig.setShardingRule(this.shardingProperties);
        return null;
    }

    public YamlShardingRuleConfiguration loadFromEnv(Environment environment) throws Exception {
        YamlShardingRuleConfiguration configurationProperties = (YamlShardingRuleConfiguration) SpringBootPropertyUtil
            .handle(environment, "sharding.jdbc.config.sharding", YamlShardingRuleConfiguration.class);
        return configurationProperties;
    }

    @Override
    public final void setEnvironment(Environment environment) {
        try {
            this.setDataSourceMap(environment);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setDataSourceMap(Environment environment) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Set<String> masterDatasourceNames = this.getEnvSetByKey(environment, "sharding.jdbc.bootstrap.master.names");
        if (CollectionUtil.isEmpty(masterDatasourceNames)) {
            throw new ShardingException("Datasource names[sharding.jdbc.bootstrap.master.names] must not empty!", new Object[0]);
        } else {
            Set<String> allDatabaseNames = this.initMasterSlaveRuleConfiguration(environment, masterDatasourceNames);
            Map<String, Object> commonDataSourceProps = this.getCommonDataSourceProps(environment);
            this.initDataSourceMapByParam(allDatabaseNames, commonDataSourceProps, environment);
        }
    }

    private void initDataSourceMapByParam(Set<String> dsNames, Map<String, Object> commonDataSourceProps, Environment environment) {
        String datasourcePropsKey = null;

        try {
            Iterator var5 = dsNames.iterator();

            while(var5.hasNext()) {
                String dsName = (String)var5.next();
                datasourcePropsKey = "sharding.jdbc.datasource." + dsName;
                Map<String, Object> dataSourceProps = (Map) SpringBootPropertyUtil.handle(environment, datasourcePropsKey, Map.class);
                Preconditions.checkState(dataSourceProps != null && !dataSourceProps.isEmpty(), "Wrong datasource[" + datasourcePropsKey + "] properties!");
                dataSourceProps.putAll(commonDataSourceProps);
                DataSourceParameter dataSource = DataSourceParameterUtil.getDataSourceParameter(new DataSourceParameter(), dataSourceProps);
                this.dataSourceMap.put(dsName, dataSource);
            }

        } catch (ReflectiveOperationException var9) {
            throw new ShardingException("Can't find datasource[" + datasourcePropsKey + "] type!", var9);
        }
    }

    private Map<String, Object> getCommonDataSourceProps(Environment environment) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String key = "common.datasource";
        return (Map)SpringBootPropertyUtil.handle(environment, key, Map.class);
    }

    private Set<String> initMasterSlaveRuleConfiguration(Environment environment, Set<String> masterDatasourceNames) {
        Set<String> slaveDatasourceNames = this.getEnvSetByKey(environment, "sharding.jdbc.bootstrap.slave.names");
        if (CollectionUtil.isEmpty(slaveDatasourceNames)) {
            if (log.isWarnEnabled()) {
                log.warn("Not found slave database!! The env[sharding.jdbc.datasource.slave.names] is not set!!");
            }

            return masterDatasourceNames;
        } else {
            String slavePrefixKey = environment.getProperty("sharding.jdbc.bootstrap.slave.key");
            if (StringUtil.isNull(slavePrefixKey)) {
                throw new ShardingException("Can't found [" + slavePrefixKey + "] value!", new Object[0]);
            } else {
                Set<String> allNames = new HashSet();
                allNames.addAll(slaveDatasourceNames);
                Iterator it = masterDatasourceNames.iterator();

                while(true) {
                    String masterName;
                    do {
                        if (!it.hasNext()) {
                            if (!slaveDatasourceNames.isEmpty()) {
                                throw new ShardingException("The slave database[sharding.jdbc.bootstrap.slave.names=" + (String)slaveDatasourceNames.stream().findFirst().get() + "] not found master database or slave key[" + slavePrefixKey + "] is incorrect!!", new Object[0]);
                            }

                            allNames.addAll(masterDatasourceNames);
                            return allNames;
                        }

                        masterName = (String)it.next();
                    } while(masterName.indexOf(slavePrefixKey) != -1);

                    String salveNamePrefix = masterName + slavePrefixKey;
                    Set<String> slaveNames = new HashSet();
                    Iterator salveIt = slaveDatasourceNames.iterator();

                    while(salveIt.hasNext()) {
                        String salveName = (String)salveIt.next();
                        if (salveName.startsWith(salveNamePrefix)) {
                            slaveNames.add(salveName);
                            salveIt.remove();
                        }
                    }

                    if (!slaveNames.isEmpty()) {
                        RoundRobinMasterSlaveLoadBalanceAlgorithm roundRobinMasterSlaveLoadBalanceAlgorithm = new RoundRobinMasterSlaveLoadBalanceAlgorithm();
                        MasterSlaveRuleConfiguration masterSlaveRuleConfiguration = new MasterSlaveRuleConfiguration(masterName, masterName, slaveNames, roundRobinMasterSlaveLoadBalanceAlgorithm);
                        this.masterSlaveRuleConfigurationList.add(masterSlaveRuleConfiguration);
                    }
                }
            }
        }
    }

    private Set<String> getEnvSetByKey(Environment environment, String propName) {
        String inlineValues = environment.getProperty(propName);
        List<String> stringValus = null;
        if (!StringUtil.isEmpty(inlineValues)) {
            inlineValues = inlineValues.trim();

            try {
                stringValus = (new InlineExpressionParser(InlineExpressionParser.handlePlaceHolder(inlineValues))).splitAndEvaluate();
            } catch (Exception var6) {
                throw new ShardingException("Parser names expression [" + propName + "] Exception!", var6);
            }
        }

        return (Set)(CollectionUtil.isEmpty(stringValus) ? Collections.EMPTY_SET : new HashSet(stringValus));
    }

    public SpringBootShardingRuleConfigurationProperties getShardingProperties() {
        return this.shardingProperties;
    }

    public void setShardingProperties(SpringBootShardingRuleConfigurationProperties shardingProperties) {
        this.shardingProperties = shardingProperties;
    }

    public Map<String, DataSourceParameter> getDataSourceMap() {
        return this.dataSourceMap;
    }

    public List<MasterSlaveRuleConfiguration> getMasterSlaveRuleConfigurationList() {
        return this.masterSlaveRuleConfigurationList;
    }
}

