package com.gc.easy.http.cloud;

import java.util.List;

/**
 * 获取服务地址
 */
public interface ServiceDiscoveryClient {
    List<String> getInstances(String serviceName);
}