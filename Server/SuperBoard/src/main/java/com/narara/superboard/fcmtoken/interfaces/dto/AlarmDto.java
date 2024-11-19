package com.narara.superboard.fcmtoken.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AlarmDto {
    private String toMemberId;
    private String title; //주효림님이 당신을 날아라 워크스페이스에 admin으로 추가하였습니다
    private String body;
    private String type;
    private String goTo; //HOME, WORKSPACE, BOARD, CARD
    private String manOfActionId;
    private String workspaceId;
    private String boardId;
    private String listId;
    private String cardId;
    private String time;
}
