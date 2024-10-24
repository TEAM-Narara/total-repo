package com.narara.superboard.board.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.CoverHolder;
import com.narara.superboard.common.interfaces.dto.NameHolder;

import java.util.Map;

public record BoardUpdateByMemberRequestDto(
        String boardName,
        Map<String, Object> background // 배경 (JSON: type과 value)
) implements CoverHolder, NameHolder {

    @Override
    public Map<String, Object> cover() {
        return background;
    }

    @Override
    public String name() {
        return boardName;
    }
}
