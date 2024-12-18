package com.narara.superboard.attachment.entity;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Card card; // 카드 키

    @Column(name = "url", nullable = false)
    private String url; // 첨부파일 URL

    // 현재는 IMAGE만 받아오지만 추후 확장성을 위해 설계
    @Column(name = "type", nullable = false)
    @Builder.Default
    private String type = "IMAGE"; // 파일 타입 (IMAGE, DOCS 등)

    // 이 부분은 중복 체크인것 같음. 그치만 인덱싱에 사용할 수도 있을 것 같다.
    @Column(name = "is_cover", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isCover = false; // 커버 이미지 여부

    @Column(name = "is_deleted",nullable = false,columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false; //삭제 여부

    public boolean changeIsCover() {
        this.isCover = !isCover;
        return this.isCover;
    }
}