package com.narara.superboard.list.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.card.CardAction;
import com.narara.superboard.common.application.validator.LastOrderValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.authority.UnauthorizedException;
import com.narara.superboard.list.ListAction;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import com.narara.superboard.list.interfaces.dto.ListUpdateRequestDto;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.websocket.constant.Action;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListServiceImpl implements ListService{

    private final BoardService boardService;

    private final NameValidator nameValidator;
    private final LastOrderValidator lastOrderValidator;

    private final BoardRepository boardRepository;
    private final ListRepository listRepository;

    @Override
    public List createList(Member member, ListCreateRequestDto listCreateRequestDto) {
        nameValidator.validateListNameIsEmpty(listCreateRequestDto);

        Board board = boardRepository.getReferenceById(listCreateRequestDto.boardId());
        lastOrderValidator.checkValidListLastOrder(board);
        boardService.checkBoardMember(board, member, ListAction.ADD_LIST);

        List list = List.createList(listCreateRequestDto, board);
        return listRepository.save(list);
    }

    @Override
    public List updateList(Member member, Long listId, ListUpdateRequestDto listUpdateRequestDto) {
        List list = getList(listId);

        nameValidator.validateListNameIsEmpty(listUpdateRequestDto);
        checkBoardMember(list, member, ListAction.EDIT_LIST);

        list.updateList(listUpdateRequestDto);
        return list;
    }

    @Override
    public List getList(Long listId) {
        return listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
    }

    @Override
    public List changeListIsArchived(Member member, Long listId) {
        List list = getList(listId);
        checkBoardMember(list, member, ListAction.CHANGE_ARCHIVED);

        list.changeListIsArchived();
        return list;
    }

    @Override
    public java.util.List<List> getArchivedList(Member member, Long boardId) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "보드"));
        boardService.checkBoardMember(board, member, ListAction.ARCHIVE_LIST);

        return listRepository.findByBoardAndIsArchived(board, true);
    }

    @Override
    public void checkBoardMember(List list, Member member, Action action) {
        java.util.List<BoardMember> boardMemberList = list.getBoard().getBoardMemberList();
        for (BoardMember boardMember : boardMemberList) {
            if (boardMember.getMember().getId().equals(member.getId())) {
                return;
            }
        }
        throw new UnauthorizedException(member.getNickname(), action);
    }
}
