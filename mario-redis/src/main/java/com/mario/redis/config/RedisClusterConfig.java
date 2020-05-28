package com.mario.redis.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

@Configuration
public class RedisClusterConfig {

  private final Logger logger = LoggerFactory.getLogger(RedisClusterConfig.class);

  // private final String defaultClusterNodes = "10.16.5.43:6379,10.16.5.44:6379,10.16.5.45:6379";

  @Value("${redis.cluster.nodes}")
  private String clusterNodes;

  @Value("${redis.password}")
  private String password;

  @Value("${redis.pool.max-active}")
  private Integer maxTotal;

  @Value("${redis.pool.max-idle}")
  private Integer maxIdle;

  @Value("${redis.pool.min-idle}")
  private Integer minIdle;

  @Value("${redis.pool.max-wait}")
  private Integer maxWaitMillis;

  /**
   * 创建连接工厂
   * @return
   */
  @Bean
  public JedisConnectionFactory createConnectionFactory() {
    JedisConnectionFactory jedisConnectionFactory = null;
    // 集群模式
    RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
    // 加载服务器集群节点
    Set<String> serviceRedisNodes = new HashSet<>(Arrays.asList(clusterNodes.split(",")));
    try {
      // 转换成Redis点节
      Set<RedisNode> clusterNodes = new HashSet<>(serviceRedisNodes.size());
      for(String node : serviceRedisNodes) {
        String[] ipAndPort = StringUtils.split(node, ":");
        String ip = ipAndPort[0];
        Integer port = Integer.parseInt(ipAndPort[1]);
        clusterNodes.add(new RedisNode(ip, port));
      }
      redisClusterConfiguration.setClusterNodes(clusterNodes);

      // Redis 连接池配置
      JedisPoolConfig poolConfig = new JedisPoolConfig();
      poolConfig.setMaxIdle(maxIdle);
      poolConfig.setMaxTotal(maxTotal);
      poolConfig.setMinIdle(minIdle);
      poolConfig.setMaxWaitMillis(maxWaitMillis);

      // 创建连接工厂
      jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration, poolConfig);
      // 设置数据库
      jedisConnectionFactory.setDatabase(0);
      // 设置密码
      jedisConnectionFactory.setPassword(password);
    } catch (Exception e) {
      logger.error("创建Redis连接工厂错误：{}", e);
    }
    return jedisConnectionFactory;
  }

  /**
   * 注册RedisTemplate
   * @param factory 连接工厂
   * @return
   */
  @Bean
  public RedisTemplate registerRedisTemplate(JedisConnectionFactory factory) {
    RedisTemplate redisTemplate = new RedisTemplate();
    redisTemplate.setConnectionFactory(factory);
    return redisTemplate;
  }
}