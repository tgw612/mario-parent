package com.mario.autoimport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Configuration
@Import({CustomAutoConfigurationImportSelector.class})
public @interface CustomImportAutoConfiguration {

  Class<?>[] value();
}