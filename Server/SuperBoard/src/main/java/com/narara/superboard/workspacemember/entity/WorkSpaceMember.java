package com.narara.superboard.workspacemember.entity;

import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "workspace_member")
public class WorkSpaceMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키

    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;  // 멤버 ID

    @JoinColumn(name = "workspace_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkSpace workSpace;  // 워크스페이스 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false, length = 50)
    private Authority authority;  // 권한 (ADMIN, MEMBER)

    @Builder.Default
    @Column(name = "\"offset\"")
    private Long offset = 0L;

    @Setter
    @Builder.Default
    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    public WorkSpaceMember(WorkSpace workSpace) {
        this.workSpace = workSpace;
    }

    public static WorkSpaceMember createWorkspaceMemberByAdmin(WorkSpace workSpace, Member member){
        return WorkSpaceMember.builder()
                .member(member)
                .workSpace(workSpace)
                .authority(Authority.ADMIN)
                .offset(1L)
                .build();
    }

    public WorkSpaceMember(Member member, Authority authority) {
        this.member = member;
        this.authority = authority;
    }

    public void editAuthority(Authority authority) {
        this.authority = authority;
    }

    public void deleted() {
        this.isDeleted = true;
    }
}
