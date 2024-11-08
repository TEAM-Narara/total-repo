package com.narara.superboard.card.interfaces.dto;

public record CardCombinedLogDto(
        Object activity,
        Long when
) implements Comparable<CardCombinedLogDto> {

    @Override
    public int compareTo(CardCombinedLogDto other) {
        return other.when.compareTo(this.when); // 최신순 정렬
    }
}
