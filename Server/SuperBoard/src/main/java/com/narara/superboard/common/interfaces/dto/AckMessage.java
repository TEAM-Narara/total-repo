package com.narara.superboard.common.interfaces.dto;

public record AckMessage(String topic, int partition, String offset, String groupId) {
}