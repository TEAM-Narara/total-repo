package com.narara.superboard.common.exception.redis;

import com.narara.superboard.common.exception.InvalidFormatException;

public class RedisInvalidDurationFormatException extends InvalidFormatException {
    public RedisInvalidDurationFormatException() {
        super("레디스", "만료 시간");
    }
}
