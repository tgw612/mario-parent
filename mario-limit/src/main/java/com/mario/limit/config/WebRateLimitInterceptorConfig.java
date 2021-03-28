package com.mario.limit.config;


import com.mario.limit.interceptor.SingleRateLimitInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(SingleRateLimitProperties.class)
@Import({SingleRateLimitInterceptor.class})
@Slf4j
public class WebRateLimitInterceptorConfig implements WebMvcConfigurer {

  @Autowired
  private SingleRateLimitProperties singleRateLimitProperties;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(singleRateLimitInterceptor())
        .addPathPatterns(singleRateLimitProperties.getPathPatterns())
        .excludePathPatterns(singleRateLimitProperties.getExcludePathPatterns());
  }

  @Bean
  public SingleRateLimitInterceptor singleRateLimitInterceptor() {
    return new SingleRateLimitInterceptor(singleRateLimitProperties);
  }

}
