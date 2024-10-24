package com.narara.superboard.boardmember.entity;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.entity.Member;
import jakarta.persistence.Column;
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
@Table(name = "board_member")
public class BoardMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키

    @JoinColumn(name = "member", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;  // 멤버 ID

    @JoinColumn(name = "board", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;  // 워크스페이스 ID

    @Column(name = "authority", nullable = false, length = 50)
    private Authority authority;  // 권한 (ADMIN, MEMBER)

    @Column(name = "is_alert", nullable = false, columnDefinition = "boolean default false")
    private boolean isAlert;

    public BoardMember(Board board) {
        this.board = board;
    }

    public BoardMember(Member member, Authority authority) {
        this.member = member;
        this.authority = authority;
    }

    public void changeIsAlert() {
        this.isAlert = !isAlert;
    }
}
