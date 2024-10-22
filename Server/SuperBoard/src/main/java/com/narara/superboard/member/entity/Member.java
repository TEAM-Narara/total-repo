package com.narara.superboard.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

/**
 * @SuperBuilder
 * : @Builder의 상속받은 필드의 빌더를 사용하지 못하는 제한 해결,
 * 상속받은 필드도 빌더에서 사용 가능
 */
@SuperBuilder
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @Column(nullable = false, length = 50)
    private String nickname;
    @Column(nullable = false, length = 50,unique = true)
    private String email;
    @Column(nullable = false, length = 64)
    private String password;
    @Column(name = "profile_img",nullable = false)
    private String profileImg;
    @Column(name="refresh_token", length = 512)
    private String refreshToken;
}
