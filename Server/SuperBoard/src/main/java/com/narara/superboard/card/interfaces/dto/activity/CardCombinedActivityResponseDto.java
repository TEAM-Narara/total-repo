package com.narara.superboard.card.interfaces.dto.activity;

import java.util.List;

public record CardCombinedActivityResponseDto(List<CardCombinedActivityDto> activityList,
                                         Long totalPages,
                                         Long totalElements) {
}
