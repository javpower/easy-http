package com.gc.easy.http;

import com.gc.easy.http.handler.EasyHttpRequestRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({EasyHttpRequestRegistrar.class})
public @interface EnableEasyHttpRequest {
    String[] value() default {};

    String[] basePackages() default {};

    Class<?>[] defaultConfiguration() default {};
}
