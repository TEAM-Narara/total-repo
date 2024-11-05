package com.narara.superboard.board.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.CoverHolder;
import com.narara.superboard.common.interfaces.dto.NameHolder;

public record BoardUpdateByMemberRequestDto(
        String boardName,
        BoardBackgroundDto background // 배경 (JSON: type과 value)
) implements CoverHolder, NameHolder {

    @Override
    public BoardBackgroundDto cover() {
        return background;
    }

    @Override
    public String name() {
        return boardName;
    }
}
