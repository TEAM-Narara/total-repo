package com.narara.superboard.fcmtoken.entity;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "alarms")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Alarm {
    @Id
    private String id;

    @Indexed  // toMemberId로 조회가 빈번하므로 인덱스 추가
    private String toMemberId;
    private String title;
    private String body;
    private String type;
    private String goTo;
    private String manOfActionId;
    private String workspaceId;
    private String boardId;
    private String listId;
    private String cardId;

    @Indexed
    private LocalDateTime createdAt;

    @Builder
    public Alarm(String toMemberId, String title, String body, String type,
                 String goTo, String manOfActionId, String workspaceId,
                 String boardId, String listId, String cardId) {
        this.toMemberId = toMemberId;
        this.title = title;
        this.body = body;
        this.type = type;
        this.goTo = goTo;
        this.manOfActionId = manOfActionId;
        this.workspaceId = workspaceId;
        this.boardId = boardId;
        this.listId = listId;
        this.cardId = cardId;
        this.createdAt = LocalDateTime.now();
    }
}
