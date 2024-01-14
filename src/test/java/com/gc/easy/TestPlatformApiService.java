package com.gc.easy;

import com.gc.easy.http.EasyHttp;
import com.gc.easy.http.EasyHttpRequest;
import com.gc.easy.http.EasyHttpVo;
import com.gc.easy.http.Param;
import org.springframework.http.HttpMethod;


@EasyHttp
public interface TestPlatformApiService {
     @EasyHttpRequest(method = HttpMethod.POST,
             value = "/api/test",
             body = TestPersonAdd.class)
     EasyHttpVo<?> addPerson(TestPersonAdd add);

     @EasyHttpRequest(method = HttpMethod.POST,
             value = "https://www.xfeie.cc/api/v1/passport/comm/sendEmailVerify",
             formName = {"email"}
     )
     TestVo baidu(@Param("email") String email);

}
