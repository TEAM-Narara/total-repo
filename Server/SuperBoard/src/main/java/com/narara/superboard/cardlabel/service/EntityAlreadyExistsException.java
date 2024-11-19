package com.narara.superboard.cardlabel.service;

public class EntityAlreadyExistsException extends IllegalArgumentException {
    public EntityAlreadyExistsException(String entity) {
        super(entity + "에 데이터가 이미 존재합니다.");
    }
}
