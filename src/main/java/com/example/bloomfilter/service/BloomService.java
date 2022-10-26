package com.example.bloomfilter.service;

import com.example.bloomfilter.config.BloomRedisTemplate;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * @author liufuqiang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BloomService {

    private final @Qualifier("bloomRedisTemplate") BloomRedisTemplate bloomRedisTemplate;

    public void bloomTest1() {
        long start = System.currentTimeMillis();
        String key = "newFilter";
        int errorCount = 0;
        for (int i = 0; i < 10000; i++) {
            boolean exists = bloomRedisTemplate.exists(key, "user" + i);
            if (!exists) {
                errorCount++;
                log.info("error hit {}", i);
            }
            bloomRedisTemplate.add(key, "user" + i);
        }
        log.info("error count {}", errorCount);
        long end = System.currentTimeMillis();
        log.info("cost time {}", end - start);
    }

    public void bloomTest2() {
        long start = System.currentTimeMillis();
        String key = "newFilter";
        bloomRedisTemplate.delete(key);
        bloomRedisTemplate.reserve(key, 0.01, 10000);
        int errorCount = 0;
        for (int i = 0; i < 10000; i++) {
//            boolean exists = bloomRedisTemplate.exists(key, "user" + i);
//            if (exists) {
//                errorCount++;
//                log.info("error hit {}", i);
//            }
            bloomRedisTemplate.add(key, "user" + i);
        }
        log.info("error count {}", errorCount);
        long end = System.currentTimeMillis();
        log.info("cost time {}", end - start);
    }

    public void guavaBFTest() {
        long start = System.currentTimeMillis();
        BloomFilter<String> filter = BloomFilter.create(
                Funnels.stringFunnel(Charset.defaultCharset()), 10000, 0.01);
        int errorCount = 0;
        for (int i = 0; i < 10000; i ++) {
            boolean exists = filter.mightContain("user" + i);
            if (exists) {
                errorCount++;
                log.info("error hit {}", i);
            }
            filter.put("user" + i);
        }
        log.info("error count {}", errorCount);
        long end = System.currentTimeMillis();
        log.info("cost time {}", end - start);
    }
}
