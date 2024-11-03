package com.narara.superboard.workspacemember.infrastructure;

import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkSpaceMemberRepository extends JpaRepository<WorkSpaceMember, Long> {
    List<WorkSpaceMember> findAllByWorkSpaceId(Long workSpaceId);

    List<WorkSpaceMember> findAllByMemberId(Long memberId);

    boolean existsByMemberAndWorkSpaceAndAuthority(Member member, WorkSpace workSpace, Authority authority);

    @Query("SELECT wsm FROM WorkSpaceMember wsm " +
            "JOIN FETCH wsm.workSpace ws " +
            "JOIN FETCH wsm.member m " +
            "WHERE ws.id = :workspaceId AND m.id = :memberId AND wsm.isDeleted = false")
    Optional<WorkSpaceMember> findFirstByWorkSpaceIdAndMemberId(@Param("workspaceId") Long workspaceId,
                                                                @Param("memberId") Long memberId);
}
