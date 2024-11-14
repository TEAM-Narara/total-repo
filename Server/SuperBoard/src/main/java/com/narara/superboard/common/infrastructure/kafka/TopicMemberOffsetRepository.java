package com.narara.superboard.common.infrastructure.kafka;

import com.narara.superboard.common.entity.TopicMemberOffset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicMemberOffsetRepository extends JpaRepository<TopicMemberOffset,Long> {
    Optional<TopicMemberOffset> findByTopicAndMemberId(String topic,Long memberId);
}
