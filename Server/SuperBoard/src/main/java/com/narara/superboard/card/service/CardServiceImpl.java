package com.narara.superboard.card.service;

import static com.narara.superboard.card.CardAction.*;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.attachment.infrastructure.AttachmentRepository;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.service.kafka.BoardOffsetService;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.infrastructure.BoardMemberRepository;
import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardHistoryRepository;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardSimpleResponseDto;
import com.narara.superboard.card.interfaces.dto.activity.CardCombinedActivityDto;
import com.narara.superboard.card.interfaces.dto.activity.CardCombinedActivityResponseDto;
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
import com.narara.superboard.fcmtoken.service.AlarmService;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.service.ListService;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.reply.interfaces.dto.ReplyInfo;
import com.narara.superboard.websocket.constant.Action;

import com.narara.superboard.workspace.entity.WorkSpace;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final ListService listService;
    private final AlarmService alarmService;

    private final CardRepository cardRepository;
    private final ListRepository listRepository;
    private final CardMemberRepository cardMemberRepository;
    private final CardHistoryRepository cardHistoryRepository;
    private final ReplyRepository replyRepository;
    private final AttachmentRepository attachmentRepository;

    private final NameValidator nameValidator;
    private final CoverValidator coverValidator;
    private final LastOrderValidator lastOrderValidator;

    private final BoardOffsetService boardOffsetService;
    private final BoardMemberRepository boardMemberRepository;

    @Override
    public Card createCard(Member member, CardCreateRequestDto cardCreateRequestDto) throws FirebaseMessagingException {
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
        CreateCardInfo createCardInfo = new CreateCardInfo(list.getId(), list.getName(), savedCard.getId(),
                savedCard.getName());

        CardHistory<CreateCardInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), list.getBoard(), savedCard,
                EventType.CREATE, EventData.CARD, createCardInfo);

        cardHistoryRepository.save(cardHistory);

        //[알림]
        alarmService.sendAddCardAlarm(member, card);

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
        DeleteCardInfo deleteCardInfo = new DeleteCardInfo(card.getList().getId(), card.getList().getName(),
                card.getId(), card.getName());

        CardHistory<DeleteCardInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), card.getList().getBoard(),
                card,
                EventType.DELETE, EventData.CARD, deleteCardInfo);

        cardHistoryRepository.save(cardHistory);
    }

    @Override
    public Card updateCard(Member member, Long cardId, CardUpdateRequestDto cardUpdateRequestDto) {
        Card card = getCard(cardId);
        checkBoardMember(card, member, EDIT_CARD);

        //cover 검증
        coverValidator.validateCoverTypeIsValid(cardUpdateRequestDto.cover());

        Card updatedCard = card.updateCard(cardUpdateRequestDto);

        boardOffsetService.saveEditCard(updatedCard); //Websocket 카드 업데이트

        // 로그 기록 추가
        UpdateCardInfo updateCardInfo = new UpdateCardInfo(updatedCard.getList().getId(),
                updatedCard.getList().getName(), updatedCard.getId(), updatedCard.getName());

        CardHistory<UpdateCardInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(),
                updatedCard.getList().getBoard(), updatedCard,
                EventType.UPDATE, EventData.CARD, updateCardInfo);

        cardHistoryRepository.save(cardHistory);

        return updatedCard;
    }

    @Override
    public java.util.List<Card> getArchivedCardList(Member member, Long boardId) {
        java.util.List<List> allListByBoard = listRepository.findAllByBoardId(boardId);
        if (allListByBoard.isEmpty()) {
            return new ArrayList<>();
        }
        java.util.List<Card> cardCollection = new ArrayList<>();
        listService.checkBoardMember(allListByBoard.getFirst(), member, GET_ARCHIVE_CARD);
        for (List list : allListByBoard) {
            cardCollection.addAll(cardRepository.findAllByListAndIsArchivedTrueAndIsDeletedFalse(list));
        }

        return cardCollection;
    }

    @Override
    public void changeArchiveStatusByCard(Member member, Long cardId) throws FirebaseMessagingException {
        Card card = getCard(cardId);
        checkBoardMember(card, member, ARCHIVE_CARD);
        card.changeArchiveStatus();

        boardOffsetService.saveEditCardArchiveDiff(card); //Websocket 카드 아카이브 상태 변경

        // 로그 기록 추가
        ArchiveStatusChangeInfo archiveStatusChangeInfo = new ArchiveStatusChangeInfo(card.getId(), card.getName(),
                card.getIsArchived());

        CardHistory<ArchiveStatusChangeInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), card.getList().getBoard(),
                card,
                EventType.ARCHIVE, EventData.CARD, archiveStatusChangeInfo);

        cardHistoryRepository.save(cardHistory);

        //알림
        alarmService.sendArchiveCard(member, card);
    }

    @Override
    public void checkBoardMember(Card card, Member member, Action action) {
        Board board = card.getList().getBoard();

        java.util.List<BoardMember> boardMemberList = board.getBoardMemberList();
        for (BoardMember boardMember : boardMemberList) {
            if (boardMember.getMember().getId().equals(member.getId())) {
                return;
            }
        }
        if (board.getVisibility().equals(Visibility.WORKSPACE)) {
            java.util.List<WorkSpaceMember> workspaceMemberList = board.getWorkSpace().getWorkspaceMemberList();
            for (WorkSpaceMember workSpaceMember : workspaceMemberList) {
                if (workSpaceMember.getMember().getId().equals(member.getId())) {
                    return;
                }
            }
        }
        throw new UnauthorizedException(member.getNickname(), action);
    }

    @Override
    public java.util.List<CardLogDetailResponseDto> getCardActivity(Long cardId) {
        java.util.List<CardHistory> cardHistoryCollection = cardHistoryRepository.findByWhere_CardIdOrderByWhenDesc(
                cardId);
        if (cardHistoryCollection.isEmpty()) {
            return new ArrayList<>();
        }
        return cardHistoryCollection.stream()
                .map(CardLogDetailResponseDto::createLogDetailResponseDto)
                .toList();
    }

    @Override
    public CardCombinedActivityResponseDto getCardCombinedLog(Long cardId, Pageable pageable) {
        // 카드 활동 및 댓글 리스트를 Page로 가져옴
        Page<CardHistory> cardHistoriesPage =
                cardHistoryRepository.findByWhere_CardIdAndEventDataNotInOrderByWhenDesc(cardId, pageable);
        Page<Reply> cardReplies =
                replyRepository.findAllByCardId(cardId, pageable);

        // 두 Page 객체의 총 페이지 수와 총 요소 수 계산
        long totalElements = cardHistoriesPage.getTotalElements() + cardReplies.getTotalElements();
        long totalPages = (long) Math.ceil((double) totalElements / pageable.getPageSize());

        // DTO로 변환 및 최신순 정렬
        java.util.List<CardCombinedActivityDto> combinedLogs =
                mergeAndLimitSortedList(cardHistoriesPage.getContent(), cardReplies.getContent(), pageable.getPageSize());

        return new CardCombinedActivityResponseDto(combinedLogs, totalPages, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<CardSimpleResponseDto> getCardsByListId(Long listId) {
        List list = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));

        return cardRepository.findByListAndIsDeletedFalseOrderByMyOrderAsc(list).stream()
                .map(CardSimpleResponseDto::of)
                .toList();
    }

    private static java.util.List<CardCombinedActivityDto> mergeAndLimitSortedList(
            java.util.List<CardHistory> cardLogs,
            java.util.List<Reply> cardReplies,
            int pageSize) {

        java.util.List<CardCombinedActivityDto> combinedList = new ArrayList<>();
        java.util.List<CardHistory> replyList = new ArrayList<>();

        for (Reply cardReply : cardReplies) {
            Card card = cardReply.getCard();
            ReplyInfo replyInfo =
                    new ReplyInfo(card.getId(), card.getName(),
                            cardReply.getId(), cardReply.getContent());
            CardHistory cardHistory = CardHistory.createCardHistory(
                    cardReply.getMember(), cardReply.getUpdatedAt(),
                    card.getList().getBoard(), card,
                    EventType.CREATE , EventData.COMMENT, replyInfo);

            replyList.add(cardHistory);
        }

        int i = 0, j = 0;

        // 병합하면서 최신순으로 정렬
        while (i < cardLogs.size() && j < replyList.size() && combinedList.size() < pageSize) {
            if (cardLogs.get(i).getWhen() >= replyList.get(j).getWhen()) {
                combinedList.add(CardCombinedActivityDto.of(cardLogs.get(i)));
                i++;
            } else {
                combinedList.add(CardCombinedActivityDto.of(replyList.get(j)));
                j++;
            }
        }

        // 남은 요소 추가 (pageSize에 도달할 때까지)
        while (i < cardLogs.size() && combinedList.size() < pageSize) {
            combinedList.add(CardCombinedActivityDto.of(cardLogs.get(i)));
            i++;
        }

        while (j < replyList.size() && combinedList.size() < pageSize) {
            combinedList.add(CardCombinedActivityDto.of(replyList.get(j)));
            j++;
        }

        return combinedList;
    }
}
