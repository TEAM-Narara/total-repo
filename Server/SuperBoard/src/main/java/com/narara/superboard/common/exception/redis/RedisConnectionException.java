package com.narara.superboard.common.exception.redis;

import io.lettuce.core.RedisException;

public class RedisConnectionException extends RedisException {
    public RedisConnectionException(String message) {
        super(message);
    }

    public RedisConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
