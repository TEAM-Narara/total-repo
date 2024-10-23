package com.narara.superboard.board.interfaces.dto;

import java.util.List;
import java.util.Map;

public record BoardUpdateRequestDto(
        String name, // 보드 이름
        Map<String, Object> background, // 배경 (JSON: type과 value)
        String visibility // 가시성 (Workspace, Private)
//        List<Long> memberIds // 보드 멤버 리스트 (멤버 ID 목록)
) {
}
