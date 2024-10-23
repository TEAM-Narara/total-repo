package com.narara.superboard.common.application.handler;

import com.narara.superboard.common.application.validator.CoverValidator;
import com.narara.superboard.common.constant.enums.CoverType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoverHandler {

    private final CoverValidator coverValidator;

    public CoverType getType(Map<String, Object> cover) {
        coverValidator.validateCoversEmpty(cover);
        coverValidator.validateCoverTypeIsEmpty(cover);

        String result = cover.get("type").toString();

        coverValidator.validateCoverTypeIsValid(result);

        return CoverType.valueOf(result.toUpperCase());
    }

    public String getTypeValue(Map<String, Object> cover) {
        return getType(cover).getValue();
    }

}
