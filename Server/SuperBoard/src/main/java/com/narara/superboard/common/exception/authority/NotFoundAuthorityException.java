package com.narara.superboard.common.exception.authority;

import com.narara.superboard.common.exception.NotFoundException;

public class NotFoundAuthorityException extends NotFoundException {
    public NotFoundAuthorityException() {
        super("권한");
    }
}
