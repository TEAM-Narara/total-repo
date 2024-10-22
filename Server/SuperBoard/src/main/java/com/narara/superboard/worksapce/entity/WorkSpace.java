package com.narara.superboard.worksapce.entity;

import com.narara.superboard.worksapce.interfaces.dto.CreateWorkspaceDto;
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

    public static WorkSpace createWorkSpace(CreateWorkspaceDto createWorkspaceDto) {
        return WorkSpace.builder()
                .name(createWorkspaceDto.name())
                .description(createWorkspaceDto.description())
                .build();
    }
}
