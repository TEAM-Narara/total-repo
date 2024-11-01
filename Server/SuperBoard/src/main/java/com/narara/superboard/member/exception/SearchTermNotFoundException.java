package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.NotFoundException;

public class SearchTermNotFoundException extends NotFoundException {

    public SearchTermNotFoundException() {
        super("멤버 검색어");
    }
}
