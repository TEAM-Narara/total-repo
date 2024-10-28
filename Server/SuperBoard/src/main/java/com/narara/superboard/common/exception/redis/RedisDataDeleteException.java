package com.narara.superboard.common.exception.redis;

public class RedisDataDeleteException extends RuntimeException {
    public RedisDataDeleteException(String key) {
        super("Redis에서 데이터를 삭제하는 중 오류가 발생했습니다. Key: " + key);
    }
}
