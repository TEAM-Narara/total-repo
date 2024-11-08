package com.narara.superboard.common.exception;

public class NotFoundContentException extends NotFoundException {
    public NotFoundContentException(){
        super("내용");
    }
    public NotFoundContentException(String entity) {
        super(entity, "내용");
    }
}
