package com.narara.superboard.board.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.CoverHolder;

import java.util.Map;

public record BoardUpdateRequestDto(
        String name, // 보드 이름
        Map<String, Object> background, // 배경 (JSON: type과 value)
        String visibility // 가시성 (Workspace, Private)
) implements BoardCoreHolder, CoverHolder {

    @Override
    public Map<String, Object> cover() {
        return background;
    }

}
