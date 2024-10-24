package com.narara.superboard.common.application.validator;

import com.narara.superboard.common.exception.DataNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class LastOrderValidator {
    public void checkValidListLastOrder(Long lastOrder) {
        if (lastOrder == null) {
            throw new DataNotFoundException("보드", "리스트의 마지막 순서");
        }
    }
}
