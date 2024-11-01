package com.narara.superboard.workspace.interfaces.dto.websocket;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DeleteWorkspaceDto {
    private Long workspaceId;
    private List<DeleteBoardDto> deleteBoardDtoList;
}
