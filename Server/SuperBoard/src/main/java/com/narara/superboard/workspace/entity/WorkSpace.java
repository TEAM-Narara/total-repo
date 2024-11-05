package com.narara.superboard.workspace.entity;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceUpdateRequestDto;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@Table(name = "workspace")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkSpace extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "workSpace")
    private List<WorkSpaceMember> workspaceMemberList;

    @OneToMany(mappedBy = "workSpace")
    private List<Board> boardCollection;

    @Builder.Default
    @Column(name = "\"offset\"")
    private Long offset = 0L;

    @Builder.Default
    private Boolean isDeleted = false;

    public static WorkSpace createWorkSpace(WorkSpaceCreateRequestDto workspaceCreateRequestDto) {
        return WorkSpace.builder()
                .name(workspaceCreateRequestDto.name())
//                .description(workspaceRequestCreateDto.description())
                .build();
    }

    public WorkSpace updateWorkSpace(String workspaceName) {
        this.name = workspaceName;
        this.offset++;
        return this;
    }

    // test
    @Builder
    public WorkSpace(Long id, String name, Long offset) {
        this.id = id;
        this.name = name;
        this.workspaceMemberList = null;
        this.offset = offset;
    }

    public void deleted() {
        this.isDeleted = true;
        this.offset++;
    }

    public void addOffset() {
        this.offset++;
    }
}
