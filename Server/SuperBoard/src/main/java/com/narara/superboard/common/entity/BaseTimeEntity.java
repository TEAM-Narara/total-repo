package com.narara.superboard.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.ZoneOffset;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter(AccessLevel.PUBLIC)
@MappedSuperclass // BaseEntity 를 상속한 엔티티들은 아래 필드들을 컬럼으로 인식하게 된다.
@EntityListeners(AuditingEntityListener.class)
// Spring Data JPA가 Auditing(자동으로 값 매핑) 기능 추가
// 도메인을 영속성 컨텍스트에 저장하거나 조회를 수행한 후에 update를 하는 경우,
public class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)
    private Long createdAt;

    @LastModifiedDate
    private Long updatedAt;

    // 엔티티가 저장되기 전 호출
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9));
        this.updatedAt = this.createdAt; // 생성 시점과 동일하게 초기화
    }

    // 엔티티가 업데이트되기 전 호출
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9));

    }
}
