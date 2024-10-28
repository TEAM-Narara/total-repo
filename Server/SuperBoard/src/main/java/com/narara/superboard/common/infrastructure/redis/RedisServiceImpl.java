package com.narara.superboard.common.infrastructure.redis;

import com.narara.superboard.common.exception.redis.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(key);

        // 데이터가 없으면 예외 발생
        if (value == null) {
            throw new RedisDataNotFoundException(key);
        }

        return value;
    }

    @Override
    public void setData(String key, String value) {
        if (key == null || key.isEmpty()) {
            throw new RedisKeyNotFoundException();
        }

        if (value == null) {
            throw new RedisValueNotFoundException();
        }

        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, value);
        } catch (Exception e) {
            throw new RedisDataSaveException(key, value);
        }
    }

    @Override
    public boolean existData(String key) {
        if (key == null || key.isEmpty()) {
            throw new RedisKeyNotFoundException();
        }

        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            throw new RedisDataNotFoundException(key);
        }
    }

    @Override
    public void setDataExpire(String key, String value, long duration) {
        if (key == null || key.isEmpty()) {
            throw new RedisKeyNotFoundException();
        }

        if (value == null) {
            throw new RedisValueNotFoundException();
        }

        if (duration <= 0) {
            throw new RedisInvalidDurationFormatException();
        }

        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, value, duration, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RedisDataSaveException(key, value, duration);
        }
    }

    @Override
    public void deleteData(String key) {
        if (key == null || key.isEmpty()) {
            throw new RedisKeyNotFoundException();
        }

        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new RedisDataDeleteException(key);
        }
    }
}
