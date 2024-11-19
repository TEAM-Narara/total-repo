package com.narara.superboard.workspace.interfaces.dto.websocket;

import com.narara.superboard.common.constant.enums.Authority;

public record AddWorkspaceMemberDto(Long memberId, Authority authority) {
}
