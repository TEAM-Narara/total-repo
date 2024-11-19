package com.narara.superboard.common.interfaces.dto;

import java.util.Objects;

public record OffsetKey(String topic, int partition, long offset, String groupId) {
    public OffsetKey(String topic, int partition, long offset, String groupId) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.groupId = groupId;
    }

    public String topic() { return topic; }
    public int partition() { return partition; }
    public long offset() { return offset; }
    public String groupId() { return groupId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OffsetKey that)) return false;
        return partition == that.partition && offset == that.offset && topic.equals(that.topic) && groupId.equals(that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, partition, offset, groupId);
    }
}