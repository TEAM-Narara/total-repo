package com.narara.superboard.workSpace.interfaces.dto;

import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberCollectionResponseDto;
import lombok.Builder;

@Builder
public record WorkSpaceDetailResponseDto(
        Long workSpaceId,
        String name,
        BoardCollectionResponseDto boardList,
        WorkspaceMemberCollectionResponseDto workspaceMemberList
) implements WorkSpaceNameHolder {
}
