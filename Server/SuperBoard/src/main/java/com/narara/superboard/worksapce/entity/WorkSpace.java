package com.narara.superboard.worksapce.entity;

import com.narara.superboard.worksapce.interfaces.dto.WorkspaceCreateDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@Table(name = "workspace")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class WorkSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    public static WorkSpace createWorkSpace(WorkspaceCreateDto workspaceCreateDto) {
        return WorkSpace.builder()
                .name(workspaceCreateDto.name())
                .description(workspaceCreateDto.description())
                .build();
    }
}
