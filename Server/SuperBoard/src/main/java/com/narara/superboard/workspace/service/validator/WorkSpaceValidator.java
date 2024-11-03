package com.narara.superboard.workspace.service.validator;

import com.narara.superboard.workspace.exception.WorkspaceNameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class WorkSpaceValidator {

    public void validateNameIsPresent(String workspaceName) {
        // 이름이 null 또는 공백이거나 빈 문자열일 때 예외 처리
        if (workspaceName == null || workspaceName.trim().isEmpty()) {
            throw new WorkspaceNameNotFoundException();
        }
    }
}
