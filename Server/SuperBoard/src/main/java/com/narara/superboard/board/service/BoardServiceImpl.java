package com.narara.superboard.board.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastrucutre.BoardRepository;
import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;
import com.narara.superboard.board.interfaces.dto.BoardDetailResponseDto;
import com.narara.superboard.common.application.handler.CoverHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CoverHandler coverHandler;

    @Override
    public BoardCollectionResponseDto getBoardCollectionResponseDto(Long workSpaceId) {
        List<Board> BoardList = boardRepository.findAllByWorkSpaceId(workSpaceId);

        List<BoardDetailResponseDto> boardDetailResponseDtoList= new ArrayList<>();

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


}
