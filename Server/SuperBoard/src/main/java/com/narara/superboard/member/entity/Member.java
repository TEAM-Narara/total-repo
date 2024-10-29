package com.narara.superboard.member.entity;

import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.member.enums.LoginType;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    @Column(name = "password")
    private String password;

    // RefreshToken을 업데이트하는 메서드
    @Setter
    @Column(name = "refresh_token")
    private String refreshToken;

    // 기본값을 false로 설정
    @Setter
    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;  // Java에서 기본값 설정

    // 로그인 타입을 LOCAL로 설정
    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'LOCAL'")
    @Setter
    private LoginType loginType = LoginType.LOCAL;  // Java에서 기본값 설정

    @OneToMany(mappedBy = "member")
    private List<WorkSpaceMember> workspaceMemberList;

    @OneToMany(mappedBy = "member")
    private List<BoardMember> boardMemberList;

    public Member(Long id, String nickname, String email, String password,String profileImgUrl) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImgUrl = profileImgUrl;
    }

}
