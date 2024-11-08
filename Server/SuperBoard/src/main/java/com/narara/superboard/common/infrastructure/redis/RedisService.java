package com.narara.superboard.common.infrastructure.redis;

import java.util.Set;

public interface RedisService {
    String getData(String key);
    void setData(String key,String value);
    boolean existData(String key);
    void setDataExpire(String key, String value, long duration);
    void deleteData(String key);

    // Redis에 ACK를 추가
    void addAckToQueue(String topic, String groupId, long offset, String ackMessage);
    // 특정 오프셋에 해당하는 ACK 조회 (예: 순서 맞는 ACK 확인용)
    Object getAckFromQueue(String topic, String groupId, long offset);
    // 연속된 ACK를 순차적으로 조회 (순서가 맞을 때까지 ACK 처리용)
    Set<String> getPendingAcks(String topic, String groupId, long startOffset, long endOffset);
    // 특정 오프셋에 해당하는 ACK를 Redis에서 삭제
    void removeAckFromQueue(String topic, String groupId, long offset);
    // 대기 큐에서 첫 오프셋 ACK를 삭제
    void removeFirstAck(String topic, String groupId);
}
