package com.narara.superboard.card.interfaces.dto;

import java.time.LocalDateTime;

public record CardCombinedLogResponseDto(Object activity,
                                         LocalDateTime timestamp,
                                         Long totalPages,
                                         Long totalElements) implements Comparable<CardCombinedLogResponseDto> {
    @Override
    public int compareTo(CardCombinedLogResponseDto other) {
        return other.timestamp.compareTo(this.timestamp); // 최신순 정렬
    }
}