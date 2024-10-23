package com.narara.superboard.common.exception;

public class NotFoundException extends IllegalArgumentException {
    public NotFoundException(String Object, String nonExistentValue) {
        super(String.format("%s의 %s(이)가 존재하지 않습니다. %s(을)를 작성해주세요.", Object, nonExistentValue, nonExistentValue));
    }
}
