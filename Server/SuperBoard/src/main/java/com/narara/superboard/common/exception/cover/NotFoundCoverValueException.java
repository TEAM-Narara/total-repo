package com.narara.superboard.common.exception.cover;

import com.narara.superboard.common.exception.NotFoundException;

public class NotFoundCoverValueException extends NotFoundException {
    public NotFoundCoverValueException() {
        super("커버의 value");
    }
}
