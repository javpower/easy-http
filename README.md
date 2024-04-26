
# 🚀 EasyHttp: 声明式HTTP客户端框架

`EasyHttp` 是一个基于注解的声明式HTTP客户端框架，旨在简化HTTP请求的发送过程，让调用第三方HTTP API变得轻松而直观。

## ✨ 特性亮点

- **基于OkHttp**：强大的底层实现，提供高效的网络操作。
- **全注解式**：通过注解配置请求，简化了模板代码。
- **本地方法风格**：以调用本地方法的方式发送HTTP请求，提高开发效率。
- **微服务支持**：支持微服务架构中的服务间调用。
- **多种请求方法**：支持GET、POST、PUT、DELETE等多种HTTP方法。
- **前置与后置处理**：支持自定义前置请求处理和异常后置处理。
- **Spring生态集成**：完美集成Spring和Spring Boot。
- **JSON支持**：内建JSON数据序列化和反序列化功能。

## 📦 极速开始

### 添加Maven依赖

在项目的 `pom.xml` 文件中添加以下依赖：

```xml
<dependency>
    <groupId>io.github.javpower</groupId>
    <artifactId>easy-http-spring-boot-starter</artifactId>
    <version>2.7.18.2</version>
</dependency>
```

### 创建接口

定义一个接口并使用 `EasyHttp` 的注解来声明HTTP请求：

```java
@EasyHttp(baseUrl = "https://www.xxx.cc")
public interface TestPlatformApiService {
    @EasyHttpRequest(method = HttpMethod.POST, value = "/api/test")
    EasyHttpVo<?> addPerson(@Body TestPersonAdd add);

    @EasyHttpRequest(method = HttpMethod.POST, value = "/api/v1/passport/comm/sendEmailVerify")
    TestVo sendEmailVerify(@Param("email") String email);
}
```

### 扫描接口

在Spring Boot的配置类或启动类上加上 `@EnableEasyHttpRequest` 注解：

```java
@SpringBootApplication
@EnableEasyHttpRequest(basePackages = "com.gc.subscribeboot.test")
public class SubscribebootApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubscribebootApplication.class, args);
    }
}
```

### 调用接口

注入接口实例并调用定义的方法：

```java
@Autowired
private TestPlatformApiService testPlatformApiService;

// 调用接口
TestVo response = testPlatformApiService.sendEmailVerify("example@example.com");
System.out.println(response.toString());
```

## 📝 注解描述

`EasyHttp` 提供了三个主要注解：

- `@EasyHttp`：定义基础URL和其他配置。
- `@EasyHttpRequest`：定义具体的HTTP请求。
- `@Param`：用于将方法参数绑定到请求的不同部分。

```java
@EasyHttp(baseUrl = "https://www.xxx.cc", //Http请求前缀，支持${}配置文件中获取 （可选）
        before = TestBeforeFactory.class, //前置处理类，需实现 BeforeFactory （可选）
        serviceName="rpc", //微服务名称 （可选）
        proxy = {"127.0.0.1","8088"}, //代理地址 （可选）
        fallback=TestFallBack.class //后置异常自定义处理 需实现 FallbackFactory（可选）
)
@Service
@Slf4j
public class TestBeforeFactory implements BeforeFactory {

    @Override
    public void doBefore(EasyHttpDto build) { //build内包含header、param、query、body、form、proxy、cookie都可修改
        build.addHeader("token","xxxxxxxx");
        //可以动态设置代理
        build.setProxy("127.0.0.1",8080);
    }
}
@Service
@Slf4j
public class TestFallBack implements FallbackFactory {

    @Override
    public Object create(Throwable throwable) {
        return null;
    }
}
@EasyHttpRequest(method = HttpMethod.POST, 
        value = "/api/test", 
        body = TestPersonAdd.class,
        formName = {"email"}
)
TestVo sendEmailVerify(@Param("email") String email);

public @interface EasyHttpRequest {
    HttpMethod method() default HttpMethod.POST; //请求类型
    String value(); //接口地址
    Class<? extends BeforeFactory> before() default NullClass.class;
    boolean ignoreBefore() default false;
    Class<?> param()  default NullClass.class;
    Class<?> query() default NullClass.class;
    Class<?> body() default NullClass.class; //指定参数为该类型的为转换为json-body ,其他同理
    Class <?>form()  default NullClass.class;
    Class<?> header() default NullClass.class;
    String[] paramName()  default {} ; 
    String[] queryName() default {};
    String[] bodyName() default {};
    String[] formName()  default {}; //指定@Param注解的参数为form数据，其他同理
    String[] headerName() default {};
    Class<? extends FallbackFactory> fallback() default NullClass.class;

}

```

## 🔧 微服务扩展

`EasyHttp` 支持与常见的服务发现工具集成，如Consul、Eureka、Nacos。
```java
/**
 * 如果使用的是Consul
 */
  @Component
  public class ServiceDiscoveryComponent implements ServiceDiscoveryClient{
 
      private final ConsulDiscoveryProperties consulProperties;
      private final ConsulDiscoveryService consulDiscoveryService;
      @Autowired
      public ServiceDiscoveryComponent(ConsulDiscoveryProperties consulProperties, ConsulDiscoveryService consulDiscoveryService) {
          this.consulProperties = consulProperties;
          this.consulDiscoveryService = consulDiscoveryService;
     }
      public List<String> getInstances(String serviceName) {
          List<String> serviceUrls = consulDiscoveryService.getServices().values()
                                 .stream()
                                .flatMap(serviceInstances -> serviceInstances.stream())
                                 .map(serviceInstance -> serviceInstance.getServiceId())
                                .map(serviceId -> consulProperties.getTags().get(serviceId))
                               .collect(Collectors.toList());
                //todo: 过滤所需要的
         return serviceUrls;
     }
  }

/**
 * 如果使用的是Eureka
 */
  @Component
  public class ServiceDiscoveryComponent implements ServiceDiscoveryClient{
 
            private final DiscoveryClient discoveryClient;
            @Autowired
            public ServiceDiscoveryComponent(DiscoveryClient discoveryClient) {
                this.discoveryClient = discoveryClient;
             }
           public List<String> getInstances(String serviceName) {
                List<String> serviceUrls = discoveryClient.getServices()
                                .stream()
                                 .flatMap(serviceName -> discoveryClient.getInstances(serviceName).stream())
                                 .map(instance -> instance.getUri().toString())
                                .collect(Collectors.toList());
               //todo: 过滤所需要的
          return serviceUrls;
     }
  }
/**
 * 如果使用的是Nacos
 */
  @Component
  public class ServiceDiscoveryComponent implements ServiceDiscoveryClient {
 
      private final NacosServiceManager nacosServiceManager;
      @Autowired
      public ServiceDiscoveryComponent(NacosServiceManager nacosServiceManager) {
          this.nacosServiceManager = nacosServiceManager;
      }
      public List<String> getInstances(String serviceName) {
          List<String> serviceUrls = nacosServiceManager.getAllInstances()
                                 .stream()
                                 .map(instance -> instance.toInetAddr().toString())
                                 .collect(Collectors.toList());
          //todo: 过滤所需要的
          return serviceUrls;
      }
  }
```

## 📜 请求日志

`EasyHttp` 提供了详细的请求日志，帮助开发者调试和监控API调用。


```java
2024-01-14 14:35:43.242  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : --> POST https://www.xxx.cc/api/v1/passport/comm/sendEmailVerify
        2024-01-14 14:35:43.242  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : Content-Type: application/x-www-form-urlencoded
        2024-01-14 14:35:43.242  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : Content-Length: 25
        2024-01-14 14:35:43.242  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0
        2024-01-14 14:35:43.243  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   :
        2024-01-14 14:35:43.243  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : email=1www7ggg%40nqmo.com
        2024-01-14 14:35:43.243  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : --> END POST (25-byte body)
        2024-01-14 14:35:44.233  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : <-- 200 https://www.xxx.cc/api/v1/passport/comm/sendEmailVerify (989ms)
        2024-01-14 14:35:44.233  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : server: nginx
        2024-01-14 14:35:44.233  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : date: Sun, 14 Jan 2024 06:35:44 GMT
        2024-01-14 14:35:44.233  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : content-type: application/json
        2024-01-14 14:35:44.233  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : cache-control: no-cache, private
2024-01-14 14:35:44.233  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : access-control-allow-origin:
        2024-01-14 14:35:44.234  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : access-control-allow-methods: GET,POST,OPTIONS,HEAD
        2024-01-14 14:35:44.234  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : access-control-allow-headers: Origin,Content-Type,Accept,Authorization,X-Request-With
        2024-01-14 14:35:44.234  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : access-control-allow-credentials: true
        2024-01-14 14:35:44.234  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : access-control-max-age: 10080
        2024-01-14 14:35:44.234  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : set-cookie: v2board_session=3fF8FwOCCSTNwDpJHfaVFLTZeVOjE8rjIdJU5iiP; expires=Sun, 14-Jan-2024 08:35:44 GMT; Max-Age=7200; path=/; httponly
        2024-01-14 14:35:44.234  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : cf-cache-status: DYNAMIC
        2024-01-14 14:35:44.234  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : report-to: {"endpoints":[{"url":"https:\/\/a.nel.cloudflare.com\/report\/v3?s=tOzSppOhzAKaJudMGgWGA119FDPeqRjzjTuFRu937nLf4lqaByZKlBY1Boub6n29%2B04OQT7mfH77F4GpJrIb7yRdyedRNCZUUQvcdldiqP83z5Li7WElXc7vtw%3D%3D"}],"group":"cf-nel","max_age":604800}
        2024-01-14 14:35:44.234  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : nel: {"success_fraction":0,"report_to":"cf-nel","max_age":604800}
        2024-01-14 14:35:44.234  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : cf-ray: 8453dc4e6cb804b7-HKG
        2024-01-14 14:35:44.234  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : alt-svc: h3=":443"; ma=86400
        2024-01-14 14:35:44.235  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : cache-control: no-cache
        2024-01-14 14:35:44.235  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   :
        2024-01-14 14:35:44.235  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : {"data":true}
        2024-01-14 14:35:44.235  INFO 44921 --- [           main] c.g.e.h.handler.EasyHttpRequestHandler   : <-- END HTTP (13-byte body)

```

## 🤝 贡献指南

我们欢迎任何形式的贡献。如果您有任何想法或改进，请通过 pull request 发送给我们。

## 📜 许可证

请在项目页面查看 `LICENSE` 文件以了解项目的许可证信息。

## 🙏 致谢

感谢 `OkHttp` 团队提供的出色HTTP客户端库，以及所有为 `EasyHttp` 贡献的开发者。
