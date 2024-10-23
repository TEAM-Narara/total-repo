package com.narara.superboard.board.entity;

import com.narara.superboard.common.constant.enums.CoverType;
import lombok.Getter;
import org.hibernate.annotations.SecondaryRow;

import java.util.Map;

@Getter
public class Background {

    private final Map<String, Object> backgroundData;
    public CoverType type;
    public String value;

    public Background(Map<String, Object> backgroundData) {
        this.backgroundData = backgroundData;
        this.type = getBackgroundType();
        this.value = getBackgroundValue();
    }

    private CoverType getBackgroundType() {
        if (backgroundData == null || !backgroundData.containsKey("type")) {
            return null;
        }
        String result = backgroundData.get("type").toString();
        return CoverType.valueOf(result.toUpperCase());
    }

    private String getBackgroundValue() {
        return backgroundData != null ? backgroundData.get("value").toString() : null;
    }
}