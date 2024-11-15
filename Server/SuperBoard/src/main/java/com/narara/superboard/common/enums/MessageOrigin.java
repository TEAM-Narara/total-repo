package com.narara.superboard.common.enums;

public enum MessageOrigin {
    RECEIVED, FETCHED;

    @Override
    public String toString() {
        return name().toUpperCase();
    }
}
