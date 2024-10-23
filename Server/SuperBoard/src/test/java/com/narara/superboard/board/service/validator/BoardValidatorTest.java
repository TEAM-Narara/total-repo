package com.narara.superboard.board.service.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.narara.superboard.board.interfaces.dto.BoardCreateRequestDto;
import com.narara.superboard.common.exception.BoardNameNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Map;

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
    @CsvSource({
            ", 'PRIVATE', ''{\"type\": \"color\", \"value\": \"#ffffff\"}'",  // 이름이 없는 경우
            "'', 'WORKSPACE', '{\"type\": \"imageurl\", \"value\": \"https://example.com/image.jpg\"}'"  // 이름이 빈 문자열인 경우
    })
    void testBoardEntityCreation(String name, String visibility, String backgroundJson) throws JsonProcessingException {
        // JSON을 Map으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> background = objectMapper.readValue(backgroundJson, new TypeReference<Map<String, Object>>() {});

        BoardCreateRequestDto boardCreateDto = new BoardCreateRequestDto(name, visibility, background);

        assertThrows(BoardNameNotFoundException.class, () -> boardValidator.validateNameIsPresent(boardCreateDto));
    }
    
}