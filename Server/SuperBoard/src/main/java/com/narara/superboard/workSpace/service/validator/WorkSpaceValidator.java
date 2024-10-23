package com.narara.superboard.workSpace.service.validator;

import com.narara.superboard.common.exception.WorkspaceNameNotFoundException;
import com.narara.superboard.workSpace.interfaces.dto.WorkSpaceNameHolder;
import org.springframework.stereotype.Component;

@Component
public class WorkSpaceValidator {

    public void validateNameIsPresent(WorkSpaceNameHolder workspaceNameHolder) {
        // 이름이 null 또는 공백이거나 빈 문자열일 때 예외 처리
        if (workspaceNameHolder.name() == null || workspaceNameHolder.name().trim().isEmpty()) {
            throw new WorkspaceNameNotFoundException();
        }
    }
}
