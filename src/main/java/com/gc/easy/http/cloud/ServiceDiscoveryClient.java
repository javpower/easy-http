package com.gc.easy.http.cloud;

import java.util.List;

/**
 * 获取服务地址
 */
public interface ServiceDiscoveryClient {
    List<String> getInstances(String serviceName);
}

//Eureka
/**
 *import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.cloud.client.discovery.DiscoveryClient;
 * import org.springframework.stereotype.Component;
 *
 * import java.util.List;
 *
 * @Component
 * public class ServiceDiscoveryComponent implements ServiceDiscoveryClient{
 *
 *     private final DiscoveryClient discoveryClient;
 *
 *     @Autowired
 *     public ServiceDiscoveryComponent(DiscoveryClient discoveryClient) {
 *         this.discoveryClient = discoveryClient;
 *     }
 *
 *     public List<String> getInstances(String serviceName) {
 *         List<String> serviceUrls = discoveryClient.getServices()
 *                 .stream()
 *                 .flatMap(serviceName -> discoveryClient.getInstances(serviceName).stream())
 *                 .map(instance -> instance.getUri().toString())
 *                 .collect(Collectors.toList());
 *
 *         return serviceUrls;
 *     }
 * }
 */
// Consul
/**
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
 * import org.springframework.cloud.consul.discovery.ConsulDiscoveryService;
 * import org.springframework.stereotype.Component;
 *
 * import java.util.List;
 * import java.util.stream.Collectors;
 *
 * @Component
 * public class ServiceDiscoveryComponent implements ServiceDiscoveryClient{
 *
 *     private final ConsulDiscoveryProperties consulProperties;
 *     private final ConsulDiscoveryService consulDiscoveryService;
 *
 *     @Autowired
 *     public ServiceDiscoveryComponent(ConsulDiscoveryProperties consulProperties, ConsulDiscoveryService consulDiscoveryService) {
 *         this.consulProperties = consulProperties;
 *         this.consulDiscoveryService = consulDiscoveryService;
 *     }
 *
 *     public List<String> getInstances(String serviceName) {
 *         List<String> serviceUrls = consulDiscoveryService.getServices().values()
 *                 .stream()
 *                 .flatMap(serviceInstances -> serviceInstances.stream())
 *                 .map(serviceInstance -> serviceInstance.getServiceId())
 *                 .map(serviceId -> consulProperties.getTags().get(serviceId))
 *                 .collect(Collectors.toList());
 *
 *         return serviceUrls;
 *     }
 * }
 */

//Nacos
/**
 *import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.stereotype.Component;
 * import com.alibaba.cloud.nacos.NacosServiceManager;
 *
 * import java.util.List;
 *
 * @Component
 * public class ServiceDiscoveryComponent implements ServiceDiscoveryClient {
 *
 *     private final NacosServiceManager nacosServiceManager;
 *
 *     @Autowired
 *     public ServiceDiscoveryComponent(NacosServiceManager nacosServiceManager) {
 *         this.nacosServiceManager = nacosServiceManager;
 *     }
 *
 *     public List<String> getInstances(String serviceName) {
 *         List<String> serviceUrls = nacosServiceManager.getAllInstances()
 *                 .stream()
 *                 .map(instance -> instance.toInetAddr().toString())
 *                 .collect(Collectors.toList());
 *
 *         return serviceUrls;
 *     }
 * }
 */