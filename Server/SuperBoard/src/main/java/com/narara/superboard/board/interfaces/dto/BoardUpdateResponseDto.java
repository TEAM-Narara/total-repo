package com.narara.superboard.board.interfaces.dto;

import java.util.List;
import java.util.Map;

public record BoardUpdateResponseDto(
        Long id, // 보드 ID
        String name, // 보드 이름
        Map<String, Object> background, // 배경 (JSON)
        String visibility // 가시성
) {
}
