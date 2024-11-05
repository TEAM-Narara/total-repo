package com.narara.superboard.card.service;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.interfaces.dto.CoverDto;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.cardmember.infrastructure.CardMemberRepository;
import com.narara.superboard.common.application.validator.LastOrderValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.service.ListService;
import com.narara.superboard.member.entity.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
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
    private CardMemberRepository cardMemberRepository;

    @Mock
    private LastOrderValidator lastOrderValidator;

    @Mock
    private ListRepository listRepository;

    @Mock
    private ListService listService; // 추가: listService를 Mock으로 설정

    @InjectMocks
    private CardServiceImpl cardService; // 실제 인스턴스 생성 후 Mock 주입

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

        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Mocking: 이름 검증 로직
        doNothing().when(nameValidator).validateCardNameIsEmpty(cardCreateRequestDto);

        // Mocking: 카드 저장 시 호출되는 로직
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        // Mocking: 카드의 마지막 순서 검증 로직이 정상적으로 동작하는지 확인 (예외 발생하지 않도록 설정)
        doNothing().when(lastOrderValidator).checkValidCardLastOrder(list);

        // when
        Card result = cardService.createCard(member, cardCreateRequestDto);

        // then
        assertEquals(cardId, result.getId());
        assertEquals(cardName, result.getName());
        verify(nameValidator, times(1)).validateCardNameIsEmpty(cardCreateRequestDto);
        verify(cardRepository, times(1)).save(any(Card.class));
        verify(cardMemberRepository, times(1)).save(any(CardMember.class));
    }

    @ParameterizedTest
    @DisplayName("리스트가 존재하지 않을 때 실패")
    @org.junit.jupiter.params.provider.ValueSource(longs = {0L, 2L})
    void shouldFailWhenListNotFound(Long listId) {
        // given
        String cardName = "Test Card";
        CardCreateRequestDto cardCreateRequestDto = new CardCreateRequestDto(listId, cardName);
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Mocking listRepository to return an empty Optional (리스트가 존재하지 않음)
        when(listRepository.findById(listId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundEntityException.class, () -> {
            cardService.createCard(member, cardCreateRequestDto);
        });

        // 검증: listRepository.findById가 1번 호출되었는지 확인
        verify(listRepository, times(1)).findById(listId);
        verifyNoMoreInteractions(cardRepository); // cardRepository는 호출되지 않아야 함
    }

    @Test
    @DisplayName("카드 조회 실패 테스트")
    void testGetCardFailure() {
        // given
        Long cardId = 999L; // 존재하지 않는 카드 ID 설정
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
        Map<String, Object> background = Map.of("type", "COLOR", "value", "#FFFFFF");
        Map<String, Object> background2 = Map.of("type", "IMAGE", "value", "https://example.com/image.png");
        return Stream.of(
                // 모든 필드가 업데이트된 경우
                Arguments.of(
                        new CardUpdateRequestDto(
                                "Updated Name",
                                "Updated Description",
                                1633024800000L,
                                1633111200000L,
                                new CoverDto((String)background.get("type"), (String)background.get("value"))
                        ),
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
                        new CardUpdateRequestDto(null, null, null, null, new CoverDto((String)background2.get("type"), (String)background2.get("value"))),
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

    @ParameterizedTest
    @DisplayName("보드 ID를 기준으로 아카이브된 카드 리스트 조회 성공")
    @ValueSource(longs = {1L, 2L})
    void testGetArchivedCardList_Success(Long boardId) {
        // given: 보드에 속한 리스트와 카드들을 모킹
        List list1 = List.builder().id(1L).name("List 1").build();
        List list2 = List.builder().id(2L).name("List 2").build();

        Card card1 = Card.builder().id(1L).name("Archived Card 1").isArchived(true).build();
        Card card2 = Card.builder().id(2L).name("Archived Card 2").isArchived(true).build();

        Member member = new Member(1L , "시현", "sisi@naver.com");
        // 리스트와 아카이브된 카드 설정
        when(listRepository.findAllByBoardId(boardId)).thenReturn(Arrays.asList(list1, list2));
        when(cardRepository.findAllByListAndIsArchivedTrue(list1)).thenReturn(Collections.singletonList(card1));
        when(cardRepository.findAllByListAndIsArchivedTrue(list2)).thenReturn(Collections.singletonList(card2));

        // when: 아카이브된 카드 리스트 조회
        java.util.List<Card> result = cardService.getArchivedCardList(member, boardId);

        // then: 반환된 카드 리스트가 예상대로 모킹된 카드들과 일치하는지 확인
        assertEquals(2, result.size());
        assertTrue(result.contains(card1));
        assertTrue(result.contains(card2));

        verify(listRepository, times(1)).findAllByBoardId(boardId);
        verify(cardRepository, times(1)).findAllByListAndIsArchivedTrue(list1);
        verify(cardRepository, times(1)).findAllByListAndIsArchivedTrue(list2);
    }

    @Test
    @DisplayName("보드 ID가 유효하지만 리스트에 아카이브된 카드가 없는 경우")
    void testGetArchivedCardList_EmptyArchivedCards() {
        // given: 보드에 속한 리스트가 있지만 아카이브된 카드가 없는 경우
        Long boardId = 1L;
        List list = List.builder().id(1L).name("List 1").build();
        Member member = new Member(1L , "시현", "sisi@naver.com");
        when(listRepository.findAllByBoardId(boardId)).thenReturn(Collections.singletonList(list));
        when(cardRepository.findAllByListAndIsArchivedTrue(list)).thenReturn(Collections.emptyList());

        // when: 아카이브된 카드 리스트 조회
        java.util.List<Card> result = cardService.getArchivedCardList(member, boardId);

        // then: 빈 리스트가 반환되는지 확인
        assertTrue(result.isEmpty());
        verify(listRepository, times(1)).findAllByBoardId(boardId);
        verify(cardRepository, times(1)).findAllByListAndIsArchivedTrue(list);
    }

    @ParameterizedTest
    @DisplayName("카드 아카이브 상태 변경 성공 테스트")
    @CsvSource({
            "1, true",
            "2, false"
    })
    void testChangeArchiveStatusByCard_Success(Long cardId, boolean isArchived) {
        // given: 카드 모킹
        Member member = new Member(1L , "시현", "sisi@naver.com");

        Board board = Board.builder()
                .id(1L)
                .name("Test Board")
                .boardMemberList(new ArrayList<>()) // boardMemberList를 빈 리스트로 초기화
                .build();

        // boardMemberList에 멤버 추가
        board.getBoardMemberList().add(new BoardMember(member, Authority.ADMIN));

        List list = List.builder()
                .id(1L)
                .name("Test List")
                .lastCardOrder(0L)
                .board(board)
                .build();

        Card card = Card.builder()
                .id(cardId)
                .name("Test Card")
                .isArchived(isArchived)
                .list(list)
                .build();

        // Mocking: getCard 호출 시 모킹된 카드 반환
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // when: 카드 아카이브 상태 변경
        cardService.changeArchiveStatusByCard(member, cardId);

        // then: 카드의 아카이브 상태가 변경된 값인지 확인
        assertEquals(!isArchived, card.getIsArchived());
        verify(cardRepository, times(1)).findById(cardId);
    }


}
