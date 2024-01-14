package com.gc.easy.http.decoder;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;

public class DistanceDecoder implements Decoder{

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     *数据解码
     * @param
     * @param type
     * @return
     * @throws Exception
     */
    @Override
    public Object decode(String value, Type type) throws Exception {
        JavaType javaType = this.getJavaType(type, null);
        return this.objectMapper.readValue(value, javaType);
    }

    protected JavaType getJavaType(Type type, Class<?> contextClass) {
        return contextClass != null ? this.objectMapper.getTypeFactory().constructType(type, contextClass) : this.objectMapper.constructType(type);
    }
}
