package com.narara.superboard.workspacemember.entity;

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
@Table(name = "workspace_member")
public class WorkSpaceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키

    @JoinColumn(name = "member", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;  // 멤버 ID

    @JoinColumn(name = "workspace", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkSpace workSpace;  // 워크스페이스 ID

    @Column(name = "authority", nullable = false, length = 50)
    private String authority;  // 권한 (ADMIN, MEMBER)

}
