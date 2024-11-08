package com.narara.superboard.board.interfaces.dto;

public record BoardCombinedLogDto(
        Object activity,
        Long when
) implements Comparable<BoardCombinedLogDto> {

    @Override
    public int compareTo(BoardCombinedLogDto other) {
        return other.when.compareTo(this.when); // 최신순 정렬
    }
}
