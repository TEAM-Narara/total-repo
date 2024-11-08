package com.narara.superboard.common.enums;

public enum KafkaRegisterType {
    WORKSPACE, BOARD;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
