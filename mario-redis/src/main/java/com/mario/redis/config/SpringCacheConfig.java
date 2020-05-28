/**
 * wljs.com Inc.
 * Copyright (c) 2019- All Rights Reserved.
 */
package com.mario.redis.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;


@Configuration
@EnableCaching
public class SpringCacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager redisCacheManager = RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .withInitialCacheConfigurations(getCacheExpireConfig())
                .cacheDefaults(getRedisCacheConfiguration(Duration.ofHours(1))).build();
        return redisCacheManager;
    }

    /**
     * 初始化cachename的缓存时间
     */
    private Map<String, RedisCacheConfiguration> getCacheExpireConfig() {
        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
        for (Map.Entry<String, Duration> entry : Durations.durationMap.entrySet()) {
            configurationMap.put(entry.getKey(), getRedisCacheConfiguration(entry.getValue()));
        }
        return configurationMap;
    }

    private RedisCacheConfiguration getRedisCacheConfiguration(Duration duration) {
        return RedisCacheConfiguration.defaultCacheConfig().prefixKeysWith(RedisConstant.Key.ACT_PRE)
                .entryTtl(duration);
    }

    /**
     * Cache过期时间设置，没有设置的CacheName默认时间为1小时
     */
    private static class Durations {
        public static Map<String, Duration> durationMap = new HashMap<>();
        static {
            durationMap.put(RedisConstant.CacheName.LONG, Duration.ofHours(1));
            durationMap.put(RedisConstant.CacheName.SHORTER, Duration.ofSeconds(3));
            durationMap.put(RedisConstant.CacheName.FIFTEEN_SECONDS, Duration.ofSeconds(15));
            durationMap.put(RedisConstant.CacheName.THIRTY_SECONDS, Duration.ofSeconds(30));
            durationMap.put(RedisConstant.CacheName.NORMAL, Duration.ofMinutes(10));
            durationMap.put(RedisConstant.CacheName.DEFAULT, Duration.ofMinutes(1));
            durationMap.put(RedisConstant.CacheName.SHORT, Duration.ofSeconds(10));
        }
    }
}
