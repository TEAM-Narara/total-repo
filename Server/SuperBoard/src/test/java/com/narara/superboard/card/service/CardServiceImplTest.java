package com.narara.superboard.card.service;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastrucuture.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastrucure.ListRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("")
class CardServiceImplTest implements MockSuperBoardUnitTests {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private NameValidator nameValidator;

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
                .build();

        when(listRepository.findById(1L)).thenReturn(Optional.ofNullable(list));

        Card savedCard = Card.builder()
                .id(cardId)
                .name(cardName)
                .description("Card Description")
                .list(list)
                .build();

        // Mocking: 검증 로직을 모킹
        doNothing().when(nameValidator).validateCardNameIsEmpty(cardCreateRequestDto);

        // Mocking: cardRepository.save 호출 시 저장된 card 객체 반환
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        // when
        Card result = cardService.createCard(cardCreateRequestDto);

        // then
        assertEquals(cardId, result.getId());
        assertEquals(cardName, result.getName());
        verify(nameValidator, times(1)).validateCardNameIsEmpty(cardCreateRequestDto);
        verify(cardRepository, times(1)).save(any(Card.class));
    }
}