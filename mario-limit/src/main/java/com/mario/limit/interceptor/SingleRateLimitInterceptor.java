package com.mario.limit.interceptor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mario.common.exception.ServiceException;
import com.mario.common.util.IpUtil;
import com.mario.limit.config.SingleRateLimitProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class SingleRateLimitInterceptor extends HandlerInterceptorAdapter {

  /*
   * DEFAULT_LIMIT_NUM: 500;
   * DEFAULT_EXPIRE_SEC: 10;
   * 表示： 10秒内，一个key请求，最大请求量为 500;
   * */
  /**
   * 单ip最大限制数量
   */
  private static final long DEFAULT_SINGLE_IP_LIMIT_NUM = 30L;

  /**
   * 过期时间
   */
  private static final long DEFAULT_SINGLE_IP_EXPIRE_SECONDS = 10L;

  /**
   * 设置过期时间，即缓存多少秒后失效
   */
  private com.google.common.cache.LoadingCache<String, AtomicLong> LoadingCache = null;

  private SingleRateLimitProperties singleRateLimitProperties;

  public SingleRateLimitInterceptor() {}

  public SingleRateLimitInterceptor(SingleRateLimitProperties singleRateLimitProperties) {
    this.singleRateLimitProperties = singleRateLimitProperties;
    Long ipAndURILimitSeconds = Objects.isNull(singleRateLimitProperties.getIpAndURILimitSeconds()) ? DEFAULT_SINGLE_IP_EXPIRE_SECONDS : singleRateLimitProperties.getIpAndURILimitSeconds();
    LoadingCache = CacheBuilder.newBuilder().expireAfterWrite(ipAndURILimitSeconds, TimeUnit.SECONDS).build(new CacheLoader<String, AtomicLong>() {
      //本地缓存没有命中时，调用load，并将结果缓存
      @Override
      public AtomicLong load(String aLong) throws Exception {
        return getInitAtomicLong();
      }
    });
  }

  private synchronized AtomicLong getInitAtomicLong() {
    return new AtomicLong(0L);
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String uri = request.getRequestURI();
    String ipAddress = IpUtil.getIpAddress(request);
    long incrementAndGet = incrementAndGet(ipAddress + uri);
    Long ipAndURILimitNum = getIpAndURILimitNum();
    if (incrementAndGet > getIpAndURILimitNum()) {
      // 拒绝请求
      log.error("ipAddress:[{}], uri: [{}], 超出请求数, limit_num:[{}], requestNum:[{}]", ipAddress, uri, ipAndURILimitNum, incrementAndGet);
      throw new ServiceException("请求过于频繁，请稍后请求");
    }
    if (log.isDebugEnabled()) {
      log.info("限流请求通过:ipAddress:[{}], uri: [{}], 当前请求数, requestNum:[{}]", ipAddress, uri, incrementAndGet);
    }
    return true;
  }

  private synchronized long incrementAndGet(String key) throws ExecutionException {
    AtomicLong atomic = LoadingCache.get(key);
    return atomic.incrementAndGet();
  }

  public Long getIpAndURILimitNum() {
    return Objects.isNull(singleRateLimitProperties.getIpAndURILimitNum()) ? DEFAULT_SINGLE_IP_LIMIT_NUM : singleRateLimitProperties.getIpAndURILimitNum();
  }

}
