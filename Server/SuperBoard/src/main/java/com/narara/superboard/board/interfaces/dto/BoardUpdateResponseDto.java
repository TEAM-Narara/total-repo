package com.narara.superboard.board.interfaces.dto;

import java.util.List;
import java.util.Map;

public record BoardUpdateResponseDto(
        Long id, // 보드 ID
        String name, // 보드 이름
        Map<String, Object> background, // 배경 (JSON)
        String visibility // 가시성
//        List<Long> memberIds // 수정된 보드 멤버 리스트 (멤버 ID 목록)
) {
}
