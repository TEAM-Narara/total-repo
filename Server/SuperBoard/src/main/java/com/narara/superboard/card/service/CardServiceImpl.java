package com.narara.superboard.card.service;

import static com.narara.superboard.card.CardAction.*;

import com.narara.superboard.attachment.infrastructure.AttachmentRepository;
import com.narara.superboard.attachment.service.AttachmentServiceImpl.AttachmentInfo;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.service.kafka.BoardOffsetService;
import com.narara.superboard.boardmember.entity.BoardMember;
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
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.service.ListService;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.reply.interfaces.dto.ReplyInfo;
import com.narara.superboard.websocket.constant.Action;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
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
        CreateCardInfo createCardInfo = new CreateCardInfo(list.getId(), list.getName(), savedCard.getId(),
                savedCard.getName());

        CardHistory<CreateCardInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), list.getBoard(), savedCard,
                EventType.CREATE, EventData.CARD, createCardInfo);

        cardHistoryRepository.save(cardHistory);

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
    public void changeArchiveStatusByCard(Member member, Long cardId) {
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

        Page<CardHistory> attachmentHistoryPage = cardHistoryRepository.findByWhere_CardIdAndEventDataInAttachmentOrderByWhenDesc(
                cardId, pageable);

        ArrayList<CardHistory> attachmentHistorys = new ArrayList<>(attachmentHistoryPage.getContent());

        // 2. Attachment 타입의 기록을 필터링하고 attachmentId로 그룹화
        Map<Long, java.util.List<CardHistory>> attachmentGroups = attachmentHistorys.stream()
                .collect(Collectors.groupingBy(ch -> ((AttachmentInfo) ch.getTarget()).attachmentId()));

        // 3. 각 attachmentId 그룹에서 delete와 create가 모두 있는 경우 두 항목을 삭제
        Iterator<CardHistory> iterator = attachmentHistorys.iterator();
        while (iterator.hasNext()) {
            CardHistory cardHistory = iterator.next();
            Long attachmentId = ((AttachmentInfo) cardHistory.getTarget()).attachmentId();

            // 그룹 내에서 delete와 create가 모두 있는지 확인
            java.util.List<CardHistory> attachmentHistory = attachmentGroups.get(attachmentId);

            boolean hasCreate = attachmentHistory.stream().anyMatch(ch -> EventType.CREATE.equals(ch.getEventType()));
            boolean hasDelete = attachmentHistory.stream().anyMatch(ch -> EventType.DELETE.equals(ch.getEventType()));

            // delete와 create가 모두 있으면 해당 그룹의 항목 삭제
            if (hasCreate && hasDelete) {
                iterator.remove();
            }
        }

        // 두 Page 객체의 총 페이지 수와 총 요소 수 계산
        long totalElements = cardHistoriesPage.getTotalElements() + cardReplies.getTotalElements();
        long totalPages = (long) Math.ceil((double) totalElements / pageable.getPageSize());

        // DTO로 변환 및 최신순 정렬
        java.util.List<CardCombinedActivityDto> combinedLogs =
                mergeAndLimitSortedList(cardHistoriesPage.getContent(), cardReplies.getContent(), attachmentHistorys, pageable.getPageSize());

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

    private static record Entry(CardHistory cardHistory, Iterator<CardHistory> iterator) {}

    public static java.util.List<CardCombinedActivityDto> mergeAndLimitSortedList(
            java.util.List<CardHistory> cardLogs,
            java.util.List<Reply> cardReplies,
            java.util.List<CardHistory> attachments,
            int pageSize) {

        java.util.List<CardCombinedActivityDto> combinedList = new ArrayList<>(pageSize);
        java.util.List<CardHistory> replyList = new ArrayList<>();

        // Reply -> CardHistory 변환 후 replyList에 추가
        for (Reply cardReply : cardReplies) {
            Card card = cardReply.getCard();
            ReplyInfo replyInfo = new ReplyInfo(card.getId(), card.getName(),
                    cardReply.getId(), cardReply.getContent());
            CardHistory cardHistory = CardHistory.createCardHistory(
                    cardReply.getMember(), cardReply.getUpdatedAt(),
                    card.getList().getBoard(), card,
                    EventType.CREATE, EventData.COMMENT, replyInfo);
            replyList.add(cardHistory);
        }

        // 최대 힙 생성 (getWhen() 값이 큰 순서대로 정렬)
        PriorityQueue<Entry> maxHeap = new PriorityQueue<>(
                Comparator.comparingLong((Entry e) -> e.cardHistory.getWhen()).reversed()
        );

        // 각 리스트의 첫 번째 요소를 힙에 추가
        if (!cardLogs.isEmpty()) maxHeap.add(new Entry(cardLogs.get(0), cardLogs.iterator()));
        if (!replyList.isEmpty()) maxHeap.add(new Entry(replyList.get(0), replyList.iterator()));
        if (!attachments.isEmpty()) maxHeap.add(new Entry(attachments.get(0), attachments.iterator()));

        // 병합 작업 수행
        while (!maxHeap.isEmpty() && combinedList.size() < pageSize) {
            Entry latestEntry = maxHeap.poll();
            combinedList.add(CardCombinedActivityDto.of(latestEntry.cardHistory()));

            // 해당 리스트의 다음 요소를 힙에 추가
            if (latestEntry.iterator().hasNext()) {
                maxHeap.add(new Entry(latestEntry.iterator().next(), latestEntry.iterator()));
            }
        }

        return combinedList;
    }
}
