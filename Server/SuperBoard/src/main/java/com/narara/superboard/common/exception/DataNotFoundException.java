package com.narara.superboard.common.exception;

public class DataNotFoundException extends IllegalStateException {
    public DataNotFoundException(String entity, String notFoundData) {
        super(entity + "에서 " + notFoundData + "값을 DB 내에서 찾을 수 없습니다.");
    }
}
