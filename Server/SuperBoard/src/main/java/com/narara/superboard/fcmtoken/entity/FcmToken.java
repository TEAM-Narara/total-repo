package com.narara.superboard.fcmtoken.entity;

import com.narara.superboard.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fcm_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "registration_token", nullable = false)
    private String registrationToken;

}