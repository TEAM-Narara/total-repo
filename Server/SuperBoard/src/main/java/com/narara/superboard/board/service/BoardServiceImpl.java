package com.narara.superboard.board.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.infrastrucuture.BoardRepository;
import com.narara.superboard.board.interfaces.dto.*;
import com.narara.superboard.board.service.validator.BoardValidator;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.application.validator.CoverValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardValidator boardValidator;
    private final CoverValidator coverValidator;
    private final NameValidator nameValidator;

    private final CoverHandler coverHandler;

    @Override
    public BoardCollectionResponseDto getBoardCollectionResponseDto(Long workSpaceId) {
        List<Board> BoardList = boardRepository.findAllByWorkSpaceId(workSpaceId);

        List<BoardDetailResponseDto> boardDetailResponseDtoList = new ArrayList<>();

        for (Board board : BoardList) {
            BoardDetailResponseDto boardDto = BoardDetailResponseDto.builder()
                    .id(board.getId())
                    .name(board.getName())
                    .backgroundType(coverHandler.getTypeValue(board.getCover()))
                    .backgroundValue(coverHandler.getValue(board.getCover()))
                    .build();

            boardDetailResponseDtoList.add(boardDto);
        }

        return new BoardCollectionResponseDto(boardDetailResponseDtoList);
    }

    @Override
    public Long createBoard(BoardCreateRequestDto boardCreateRequestDto) {
        boardValidator.validateNameIsPresent(boardCreateRequestDto);
        boardValidator.validateVisibilityIsValid(boardCreateRequestDto);
        boardValidator.validateVisibilityIsPresent(boardCreateRequestDto);

        Board board = Board.builder()
                .cover(boardCreateRequestDto.background())
                .name(boardCreateRequestDto.name())
                .visibility(Visibility.fromString(boardCreateRequestDto.visibility()))
                .build();

        Board saveBoard = boardRepository.save(board);

        return saveBoard.getId();
    }

    @Override
    public Board getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundEntityException(boardId, "Board"));
    }

    @Override
    public void deleteBoard(Long boardId) {
        Board board = getBoard(boardId);
        boardRepository.delete(board);
    }

    @Override
    public Board updateBoard(Long boardId, BoardUpdateRequestDto boardUpdateRequestDto) {
        boardValidator.validateNameIsPresent(boardUpdateRequestDto);
        boardValidator.validateVisibilityIsPresent(boardUpdateRequestDto);
        boardValidator.validateVisibilityIsValid(boardUpdateRequestDto);

        if (boardUpdateRequestDto.cover() != null) {
            coverValidator.validateContainCover(boardUpdateRequestDto);
        }

        Board board = getBoard(boardId);

        return board.updateBoardByAdmin(boardUpdateRequestDto);
    }

    @Override
    public Board updateBoardByMember(Long boardId, BoardUpdateByMemberRequestDto boardUpdateByMemberRequestDto) {
        nameValidator.validateNameIsEmpty(boardUpdateByMemberRequestDto);

        if (boardUpdateByMemberRequestDto.cover() != null) {
            coverValidator.validateContainCover(boardUpdateByMemberRequestDto);
        }

        Board board = getBoard(boardId);

        return board.updateBoardByMember(boardUpdateByMemberRequestDto);
    }

    // 아카이브된 보드 리스트 조회
    @Override
    public List<Board> getArchivedBoards(Long workspaceId) {
        return boardRepository.findAllByWorkSpaceIdAndIsArchivedTrue(workspaceId);
    }

    // 보드 아카이브 상태 변경
    @Override
    public void updateArchiveStatus(Long boardId, boolean isArchived) {
        Board board = getBoard(boardId);
        board.changeArchiveStatus(isArchived);
    }

}
