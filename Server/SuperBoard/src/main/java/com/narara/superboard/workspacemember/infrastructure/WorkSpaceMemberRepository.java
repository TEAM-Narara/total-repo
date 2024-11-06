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
    @Query("SELECT wsm from WorkSpaceMember wsm left join wsm.workSpace ws left join wsm.member where ws.id = :workspaceId and wsm.isDeleted = false")
    List<WorkSpaceMember> findAllByWorkSpaceId(@Param("workspaceId") Long workspaceId);

    List<WorkSpaceMember> findAllByMemberId(Long memberId);

    boolean existsByMemberAndWorkSpaceAndAuthority(Member member, WorkSpace workSpace, Authority authority);


    List<WorkSpaceMember> findAllByMember(Member member);

    @Query("SELECT wsm FROM WorkSpaceMember wsm " +
            "JOIN FETCH wsm.workSpace ws " +
            "JOIN FETCH wsm.member m " +
            "WHERE ws.id = :workspaceId AND m.id = :memberId AND wsm.isDeleted = false")
    Optional<WorkSpaceMember> findFirstByWorkSpaceIdAndMemberId(@Param("workspaceId") Long workspaceId,
                                                                @Param("memberId") Long memberId);

    @Query("select wm.member from WorkSpaceMember wm" +
            " where wm.workSpace.id = :workspaceId and wm.isDeleted = false")
    List<Member> findAllMembersByWorkspaceId(@Param("workspaceId") Long workspaceId);
}
