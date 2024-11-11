package com.narara.superboard.board.interfaces.dto;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.interfaces.dto.CoverDto;
import lombok.Builder;

@Builder
public record BoardDetailResponseDto(
        Long id,
        Long workspaceId,
        String name,
        CoverDto cover,
        String visibility,
        Boolean isClosed
) {
    public static BoardDetailResponseDto of(Board board, CoverHandler coverHandler) {
        return BoardDetailResponseDto.builder()
                .id(board.getId())
                .workspaceId(board.getWorkSpace().getId())
                .name(board.getName())
                .cover(new CoverDto(coverHandler.getTypeValue(board.getCover()), coverHandler.getValue(board.getCover())))
                .visibility(board.getVisibility().name())
                .isClosed(board.getIsArchived())
                .build();
    }

    //coverHandler 없는 버전 TODO 개선
    public static BoardDetailResponseDto of(Board board) {
        return BoardDetailResponseDto.builder()
                .id(board.getId())
                .workspaceId(board.getWorkSpace().getId())
                .name(board.getName())
                .cover(new CoverDto((String)board.getCover().get("type"), (String)board.getCover().get("value")))
                .visibility(board.getVisibility().name())
                .isClosed(board.getIsArchived())
                .build();
    }
}
