package com.narara.superboard.board.infrastructure;

import com.narara.superboard.board.interfaces.dto.BoardSearchDto;
import com.narara.superboard.workspace.interfaces.dto.MyBoardCollectionResponse.MyBoardWorkspaceCollectionDto;
import java.util.List;

public interface BoardSearchRepositoryCustom {
    List<MyBoardWorkspaceCollectionDto> searchBoardsAndWorkspaces(String keyword, Long memberId);
}
