package com.narara.superboard.common.application.validator;

import com.narara.superboard.common.exception.NotFoundNameException;
import com.narara.superboard.common.interfaces.dto.NameHolder;
import org.springframework.stereotype.Component;

@Component
public class NameValidator {

    public void validateNameIsEmpty(NameHolder nameHolder) {
        if (nameHolder.name() == null || nameHolder.name().isEmpty()) {
            throw new NotFoundNameException("보드");
        }
    }
}
