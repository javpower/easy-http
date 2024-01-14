package com.gc.easy.http.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.Cookie;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EasyHttpDto implements Serializable {

    private HashMap<String, Object> header;
    private HashMap<String, Object> param;
    private HashMap<String, Object> query;
    private HashMap<String, Object> body;
    private HashMap<String, Object> form;
    private InetSocketAddress proxy=null;

    private String fullBodyEncrypt=null;

    private Cookie cookie=null;

    public EasyHttpDto addHeader(String key,Object value){
        this.header.put(key,value);
        return this;
    }
    public EasyHttpDto addAllHeader(Map<String, Object> header){
        this.header.putAll(header);
        return this;
    }
    public EasyHttpDto addParam(String key,Object value){
        this.param.put(key,value);
        return this;
    }
    public EasyHttpDto addAllParam(Map<String, Object> param){
        this.param.putAll(param);
        return this;
    }
    public EasyHttpDto addQuery(String key,Object value){
        this.query.put(key,value);
        return this;
    }
    public EasyHttpDto addAllQuery(Map<String, Object> query){
        this.query.putAll(query);
        return this;
    }
    public EasyHttpDto addBody(String key,Object value){
        this.body.put(key,value);
        return this;
    }
    public EasyHttpDto addAllBody(Map<String, Object> body){
        this.body.putAll(body);
        return this;
    }

    public EasyHttpDto addForm(String key,Object value){
        this.form.put(key,value);
        return this;
    }
    public EasyHttpDto addAllForm(Map<String, Object> form){
        this.form.putAll(form);
        return this;
    }
    public EasyHttpDto setProxy(String host, int port){
        this.proxy=InetSocketAddress.createUnresolved(host, port);
        return this;
    }
    public EasyHttpDto setFullBodyEncrypt(String body){
        this.fullBodyEncrypt=body;
        return this;
    }
    public EasyHttpDto setCookie(Cookie cookie){
        this.cookie=cookie;
        return this;
    }
    public Boolean existHeader(){
        return this.header.size() > 0;
    }
    public Boolean existParam(){
        return this.param.size() > 0;
    }
    public Boolean existQuery(){
        return this.query.size() > 0;
    }
    public Boolean existBody(){
        return this.body.size() > 0;
    }
    public Boolean existForm(){
        return this.form.size() > 0;
    }
    public Boolean existProxy(){
        return this.proxy!=null;
    }
    public Boolean existCookie(){
        return this.cookie!=null;
    }
    public Boolean existFullBody(){
        return this.fullBodyEncrypt!=null&&this.fullBodyEncrypt.length()>0;
    }

}