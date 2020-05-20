package com.mario.shardingjdbc.autoconfigure;

import com.google.common.base.Preconditions;
import com.mario.common.util.CollectionUtil;
import com.mario.common.util.SpringBootPropertyUtil;
import com.mario.common.util.StringUtil;
import io.shardingsphere.api.algorithm.masterslave.RoundRobinMasterSlaveLoadBalanceAlgorithm;
import io.shardingsphere.api.config.MasterSlaveRuleConfiguration;
import io.shardingsphere.core.exception.ShardingException;
import io.shardingsphere.core.util.InlineExpressionParser;
import io.shardingsphere.shardingjdbc.spring.boot.masterslave.SpringBootMasterSlaveRuleConfigurationProperties;
import io.shardingsphere.shardingjdbc.spring.boot.sharding.SpringBootShardingRuleConfigurationProperties;
import io.shardingsphere.shardingjdbc.util.DataSourceUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;


/**
 * sharding.jdbc.bootstrap.enabled = true sharding.jdbc.bootstrap.master.names =
 * ds$->{0..1},mallbase sharding.jdbc.bootstrap.slave.names = ds$->{0..1}slave$->{0..1},mallbaseslave
 * sharding.jdbc.bootstrap.slave.key = slave
 * <p>
 * #数据源 sharding.jdbc.datasource.mallbase.url = jdbc:mysql://127.0.0.1:3306/mall_order0?autoReconnect=true&tinyInt1isBit=false&useUnicode=true&useSSL=false
 * sharding.jdbc.datasource.mallbaseslave.url = jdbc:mysql://127.0.0.1:3306/mall_order0?autoReconnect=true&tinyInt1isBit=false&useUnicode=true&useSSL=false
 * <p>
 * sharding.jdbc.datasource.ds0slave0.url = jdbc:mysql://127.0.0.1:3306/mall_order0?autoReconnect=true&tinyInt1isBit=false&useUnicode=true&useSSL=false
 * sharding.jdbc.datasource.ds0slave1.url = jdbc:mysql://1127.0.0.1:3306/mall_order1?autoReconnect=true&tinyInt1isBit=false&useUnicode=true&useSSL=false
 * sharding.jdbc.datasource.ds1slave0.url = jdbc:mysql://1127.0.0.1:3306/mall_order0?autoReconnect=true&tinyInt1isBit=false&useUnicode=true&useSSL=false
 * sharding.jdbc.datasource.ds1slave1.url = jdbc:mysql://1127.0.0.1:3306/mall_order1?autoReconnect=true&tinyInt1isBit=false&useUnicode=true&useSSL=false
 * <p>
 * sharding.jdbc.datasource.ds0.url = jdbc:mysql://1127.0.0.1:3306/mall_order0?autoReconnect=true&tinyInt1isBit=false&useUnicode=true&useSSL=false
 * sharding.jdbc.datasource.ds1.url = jdbc:mysql://1127.0.0.1:3306/mall_order1?autoReconnect=true&tinyInt1isBit=false&useUnicode=true&useSSL=false
 * 所有公共数据源配置 common.datasource 从节点公共配置（配置则会覆盖common.datasource） slave.datasource
 */
@Slf4j
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
//比DataSourceTransactionManagerAutoConfiguration启动级别高
@ConditionalOnProperty(value = ShardingJdbcBootConfiguration.SHARDING_BOOTSTRAP_ENABLED, havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties({
    SpringBootShardingRuleConfigurationProperties.class,
    SpringBootMasterSlaveRuleConfigurationProperties.class})
public class ShardingJdbcBootConfiguration implements EnvironmentAware, DisposableBean {

  static final String SHARDING_BOOTSTRAP_ENABLED = "sharding.jdbc.bootstrap.enabled";
  private static final String DATASOURCE_PREFIX = "sharding.jdbc.datasource.";
  private static final String PREFIX = "sharding.jdbc.bootstrap.";
  private static final String MASTER_INLINE_NAMES = PREFIX + "master.names";
  private static final String SLAVE_INLINE_NAMES = PREFIX + "slave.names";
  private static final String SLAVE_KEY = PREFIX + "slave.key";
  private static final String SHARDING_DEBUG_ENABLED = "sharding.jdbc.bootfast.enabled";//TODO (并行初始化数据源)
  private static final String SHARDING_DEBUG_THREADS = "sharding.jdbc.bootfast.threads";//TODO

  @Getter
  @Setter
  @Autowired
  private SpringBootShardingRuleConfigurationProperties shardingProperties;

//    @Autowired(required = false)
//    private StringEncDecryption passwordDecrypter;

  @Getter
  private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

  @Getter
  private final List<MasterSlaveRuleConfiguration> masterSlaveRuleConfigurationList = new ArrayList();

  private Environment environment;

  /**
   * sharding-jdbc 数据源，(初始化出错时 由ShardingJdbcBootConfiguration.destroy()回收资源，那还有必须加@Bean(destroyMethod
   * = "close")吗，有，正常情况有必要）
   *
   * @return
   * @throws Exception
   */
  @Bean(destroyMethod = "close")
  public DataSource dataSource(@Nullable ObjectProvider<DataSourceWrapper> dataSourceWrapper)
      throws Exception {
    DataSource dataSource = createDataSource();
    if (dataSourceWrapper != null) {
      DataSourceWrapper rawDataSourceWrapper = dataSourceWrapper.getIfAvailable();
      if (rawDataSourceWrapper != null) {
        dataSource = rawDataSourceWrapper.wrapDataSource(dataSource);
      }
    }

    return dataSource;
  }

  /**
   * sharding-jdbc 数据源
   *
   * @return
   * @throws Exception
   */
  public DataSource createDataSource() throws Exception {
    //如果配置了快速启动
    Boolean fastEnabled = environment.getProperty(SHARDING_DEBUG_ENABLED, Boolean.class, false);
    if (fastEnabled) {
      Integer threadsSize = environment.getProperty(SHARDING_DEBUG_THREADS, Integer.class,
          (Runtime.getRuntime().availableProcessors() << 1) + 1);
      return new ShardingLazyDataSource(dataSourceMap, shardingProperties,
          masterSlaveRuleConfigurationList, threadsSize);
    }
    return ShardingDataSourceFactoryUtil
        .createShardingDataSource(shardingProperties, masterSlaveRuleConfigurationList,
            dataSourceMap);
  }

  /**
   * 设置ENV 环境变量时 去解析数据源(包含分库／不分库) 必须是groovy 表达式
   *
   * @param environment
   * @prifix sharding.jdbc.datasource.
   */
  @Override
  public final void setEnvironment(final Environment environment) {
    this.setDataSourceMap(environment);
    this.environment = environment;
  }

  private void setDataSourceMap(final Environment environment) {
    //TODO 主节点名字集合
    Set<String> masterDatasourceNames = this.getEnvSetByKey(environment, MASTER_INLINE_NAMES);
    if (CollectionUtil.isEmpty(masterDatasourceNames)) {
      throw new ShardingException("Datasource names[" + MASTER_INLINE_NAMES + "] must not empty!");
    }

    //TODO 从节点名字集合
    Set<String> slaveDatasourceNames = this
        .initMasterSlaveRuleConfigurationAndReturnSlaveNames(environment, masterDatasourceNames);
    //TODO 验证节点是否重复（冲突）
    checkNamesConflict(masterDatasourceNames, slaveDatasourceNames);

    //TODO 公共的数据源配置
    String dataSourceKey = "spring.datasource";
    Map<String, Object> commonDataSourceProps = this
        .getCommonDataSourceProps(environment, dataSourceKey);
    if (CollectionUtil.isEmpty(commonDataSourceProps)) {
      throw new ShardingException("Can't found Datasource[" + dataSourceKey + "] value!");
    }

    this.initDataSourceMapByParam(masterDatasourceNames, commonDataSourceProps, environment);

    //TODO 判断是否有从节点配置
    if (!slaveDatasourceNames.isEmpty()) {
      //TODO 从库公共的数据源配置
      String slaveDataSourceKey = "slave.datasource";
      Map<String, Object> slaveDataSourceProps = this
          .getCommonDataSourceProps(environment, slaveDataSourceKey);
      if (CollectionUtil.isEmpty(slaveDataSourceProps)) {
        if (log.isWarnEnabled()) {
          log.warn("Not found slave database config!! The env[{}] is not set!!",
              slaveDataSourceKey);
        }
        //TODO 使用公共数据源配置
        slaveDataSourceProps = commonDataSourceProps;
      } else {
        //TODO 从库公共的数据源配置【slave.datasource】 配置覆盖 公共的数据源配置【common.datasource】
        Map<String, Object> newSalveDataSourceProps = new HashMap<>(commonDataSourceProps);
        newSalveDataSourceProps.putAll(slaveDataSourceProps);
        slaveDataSourceProps = newSalveDataSourceProps;
      }
      this.initDataSourceMapByParam(slaveDatasourceNames, slaveDataSourceProps, environment);
    } else {
      //not found slave nodes names
      if (log.isWarnEnabled()) {
        log.warn("No slave database names config!!");
      }
    }
  }

  private void checkNamesConflict(Set<String> masterDatasourceNames,
      Set<String> slaveDatasourceNames) {
    Set<String> allNames = new HashSet<>(masterDatasourceNames);
    allNames.addAll(slaveDatasourceNames);

    boolean conflict =
        allNames.size() != (masterDatasourceNames.size() + slaveDatasourceNames.size());
    if (conflict) {
      //主从节点名字冲突
      throw new ShardingException("The Master and slave datasource names conflict!!");
    }
  }

  private void initDataSourceMapByParam(Set<String> dsNames,
      Map<String, Object> dataSourcePropsParam,
      Environment environment) {
    String datasourcePropsKey = null;
    try {
      for (String dsName : dsNames) {
        datasourcePropsKey = DATASOURCE_PREFIX + dsName;
        //io.shardingsphere.shardingjdbc.spring.boot.util.PropertyUtil.handle
        Map<String, Object> dataSourceProps = SpringBootPropertyUtil
            .handle(environment, datasourcePropsKey, Map.class);
        Preconditions.checkState(dataSourceProps != null && !dataSourceProps.isEmpty(),
            "Wrong datasource[" + datasourcePropsKey + "] properties!");
        //TODO 使用每个数据源指定的配置 覆盖 默认的配置
        dataSourcePropsParam.put("url", dataSourceProps.get("url"));
        dataSourceProps.putAll(dataSourcePropsParam);
        //解密密码
        //this.decryptPwd(newDataSourceProps);

        //ReflectiveOperationUtil.getInstance(dataSourceProps.get("type").toString(), dataSourceProps)
        DataSource dataSource = DataSourceUtil
            .getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
        dataSourceMap.put(dsName, dataSource);
      }
    } catch (final ReflectiveOperationException ex) {
      throw new ShardingException("Can't find datasource[" + datasourcePropsKey + "] type!", ex);
    }
  }

  /**
   * 获取公共的数据源配置
   *
   * @param environment
   * @return
   * @prifix common.datasource
   */
  private Map<String, Object> getCommonDataSourceProps(Environment environment, String key) {
    //io.shardingsphere.shardingjdbc.spring.boot.util.PropertyUtil.handle
    try {
      return SpringBootPropertyUtil.handle(environment, key, Map.class);
    } catch (Exception ex) {
      if (isNoSuchElementException(ex)) {
        if (log.isWarnEnabled()) {
          log.warn("Not found key[{}] from Environment!!", key);
        }
      }
    }
    return null;
  }

  private boolean isNoSuchElementException(Exception ex) {
    Throwable tempEx = ex;
    while (tempEx != null) {
      if (tempEx instanceof NoSuchElementException) {
        return true;
      }
      tempEx = tempEx.getCause();
    }
    return false;
  }

  /**
   * 初始化MasterSlaveRuleConfig 并 返回从节点名字集合
   *
   * @param environment
   * @param masterDatasourceNames
   * @return
   */
  private Set<String> initMasterSlaveRuleConfigurationAndReturnSlaveNames(Environment environment,
      Set<String> masterDatasourceNames) {
    Set<String> slaveDatasourceNames = this.getEnvSetByKey(environment, SLAVE_INLINE_NAMES);
    if (CollectionUtil.isEmpty(slaveDatasourceNames)) {
      if (log.isWarnEnabled()) {
        log.warn(
            "Not found slave database!! The env[sharding.jdbc.datasource.slave.names] is not set!!");
      }
      return Collections.emptySet();
    }
    String slavePrefixKey = environment.getProperty(SLAVE_KEY);//eg. "slave" ds0_SALVE0 ds0_SALVE1
    if (StringUtil.isBlank(slavePrefixKey)) {
      throw new ShardingException("Can't found [" + slavePrefixKey + "] value!");
    }

    Set<String> returnSlaveDatasourceNames = new HashSet<>();
    returnSlaveDatasourceNames.addAll(slaveDatasourceNames);

    for (Iterator<String> it = masterDatasourceNames.iterator(); it.hasNext(); ) {
      String masterName = it.next();
      //有可能是master
      if (masterName.indexOf(slavePrefixKey) == -1) {
        String salveNamePrefix = masterName + slavePrefixKey;
        Set<String> slaveNames = new HashSet<>();
        for (Iterator<String> salveIt = slaveDatasourceNames.iterator(); salveIt.hasNext(); ) {
          String salveName = salveIt.next();
          if (salveName.startsWith(salveNamePrefix)) {
            slaveNames.add(salveName);
            salveIt.remove();
          }
        }
        //是否有主从
        if (!slaveNames.isEmpty()) {
          RoundRobinMasterSlaveLoadBalanceAlgorithm roundRobinMasterSlaveLoadBalanceAlgorithm =
              new RoundRobinMasterSlaveLoadBalanceAlgorithm();
          MasterSlaveRuleConfiguration masterSlaveRuleConfiguration = new MasterSlaveRuleConfiguration(
              masterName, masterName,
              slaveNames, roundRobinMasterSlaveLoadBalanceAlgorithm);
          //解析主从规则配置
          masterSlaveRuleConfigurationList.add(masterSlaveRuleConfiguration);
        }
      }
    }

    //判断从库是否全部已经找到所属主库
    if (!slaveDatasourceNames.isEmpty()) {
      throw new ShardingException(
          "The slave database[" + SLAVE_INLINE_NAMES + "=" + slaveDatasourceNames.stream()
              .findFirst().get() + "] not found master database or slave key[" + slavePrefixKey
              + "] is incorrect!!");
    }
    return returnSlaveDatasourceNames;
  }

  private Set<String> getEnvSetByKey(Environment environment, String propName) {
    String inlineValues = environment.getProperty(propName);
    List<String> stringValus = null;
    if (StringUtils.isNotBlank(inlineValues)) {
      inlineValues = inlineValues.trim();
      try {
        //TODO 行表达式标识符可以使用${...}或$->{...}，但前者与Spring本身的属性文件占位符冲突，因此在Spring环境中使用行表达式标识符建议使用$->{...}。
        stringValus = new InlineExpressionParser(
            InlineExpressionParser.handlePlaceHolder(inlineValues)).splitAndEvaluate();
      } catch (Exception e) {
        throw new ShardingException("Parser names expression [" + propName + "] Exception!", e);
      }
    }
    if (CollectionUtil.isEmpty(stringValus)) {
      return Collections.EMPTY_SET;
    }

    return new HashSet<>(stringValus);
  }

  @Override
  public void destroy() throws Exception {
    dataSourceMap.forEach((k, v) -> {
      try {
        v.getClass().getDeclaredMethod("close").invoke(v);
      } catch (final ReflectiveOperationException ignored) {
      }
    });
  }

    /*private void decryptPwd(Map<String, Object> dataSourceProps) {
        if (passwordDecrypter != null) {
            String pwdKey = "password";
            Object o = dataSourceProps.get(pwdKey);
            if (o != null) {
                String pwdVal = o.toString();
                try {
                    pwdVal = passwordDecrypter.decrypt(pwdVal);
                    dataSourceProps.put(pwdKey, pwdVal);
                } catch (Exception e) {
                    throw new ShardingException("[DES decrypt ERROR] Error enountered during decryptio datasource password[" + pwdVal + "]!", e);
                }

            }
        }
    }*/
}