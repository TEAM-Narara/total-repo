package com.narara.superboard.workspacemember.infrastructure;

import com.narara.superboard.workspacemember.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkSpaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
}
