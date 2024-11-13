package com.narara.superboard.common.infrastructure.redis;

import com.narara.superboard.common.exception.redis.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    // private final RedisTemplate<String, Object> redisAckTemplate;

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
        log.info("existData 메서드 시작 - 조회할 키: {}", key);

        if (key == null || key.isEmpty()) {
            log.error("키가 null이거나 비어 있습니다. RedisKeyNotFoundException 예외 발생");
            throw new RedisKeyNotFoundException();
        }

        try {
            log.info("Redis에서 키 존재 여부 확인 중...");
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis에서 키 조회 중 예외 발생 - 키: {}", key, e);
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

    @Override
    public void addAckToQueue(String topic, String groupId, long offset, String ackMessage) {
        String key = "ackQueue:" + topic + ":" + groupId;
        redisTemplate.opsForZSet().add(key, ackMessage, offset);
    }

    @Override
    public Object getAckFromQueue(String topic, String groupId, long offset) {
        String key = "ackQueue:" + topic + ":" + groupId;
        Set<String> resultSet = redisTemplate.opsForZSet().rangeByScore(key, offset, offset);
        return resultSet != null && !resultSet.isEmpty() ? resultSet.iterator().next() : null;
    }

    @Override
    public Set<String> getPendingAcks(String topic, String groupId, long startOffset, long endOffset) {
        String key = "ackQueue:" + topic + ":" + groupId;
        return redisTemplate.opsForZSet().rangeByScore(key, startOffset, endOffset);
    }

    @Override
    public void removeAckFromQueue(String topic, String groupId, long offset) {
        String key = "ackQueue:" + topic + ":" + groupId;
        redisTemplate.opsForZSet().removeRangeByScore(key, offset, offset);
    }

    @Override
    public void removeFirstAck(String topic, String groupId) {
        String key = "ackQueue:" + topic + ":" + groupId;
        redisTemplate.opsForZSet().removeRange(key, 0, 0);
    }

    @Override
    public Long getLastAcknowledgedOffset(String topic, String groupId) {
        String key = "lastOffset:" + topic + ":" + groupId;
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            // 값이 없으면 Redis에 -1 저장하고 반환
            setLastAcknowledgedOffset(topic, groupId, -1L);
            return -1L;
        }

        return Long.parseLong(value);
    }

    @Override
    public void setLastAcknowledgedOffset(String topic, String groupId, Long offset) {
        String key = "lastOffset:" + topic + ":" + groupId;
        redisTemplate.opsForValue().set(key, String.valueOf(offset));
    }
}
