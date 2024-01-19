<h1 align="center">EasyHttp - 声明式HTTP客户端框架</h1>

项目介绍：
-------------------------------------


很多⼤名鼎鼎的http开源框架可以实现任何形式的http调⽤，⽐如 apache 的 httpClient 包，⾮常优秀的 Okhttp ， jersey client 。
这些 http 开源框架的接⼝使⽤相对来说，都不太⼀样。不管选哪个，在我这个场景⾥来说，我都不希望在调⽤每个第三⽅的http api时写上⼀堆http调⽤代码。<br>
相比于这些您不再用写一大堆重复的代码了，EasyHttp是一个高层的、极简的声明式HTTP调用API框架，像调用本地方法一样去发送HTTP请求<br>

EasyHttp有哪些特性？
-----
* 以OkHttp为后端框架，全注解式请求
* 通过调用本地方法的方式去发送Http请求, 实现了业务逻辑与Http协议之间的解耦
* 可扩展微服务之间的调用
* 支持所有请求方法：GET, HEAD, OPTIONS, TRACE, POST, DELETE, PUT, PATCH
* 支持前置Before处理：参数修改加密、代理、Cookie、Token等
* 支持后置Fallback异常自定义处理
* 支持Spring和Springboot集成
* JSON格式数据序列化和反序列化
* @EnableEasyHttpRequest、@EasyHttp、@EasyHttpRequest三个注解就能完成所有请求的定义

极速开始
-------------------------------------
以下例子基于Spring Boot

### 第一步：添加Maven依赖

直接添加以下maven依赖即可

```xml
<dependency>
    <groupId>io.github.javpower</groupId>
    <artifactId>easy-http-spring-boot-starter</artifactId>
    <version>2.7.18.2</version>
</dependency>
```

### 第二步：创建一个`interface`

```java


import com.gc.easy.http.EasyHttp;
import com.gc.easy.http.EasyHttpRequest;
import com.gc.easy.http.EasyHttpVo;
import com.gc.easy.http.Param;
import org.springframework.http.HttpMethod;


@EasyHttp(baseUrl = "https://www.xxx.cc")
public interface TestPlatformApiService {
    @EasyHttpRequest(method = HttpMethod.POST,
            value = "/api/test",
            body = TestPersonAdd.class)
    EasyHttpVo<?> addPerson(TestPersonAdd add);
    
    @EasyHttpRequest(method = HttpMethod.POST,
            value = "/api/v1/passport/comm/sendEmailVerify",
            formName = {"email"}
    )
    TestVo sendEmailVerify(@Param("email") String email);
}


```

### 第三步：扫描接口

在Spring Boot的配置类或者启动类上加上`@EnableEasyHttpRequest`注解，并在`value`或`basePackages`属性里填上远程接口的所在的包名

```java
@SpringBootApplication
@EnableScheduling
@EnableEasyHttpRequest("com.gc.subscribeboot.test")
public class SubscribebootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubscribebootApplication.class, args);
    }
}
```

### 第四步：调用接口

OK，我们可以愉快地调用接口了

```java
// 注入接口实例
@Autowired
private TestPlatformApiService testPlatformApiService;
...
// 调用接口
        TestVo baidu = testPlatformApiService.sendEmailVerify("1www7ggg@nqmo.com");
        System.out.println(baidu.toString());
```

## 注解描述

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

## 微服务扩展
   需实现 ServiceDiscoveryClient <br>
   下面给出Consul、Eureka、Nacos三种例子 <br>
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
## 请求日志

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