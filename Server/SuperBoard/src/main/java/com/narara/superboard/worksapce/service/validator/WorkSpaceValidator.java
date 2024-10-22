package com.narara.superboard.worksapce.service.validator;

import com.narara.superboard.common.exception.WorkspaceNameNotFoundException;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceCreateDto;
import org.springframework.stereotype.Component;

@Component
public class WorkSpaceValidator {

    public void validateCreateDto(WorkspaceCreateDto workspaceCreateDto) {
        // 이름이 null 또는 공백이거나 빈 문자열일 때 예외 처리
        if (workspaceCreateDto.name() == null || workspaceCreateDto.name().trim().isEmpty()) {
            throw new WorkspaceNameNotFoundException();
        }
    }
}
