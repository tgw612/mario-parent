package com.mario.redis.config;

import com.mario.common.util.StringUtil;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisClusterConfiguration;

public class RedisClusterConfigurationFactoryBean implements FactoryBean<RedisClusterConfiguration>,
    InitializingBean {

  private static final Logger log = LoggerFactory
      .getLogger(RedisClusterConfigurationFactoryBean.class);
  private String clusterHosts;
  private RedisClusterConfiguration redisClusterConfiguration;
  private Integer maxRedirections;
  private Pattern p = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

  public RedisClusterConfigurationFactoryBean() {
  }

  @Override
  public RedisClusterConfiguration getObject() {
    return this.redisClusterConfiguration;
  }

  @Override
  public Class<? extends RedisClusterConfiguration> getObjectType() {
    return this.redisClusterConfiguration != null ? this.redisClusterConfiguration.getClass()
        : RedisClusterConfiguration.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  private Set<String> parseHostAndPort() {
    if (this.clusterHosts != null && !this.clusterHosts.trim().isEmpty()) {
      String[] split = this.clusterHosts.split(",");
      Set<String> sentinelHosts = new HashSet();
      String[] var3 = split;
      int var4 = split.length;

      for (int var5 = 0; var5 < var4; ++var5) {
        String v = var3[var5];
        if (StringUtil.isNotBlank(v)) {
          sentinelHosts.add(v.trim());
        }
      }

      if (sentinelHosts.isEmpty()) {
        log.error("jedis clusterHosts[ip:port] 配置不能为空");
        throw new IllegalArgumentException("jedis clusterHosts[ip:port] 配置不能为空");
      } else {
        Iterator var7 = sentinelHosts.iterator();

        boolean isIpPort;
        do {
          if (!var7.hasNext()) {
            return sentinelHosts;
          }
          String host = (String) var7.next();
          isIpPort = this.p.matcher(host).matches();
        } while (isIpPort);
        log.error("解析 jedis clusterHosts[ip 或 port] 不合法");
        throw new IllegalArgumentException("解析 jedis clusterHosts[ip 或 port] 不合法");
      }
    } else {
      log.error("jedis clusterHosts[ip:port] 配置不能为空");
      throw new IllegalArgumentException("jedis clusterHosts[ip:port] 配置不能为空");
    }
  }

  @Override
  public void afterPropertiesSet() {
    Set<String> haps = this.parseHostAndPort();
    this.redisClusterConfiguration = new RedisClusterConfiguration(haps);
    this.redisClusterConfiguration.setMaxRedirects(this.maxRedirections);
  }

  public void setClusterHosts(String clusterHosts) {
    this.clusterHosts = clusterHosts;
  }

  public void setMaxRedirections(int maxRedirections) {
    this.maxRedirections = maxRedirections;
  }
}

