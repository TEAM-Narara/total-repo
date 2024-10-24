package com.narara.superboard.common.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoverType {
    IMAGE("IMAGE"),
    COLOR("COLOR");

    private final String value;
}
