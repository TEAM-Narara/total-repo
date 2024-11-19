package com.narara.superboard.workspace.interfaces.dto;

import com.narara.superboard.board.interfaces.dto.BoardDetailResponseDto;
import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import java.util.List;
import lombok.Builder;

@Builder
public record WorkSpaceDetailResponseDto(
        Long workspaceId,
        String name,
        List<BoardDetailResponseDto> boardList,
        MemberCollectionResponseDto workspaceMemberList
) implements WorkSpaceNameHolder {
}
