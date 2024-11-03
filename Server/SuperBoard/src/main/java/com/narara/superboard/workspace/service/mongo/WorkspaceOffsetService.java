package com.narara.superboard.workspace.service.mongo;

import com.narara.superboard.websocket.enums.WorkspaceAction;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.entity.mongo.WorkspaceOffset;
import com.narara.superboard.workspace.entity.mongo.WorkspaceOffset.DiffInfo;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceDiffDto;
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
    private final MongoTemplate mongoTemplate;

    public void saveEditWorkspaceOffset(WorkSpace workspace) {
        Query query = new Query(Criteria.where("workspaceId").is(workspace.getId()));
        WorkspaceOffset workspaceOffset = mongoTemplate.findOne(query, WorkspaceOffset.class);

        Map<String, Object> data = new HashMap<>();
        data.put("workspaceId", workspace.getId());
        data.put("workspaceName", workspace.getName());

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

        WorkspaceOffset saved = mongoTemplate.save(workspaceOffset);
    }

    public List<WorkspaceDiffDto> getDiffListFromOffset(Long workspaceId, Long fromOffset) {
        Query query = new Query(Criteria.where("workspaceId").is(workspaceId));
        WorkspaceOffset workspaceOffset = mongoTemplate.findOne(query, WorkspaceOffset.class);

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
}
