package com.mario.limit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "ratelimit.single")
public class SingleRateLimitProperties {

  /**
   * ip和URI限流周期
   * 默认：10秒
   */
  @Value("${ipAndURILimitSeconds:10}")
  @Getter
  @Setter
  private Long ipAndURILimitSeconds;

  /**
   * ip和URI限流数量
   * 默认：10秒内30个请求
   * */
  @Value("${ipAndURILimitNum:30}")
  @Getter
  @Setter
  private Long ipAndURILimitNum;

  /**
   * 限流拦截路径
   * 默认拦截 /api/**
   */
  @Getter
  @Setter
  @Value("${pathPatterns:/rateLimit/**}")
  private List<String> pathPatterns;

  /**
   * 忽略拦截路径
   */
  @Getter
  @Setter
  @Value("${excludePathPatterns:/excludeRateLimit/**}")
  private List<String> excludePathPatterns;

}