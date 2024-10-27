package com.narara.superboard.common.application.validator;

import static org.junit.jupiter.api.Assertions.*;

import com.narara.superboard.common.exception.NotFoundContentException;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("내용 검증에 대한 단위 테스트")
class ContentValidatorTest {

    private ContentValidator contentValidator;

    @BeforeEach
    void setUp() {
        contentValidator = new ContentValidator();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("내용이 null이거나 비어있으면 ContentNotFoundException 발생")
    void shouldThrowExceptionWhenContentIsEmptyOrBlank(String invalidContent) {
        // given
        ReplyCreateRequestDto requestDto = new ReplyCreateRequestDto(1L, invalidContent);

        // then
        assertThrows(NotFoundContentException.class, () -> contentValidator.validateReplyContentIsEmpty(requestDto));
    }

}
