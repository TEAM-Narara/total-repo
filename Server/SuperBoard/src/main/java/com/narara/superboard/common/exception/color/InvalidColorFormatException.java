package com.narara.superboard.common.exception.color;

import com.narara.superboard.common.exception.InvalidFormatException;

public class InvalidColorFormatException extends InvalidFormatException {


    public InvalidColorFormatException(String entity) {
        super(entity, "색상");
    }

    public InvalidColorFormatException() {
        super("색상");
    }
}