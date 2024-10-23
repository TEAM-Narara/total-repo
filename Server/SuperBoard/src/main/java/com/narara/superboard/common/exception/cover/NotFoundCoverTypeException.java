package com.narara.superboard.common.exception.cover;

import com.narara.superboard.common.exception.NotFoundException;

public class NotFoundCoverTypeException extends NotFoundException {
    public NotFoundCoverTypeException() {
        super("커버의 타입");
    }
}
