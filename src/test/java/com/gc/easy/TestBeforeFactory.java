package com.gc.easy;

import com.gc.easy.http.entity.EasyHttpDto;
import com.gc.easy.http.handler.BeforeFactory;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.JsonUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestBeforeFactory implements BeforeFactory {

    @Override
    public void doBefore(EasyHttpDto build) {
        build.addHeader("token","xxxxxxxx");
        log.info("Header: "+JsonUtil.toJson(build.getHeader()));
        build.setFullBodyEncrypt("ss"+JsonUtil.toJson(build.getBody())+"ss");
        //设置代理,可以动态
        //build.setProxy("127.0.0.1",8080);
    }
}
