package com.narara.superboard.member.entity;

import com.narara.superboard.alert.Alert;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.fcmtoken.entity.FcmToken;
import com.narara.superboard.member.enums.LoginType;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.memberbackground.MemberBackground;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.*;

import java.util.List;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "email", nullable = false,unique = true)
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
    private Boolean isDeleted;  // Java에서 기본값 설정

    // 로그인 타입을 LOCAL로 설정
    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'LOCAL'")
    @Setter
    private LoginType loginType;  // Java에서 기본값 설정

    @OneToMany(mappedBy = "member")
    private List<FcmToken> fcmTokenList;

    @OneToMany(mappedBy = "member")
    private List<Alert> alretList;

    @OneToMany(mappedBy = "member")
    private List<MemberBackground> mamberBackgroundList;

    @OneToMany(mappedBy = "member")
    private List<WorkSpaceMember> workspaceMemberList;

    @OneToMany(mappedBy = "member")
    private List<BoardMember> boardMemberList;

    @OneToMany(mappedBy = "member")
    private List<CardMember> cardMemberList;

    @OneToMany(mappedBy = "member")
    private List<Reply> replyList;

    public Member(Long id, String nickname, String email,String profileImgUrl) {

        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
    }


    public Member(Long id, String nickname, String email, String password,String profileImgUrl) {

        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImgUrl = profileImgUrl;
    }

    public Member(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }
}
