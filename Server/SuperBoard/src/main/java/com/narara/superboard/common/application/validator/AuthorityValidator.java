package com.narara.superboard.common.application.validator;

import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.common.exception.authority.InvalidAuthorityFormatException;
import com.narara.superboard.common.exception.authority.NotFoundAuthorityException;

public class AuthorityValidator {

    public void validateAuthorityTypeIsValid(String authorityValue) {
        // 1. AuthorityValue가 null인 경우 예외 발생
        if (authorityValue == null || authorityValue.isEmpty()) {
            throw new NotFoundAuthorityException();
        }

        // 2. 주어진 값이 유효한 authority의 value와 일치하는지 확인
        boolean isValid = false;
        for (Authority authority : Authority.values()) {
            if (authority.toString().equals(authorityValue)) {
                isValid = true;
                break;
            }
        }

        // 3. 유효하지 않으면 예외 발생
        if (!isValid) {
            throw new InvalidAuthorityFormatException();
        }
    }
    
}
