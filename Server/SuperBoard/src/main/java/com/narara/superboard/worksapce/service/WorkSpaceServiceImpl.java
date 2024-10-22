package com.narara.superboard.worksapce.service;

import com.narara.superboard.worksapce.entity.WorkSpace;
import com.narara.superboard.worksapce.infrastructure.WorkSpaceRepository;
import com.narara.superboard.worksapce.interfaces.dto.CreateWorkspaceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkSpaceServiceImpl implements WorkSpaceService {

    private final WorkSpaceRepository workSpaceRepository;
    private final WorkSpaceValidator workSpaceValidator;

    @Override
    public void createWorkSpace(CreateWorkspaceDto createWorkspaceDto) {
        workSpaceValidator.validateCreateDto(createWorkspaceDto);

        WorkSpace workSpace = WorkSpace.createWorkSpace(createWorkspaceDto);
        workSpaceRepository.save(workSpace);
    }
}
