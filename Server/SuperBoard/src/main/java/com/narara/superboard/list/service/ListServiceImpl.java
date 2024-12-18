package com.narara.superboard.list.service;

import com.narara.superboard.board.document.BoardHistory;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.infrastructure.BoardHistoryRepository;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.board.service.kafka.BoardOffsetService;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.common.application.validator.LastOrderValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.authority.UnauthorizedException;
import com.narara.superboard.list.ListAction;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import com.narara.superboard.list.interfaces.dto.ListSimpleResponseDto;
import com.narara.superboard.list.interfaces.dto.ListUpdateRequestDto;
import com.narara.superboard.list.interfaces.dto.info.ArchiveListInfo;
import com.narara.superboard.list.interfaces.dto.info.CreateListInfo;
import com.narara.superboard.list.interfaces.dto.info.UpdateListInfo;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.websocket.constant.Action;
import java.util.ArrayList;

import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ListServiceImpl implements ListService{
    private final BoardService boardService;

    private final NameValidator nameValidator;
    private final LastOrderValidator lastOrderValidator;

    private final BoardRepository boardRepository;
    private final ListRepository listRepository;
    private final BoardHistoryRepository boardHistoryRepository;

    private final BoardOffsetService boardOffsetService;

    @Transactional
    @Override
    public List createList(Member member, ListCreateRequestDto listCreateRequestDto) {
        nameValidator.validateListNameIsEmpty(listCreateRequestDto);

        Board board = boardRepository.getReferenceById(listCreateRequestDto.boardId());
        lastOrderValidator.checkValidListLastOrder(board);
        boardService.checkBoardMember(board, member, ListAction.ADD_LIST);

        List list = List.createList(listCreateRequestDto, board);

        List savedlist = listRepository.save(list);
        boardOffsetService.saveAddListDiff(savedlist); //Websocket 리스트 생성
        // 리스트 생성 로그 기록
        CreateListInfo createListInfo = new CreateListInfo(savedlist.getId(), savedlist.getName(), board.getId());

        BoardHistory<CreateListInfo> boardHistory = BoardHistory.createBoardHistory(
                member, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9)), board, EventType.CREATE, EventData.LIST, createListInfo);

        boardHistoryRepository.save(boardHistory);

        return savedlist;
    }

    @Transactional
    @Override
    public List updateList(Member member, Long listId, ListUpdateRequestDto listUpdateRequestDto) {
        List list = getList(listId);

        nameValidator.validateListNameIsEmpty(listUpdateRequestDto);
        checkBoardMember(list, member, ListAction.EDIT_LIST);

        list.updateList(listUpdateRequestDto);

        boardOffsetService.saveEditListDiff(list);  //Websocket 리스트 업데이트

        // 리스트 업데이트 로그 기록
        UpdateListInfo updateListInfo = new UpdateListInfo(list.getId(), list.getName());

        BoardHistory<UpdateListInfo> boardHistory = BoardHistory.createBoardHistory(
                member, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9)), list.getBoard(), EventType.UPDATE, EventData.LIST, updateListInfo);

        boardHistoryRepository.save(boardHistory);

        return list;
    }

    @Override
    public List getList(Long listId) {
        return listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
    }

    @Transactional
    @Override
    public List changeListIsArchived(Member member, Long listId) {
        List list = getList(listId);
        checkBoardMember(list, member, ListAction.CHANGE_ARCHIVED);

        list.changeListIsArchived();
        boardOffsetService.saveEditListArchiveDiff(list); // Websocket 리스트 아카이브화

        // 리스트 아카이브 상태 변경 로그 기록
        ArchiveListInfo archiveListInfo = new ArchiveListInfo(list.getId(), list.getName(), list.getIsArchived());

        BoardHistory<ArchiveListInfo> boardHistory = BoardHistory.createBoardHistory(
                member, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9)), list.getBoard(), EventType.ARCHIVE, EventData.LIST, archiveListInfo);

        boardHistoryRepository.save(boardHistory);

        return list;
    }

    @Override
    public java.util.List<List> getArchivedList(Member member, Long boardId) {

        Board board = boardRepository.findByIdAndIsDeletedFalse(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "보드"));
        boardService.checkBoardMember(board, member, ListAction.ARCHIVE_LIST);

        java.util.List<List> archivedList = listRepository.findByBoardAndIsArchivedAndIsDeletedFalse(board, true);
        if (archivedList.isEmpty()) {
            return new ArrayList<>();
        }
        return archivedList;
    }

    @Override
    public void checkBoardMember(List list, Member member, Action action) {
        Board board = list.getBoard();
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
    @Transactional(readOnly = true)
    public java.util.List<ListSimpleResponseDto> getListsByBoardId(Long boardId) {
        Board board = boardRepository.findByIdAndIsDeletedFalse(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "보드"));

        return listRepository.findByBoardAndIsDeletedFalseOrderByMyOrderAsc(board).stream()
                .map(ListSimpleResponseDto::of)
                .toList();
    }
}
