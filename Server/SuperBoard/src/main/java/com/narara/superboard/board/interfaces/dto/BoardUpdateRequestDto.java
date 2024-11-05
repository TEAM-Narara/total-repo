package com.narara.superboard.board.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.CoverHolder;

public record BoardUpdateRequestDto(
        String name, // 보드 이름
        BoardBackgroundDto background, // 배경 (JSON: type과 value)
        String visibility // 가시성 (Workspace, Private)
) implements BoardCoreHolder, CoverHolder {

    @Override
    public BoardBackgroundDto cover() {
        return background;
    }

}
