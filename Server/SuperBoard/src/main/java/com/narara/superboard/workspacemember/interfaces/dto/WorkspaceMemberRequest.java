package com.narara.superboard.workspacemember.interfaces.dto;

import com.narara.superboard.common.constant.enums.Authority;

public record WorkspaceMemberRequest(Long memberId, Authority authority) {
}
