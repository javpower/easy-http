package com.gc.easy;

import com.gc.easy.http.handler.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestFallBack implements FallbackFactory {


    @Override
    public Object create(Throwable throwable) {
        return null;
    }
}
