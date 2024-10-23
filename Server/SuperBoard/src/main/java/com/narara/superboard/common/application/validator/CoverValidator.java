package com.narara.superboard.common.application.validator;

import com.narara.superboard.common.constant.enums.CoverType;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.common.exception.cover.InvalidCoverTypeFormatException;
import com.narara.superboard.common.exception.cover.NotFoundCoverTypeException;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CoverValidator {

    public void validateCoverTypeIsValid(String coverTypeValue) {
        // 1. coverTypeValue가 null인 경우 예외 발생
        if (coverTypeValue == null || coverTypeValue.isEmpty()) {
            throw new NotFoundCoverTypeException();
        }

        // 2. 주어진 값이 유효한 CoverType의 value와 일치하는지 확인
        boolean isValid = false;
        for (CoverType type : CoverType.values()) {
            if (type.getValue().equals(coverTypeValue)) {
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
}
