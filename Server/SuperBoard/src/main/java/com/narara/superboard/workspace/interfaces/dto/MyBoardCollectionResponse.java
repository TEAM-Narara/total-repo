package com.narara.superboard.workspace.interfaces.dto;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.workspace.entity.WorkSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyBoardCollectionResponse {
    private List<MyBoardWorkspaceCollectionDto> workspaceCollection;

    public static MyBoardCollectionResponse of(List<BoardMember> boardMemberList) {
        //boardMemberList는 workspace id순으로 정렬되어 보내져야한다
        List<MyBoardWorkspaceCollectionDto> myBoardWorkspaceCollectionDtos = new ArrayList<>();

        Map<WorkSpace, List<BoardMember>> map = new HashMap<>();

        for (BoardMember boardMember: boardMemberList) {
            WorkSpace workSpace = boardMember.getBoard().getWorkSpace();
            List<BoardMember> tmpList = map.getOrDefault(workSpace, new ArrayList<>());
            tmpList.add(boardMember);
            map.put(workSpace, tmpList);
        }

        for (WorkSpace workspace: map.keySet()) {
            List<MyBoardCollectionDto> boardCollectionDtoList = new ArrayList<>();

            for (BoardMember boardMember: map.get(workspace)) {
                Board board = boardMember.getBoard();

                boardCollectionDtoList.add(new MyBoardCollectionDto(
                        board.getId(),
                        board.getName(),
                        (String)board.getCover().get("type"),
                        (String)board.getCover().get("value"),
                        boardMember.isAlert(),
                        board.getOffset()
                ));
            }

            myBoardWorkspaceCollectionDtos.add(new MyBoardWorkspaceCollectionDto(
                    workspace.getId(),
                    workspace.getName(),
                    boardCollectionDtoList
            ));
        }

        return new MyBoardCollectionResponse(myBoardWorkspaceCollectionDtos);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyBoardWorkspaceCollectionDto {
        private Long workspaceId;
        private String workspaceName;
        private List<MyBoardCollectionDto> boardCollection;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyBoardCollectionDto {
        private Long boardId;
        private String boardName;
        private String backgroundType;
        private String backgroundValue;
        private boolean isBoardWatch;
        private Long boardVersion;
    }
}
