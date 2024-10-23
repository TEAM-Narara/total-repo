package com.narara.superboard.common.exception.cover;

import com.narara.superboard.common.exception.InvalidFormatException;

public class InvalidCoverTypeFormatException extends InvalidFormatException {

    public InvalidCoverTypeFormatException() {
        super("커버의 타입");
    }
}
