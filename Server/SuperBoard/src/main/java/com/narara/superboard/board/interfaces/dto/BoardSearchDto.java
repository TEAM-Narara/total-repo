package com.narara.superboard.board.interfaces.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardSearchDto {
    private Long boardId;
    private String boardName;
    private Long workspaceId;
    private String workspaceName;
}
