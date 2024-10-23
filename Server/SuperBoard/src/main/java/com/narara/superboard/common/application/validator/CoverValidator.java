package com.narara.superboard.common.application.validator;

import com.narara.superboard.common.constant.enums.CoverType;
import com.narara.superboard.common.exception.InvalidCoverTypeFormatException;

import java.util.EnumSet;


public class CoverValidator {

    public void validateCoverTypeIsValid(CoverType coverType) {
        // CoverType이 미리 정의된 유효한 값에 포함되는지 확인
        boolean isValid = EnumSet.allOf(CoverType.class).contains(coverType);

        // 유효하지 않으면 예외 발생
        if (!isValid) {
            throw new InvalidCoverTypeFormatException();
        }
    }

}
