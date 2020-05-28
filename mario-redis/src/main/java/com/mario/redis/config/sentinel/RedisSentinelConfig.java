package com.mario.redis.config.sentinel;

import com.mario.redis.config.RedisClusterConfig;
import java.util.HashSet;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisSentinelConfig {

    private final Logger logger = LoggerFactory.getLogger(RedisClusterConfig.class);

    @Value("${redis.jndi-name}")
    private String redisJndiName;

    /**
     * 创建连接工厂
     * @return
     */
    @Bean
    public JedisConnectionFactory createConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = null;
        // 哨兵模式
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        try {
            // 加载JNDI配置(因为有些敏感信息运维是不希望IT直接看到的)
            Context context = new InitialContext();
            RedisConfigBean redisConfigBean = (RedisConfigBean)context.lookup(redisJndiName);

            // 设置哨兵Master
            redisSentinelConfiguration.setMaster(redisConfigBean.getMasterName());
            // 获取到所有节点
            Set<String> serviceRedisNodes = redisConfigBean.getSentinelNodes();
            // 转换成Redis点节
            Set<RedisNode> sentinelNodes = new HashSet<>();
            for(String node : serviceRedisNodes) {
                String[] ipAndPort = StringUtils.split(node, ":");
                String ip = ipAndPort[0];
                Integer port = Integer.parseInt(ipAndPort[1]);
                sentinelNodes.add(new RedisNode(ip, port));
            }
            redisSentinelConfiguration.setSentinels(sentinelNodes);
            // 创建连接工厂
            jedisConnectionFactory = new JedisConnectionFactory(redisSentinelConfiguration, redisConfigBean.getMasterPoolConfig());
            jedisConnectionFactory.setDatabase(redisConfigBean.getDatabase());
            jedisConnectionFactory.setPassword(redisConfigBean.getPassword());
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
