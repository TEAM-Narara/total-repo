package com.narara.superboard.card.service;

import static com.narara.superboard.card.CardAction.*;

import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.card.CardAction;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.cardmember.infrastructure.CardMemberRepository;
import com.narara.superboard.common.application.validator.CoverValidator;
import com.narara.superboard.common.application.validator.LastOrderValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.authority.UnauthorizedException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.service.ListService;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.websocket.constant.Action;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final ListService listService;

    private final CardRepository cardRepository;
    private final ListRepository listRepository;
    private final CardMemberRepository cardMemberRepository;

    private final NameValidator nameValidator;
    private final CoverValidator coverValidator;
    private final LastOrderValidator lastOrderValidator;

    @Override
    public Card createCard(Member member, CardCreateRequestDto cardCreateRequestDto) {
        nameValidator.validateCardNameIsEmpty(cardCreateRequestDto);

        List list = listRepository.findById(cardCreateRequestDto.listId())
                .orElseThrow(() -> new NotFoundEntityException(cardCreateRequestDto.listId(), "리스트"));

        lastOrderValidator.checkValidCardLastOrder(list);
        listService.checkBoardMember(list, member, ARCHIVE_CARD);

        Card card = Card.createCard(cardCreateRequestDto, list);

        CardMember cardMember = CardMember.createCardMember(card, member);
        cardMemberRepository.save(cardMember);

        return cardRepository.save(card);
    }

    @Override
    public Card getCard(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
    }

    @Override
    public void deleteCard(Member member, Long cardId) {
        Card card = getCard(cardId);
        checkBoardMember(card, member, DELETE_CARD);
        card.delete();
    }

    @Override
    public Card updateCard(Member member, Long cardId, CardUpdateRequestDto cardUpdateRequestDto) {
        Card card = getCard(cardId);
        checkBoardMember(card, member, EDIT_CARD);
        coverValidator.validateCardCover(cardUpdateRequestDto);

        return card.updateCard(cardUpdateRequestDto);
    }

    @Override
    public java.util.List<Card> getArchivedCardList(Member member, Long boardId) {
        java.util.List<List> allListByBoard = listRepository.findAllByBoardId(boardId);
        java.util.List<Card> cardCollection = new ArrayList<>();
        listService.checkBoardMember(allListByBoard.getFirst(),member, GET_ARCHIVE_CARD);
        for (List list : allListByBoard) {
            cardCollection.addAll(cardRepository.findAllByListAndIsArchivedTrue(list));
        }

        return cardCollection;
    }

    @Override
    public void changeArchiveStatusByCard(Member member, Long cardId) {
        Card card = getCard(cardId);
        checkBoardMember(card, member, ARCHIVE_CARD);
        card.changeArchiveStatus();
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
}
