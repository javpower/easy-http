package com.gc.easy.http;

import com.gc.easy.http.entity.NullClass;
import com.gc.easy.http.handler.BeforeFactory;
import com.gc.easy.http.handler.FallbackFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EasyHttp {
    String baseUrl() default ""; // 请求地址前缀
    String serviceName() default ""; // 微服务名称

    Class<? extends BeforeFactory> before() default NullClass.class; //前置处理类

    String[] proxy() default {}; //代理 {"ip","port"}

    Class<? extends FallbackFactory> fallback() default NullClass.class; //后置异常处理

}