package com.narara.superboard.common.exception.kafka;

public class DuplicateListenerRegistrationException extends RuntimeException {
    public DuplicateListenerRegistrationException(String message) {
        super(message);
    }
}