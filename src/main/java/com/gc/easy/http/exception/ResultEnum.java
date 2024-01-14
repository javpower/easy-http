package com.gc.easy.http.exception;

/**
 * @description:
 * @author: gc.x
 * @create: 2019-03-16 16:21:34
 **/
public interface ResultEnum<T>{

	T getCode();  
	
    String getDesc();  
}
