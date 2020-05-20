package com.mario;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({METHOD, TYPE})
@Retention(RUNTIME)
@Inherited
public @interface MqOrderConsumer {

  /**
   * 分组，不同组，则会根据组的名字加载对应的配置
   *
   * @return
   */
  String group() default "defaultMqOrderConsumer";//则当前的配置项为 mq.consumers.defaultMqOrderConsumer.XXX = XXX
}