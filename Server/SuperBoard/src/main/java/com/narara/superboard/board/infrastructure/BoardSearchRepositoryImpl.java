package com.narara.superboard.board.infrastructure;

import static com.narara.superboard.board.entity.QBoard.board;
import static com.narara.superboard.boardmember.entity.QBoardMember.boardMember;
import static com.narara.superboard.workspace.entity.QWorkSpace.workSpace;
import static com.narara.superboard.workspacemember.entity.QWorkSpaceMember.workSpaceMember;

import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.workspace.interfaces.dto.MyBoardCollectionResponse.MyBoardCollectionDto;
import com.narara.superboard.workspace.interfaces.dto.MyBoardCollectionResponse.MyBoardWorkspaceCollectionDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
public class BoardSearchRepositoryImpl implements BoardSearchRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MyBoardWorkspaceCollectionDto> searchBoardsAndWorkspaces(String keyword, Long memberId) {
        List<Tuple> results = queryFactory
                .select(
                        workSpace.id,
                        workSpace.name,
                        board.id,
                        board.name,
                        board.cover,
                        boardMember.isAlert,
                        board.offset
                )
                .from(workSpace)
                .leftJoin(board).on(board.workSpace.eq(workSpace))
                .leftJoin(workSpaceMember).on(workSpaceMember.workSpace.eq(workSpace))
                .leftJoin(boardMember).on(boardMember.board.eq(board)
                        .and(boardMember.member.id.eq(memberId)))
                .where(
                        workSpaceMember.member.id.eq(memberId)
                                .and(workSpaceMember.isDeleted.isFalse())
                                .and(workSpace.isDeleted.isFalse())
                                .and(board.isDeleted.isFalse())
                                .and(
                                        board.visibility.eq(Visibility.WORKSPACE)
                                                .or(boardMember.member.id.eq(memberId)
                                                        .and(board.visibility.eq(Visibility.PRIVATE)))
                                )
                                .and(containsKeyword(keyword)) // 키워드 검색 조건을 AND로 변경
                )
                .orderBy(workSpace.id.asc())
                .fetch();

        // 결과를 그룹화하여 DTO 구조로 변환
        Map<Long, MyBoardWorkspaceCollectionDto> workspaceMap = new HashMap<>();

        for (Tuple tuple : results) {
            Long workspaceId = tuple.get(workSpace.id);
            String workspaceName = tuple.get(workSpace.name);

            workspaceMap.computeIfAbsent(workspaceId, k -> new MyBoardWorkspaceCollectionDto(
                    workspaceId,
                    workspaceName,
                    new ArrayList<>()
            ));

            // board 정보가 있는 경우에만 추가
            if (tuple.get(board.id) != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> cover = tuple.get(board.cover);

                MyBoardCollectionDto boardDto = new MyBoardCollectionDto(
                        tuple.get(board.id),
                        tuple.get(board.name),
                        cover != null ? (String) cover.get("type") : null,
                        cover != null ? (String) cover.get("value") : null,
                        tuple.get(boardMember.isAlert),
                        tuple.get(board.offset)
                );

                workspaceMap.get(workspaceId).getBoardCollection().add(boardDto);
            }
        }

        return new ArrayList<>(workspaceMap.values());
    }

    private BooleanExpression containsKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return workSpace.name.containsIgnoreCase(keyword)
                .or(board.name.containsIgnoreCase(keyword));
    }
}
