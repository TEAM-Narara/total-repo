package com.narara.superboard.common.exception.color;

import com.narara.superboard.common.exception.NotFoundException;

public class NotFoundColorException extends NotFoundException {

    public NotFoundColorException(String entity) {
        super(entity, "색상");
    }
}
