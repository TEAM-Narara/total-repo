package com.narara.superboard.card.service;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastrucuture.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.common.application.validator.CoverValidator;
import com.narara.superboard.common.application.validator.LastOrderValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastrucure.ListRepository;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("카드 서비스에 대한 단위 테스트")
class CardServiceImplTest implements MockSuperBoardUnitTests {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private NameValidator nameValidator;

    @Mock
    private CoverValidator coverValidator;

    @Mock
    private LastOrderValidator lastOrderValidator;

    @Mock
    private ListRepository listRepository;

    @InjectMocks
    private CardServiceImpl cardService;


    @Test
    @DisplayName("카드 생성 성공 테스트")
    void testCreateCardSuccess() {
        // given
        Long cardId = 1L;
        String cardName = "Test Card";
        CardCreateRequestDto cardCreateRequestDto = new CardCreateRequestDto(1L, cardName);

        List list = List.builder()
                .id(1L)
                .name("Test List")
                .lastCardOrder(0L)
                .build();

        when(listRepository.findById(1L)).thenReturn(Optional.ofNullable(list));

        Card savedCard = Card.builder()
                .id(cardId)
                .name(cardName)
                .description("Card Description")
                .list(list)
                .build();

        // Mocking: 이름 검증 로직
        doNothing().when(nameValidator).validateCardNameIsEmpty(cardCreateRequestDto);

        // Mocking: 카드 저장 시 호출되는 로직
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        // Mocking: 카드의 마지막 순서 검증 로직이 정상적으로 동작하는지 확인 (예외 발생하지 않도록 설정)
        doNothing().when(lastOrderValidator).checkValidCardLastOrder(list);

        // when
        Card result = cardService.createCard(cardCreateRequestDto);

        // then
        assertEquals(cardId, result.getId());
        assertEquals(cardName, result.getName());
        verify(nameValidator, times(1)).validateCardNameIsEmpty(cardCreateRequestDto);
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @ParameterizedTest
    @DisplayName("리스트가 존재하지 않을 때 실패")
    @org.junit.jupiter.params.provider.ValueSource(longs = {0L, 2L})
    void shouldFailWhenListNotFound(Long listId) {
        // given
        String cardName = "Test Card";
        CardCreateRequestDto cardCreateRequestDto = new CardCreateRequestDto(listId, cardName);

        // Mocking listRepository to return an empty Optional (리스트가 존재하지 않음)
        when(listRepository.findById(listId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundEntityException.class, () -> {
            cardService.createCard(cardCreateRequestDto);
        });

        // 검증: listRepository.findById가 1번 호출되었는지 확인
        verify(listRepository, times(1)).findById(listId);
        verifyNoMoreInteractions(cardRepository); // cardRepository는 호출되지 않아야 함
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @DisplayName("카드 조회 실패 테스트")
    void testGetCardFailure(Long cardId) {
        // given
        // Mocking: 카드가 존재하지 않는 경우
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // when & then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> {
            cardService.getCard(cardId);
        });

        assertEquals("해당하는 카드(이)가 존재하지 않습니다. 카드ID: " + cardId, exception.getMessage());
        verify(cardRepository, times(1)).findById(cardId);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @DisplayName("카드 조회 성공 테스트")
    void testGetCardSuccess(Long cardId) {
        // given
        Card card = Card.builder()
                .id(cardId)
                .name("Test Card")
                .build();

        // Mocking: 카드가 존재하는 경우
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // when
        Card result = cardService.getCard(cardId);

        // then
        assertNotNull(result);
        assertEquals(cardId, result.getId());
        assertEquals("Test Card", result.getName());
        verify(cardRepository, times(1)).findById(cardId);
    }

    @ParameterizedTest
    @MethodSource("provideValidUpdateCardCases")
    @DisplayName("카드 수정에 대한 Card의 다양한 필드 업데이트 성공 테스트")
    void updateCardSuccess(CardUpdateRequestDto requestDto, String expectedName, String expectedDescription, Map<String, Object> expectedCover, Long expectedStartAt, Long expectedEndAt) {
        // given - 필요한 메서드에서만 Card 객체 생성
        Card card = Card.builder()
                .name("Existing Name")
                .build();

        // when
        Card updatedCard = card.updateCard(requestDto);

        // then
        assertEquals(expectedName, updatedCard.getName());
        assertEquals(expectedDescription, updatedCard.getDescription());
        assertEquals(expectedCover, updatedCard.getCover());
        assertEquals(expectedStartAt, updatedCard.getStartAt());
        assertEquals(expectedEndAt, updatedCard.getEndAt());
    }

    private static Stream<Arguments> provideValidUpdateCardCases() {
        return Stream.of(
                // 모든 필드가 업데이트된 경우
                Arguments.of(
                        new CardUpdateRequestDto("Updated Name", "Updated Description", 1633024800000L, 1633111200000L, Map.of("type", "COLOR", "value", "#FFFFFF")),
                        "Updated Name", "Updated Description", Map.of("type", "COLOR", "value", "#FFFFFF"), 1633024800000L, 1633111200000L
                ),
                // 이름과 설명만 업데이트된 경우
                Arguments.of(
                        new CardUpdateRequestDto("Updated Name Only", "Updated Description Only", null, null, null),
                        "Updated Name Only", "Updated Description Only", null, null, null
                ),
                // 시작 날짜와 종료 날짜만 업데이트된 경우
                Arguments.of(
                        new CardUpdateRequestDto(null, null, 1633204800000L, 1633301200000L, null),
                        "Existing Name", null, null, 1633204800000L, 1633301200000L
                ),
                // 커버만 업데이트된 경우
                Arguments.of(
                        new CardUpdateRequestDto(null, null, null, null, Map.of("type", "IMAGE", "value", "https://example.com/image.png")),
                        "Existing Name", null, Map.of("type", "IMAGE", "value", "https://example.com/image.png"), null, null
                ),
                // 이름이 비어 있는 경우 기존 이름 유지
                Arguments.of(
                        new CardUpdateRequestDto("   ", "Description Updated", null, null, null),
                        "Existing Name", "Description Updated", null, null, null
                ),
                // 설명만 업데이트된 경우
                Arguments.of(
                        new CardUpdateRequestDto(null, "Updated Description Only", null, null, null),
                        "Existing Name", "Updated Description Only", null, null, null
                ),
                // 시작 날짜만 업데이트된 경우
                Arguments.of(
                        new CardUpdateRequestDto(null, null, 1633024800000L, null, null),
                        "Existing Name", null, null, 1633024800000L, null
                )
        );
    }
}
