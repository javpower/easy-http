package com.gc.easy.http.entity;

import com.gc.easy.http.handler.BeforeFactory;
import com.gc.easy.http.handler.FallbackFactory;
import org.springframework.stereotype.Service;

@Service
public class NullClass implements BeforeFactory, FallbackFactory {
    @Override
    public void doBefore(EasyHttpDto build) {

    }

    @Override
    public Object create(Throwable throwable) {
        return null;
    }
}
