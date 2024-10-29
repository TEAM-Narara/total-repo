package com.narara.superboard.member.entity;

import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.replymember.entity.ReplyMember;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
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

    @OneToMany(mappedBy = "member")
    private List<WorkSpaceMember> workspaceMemberList;

    @OneToMany(mappedBy = "member")
    private List<BoardMember> boardMemberList;

    @OneToMany(mappedBy = "member")
    private List<CardMember> cardMemberList;

    @OneToMany(mappedBy = "member")
    private List<ReplyMember> replyMemberList;


    public Member(Long id, String nickname, String email, String profileImgUrl) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
    }

    public Member(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }
}
