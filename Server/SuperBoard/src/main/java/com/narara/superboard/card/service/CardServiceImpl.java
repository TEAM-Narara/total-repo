package com.narara.superboard.card.service;

import static com.narara.superboard.card.CardAction.*;

import com.narara.superboard.board.service.kafka.BoardOffsetService;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardHistoryRepository;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.card.interfaces.dto.log.*;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.cardmember.infrastructure.CardMemberRepository;
import com.narara.superboard.common.application.validator.CoverValidator;
import com.narara.superboard.common.application.validator.LastOrderValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.authority.UnauthorizedException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.service.ListService;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.websocket.constant.Action;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final ListService listService;

    private final CardRepository cardRepository;
    private final ListRepository listRepository;
    private final CardMemberRepository cardMemberRepository;
    private final CardHistoryRepository cardHistoryRepository;

    private final NameValidator nameValidator;
    private final CoverValidator coverValidator;
    private final LastOrderValidator lastOrderValidator;

    private final BoardOffsetService boardOffsetService;

    @Override
    public Card createCard(Member member, CardCreateRequestDto cardCreateRequestDto) {
        nameValidator.validateCardNameIsEmpty(cardCreateRequestDto);

        List list = listRepository.findById(cardCreateRequestDto.listId())
                .orElseThrow(() -> new NotFoundEntityException(cardCreateRequestDto.listId(), "리스트"));

        lastOrderValidator.checkValidCardLastOrder(list);
        listService.checkBoardMember(list, member, ARCHIVE_CARD);

        Card card = Card.createCard(cardCreateRequestDto, list);


        Card savedCard = cardRepository.save(card);
        CardMember cardMember = CardMember.createCardMember(savedCard, member);
        cardMemberRepository.save(cardMember);

        boardOffsetService.saveAddCard(card); //Websocket 카드 생성
        boardOffsetService.saveAddCardMember(cardMember); //Websocket 카드 멤버 생성

        // 로그 기록 추가
        CreateCardInfo createCardInfo = new CreateCardInfo(list.getId(), list.getName(), savedCard.getId(), savedCard.getName());

        CardHistory<CreateCardInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), list.getBoard(), savedCard,
                EventType.CREATE, EventData.CARD, createCardInfo);

        cardHistoryRepository.save(cardHistory);
        //TODO Websocket 카드 생성 로그 추가

        return savedCard;
    }

    @Override
    public Card getCard(Long cardId) {
        return cardRepository.findByIdAndIsDeletedFalse(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
    }

    @Override
    public void deleteCard(Member member, Long cardId) {
        Card card = getCard(cardId);
        checkBoardMember(card, member, DELETE_CARD);
        card.delete();

        boardOffsetService.saveDeleteCard(card); //Websocket 카드 삭제

        // 로그 기록 추가
        DeleteCardInfo deleteCardInfo = new DeleteCardInfo(card.getList().getId(), card.getList().getName(), card.getId(), card.getName());

        CardHistory<DeleteCardInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), card.getList().getBoard(), card,
                EventType.DELETE, EventData.CARD, deleteCardInfo);

        cardHistoryRepository.save(cardHistory);
        //TODO Websocket 카드 삭제 로그 추가
    }

    @Override
    public Card updateCard(Member member, Long cardId, CardUpdateRequestDto cardUpdateRequestDto) {
        Card card = getCard(cardId);
        checkBoardMember(card, member, EDIT_CARD);

        if (cardUpdateRequestDto.cover() != null) {
            coverValidator.validateCoverTypeIsValid(cardUpdateRequestDto.cover());
        }
        Card updatedCard = card.updateCard(cardUpdateRequestDto);

        boardOffsetService.saveEditCard(updatedCard); //Websocket 카드 업데이트

        // 로그 기록 추가
        UpdateCardInfo updateCardInfo = new UpdateCardInfo(updatedCard.getList().getId(), updatedCard.getList().getName(), updatedCard.getId(), updatedCard.getName());

        CardHistory<UpdateCardInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), updatedCard.getList().getBoard(), updatedCard,
                EventType.UPDATE, EventData.CARD, updateCardInfo);

        cardHistoryRepository.save(cardHistory);
        //TODO Websocket 카드 업데이트 로그 추가

        return updatedCard;
    }

    @Override
    public java.util.List<Card> getArchivedCardList(Member member, Long boardId) {
        java.util.List<List> allListByBoard = listRepository.findAllByBoardId(boardId);
        if (allListByBoard.isEmpty()) {
            return new ArrayList<>();
        }
        java.util.List<Card> cardCollection = new ArrayList<>();
        listService.checkBoardMember(allListByBoard.getFirst(),member, GET_ARCHIVE_CARD);
        for (List list : allListByBoard) {
            cardCollection.addAll(cardRepository.findAllByListAndIsArchivedTrueAndIsDeletedFalse(list));
        }

        return cardCollection;
    }

    @Override
    public void changeArchiveStatusByCard(Member member, Long cardId) {
        Card card = getCard(cardId);
        checkBoardMember(card, member, ARCHIVE_CARD);
        card.changeArchiveStatus();

        boardOffsetService.saveEditCardArchiveDiff(card); //Websocket 카드 아카이브 상태 변경

        // 로그 기록 추가
        ArchiveStatusChangeInfo archiveStatusChangeInfo = new ArchiveStatusChangeInfo(card.getId(), card.getName(), card.getIsArchived());

        CardHistory<ArchiveStatusChangeInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), card.getList().getBoard(), card,
                EventType.ARCHIVE, EventData.CARD, archiveStatusChangeInfo);

        cardHistoryRepository.save(cardHistory);
        //TODO Websocket 카드 아카이브 상태 변경 로그 추가
    }

    @Override
    public void checkBoardMember(Card card, Member member, Action action) {
        java.util.List<BoardMember> boardMemberList = card.getList().getBoard().getBoardMemberList();
        for (BoardMember boardMember : boardMemberList) {
            if (boardMember.getMember().getId().equals(member.getId())) {
                return;
            }
        }
        throw new UnauthorizedException(member.getNickname(), action);
    }

    @Override
    public java.util.List<CardActivityDetailResponseDto> getCardActivity(Long cardId) {
        java.util.List<CardHistory> cardHistoryCollection = cardHistoryRepository.findByWhere_CardIdOrderByWhenDesc(cardId);
        if (cardHistoryCollection.isEmpty()) {
            return new ArrayList<>();
        }
        return cardHistoryCollection.stream()
                .map(CardActivityDetailResponseDto::createActivityDetailResponseDto)
                .toList();
    }
}
