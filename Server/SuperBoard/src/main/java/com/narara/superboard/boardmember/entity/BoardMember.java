package com.narara.superboard.boardmember.entity;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.member.entity.Member;
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
public class BoardMember extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키

    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;  // 멤버 ID

    @JoinColumn(name = "board_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;  // 워크스페이스 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false, length = 50)
    private Authority authority;  // 권한 (ADMIN, MEMBER)

    @Column(name = "is_alert", nullable = false, columnDefinition = "boolean default false")
    private boolean isAlert;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    public static BoardMember createBoardMemberByAdmin(Board board, Member member){
        return BoardMember.builder()
                .board(board)
                .member(member)
                .authority(Authority.ADMIN)
                .build();
    }

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

    public void editAuthority(Authority authority) {
        this.authority = authority;
    }

    public void deleted() {
        this.isDeleted = true;
    }
}
