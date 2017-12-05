package com.gen;


import com.gen.framework.common.services.CacheService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheServiceTest {

    @Autowired
    private CacheService cacheService;

    @Test
    public void toTest()throws Exception{
        String tel="13416606582";
        this.cacheService.setValidCode(tel,"124567");
        Thread.sleep(11000);
        System.out.println(this.cacheService.getValidCode(tel));
        this.cacheService.deleteValidCode(tel);
        System.out.println(this.cacheService.getValidCode(tel));
    }
}
