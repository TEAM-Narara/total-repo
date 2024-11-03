package com.narara.superboard.workspace.service.mongo;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.websocket.enums.WorkspaceAction;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.entity.mongo.WorkspaceOffset;
import com.narara.superboard.workspace.entity.mongo.WorkspaceOffset.DiffInfo;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceDiffDto;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

//TODO: MongoDB tx 추가해주세요
@Slf4j
@RequiredArgsConstructor
@Service
public class WorkspaceOffsetService {
    public static final String WORKSPACE = "WORKSPACE";
    public static final String WORKSPACE_ID_COLUMN = "workspaceId";
    public static final String WORKSPACE_NAME_COLUMN = "workspaceName";
    public static final String MEMBER_ID_COLUMN = "memberId";
    public static final String MEMBER_NAME_COLUMN = "memberName";
    public static final String AUTHORITY_COLUMN = "authority";
    public static final String BOARD_ID_COLUMN = "boardId";
    public static final String BOARD_NAME_COLUMN = "boardName";
    private final MongoTemplate mongoTemplate;

    public void saveEditWorkspaceDiff(WorkSpace workspace) {
        WorkspaceOffset workspaceOffset = getWorkspaceOffset(workspace.getId());

        Map<String, Object> data = new HashMap<>();
        data.put(WORKSPACE_ID_COLUMN, workspace.getId());
        data.put(WORKSPACE_NAME_COLUMN, workspace.getName());

        DiffInfo diffInfo = new DiffInfo(
                workspace.getOffset(),
                workspace.getUpdatedAt(),
                WORKSPACE,
                WorkspaceAction.EDIT_WORKSPACE.name(),
                data
        );

        if (workspaceOffset == null) {
            workspaceOffset = new WorkspaceOffset(workspace.getId(), new ArrayList<>());
        }

        workspaceOffset.getDiffList().add(diffInfo);

        mongoTemplate.save(workspaceOffset);
    }

    private WorkspaceOffset getWorkspaceOffset(Long workspace) {
        Query query = new Query(Criteria.where("workspaceId").is(workspace));
        WorkspaceOffset workspaceOffset = mongoTemplate.findOne(query, WorkspaceOffset.class);
        return workspaceOffset;
    }

    //특정 offset 이후 변경사항 불러오는 로직
    public List<WorkspaceDiffDto> getDiffListFromOffset(Long workspaceId, Long fromOffset) {
        WorkspaceOffset workspaceOffset = getWorkspaceOffset(workspaceId);

        if (workspaceOffset == null || workspaceOffset.getDiffList() == null) {
            log.debug("No diff list found for workspace: {}", workspaceId);
            return Collections.emptyList();
        }

        return workspaceOffset.getDiffList().stream()
                .filter(diffInfo -> diffInfo.getOffset() >= fromOffset)
                .sorted(Comparator.comparing(DiffInfo::getOffset))
                .map(WorkspaceDiffDto::from)
                .collect(Collectors.toList());
    }

    public void saveDeleteWorkspaceDiff(WorkSpace workspace) {
        WorkspaceOffset workspaceOffset = getWorkspaceOffset(workspace.getId());

        Map<String, Object> data = new HashMap<>();
        data.put(WORKSPACE_ID_COLUMN, workspace.getId());

        DiffInfo diffInfo = new DiffInfo(
                workspace.getOffset(),
                workspace.getUpdatedAt(),
                WORKSPACE,
                WorkspaceAction.DELETE_WORKSPACE.name(),
                data
        );

        if (workspaceOffset == null) {
            workspaceOffset = new WorkspaceOffset(workspace.getId(), new ArrayList<>());
        }

        workspaceOffset.getDiffList().add(diffInfo);

        mongoTemplate.save(workspaceOffset);
    }

    public void saveAddMemberDiff(WorkSpaceMember workspaceMember) {
        WorkSpace workspace = workspaceMember.getWorkSpace();
        WorkspaceOffset workspaceOffset = getWorkspaceOffset(workspace.getId());

        Map<String, Object> data = new HashMap<>();
        data.put(WORKSPACE_ID_COLUMN, workspace.getId());
        data.put(MEMBER_ID_COLUMN, workspace.getId());
        data.put(MEMBER_NAME_COLUMN, workspace.getId());
        data.put(AUTHORITY_COLUMN, workspace.getName());

        DiffInfo diffInfo = new DiffInfo(
                workspace.getOffset(),
                workspace.getUpdatedAt(),
                WORKSPACE,
                WorkspaceAction.ADD_MEMBER.name(),
                data
        );

        if (workspaceOffset == null) {
            workspaceOffset = new WorkspaceOffset(workspace.getId(), new ArrayList<>());
        }

        workspaceOffset.getDiffList().add(diffInfo);

        mongoTemplate.save(workspaceOffset);
    }

    public void saveEditMemberDiff(WorkSpaceMember workspaceMember) {
        WorkSpace workspace = workspaceMember.getWorkSpace();
        WorkspaceOffset workspaceOffset = getWorkspaceOffset(workspace.getId());

        Map<String, Object> data = new HashMap<>();
        data.put(WORKSPACE_ID_COLUMN, workspace.getId());
        data.put(MEMBER_ID_COLUMN, workspace.getId());
        data.put(AUTHORITY_COLUMN, workspace.getName());

        DiffInfo diffInfo = new DiffInfo(
                workspace.getOffset(),
                workspace.getUpdatedAt(),
                WORKSPACE,
                WorkspaceAction.EDIT_MEMBER.name(),
                data
        );

        if (workspaceOffset == null) {
            workspaceOffset = new WorkspaceOffset(workspace.getId(), new ArrayList<>());
        }

        workspaceOffset.getDiffList().add(diffInfo);

        mongoTemplate.save(workspaceOffset);
    }

    public void saveDeleteMemberDiff(WorkSpaceMember workspaceMember) {
        WorkSpace workspace = workspaceMember.getWorkSpace();
        WorkspaceOffset workspaceOffset = getWorkspaceOffset(workspace.getId());

        Map<String, Object> data = new HashMap<>();
        data.put(WORKSPACE_ID_COLUMN, workspace.getId());
        data.put(MEMBER_ID_COLUMN, workspace.getId());

        DiffInfo diffInfo = new DiffInfo(
                workspace.getOffset(),
                workspace.getUpdatedAt(),
                WORKSPACE,
                WorkspaceAction.DELETE_MEMBER.name(),
                data
        );

        if (workspaceOffset == null) {
            workspaceOffset = new WorkspaceOffset(workspace.getId(), new ArrayList<>());
        }

        workspaceOffset.getDiffList().add(diffInfo);

        mongoTemplate.save(workspaceOffset);
    }

    public void saveAddBoardDiff(Board board) {
        WorkSpace workspace = board.getWorkSpace();
        WorkspaceOffset workspaceOffset = getWorkspaceOffset(workspace.getId());

        Map<String, Object> data = new HashMap<>();
        data.put(WORKSPACE_ID_COLUMN, workspace.getId());
        data.put(BOARD_ID_COLUMN, board.getId());
        data.put(BOARD_NAME_COLUMN, board.getName());
        data.put("backgroundType", board.getCover().get("type"));
        data.put("backgroundValue", board.getCover().get("value"));
        data.put("isClosed", board.getIsArchived());

        DiffInfo diffInfo = new DiffInfo(
                workspace.getOffset(),
                workspace.getUpdatedAt(),
                WORKSPACE,
                WorkspaceAction.ADD_BOARD.name(),
                data
        );

        if (workspaceOffset == null) {
            workspaceOffset = new WorkspaceOffset(workspace.getId(), new ArrayList<>());
        }

        workspaceOffset.getDiffList().add(diffInfo);

        mongoTemplate.save(workspaceOffset);
    }
}
