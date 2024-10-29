package com.narara.superboard.common.exception.redis;

import com.narara.superboard.common.exception.NotFoundException;

public class RedisKeyNotFoundException extends NotFoundException {
    public RedisKeyNotFoundException() {
        super("레디스", "key");
    }
}
