package com.narara.superboard.common.exception.redis;

import com.narara.superboard.common.exception.NotFoundException;
import io.lettuce.core.RedisException;

public class RedisDataNotFoundException extends RedisException {
    public RedisDataNotFoundException(String key) {
        super("Redis에서 데이터를 찾을 수 없습니다. Key: " + key);
    }
}
