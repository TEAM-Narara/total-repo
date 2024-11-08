package com.narara.superboard.board.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.CoverDto;
import com.narara.superboard.common.interfaces.dto.CoverHolder;
import com.narara.superboard.common.interfaces.dto.NameHolder;

public record BoardUpdateResponseDto(
        Long id, // 보드 ID
        String boardName, // 보드 이름
        CoverDto background, // 배경 (JSON: type과 value)
        String visibility // 가시성
) implements CoverHolder, NameHolder {
    @Override
    public CoverDto cover() {
        return background;
    }

    @Override
    public String name() {
        return boardName;
    }
}
