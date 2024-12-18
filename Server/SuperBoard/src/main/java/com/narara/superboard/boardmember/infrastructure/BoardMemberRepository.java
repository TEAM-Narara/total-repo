package com.narara.superboard.boardmember.infrastructure;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.boardmember.entity.BoardMember;
import java.util.List;
import java.util.Optional;

import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.entity.Member;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardMemberRepository extends JpaRepository<BoardMember,Long> {
    boolean existsByMemberAndBoardAndAuthority(Member member, Board board, Authority authority);

    List<BoardMember> findAllByBoardId(Long boardId);

    // 필요한 경우 Member 목록만 조회
    @Query("select bm.member from BoardMember bm " +
            "where bm.board.id = :boardId and bm.isDeleted = false")
    List<Member> findAllMembersByBoardId(@Param("boardId") Long boardId);

    @Query("select bm.member from BoardMember bm " +
            "where bm.board.id = :boardId and bm.isDeleted = false and bm.isAlert = true")
    Set<Member> findAllMemberByBoardAndWatchTrue(@Param("boardId") Long boardId);

    Optional<BoardMember> findByBoardIdAndMemberAndIsDeletedIsFalse(Long boardId, Member member);
    Optional<BoardMember> findFirstByBoardAndMemberAndIsDeletedIsFalse(Board board, Member member);
    Optional<BoardMember> findFirstByBoard_IdAndMember_Id(Long boardId, Long memberId);

    @Query("select bm from BoardMember bm join bm.board b where bm.member.id = :memberId and b.isDeleted = false order by b.id ")
    List<BoardMember> findByMemberId(@Param("memberId") Long memberId);
}
