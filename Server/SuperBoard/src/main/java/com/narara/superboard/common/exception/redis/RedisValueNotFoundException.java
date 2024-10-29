package com.narara.superboard.common.exception.redis;

import com.narara.superboard.common.exception.NotFoundException;

public class RedisValueNotFoundException extends NotFoundException {
    public RedisValueNotFoundException() {
        super("레디스", "value");
    }
}
