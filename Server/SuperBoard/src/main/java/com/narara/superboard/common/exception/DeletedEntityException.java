package com.narara.superboard.common.exception;

public class DeletedEntityException extends IllegalArgumentException {
    public DeletedEntityException() {
        super("삭제된 객체입니다.");
    }

    public DeletedEntityException(String entity) {
        super("삭제된 " + entity + "을 찾을 수 없습니다.");
    }

    public DeletedEntityException(Long id, String entity) {
        super(String.format("삭제된 %s(이)가 존재하지 않습니다. %sID: %d", entity, entity, id));
    }
}
