package com.narara.superboard.board.service.validator;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.board.exception.BoardInvalidVisibilityFormatException;
import com.narara.superboard.board.exception.BoardVisibilityNotFoundException;
import com.narara.superboard.common.exception.cover.InvalidCoverTypeFormatException;
import com.narara.superboard.common.interfaces.dto.CoverDto;
import com.narara.superboard.board.interfaces.dto.BoardCreateRequestDto;
import com.narara.superboard.board.exception.BoardNameNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("보드 검증 테스트")
class BoardValidatorTest implements MockSuperBoardUnitTests {

    public static final String BOARD_NAME1 = "날아라 보드";
    public static final String BOARD_NAME2 = "나의 보드";
    public static final String BOARD_NAME3 = "마이 보드";

    @InjectMocks
    private BoardValidator boardValidator;


    @DisplayName("생성 DTO에 이름이 없으면 에러가 발생한다.")
    @ParameterizedTest
    @MethodSource("provideInvalidBoardDataByNoName")
    void testBoardEntityCreationByName(Long workspaceId, String name, String visibility, Map<String, Object> background) {
        BoardCreateRequestDto boardCreateDto = new BoardCreateRequestDto(workspaceId, name, visibility, new CoverDto((String)background.get("type"), (String)background.get("value")));

        assertThrows(BoardNameNotFoundException.class, () -> boardValidator.validateNameIsPresent(boardCreateDto));
    }

    static Stream<Arguments> provideInvalidBoardDataByNoName() {
        return Stream.of(
                Arguments.of(1L, null, "PRIVATE", Map.of("type", "color", "value", "#ffffff")),
                Arguments.of(2L, "", "WORKSPACE", Map.of("type", "image", "value", "https://example.com/image.jpg"))
        );
    }

    @DisplayName("생성 DTO에 가시성 정보가 없으면 에러가 발생한다.")
    @ParameterizedTest
    @MethodSource("provideInvalidBoardDataByNoVisibility")
    void testBoardEntityCreationByVisibility(Long workspaceId, String name, String visibility, Map<String, Object> background) {
        BoardCreateRequestDto boardCreateDto = new BoardCreateRequestDto(workspaceId, name, visibility, new CoverDto((String)background.get("type"), (String)background.get("value")));

        assertThrows(BoardVisibilityNotFoundException.class, () -> boardValidator.validateVisibilityIsPresent(boardCreateDto));
    }

    static Stream<Arguments> provideInvalidBoardDataByNoVisibility() {
        return Stream.of(
                Arguments.of(1L, BOARD_NAME1, null, Map.of("type", "color", "value", "#ffffff")),
                Arguments.of(2L, BOARD_NAME2, " ", Map.of("type", "image", "value", "https://example.com/image.jpg"))
        );
    }

    @DisplayName("생성 DTO에 잘못된 가시성 정보가 있으면 에러가 발생한다.")
    @ParameterizedTest
    @MethodSource("provideInvalidBoardDataByInvalidVisibility")
    void testBoardEntityCreationByInvalidVisibility(Long workspaceId, String name, String visibility, Map<String, Object> background) {
        BoardCreateRequestDto boardCreateDto = new BoardCreateRequestDto(workspaceId, name, visibility, new CoverDto((String)background.get("type"), (String)background.get("value")));

        assertThrows(BoardInvalidVisibilityFormatException.class, () -> boardValidator.validateVisibilityIsValid(boardCreateDto));
    }

    static Stream<Arguments> provideInvalidBoardDataByInvalidVisibility() {
        return Stream.of(
                Arguments.of(1L, BOARD_NAME1, "COCOBALL", Map.of("type", "color", "value", "#ffffff")),
                Arguments.of(2L, BOARD_NAME2, "TOSS", Map.of("type", "image", "value", "https://example.com/image.jpg"))
        );
    }

    @DisplayName("cover 의 type이 정상적인 값이면 에러가 발생하지 않는다.")
    @ParameterizedTest
    @MethodSource("provideValidBoardDataByInvalidCoverType")
    void testBoardEntityCreationByValidCoverType(Long workspaceId, String name, String visibility, Map<String, Object> background) {
        BoardCreateRequestDto boardCreateDto = new BoardCreateRequestDto(
                workspaceId,
                name,
                visibility,
                new CoverDto((String)background.get("type"), (String)background.get("value"))
        );

        assertDoesNotThrow(() -> boardValidator.validateBackgroundIsValid(boardCreateDto));
    }

    static Stream<Arguments> provideValidBoardDataByInvalidCoverType() {
        return Stream.of(
                Arguments.of(1L, BOARD_NAME1, "COCOBALL", Map.of("type", "COLOR", "value", "#ffffff")),
                Arguments.of(2L, BOARD_NAME2, "TOSS", Map.of("type", "IMAGE", "value", "https://example.com/image.jpg")),
                Arguments.of(3L, BOARD_NAME3, "SOCKET", Map.of("type", "NONE", "value", "https://example.com/image.jpg"))
        );
    }

    @DisplayName("cover 의 type이 이상하면 에러가 발생한다.")
    @ParameterizedTest
    @MethodSource("provideInvalidBoardDataByInvalidCoverType")
    void testBoardEntityCreationByInvalidCoverType(Long workspaceId, String name, String visibility, Map<String, Object> background) {
        BoardCreateRequestDto boardCreateDto = new BoardCreateRequestDto(
                workspaceId,
                name,
                visibility,
                new CoverDto((String)background.get("type"), (String)background.get("value"))
        );

        assertThrows(InvalidCoverTypeFormatException.class, () -> boardValidator.validateBackgroundIsValid(boardCreateDto));
    }

    static Stream<Arguments> provideInvalidBoardDataByInvalidCoverType() {
        return Stream.of(
                Arguments.of(1L, BOARD_NAME1, "COCOBALL", Map.of("type", "ColoR", "value", "#ffffff")),
                Arguments.of(2L, BOARD_NAME2, "TOSS", Map.of("type", "IMAGEE", "value", "https://example.com/image.jpg")),
                Arguments.of(3L, BOARD_NAME3, "SOCKET", Map.of("type", "COFFEE", "value", "https://example.com/image.jpg"))
        );
    }
}
