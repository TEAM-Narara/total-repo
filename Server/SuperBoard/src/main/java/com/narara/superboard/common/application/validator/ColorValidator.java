package com.narara.superboard.common.application.validator;

import com.narara.superboard.common.exception.color.InvalidColorFormatException;
import com.narara.superboard.common.interfaces.dto.ColorHolder;
import org.springframework.stereotype.Component;

@Component
public class ColorValidator {

    public void validateLabelColor(ColorHolder colorHolder) {
        Long color = colorHolder.color();

        //변경하지 말아달라는 뜻
        if (color == null) {
            return;
        }

        validateColorIsValid(color, "라벨");
    }

    public void validateColorIsValid(Long color, String entity) {
        // color 값이 0x00000000 ~ 0xFFFFFFFF 범위에 있는지 확인
        if (color < 0x00000000L || color > 0xFFFFFFFFL) {
            throw new InvalidColorFormatException(entity);
        }
    }
}
