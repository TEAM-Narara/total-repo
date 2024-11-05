package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.InvalidFormatException;

public class InvalidSearchTermFormatException extends InvalidFormatException {
    public InvalidSearchTermFormatException() {
        super("멤버 검색어");
    }
}
