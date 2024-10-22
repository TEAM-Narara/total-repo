package com.narara.superboard.common.exception;

public class WorkspaceNameNotFoundException extends NotFoundException {
    public WorkspaceNameNotFoundException() {
        super("워크스페이스", "이름");
    }
}
