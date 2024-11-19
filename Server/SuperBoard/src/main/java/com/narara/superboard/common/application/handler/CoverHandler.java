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
        coverValidator.validateCoverTypeIsValid(cover);

        return CoverType.fromString(cover.get("type").toString().toUpperCase());
    }

    public String getTypeValue(Map<String, Object> cover) {
        return getType(cover).toString();
    }

    public String getValue(Map<String, Object> cover) {
        coverValidator.validateCoversEmpty(cover);
        coverValidator.validateCoverValueIsEmpty(cover);

        return cover.get("value").toString();
    }
}
