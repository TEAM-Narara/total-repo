package com.narara.superboard.memberbackground.entity;

import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_background")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberBackground extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Column(nullable = false,name = "img_url")
    private String imgUrl;
}