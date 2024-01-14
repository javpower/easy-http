package com.gc.easy.http.handler;

import com.gc.easy.http.EasyHttpRequest;
import com.gc.easy.http.entity.EasyFileStream;
import com.gc.easy.http.entity.EasyHttpDto;
import com.gc.easy.http.entity.NullClass;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.CollectionUtil;
import net.dreamlu.mica.core.utils.JsonUtil;
import net.dreamlu.mica.http.HttpRequest;
import net.dreamlu.mica.http.LogLevel;
import net.dreamlu.mica.http.Method;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class EasyHttpRequestHandler {

  public Object doHttp(List<String> parameters, Object[] args, HttpMethod httpMethod, String requestUrl, Class<?> type, BeforeFactory beforeFactory, EasyHttpRequest request, String[] proxy, FallbackFactory fallbackFactory){
      EasyHttpDto build = EasyHttpDto.builder().header(new HashMap<>()).body(new HashMap<>()).form(new HashMap<>()).param(new HashMap<>()).query(new HashMap<>()).build();
      if(proxy!=null&&proxy.length==2){
          build.setProxy(proxy[0],  Integer.parseInt(proxy[1]));
      }
      if (args!=null && args.length>0) {
          //解析参数
          Class<?> headerClass = request.header();
          Class<?> bodyClass = request.body();
          Class<?> formClass = request.form();
          Class<?> paramClass = request.param();
          Class<?> queryClass = request.query();
          String[] headerName = request.headerName();
          String[] formName = request.formName();
          String[] paramName = request.paramName();
          String[] queryName = request.queryName();
          String[] bodyName = request.bodyName();
          IntStream.range(0, args.length)
                  .forEach(i -> {
                      Object arg = args[i];
                      String parameter = parameters.get(i);
                      if(arg instanceof File || arg instanceof MultipartFile || arg instanceof EasyFileStream){
                          build.addForm(parameter,arg);
                          return;
                      }
                      //转换参数
                      Map<String, Object> map;
                      if(arg.getClass().isPrimitive()||arg instanceof String){
                          //基本数据类型
                          map=CollectionUtil.toMap(parameter,arg);
                      }else {
                          String v = JsonUtil.toJson(arg);
                          map = JsonUtil.readMap(v);
                      }
                      //填充参数
                      if(headerName.length>0&& Arrays.asList(headerName).contains(parameter)){
                          build.addAllHeader(map);
                      }else if(formName.length>0&& Arrays.asList(formName).contains(parameter)){
                          build.addAllForm(map);
                      }else if(paramName.length>0&& Arrays.asList(paramName).contains(parameter)){
                          build.addAllParam(map);
                      }else if(queryName.length>0&& Arrays.asList(queryName).contains(parameter)){
                          build.addAllQuery(map);
                      }else if(bodyName.length>0&& Arrays.asList(bodyName).contains(parameter)){
                          build.addAllBody(map);
                      }else if(headerClass != NullClass.class && arg.getClass().getName().equals(headerClass.getName())){
                          build.addAllHeader(map);
                      }else if (bodyClass !=NullClass.class && arg.getClass().getName().equals(bodyClass.getName())){
                          build.addAllBody(map);
                      }else if (formClass !=NullClass.class && arg.getClass().getName().equals(formClass.getName()) ){
                          build.addAllForm(map);
                      }else if (paramClass !=NullClass.class && arg.getClass().getName().equals(paramClass.getName())){
                          build.addAllParam(map);
                      }else if (queryClass !=NullClass.class && arg.getClass().getName().equals(queryClass.getName())){
                          build.addAllQuery(map);
                      }
                  });
      }
      //前置处理
      if(beforeFactory !=null){
          beforeFactory.doBefore(build);
      }
      //构建请求
      HttpRequest httpRequest;
      switch (httpMethod.name()){
          case Method.GET:
              httpRequest = HttpRequest.get(requestUrl);
              break;
          case Method.POST:
              httpRequest = HttpRequest.post(requestUrl);
              break;
          case Method.PUT:
              httpRequest = HttpRequest.put(requestUrl);
              break;
          case Method.DELETE:
              httpRequest = HttpRequest.delete(requestUrl);
              break;
          case Method.PATCH:
              httpRequest = HttpRequest.patch(requestUrl);
              break;
          default:
              return null;
      }
      if(build.existParam()){
          HashMap<String, Object> param = build.getParam();
          Set<Map.Entry<String, Object>> entries = param.entrySet();
          if (entries.iterator().hasNext()) {
              Map.Entry<String, Object> p = entries.iterator().next();
              httpRequest=httpRequest.pathParam(p.getKey(),p.getValue());
          }
      }
      if(build.existHeader()){
          HashMap<String, Object> header = build.getHeader();
          Set<Map.Entry<String, Object>> entries = header.entrySet();
          if (entries.iterator().hasNext()) {
              Map.Entry<String, Object> p = entries.iterator().next();
              httpRequest=httpRequest.addHeader(p.getKey(),String.valueOf(p.getValue()));
          }
      }
      if(build.existQuery()){
          HashMap<String, Object> query = build.getQuery();
          httpRequest=httpRequest.queryMap(query);
      }
      if(build.existForm()){
          HashMap<String, Object> param = build.getForm();
          Set<Map.Entry<String, Object>> entries = param.entrySet();
          if (entries.iterator().hasNext()) {
              Map.Entry<String, Object> p = entries.iterator().next();
              if(p.getValue() instanceof MultipartFile){
                  try {
                      httpRequest= httpRequest.multipartFormBuilder().add(p.getKey(),((MultipartFile) p.getValue()).getName(),((MultipartFile) p.getValue()).getBytes()).build();
                  } catch (IOException e) {
                      throw new RuntimeException(e);
                  }
              }else if(p.getValue() instanceof File){
                  httpRequest= httpRequest.multipartFormBuilder().add(p.getKey(),(File) p.getValue()).build();
              }else if(p.getValue() instanceof EasyFileStream){
                  httpRequest= httpRequest.multipartFormBuilder().add(p.getKey(),((EasyFileStream) p.getValue()).getFileName(),((EasyFileStream) p.getValue()).getInputStreamBytes()).build();
              }else {
                  httpRequest= httpRequest.formBuilder().addEncoded(p.getKey(),p.getValue()).build();
              }
          }
      }
      if(build.existFullBody()){
          String fullBodyEncrypt = build.getFullBodyEncrypt();
          httpRequest=httpRequest.bodyString(fullBodyEncrypt);

      }else if(build.existBody()){
          HashMap<String, Object> body = build.getBody();
          httpRequest=httpRequest.bodyJson(body);
      }
      httpRequest=httpRequest.disableSslValidation();
      if(build.existProxy()){
          httpRequest=httpRequest.proxy(build.getProxy());
      }
      if(build.existCookie()){
          httpRequest= httpRequest.addCookie(build.getCookie());
      }
      httpRequest = httpRequest.useLog(log::info, LogLevel.BODY);
      //后置异常处理
      try {
          return httpRequest.execute().asValue(type);
      }catch (Throwable throwable){
          if(fallbackFactory!=null){
              return fallbackFactory.create(throwable);
          }
          throw throwable;
      }
  }
}
