package com.narara.superboard.board.service;

import static org.junit.jupiter.api.Assertions.*;

import com.narara.superboard.common.application.handler.CoverHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@DisplayName("VisibilityValidator 테스트")
class VisibilityValidatorTest {
    @InjectMocks
    private VisibilityValidator visibilityValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @DisplayName("유효하지 않은 Visibility 값 테스트")
    @ValueSource(strings = { "", "   ", "null" })
    void testInvalidVisibility(String stringVisibility) {
        assertThrows(IllegalArgumentException.class, () -> visibilityValidator.validateVisibilityIsPresent(stringVisibility));
    }

}
