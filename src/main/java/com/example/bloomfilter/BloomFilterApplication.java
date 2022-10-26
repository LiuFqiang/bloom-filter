package com.example.bloomfilter;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BloomFilterApplication {

    public static void main (String[] args) {

        SpringApplication.run(BloomFilterApplication.class, args);
    }

}
