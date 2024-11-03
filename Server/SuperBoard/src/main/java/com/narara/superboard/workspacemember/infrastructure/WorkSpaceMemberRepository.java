package com.narara.superboard.workspacemember.infrastructure;

import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkSpaceMemberRepository extends JpaRepository<WorkSpaceMember, Long> {
    List<WorkSpaceMember> findAllByWorkSpaceId(Long workSpaceId);
    List<WorkSpaceMember> findAllByMemberId(Long memberId);
    boolean existsByMemberAndWorkSpaceAndAuthority(Member member, WorkSpace workSpace, Authority authority);

    List<WorkSpaceMember> findAllByMember(Member member);
}
