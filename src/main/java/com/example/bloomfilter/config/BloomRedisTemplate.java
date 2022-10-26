package com.example.bloomfilter.config;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * redis操作布隆过滤器基本命令
 * @author liufuqiang
 */
public class BloomRedisTemplate extends StringRedisTemplate {

    private static final Logger logger = LoggerFactory.getLogger(BloomRedisTemplate.class);

    private static final RedisScript<String> BF_RESERVE_SCRIPT =
            new DefaultRedisScript<>("return redis.call('bf.reserve', KEYS[1], ARGV[1], ARGV[2])", String.class);

    private static final RedisScript<Boolean> BF_ADD_SCRIPT =
            new DefaultRedisScript<>("return redis.call('bf.add', KEYS[1], ARGV[1])", Boolean.class);

    private static final RedisScript<Boolean> BF_EXISTS_SCRIPT =
            new DefaultRedisScript<>("return redis.call('bf.exists', KEYS[1], ARGV[1])", Boolean.class);

    private static final RedisScript<List> BF_MULTI_ADD_SCRIPT =
            new DefaultRedisScript<>("return redis.call('bf.madd', KEYS[1], ARGV[1])", List.class);

    private static final RedisScript<List> BF_MULTI_EXISTS_SCRIPT =
            new DefaultRedisScript<>("return redis.call('bf.mexists', KEYS[1], ARGV[1])", List.class);

    public <T> T execute (RedisScript<T> script, String key, Object... args) {
        return super.execute(script, Arrays.asList(key), args);
    }

    /**
     * 创建布隆过滤器
     * @param key       key值
     * @param errorRate 错误率
     * @param capacity  预计放入元素数量
     * <a href="https://redis.io/commands/bf.reserve/">BF.RESERVE</a>
     * @return
     */
    public boolean reserve(String key, double errorRate, int capacity) {
        checkArgument( errorRate > 0, "errorRate (%s) must be > 0.0", errorRate);
        checkArgument(errorRate < 1.0, "errorRate (%s) must be < 1.0", errorRate);
        checkArgument(capacity > 0, "capacity (%s) must be >= 0", capacity);

        try {
            execute(BF_RESERVE_SCRIPT, key, String.valueOf(errorRate), String.valueOf(capacity));
            return true;
        } catch (Exception e) {
            logger.error("bf reserve error {}", e.getMessage());
            return false;
        }
    }

    /**
     * 添加元素
     * @param key   key值
     * @param value 需要添加的值
     * @return
     */
    public boolean add(String key, String value) {
        return execute(BF_ADD_SCRIPT, key, value);
    }

    /**
     * 查看元素是否存在
     * @param key   key值
     * @param value 需要判断的值
     * @return
     */
    public boolean exists(String key, String value) {
        return execute(BF_EXISTS_SCRIPT, key, value);
    }

    /**
     * 批量添加元素
     * @param key    key值
     * @param values 需要添加的值
     * @return
     */
    public List<Integer> multiAdd(String key, Collection<String> values) {
        return execute(BF_MULTI_ADD_SCRIPT, key, values.stream().collect(Collectors.joining(" ")));
    }

    /**
     * 批量判断是否存在
     * @param key    key值
     * @param values 需要判断的值
     * @return
     */
    public List<Integer> multiExists(String key, Collection<String> values) {
        return execute(BF_MULTI_EXISTS_SCRIPT, key, values.stream().collect(Collectors.joining(" ")));
    }

    /**
     * 参数校验
     * @param b
     * @param errorMessageTemplate
     * @param p1
     */
    private static void checkArgument(boolean b, @Nullable String errorMessageTemplate, @Nullable Object p1) {
        if (!b) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, p1));
        }
    }

}
