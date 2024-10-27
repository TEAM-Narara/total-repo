package com.narara.superboard.common.application.validator;

import com.narara.superboard.common.exception.NotFoundContentException;
import com.narara.superboard.common.interfaces.dto.ContentHolder;
import org.springframework.stereotype.Component;

@Component
public class ContentValidator {
    public void validateReplyContentIsEmpty(ContentHolder contentHolder) {
        if (contentHolder.content() == null || contentHolder.content().isEmpty()) {
            throw new NotFoundContentException("댓글");
        }
    }
}
