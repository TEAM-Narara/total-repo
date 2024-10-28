package com.narara.superboard.common.application.validator;

import com.narara.superboard.common.exception.color.NotFoundColorException;
import org.springframework.stereotype.Component;

@Component
public class ColorValidator {

    public void validateColorIsEmpty(Long color, String entity) {
        if (color == null) {
            throw new NotFoundColorException(entity);
        }
    }




}
