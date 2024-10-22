package com.narara.superboard.worksapce.service;

import com.narara.superboard.worksapce.entity.WorkSpace;
import com.narara.superboard.worksapce.infrastructure.WorkSpaceRepository;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceCreateDto;
import com.narara.superboard.worksapce.service.validator.WorkSpaceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkSpaceServiceImpl implements WorkSpaceService {

    private final WorkSpaceRepository workSpaceRepository;
    private final WorkSpaceValidator workSpaceValidator;

    @Override
    public void createWorkSpace(WorkspaceCreateDto workspaceCreateDto) {
        workSpaceValidator.validateCreateDto(workspaceCreateDto);

        WorkSpace workSpace = WorkSpace.createWorkSpace(workspaceCreateDto);
        workSpaceRepository.save(workSpace);
    }
}
