package com.narara.superboard.board.entity;

import com.narara.superboard.common.constant.enums.CoverType;

import java.util.Map;

public class Cover {

    private final Map<String, Object> coverData;


    public Cover(Map<String, Object> coverData) {
        this.coverData = coverData;
    }

    public CoverType getType() {
        if (coverData == null || !coverData.containsKey("type")) {
            return null;
        }
        String result = coverData.get("type").toString();
        return CoverType.valueOf(result.toUpperCase());
    }

    public String getTypeValue() {
        return getType().getValue();
    }

    public String getValue() {
        return coverData != null ? coverData.get("value").toString() : null;
    }
}