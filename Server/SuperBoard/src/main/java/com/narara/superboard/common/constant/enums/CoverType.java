package com.narara.superboard.common.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoverType {
    IMAGE("image"),
    COLOR("color");

    private final String value;
}
