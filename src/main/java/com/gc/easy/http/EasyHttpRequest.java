package com.gc.easy.http;


import com.gc.easy.http.entity.NullClass;
import com.gc.easy.http.handler.BeforeFactory;
import com.gc.easy.http.handler.FallbackFactory;
import org.springframework.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EasyHttpRequest {
    HttpMethod method() default HttpMethod.POST;

    String value();
    Class<? extends BeforeFactory> before() default NullClass.class;
    boolean ignoreBefore() default false;
    Class<?> param()  default NullClass.class;
    Class<?> query() default NullClass.class;
    Class<?> body() default NullClass.class;
    Class <?>form()  default NullClass.class;
    Class<?> header() default NullClass.class;

    String[] paramName()  default {} ;
    String[] queryName() default {};
    String[] bodyName() default {};
    String[] formName()  default {};
    String[] headerName() default {};

    Class<? extends FallbackFactory> fallback() default NullClass.class;

}
