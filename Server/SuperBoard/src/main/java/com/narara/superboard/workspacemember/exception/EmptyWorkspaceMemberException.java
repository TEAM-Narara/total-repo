package com.narara.superboard.workspacemember.exception;

public class EmptyWorkspaceMemberException extends IllegalStateException {
    public EmptyWorkspaceMemberException() {
        super("워크스페이스에는 적어도 한 명의 Admin 이 존재해야합니다");
    }

    public EmptyWorkspaceMemberException(String s) {
        super("워크스페이스에는 적어도 한 명의 Admin 이 존재해야합니다");
    }
}
