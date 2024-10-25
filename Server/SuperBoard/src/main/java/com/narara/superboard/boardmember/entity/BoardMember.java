package com.narara.superboard.boardmember.entity;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.entity.WorkSpace;
import jakarta.persistence.*;
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
    private Board board;  // 보드 ID

    @Column(name = "authority", nullable = false, length = 50)
    private String authority;  // 권한 (ADMIN, MEMBER)

    @Column(name = "is_alert", nullable = false, columnDefinition = "boolean default false")
    private boolean isAlert;
}
