package com.example.bloomfilter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author liufuqiang
 */
@Configuration
public class RedisConfig {

    @Value("${redis.bloom.host}")
    private String host;
    @Value("${redis.bloom.port}")
    private Integer port;
    @Value("${redis.bloom.timeout}")
    private Integer timeout;
    @Value("${redis.bloom.pool.min-idle}")
    private Integer minIdle;
    @Value("${redis.bloom.pool.max-idle}")
    private Integer maxIdle;
    @Value("${redis.bloom.pool.max-active}")
    private Integer maxActive;
    @Value("${redis.bloom.pool.max-wait}")
    private Integer maxWaitMillis;

    @Bean(name="bloomJedisPoolConfig")
    public JedisPoolConfig bloomJedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMinIdle(minIdle);
        return jedisPoolConfig;
    }

    @Bean(name="stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(JedisPoolConfig bloomJedisPoolConfig){
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(bloomJedisPoolConfig);
        jedisConnectionFactory.setTimeout(timeout);
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.afterPropertiesSet();
        stringRedisTemplate.setConnectionFactory(jedisConnectionFactory);
        return stringRedisTemplate;
    }

    @Bean(name = "bloomRedisTemplate")
    public BloomRedisTemplate bloomRedisTemplate(JedisPoolConfig bloomJedisPoolConfig){
        BloomRedisTemplate bloomRedisTemplate = new BloomRedisTemplate();
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(bloomJedisPoolConfig);
        jedisConnectionFactory.setTimeout(timeout);
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.afterPropertiesSet();
        bloomRedisTemplate.setConnectionFactory(jedisConnectionFactory);
        return bloomRedisTemplate;
    }
}
