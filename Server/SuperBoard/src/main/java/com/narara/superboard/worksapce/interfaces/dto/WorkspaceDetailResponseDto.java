package com.narara.superboard.worksapce.interfaces.dto;

import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberCollectionResponseDto;
import lombok.Builder;

@Builder
public record WorkspaceDetailResponseDto(
        Long workSpaceId,
        String name,
        BoardCollectionResponseDto boardList,
        WorkspaceMemberCollectionResponseDto workspaceMemberList
) implements WorkspaceNameHolder {
}
