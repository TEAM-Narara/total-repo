package com.narara.superboard.common.application.validator;

import com.narara.superboard.common.constant.enums.CoverType;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.common.exception.cover.InvalidCoverTypeFormatException;
import com.narara.superboard.common.exception.cover.NotFoundCoverTypeException;
import com.narara.superboard.common.exception.cover.NotFoundCoverValueException;
import java.util.Map;

import com.narara.superboard.common.interfaces.dto.CoverHolder;
import org.springframework.stereotype.Component;

@Component
public class CoverValidator {

    public void validateContainCover(CoverHolder cover) {
        validateCoversEmpty(cover.cover());
        validateCoverTypeIsEmpty(cover.cover());
        validateCoverValueIsEmpty(cover.cover());
    }

    public void validateCoverTypeIsValid(String coverTypeValue) {
        // 1. coverTypeValue가 null인 경우 예외 발생
        if (coverTypeValue == null || coverTypeValue.isEmpty()) {
            throw new NotFoundCoverTypeException();
        }

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
        if (!cover.containsKey("value")) {
            throw new NotFoundCoverValueException();
        }
    }

    // TODO: color는 #FFF 이렇게오고 image는 https로 시작하는지 확인하는 로직 추가

}
