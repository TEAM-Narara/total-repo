package com.narara.superboard.common.exception;

public class InvalidFormatException extends IllegalArgumentException {
    public InvalidFormatException(String field) {
        super(String.format("%s(이)가 적합한 형식이 아닙니다. %s(을)를 다시 작성해주세요.", field, field));
    }

    public InvalidFormatException(String Object, String field) {
        super(String.format("%s의 %s(이)가 적합한 형식이 아닙니다. %s(을)를 다시 작성해주세요.", Object, field, field));
    }
}
