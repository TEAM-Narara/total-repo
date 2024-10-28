package com.narara.superboard.common.exception.redis;

public class RedisDataNotFoundException extends RuntimeException{
    public RedisDataNotFoundException(String key) {
        super("Redis에서 데이터를 찾을 수 없습니다. Key: " + key);
    }
}
