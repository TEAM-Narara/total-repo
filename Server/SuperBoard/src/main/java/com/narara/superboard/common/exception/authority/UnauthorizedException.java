package com.narara.superboard.common.exception.authority;

import com.narara.superboard.websocket.constant.Action;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // HTTP 403 상태 코드 반환
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("해당 행위에 대한 권한이 없습니다.");
    }

    public UnauthorizedException(Action action) {
        super(String.format("%s에 대한 권한이 없습니다.", action.getValue()));
    }

    public UnauthorizedException(String nickname, Action action) {
        super(String.format("멤버 %s(은)는 %s에 대한 권한이 없습니다.", nickname, action.getValue()));
    }
}
