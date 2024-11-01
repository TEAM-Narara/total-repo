package com.narara.superboard.member.interfaces.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchMemberListResponseDto(List<SearchMemberResponseDto> searchMemberResponseDtoList,
                                          Integer totalPages, Long totalElements) {
}
