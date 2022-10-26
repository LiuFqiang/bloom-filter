package com.example.bloomfilter;

import com.example.bloomfilter.service.BloomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BloomFilterApplicationTests {

    @Autowired
    private BloomService bloomService;

    @Test
    void contextLoads () {
        bloomService.bloomTest2();
    }

    @Test
    void guavaBFTest() {
        bloomService.guavaBFTest();
    }

}
