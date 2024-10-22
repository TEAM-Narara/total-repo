package com.narara.superboard.worksapce.service;

import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.WorkspaceNameNotFoundException;
import com.narara.superboard.worksapce.entity.WorkSpace;
import com.narara.superboard.worksapce.infrastructure.WorkSpaceRepository;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceRequestCreateDto;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceUpdateRequestDto;
import com.narara.superboard.worksapce.service.validator.WorkSpaceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkSpaceServiceImpl implements WorkSpaceService {

    private final WorkSpaceRepository workSpaceRepository;
    private final WorkSpaceValidator workSpaceValidator;

    @Override
    public void createWorkSpace(WorkspaceRequestCreateDto workspaceRequestCreateDto) throws WorkspaceNameNotFoundException {
        workSpaceValidator.validateNameIsPresent(workspaceRequestCreateDto);

        WorkSpace workSpace = WorkSpace.createWorkSpace(workspaceRequestCreateDto);
        workSpaceRepository.save(workSpace);
    }

    @Override
    public WorkSpace updateWorkSpace(Long workSpaceId, WorkspaceUpdateRequestDto workspaceUpdateRequestDto) throws WorkspaceNameNotFoundException{
        workSpaceValidator.validateNameIsPresent(workspaceUpdateRequestDto);

        WorkSpace workSpace = getWorkSpace(workSpaceId);

        return workSpace.updateWorkSpace(workspaceUpdateRequestDto);
    }

    @Override
    public void deleteWorkSpace(Long workSpaceId) {
        WorkSpace workSpace = getWorkSpace(workSpaceId);
        workSpaceRepository.delete(workSpace);
    }

    @Override
    public WorkSpace getWorkSpace(Long workSpaceId) {
        return workSpaceRepository.findById(workSpaceId)
                .orElseThrow(() -> new NotFoundEntityException(workSpaceId, "WorkSpace"));
    }
}
