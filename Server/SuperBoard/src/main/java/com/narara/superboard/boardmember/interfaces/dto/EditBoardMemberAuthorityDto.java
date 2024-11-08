package com.narara.superboard.boardmember.interfaces.dto;

import com.narara.superboard.common.constant.enums.Authority;

public record EditBoardMemberAuthorityDto(Long memberId, Authority authority) {
}
