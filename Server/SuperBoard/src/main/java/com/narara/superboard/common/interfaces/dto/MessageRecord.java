package com.narara.superboard.common.interfaces.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public record MessageRecord(Long offset, JsonNode message) {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * JSON 문자열을 받아 JsonNode로 변환하여 MessageRecord를 생성하는 팩토리 메서드
     *
     * @param offset      메시지의 오프셋
     * @param messageJson JSON 문자열 형태의 메시지
     * @return MessageRecord 객체 또는 변환 실패 시 null
     */
    public static MessageRecord of(Long offset, String messageJson) {
        try {
            // JSON 문자열을 JsonNode 객체로 변환
            JsonNode messageNode = objectMapper.readTree(messageJson);
            return new MessageRecord(offset, messageNode);
        } catch (Exception e) {
            return null;
        }
    }
}
