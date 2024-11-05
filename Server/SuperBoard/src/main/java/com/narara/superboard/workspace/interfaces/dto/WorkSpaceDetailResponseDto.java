package com.narara.superboard.workspace.interfaces.dto;

import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;
import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import lombok.Builder;

@Builder
public record WorkSpaceDetailResponseDto(
        Long workSpaceId,
        String name,
        BoardCollectionResponseDto boardList,
        MemberCollectionResponseDto workspaceMemberList
) implements WorkSpaceNameHolder {
}
