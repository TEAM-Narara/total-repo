package com.narara.superboard.worksapce.entity;

import com.narara.superboard.worksapce.interfaces.dto.WorkspaceRequestCreateDto;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceUpdateRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@Table(name = "workspace")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "name", nullable = false)
    private String name;


    public static WorkSpace createWorkSpace(WorkspaceRequestCreateDto workspaceRequestCreateDto) {
        return WorkSpace.builder()
                .name(workspaceRequestCreateDto.name())
//                .description(workspaceRequestCreateDto.description())
                .build();
    }

    public WorkSpace updateWorkSpace(WorkspaceUpdateRequestDto workspaceUpdateRequestDto) {
        this.name = workspaceUpdateRequestDto.name();
//        this.description = workspaceUpdateRequestDto.description();
        return this;
    }
}
