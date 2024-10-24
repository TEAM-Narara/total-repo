package com.narara.superboard.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class NotFoundEntityException extends IllegalArgumentException {

    private final Long id;  // 예외가 발생한 엔티티의 ID
    private final String entity;  // 예외가 발생한 엔티티 이름

    // 생성자에서 ID와 엔티티 이름을 전달받아 저장
    public NotFoundEntityException(Long id, String entity) {
        super(String.format("해당하는 %s(이)가 존재하지 않습니다. %sID: %d", entity, entity, id));
        this.id = id;
        this.entity = entity;
        log.error("{}(을)를 찾을 수 없습니다. ID: {}", entity, id);
    }

    // 생성자에서 ID와 엔티티 이름을 전달받아 저장
    public NotFoundEntityException(String entity1, Long id1, String entity2, Long id2, String entity) {
        super(String.format("해당하는 %s(이)가 존재하지 않습니다. %sID: %d %sID: %d", entity, entity1, id1, entity2 ,id2));
        this.id = id1;
        this.entity = entity;
        log.error("{}(을)를 찾을 수 없습니다. ID: {} ID: {}", entity, id1, id2);
    }

}
