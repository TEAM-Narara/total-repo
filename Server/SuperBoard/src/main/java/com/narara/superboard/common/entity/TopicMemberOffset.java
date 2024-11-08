package com.narara.superboard.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "topic_member_offset", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"topic", "member_id"})
})
public class TopicMemberOffset extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "topic",nullable = false)
    private String topic;

    @Column(name = "member_id",nullable = false)
    private Long memberId;

    @Column(name = "last_offset",nullable = false,columnDefinition = "BIGINT DEFAULT -1")
    @Builder.Default
    private Long lastOffset = -1L;

    public TopicMemberOffset(String topic, Long memberId) {
        this.topic = topic;
        this.memberId = memberId;
    }

    public void updateLastOffset(Long newOffset) {
        this.lastOffset = newOffset;
    }
}
