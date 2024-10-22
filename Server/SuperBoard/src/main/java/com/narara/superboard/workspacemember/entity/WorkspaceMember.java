package com.narara.superboard.workspacemember.entity;

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
@Table(name = "workspace_member")
public class WorkspaceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키

    @Column(name = "member_id", nullable = false)
    private Long memberId;  // 멤버 ID

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;  // 워크스페이스 ID

    @Column(name = "authority", nullable = false, length = 50)
    private String authority;  // 권한 (ADMIN, MEMBER)

}
