package com.gc.easy.http.util;

import com.gc.easy.http.Param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ReflectMethodUtil {
    public static List<String> getParamNames(Method method) {
        notNull(method, "method");
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        return getParamNames(parameterAnnotations);
    }
    public static void notNull(Object object, String name) {
        if (null == object) {
            throw new IllegalArgumentException(name + " can not be null!");
        }
    }
    public static List<String> getParamNames(Annotation[][] parameterAnnotations) {
        if (parameterAnnotations==null||parameterAnnotations.length<1) {
            return Collections.emptyList();
        } else {
            int paramSize = parameterAnnotations.length;
            List<String> resultList =new ArrayList<>(paramSize);

            for(int i = 0; i < paramSize; ++i) {
                Annotation[] annotations = parameterAnnotations[i];
                String paramName = getParamName(i, annotations);
                resultList.add(paramName);
            }

            return resultList;
        }
    }
    private static String getParamName(int index, Annotation[] annotations) {
        String defaultName = "arg" + index;
        if (annotations != null && annotations.length >= 1) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(Param.class)) {
                    Param param = (Param) annotation;
                    return param.value();
                }
            }
        }
        return defaultName;
    }
}