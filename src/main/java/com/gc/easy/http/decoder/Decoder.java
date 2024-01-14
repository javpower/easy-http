package com.gc.easy.http.decoder;

import java.lang.reflect.Type;

public interface Decoder {
    Object decode(String hikVo, Type type) throws Exception;
}
