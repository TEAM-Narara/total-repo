package com.narara.superboard.workspace.interfaces.dto.websocket;

import com.narara.superboard.workspace.entity.mongo.WorkspaceOffset.DiffInfo;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceDiffDto {
    // private Long offset;
    private Long updatedAt;
    private String target;
    private String action;
    private Map<String, Object> data;

    public static WorkspaceDiffDto from(DiffInfo diffInfo) {
        return new WorkspaceDiffDto(
                // diffInfo.getOffset(),
                diffInfo.getUpdatedAt(),
                diffInfo.getTarget(),
                diffInfo.getAction(),
                diffInfo.getData()
        );
    }
}
