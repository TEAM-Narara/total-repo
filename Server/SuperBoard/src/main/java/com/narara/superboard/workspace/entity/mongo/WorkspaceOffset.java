package com.narara.superboard.workspace.entity.mongo;

import jakarta.persistence.Id;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

//TODO: kafka로 변경시 삭제
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Document(collection = "workspaceOffsetCollection")
public class WorkspaceOffset {
    @Id
    private ObjectId id;
    private Long workspaceId;
    private List<DiffInfo> diffList;
//    private Integer lastOffset;

    public WorkspaceOffset(Long workspaceId, List<DiffInfo> diffList) {
        this.workspaceId = workspaceId;
        this.diffList = diffList;
    }

    @Data
    @AllArgsConstructor
    public static class DiffInfo {
        private Long offset;
        private Long updatedAt;
        private String target;
        private String action;
        private Map<String, Object> data;
    }
}