package com.narara.superboard.common.exception;

public class NotFoundNameException extends NotFoundException {

    public NotFoundNameException(){
        super("이름");
    }
    public NotFoundNameException(String entity) {
        super(entity, "이름");
    }
}
