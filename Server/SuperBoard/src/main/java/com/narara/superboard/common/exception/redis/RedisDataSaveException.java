package com.narara.superboard.common.exception.redis;

public class RedisDataSaveException extends RuntimeException {

    // 단순 key, value 저장 실패의 경우
    public RedisDataSaveException(String key, String value) {
        super(String.format("Redis에 데이터를 저장하는 중 오류가 발생했습니다. Key: %s, Value: %s", key, value));
    }

    // 만료 시간을 포함한 저장 실패의 경우
    public RedisDataSaveException(String key, String value, long timeoutInSeconds) {
        super(String.format("Redis에 데이터를 저장하는 중 오류가 발생했습니다. Key: %s, Value: %s, 만료 시간: %d초", key, value, timeoutInSeconds));
    }

    // 모든 인자가 없는 기본 예외 메시지
    public RedisDataSaveException() {
        super("Redis에 데이터를 저장하는 중 알 수 없는 오류가 발생했습니다.");
    }
}
