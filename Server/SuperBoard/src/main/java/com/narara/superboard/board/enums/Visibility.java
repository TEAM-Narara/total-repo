package com.narara.superboard.board.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Visibility {
    PRIVATE("private"),
    PUBLIC("workspace");

    private final String value;
}
