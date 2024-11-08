package com.narara.superboard.workspace.exception;

import com.narara.superboard.common.exception.NotFoundException;

public class WorkspaceNameNotFoundException extends NotFoundException {
    public WorkspaceNameNotFoundException() {
        super("워크스페이스", "이름");
    }
}
