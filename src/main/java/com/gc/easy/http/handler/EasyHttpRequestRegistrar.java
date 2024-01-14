package com.gc.easy.http.handler;

import com.gc.easy.http.EasyHttp;
import com.gc.easy.http.EnableEasyHttpRequest;
import com.gc.easy.http.decoder.DistanceDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EasyHttpRequestRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware, BeanFactoryAware {
    private final static Logger log = LoggerFactory.getLogger(EasyHttpRequestRegistrar.class);

    private static final String SCAN_BASE_PACKAGE_DEFAULT = "com.gc";

    private String[] scanBasePackages;

    private ClassLoader classLoader;

    private ResourceLoader resourceLoader;

    private Environment environment;

    private BeanFactory beanFactory;

    //注册相关处理类
    private void registerDefaultConfiguration(AnnotationMetadata metadata,
                                              BeanDefinitionRegistry registry) {
        Map<String, Object> defaultAttrs = metadata
                .getAnnotationAttributes(EnableEasyHttpRequest.class.getName(), true);

        //获取包的扫描路径
        assert defaultAttrs != null;
        String[] value = (String[]) defaultAttrs.get("value");
        if(value!=null&&value.length>0){
            this.scanBasePackages = value;
        }else {
            String[] basePackages = (String[]) defaultAttrs.get("basePackages");
            if(basePackages!=null&&basePackages.length>0){
                this.scanBasePackages = basePackages;
            }
        }

        if (defaultAttrs.containsKey("defaultConfiguration")) {
            BeanDefinitionBuilder builderDistanceDecoder = BeanDefinitionBuilder
                    .genericBeanDefinition(DistanceDecoder.class);
            registry.registerBeanDefinition("distanceDecoder",builderDistanceDecoder.getBeanDefinition());

            BeanDefinitionBuilder builderDistanceHandler = BeanDefinitionBuilder
                    .genericBeanDefinition(DistanceHandler.class);
            builderDistanceHandler.setLazyInit(false);
            registry.registerBeanDefinition("builderDistanceHandler",builderDistanceHandler.getBeanDefinition());

            BeanDefinitionBuilder builderHttpRequestHandler = BeanDefinitionBuilder
                    .genericBeanDefinition(EasyHttpRequestHandler.class);
            builderHttpRequestHandler.setLazyInit(false);
            registry.registerBeanDefinition("builderHttpRequestHandler",builderHttpRequestHandler.getBeanDefinition());
        }

        registerProxy(registry);
    }

    private void registerProxy(BeanDefinitionRegistry beanDefinitionRegistry){
        try {
            ClassPathScanningCandidateComponentProvider classScanner = this.getClassScanner();
            classScanner.setResourceLoader(this.resourceLoader);
            AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(EasyHttp.class);
            classScanner.addIncludeFilter(annotationTypeFilter);
            log.info("开始扫描http请求bean。。。。。");
            //扫描Bean
            for (String basePackage : getScanBasePackages()) {
                //注册bean
                Set<BeanDefinition> beanDefinitionSet = classScanner.findCandidateComponents(basePackage);
                Iterator iterator = beanDefinitionSet.iterator();
                while(iterator.hasNext()) {
                    BeanDefinition beanDefinition = (BeanDefinition)iterator.next();
                    if (beanDefinition instanceof AnnotatedBeanDefinition) {
                        this.registerBeans((AnnotatedBeanDefinition)beanDefinition);
                    }
                }
            }

        } catch (Exception e) {
            log.error("distance初始化异常" + e);
        }
    }

    private void registerBeans(AnnotatedBeanDefinition annotatedBeanDefinition) {
        String className = annotatedBeanDefinition.getBeanClassName();
        ((DefaultListableBeanFactory)this.beanFactory).registerSingleton(className, this.createProxy(annotatedBeanDefinition));
    }

    private Object createProxy(AnnotatedBeanDefinition annotatedBeanDefinition) {
        try {
            AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
            Class<?> target = Class.forName(annotationMetadata.getClassName());
            InvocationHandler invocationHandler = this.createInvocationHandler();
            Object proxy = Proxy.newProxyInstance(target.getClassLoader(), new Class[]{target}, invocationHandler);
            if (proxy == null) {
                return null;
            }
            return proxy;
        } catch (ClassNotFoundException e) {
            EasyHttpRequestRegistrar.log.error(e.getMessage());
            return null;
        }
    }

    private InvocationHandler createInvocationHandler() {
        return new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                DistanceHandler distanceHandler = EasyHttpRequestRegistrar.this.beanFactory.getBean(DistanceHandler.class);
                EasyHttpRequestHandler httpRequestHandler = distanceHandler.getHttpRequestHandler();
                if (httpRequestHandler == null) {
                    distanceHandler.setHttpRequestHandler(EasyHttpRequestRegistrar.this.beanFactory.getBean(EasyHttpRequestHandler.class));
                }
                return distanceHandler.handle(proxy, method, args);
            }
        };
    }

    private ClassPathScanningCandidateComponentProvider getClassScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isInterface()) {
                    try {
                        Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(), EasyHttpRequestRegistrar.this.classLoader);
                        return !target.isAnnotation();
                    } catch (Exception var3) {
                        EasyHttpRequestRegistrar.log.error("loadClass Exception:", var3);
                    }
                }

                return false;
            }
        };
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public String[] getScanBasePackages() {
        if (scanBasePackages == null || scanBasePackages.length == 0) {
            return new String[]{SCAN_BASE_PACKAGE_DEFAULT};
        }
        return scanBasePackages;
    }

    public void setScanBasePackages(String[] scanBasePackages) {
        this.scanBasePackages = scanBasePackages;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        this.registerDefaultConfiguration(annotationMetadata,beanDefinitionRegistry);
    }
}
