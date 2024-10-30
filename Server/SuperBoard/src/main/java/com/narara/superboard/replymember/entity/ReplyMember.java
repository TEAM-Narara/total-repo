package com.narara.superboard.replymember.entity;

import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reply_member")
public class ReplyMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키

    @JoinColumn(name = "member", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;  // 멤버 ID

    @JoinColumn(name = "reply", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Reply reply;  // 워크스페이스 ID

    public static ReplyMember createReplyMember(Reply reply, Member member){
        return ReplyMember.builder()
                .reply(reply)
                .member(member)
                .build();
    }

    public ReplyMember(Reply reply) {
        this.reply = reply;
    }

    public ReplyMember(Member member) {
        this.member = member;
    }

}
