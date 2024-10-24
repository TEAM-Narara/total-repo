package com.narara.superboard.common.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Authority {
    ADMIN("ADMIN"),
    USER("USER");

    private final String value;
}
