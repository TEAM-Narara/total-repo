package com.narara.superboard.common.application.validator;

import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.common.constant.enums.CoverType;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.common.exception.cover.InvalidCoverTypeFormatException;
import com.narara.superboard.common.exception.cover.NotFoundCoverTypeException;
import com.narara.superboard.common.exception.cover.NotFoundCoverValueException;
import java.util.HashMap;
import java.util.Map;

import com.narara.superboard.common.interfaces.dto.CoverHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoverValidator {

    public void validateContainCover(CoverHolder cover) {
//        validateCoversEmpty(cover.cover());
//        validateCoverTypeIsEmpty(cover.cover());
//        validateCoverValueIsEmpty(cover.cover());
        // TODO: validateCoverTypeIsValid(cover.cover().get("type").toString()); 추가하기.
    }

    public void validateCardCover(CardUpdateRequestDto cardUpdateRequestDto) {
        Map<String, Object> cover = new HashMap<>(){{
            put("type", cardUpdateRequestDto.cover().type());
            put("value", cardUpdateRequestDto.cover().value());
        }};

        validateCoverTypeIsEmpty(cover);
        validateCoverValueIsEmpty(cover);
        validateCoverTypeIsValid(cover);
    }


    // CoverHandler.getType()
    public void validateCoverTypeIsValid(Map<String, Object> cover) {
        // 1. coverTypeValue가 null인 경우 예외 발생
        if (cover == null || cover.isEmpty()) {
            throw new NotFoundCoverTypeException();
        }
        String coverTypeValue = (String)cover.get("type");

        // 2. 주어진 값이 유효한 CoverType의 value와 일치하는지 확인
        boolean isValid = false;
        for (CoverType type : CoverType.values()) {
            if (type.toString().equals(coverTypeValue)) {
                isValid = true;
                break;
            }
        }

        // 3. 유효하지 않으면 예외 발생
        if (!isValid) {
            throw new InvalidCoverTypeFormatException();
        }
    }

    public void validateCoversEmpty(Map<String, Object> cover) {
        if (cover == null || cover.isEmpty()) {
            throw new NotFoundException("커버");
        }
    }

    public void validateCoverTypeIsEmpty(Map<String, Object> cover) {
        if (!cover.containsKey("type")) {
            throw new NotFoundCoverTypeException();
        }
    }

    public void validateCoverValueIsEmpty(Map<String, Object> cover) {
        if (!cover.containsKey("value") || cover.get("value") == null) {
            throw new NotFoundCoverValueException();
        }
    }

    // TODO: color는 #FFF 이렇게오고 image는 https로 시작하는지 확인하는 로직 추가

}
