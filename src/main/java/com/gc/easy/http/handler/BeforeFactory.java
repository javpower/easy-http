package com.gc.easy.http.handler;

import com.gc.easy.http.entity.EasyHttpDto;

public interface BeforeFactory {
    void doBefore(EasyHttpDto build);
}
