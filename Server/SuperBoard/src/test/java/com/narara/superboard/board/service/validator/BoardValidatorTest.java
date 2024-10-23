package com.narara.superboard.board.service.validator;

import com.narara.superboard.board.exception.BoardInvalidVisibilityFormatException;
import com.narara.superboard.board.exception.BoardVisibilityNotFoundException;
import com.narara.superboard.board.interfaces.dto.BoardCreateRequestDto;
import com.narara.superboard.board.exception.BoardNameNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("보드 검증 테스트")
class BoardValidatorTest {

    @InjectMocks
    private BoardValidator boardValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }


    @DisplayName("생성 DTO에 이름이 없으면 에러가 발생한다.")
    @ParameterizedTest
    @MethodSource("provideInvalidBoardDataByNoName")
    void testBoardEntityCreationByName(String name, String visibility, Map<String, Object> background) {
        BoardCreateRequestDto boardCreateDto = new BoardCreateRequestDto(name, visibility, background);

        assertThrows(BoardNameNotFoundException.class, () -> boardValidator.validateNameIsPresent(boardCreateDto));
    }

    static Stream<Arguments> provideInvalidBoardDataByNoName() {
        return Stream.of(
                Arguments.of(null, "PRIVATE", Map.of("type", "color", "value", "#ffffff")),
                Arguments.of("", "WORKSPACE", Map.of("type", "image", "value", "https://example.com/image.jpg"))
        );
    }

    @DisplayName("생성 DTO에 가시성 정보가 없으면 에러가 발생한다.")
    @ParameterizedTest
    @MethodSource("provideInvalidBoardDataByNoVisibility")
    void testBoardEntityCreationByVisibility(String name, String visibility, Map<String, Object> background) {
        BoardCreateRequestDto boardCreateDto = new BoardCreateRequestDto(name, visibility, background);

        assertThrows(BoardVisibilityNotFoundException.class, () -> boardValidator.validateVisibilityIsPresent(boardCreateDto));
    }

    static Stream<Arguments> provideInvalidBoardDataByNoVisibility() {
        return Stream.of(
                Arguments.of("날아라 보드", null, Map.of("type", "color", "value", "#ffffff")),
                Arguments.of("나의 보드", " ", Map.of("type", "image", "value", "https://example.com/image.jpg"))
        );
    }

    @DisplayName("생성 DTO에 잘못된 가시성 정보가 있으면 에러가 발생한다.")
    @ParameterizedTest
    @MethodSource("provideInvalidBoardDataByInvalidVisibility")
    void testBoardEntityCreationByInvalidVisibility(String name, String visibility, Map<String, Object> background) {
        BoardCreateRequestDto boardCreateDto = new BoardCreateRequestDto(name, visibility, background);

        assertThrows(BoardInvalidVisibilityFormatException.class, () -> boardValidator.validateVisibilityIsValid(boardCreateDto));
    }

    static Stream<Arguments> provideInvalidBoardDataByInvalidVisibility() {
        return Stream.of(
                Arguments.of("날아라 보드", "COCOBALL", Map.of("type", "color", "value", "#ffffff")),
                Arguments.of("나의 보드", "TOSS", Map.of("type", "image", "value", "https://example.com/image.jpg"))
        );
    }
}