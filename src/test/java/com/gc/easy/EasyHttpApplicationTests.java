package com.gc.easy;

import com.gc.easy.http.EnableEasyHttpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableEasyHttpRequest("com.gc.easy")
class EasyHttpApplicationTests {
    @Autowired
    private TestPlatformApiService testPlatformApiService;

    @Test
    void contextLoads() {
        testPlatformApiService.addPerson(new TestPersonAdd());
    }


    @Test
    void baidu() {
        TestVo baidu = testPlatformApiService.baidu("1www7ggg@nqmo.com");
        System.out.println(baidu.toString());
    }
}
