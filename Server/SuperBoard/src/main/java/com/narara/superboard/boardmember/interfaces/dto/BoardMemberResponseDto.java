package com.narara.superboard.boardmember.interfaces.dto;

import java.util.List;

public record BoardMemberResponseDto(List<MemberResponseDto> workspaceMembers, List<MemberResponseDto> boardMembers) {
}
