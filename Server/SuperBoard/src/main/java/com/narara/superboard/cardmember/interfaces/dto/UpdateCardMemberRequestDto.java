package com.narara.superboard.cardmember.interfaces.dto;

import java.util.List;

public record UpdateCardMemberRequestDto(Long cardId, Long memberId, Boolean is_alert) {
}
