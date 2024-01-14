package com.gc.easy.http.handler;

import com.gc.easy.http.EasyHttp;
import com.gc.easy.http.EasyHttpRequest;
import com.gc.easy.http.cloud.ServiceDiscoveryClient;
import com.gc.easy.http.entity.NullClass;
import com.gc.easy.http.util.ReflectMethodUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.BeanUtil;
import net.dreamlu.mica.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.lang.reflect.Method;
import java.util.List;


@Slf4j
@Data
public class DistanceHandler implements EnvironmentAware {

    @Autowired(required = true)
    private EasyHttpRequestHandler httpRequestHandler;
    @Autowired(required = false)
    private ServiceDiscoveryClient serviceDiscoveryClient;

    private Environment environment;

    public Object handle(Object proxy, Method method, Object[] args){
        EasyHttpRequest request = method.getAnnotation(EasyHttpRequest.class);
        String value = request.value();
        HttpMethod httpMethod = request.method();
       // Type returnType = method.getGenericReturnType();
        Class<?> returnType = method.getReturnType();
        Class<?> declaringClass = method.getDeclaringClass();
        List<String> parameters =  ReflectMethodUtil.getParamNames(method);
        EasyHttp httpConnection = declaringClass.getAnnotation(EasyHttp.class);
        //前置处理类
        BeforeFactory beforeFactory;
        Class<? extends BeforeFactory> beforeHandlerClass = httpConnection.before();
        beforeFactory =beforeHandlerClass!= NullClass.class? BeanUtil.newInstance(beforeHandlerClass) :null;
        Class<? extends BeforeFactory> before = request.before();
        if(before != NullClass.class && request.ignoreBefore()){
            beforeFactory =null;
        }else if (before != NullClass.class){
            beforeFactory = BeanUtil.newInstance(before);
        }
        //后置异常处理类
        Class<? extends FallbackFactory> fallback_c = httpConnection.fallback();
        FallbackFactory fallbackFactory = fallback_c != NullClass.class ? BeanUtil.newInstance(fallback_c) : null;
        Class<? extends FallbackFactory> fallback_m = request.fallback();
        if(fallback_m!= NullClass.class){
            fallbackFactory=BeanUtil.newInstance(fallback_m);
        }
        //请求地址
        String baseUrl = httpConnection.baseUrl();
        if (StringUtil.isNotBlank(baseUrl)&&baseUrl.startsWith("${") && baseUrl.endsWith("}")) {
            baseUrl = environment.getProperty(baseUrl.substring(2, baseUrl.length() - 1));
        }
        String serviceName = httpConnection.serviceName();
        if(StringUtil.isNotBlank(serviceName)){
            List<String> instances = serviceDiscoveryClient.getInstances(serviceName);
            if(!CollectionUtils.isEmpty(instances)){
                baseUrl=instances.get(0);
            }
        }
        String requestUrl =  baseUrl+value;
        return this.httpRequestHandler.doHttp(parameters,args, httpMethod, requestUrl, returnType, beforeFactory,request,httpConnection.proxy(), fallbackFactory);
    }

    public EasyHttpRequestHandler getHttpRequestHandler() {
        return this.httpRequestHandler;
    }

    public void setHttpRequestHandler(EasyHttpRequestHandler httpRequestHandler) {
        this.httpRequestHandler = httpRequestHandler;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * 占位符解析
     */
    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }
}
