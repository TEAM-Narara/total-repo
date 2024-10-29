package com.narara.superboard.workspace.interfaces;

import com.narara.superboard.board.interfaces.dto.websocket.BoardUpdateData;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketResponse;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceCreateData;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceDeleteData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Slf4j
@CrossOrigin
@RequiredArgsConstructor
@Controller
public class WorkspaceWebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/workspaces/create")
    @SendTo("/topic/member/1") //임시로 만든 거
    public WebSocketResponse createWorkspace(WorkSpaceCreateRequestDto dto) {
        //workspace 추가로직
        //workspace에 권한이 있는 사람에게 broadcast 메시지 보내기

        return new WebSocketResponse("WORKSPACE", "ADD_WORKSPACE", new WorkspaceCreateData(1L, "새워크"));
    }

    @MessageMapping("/workspace/{workspaceId}/delete")
    public void deleteWorkspace(@DestinationVariable Long workspaceId) {
        //삭제할 수 있는권한이 있는지 확인
        //workspace 밑에있는 보드들 조회

        // 워크스페이스 삭제 로직 수행

        //⇒ 모든 워크스페이스 멤버에게
        // 1. Member 구독자들에게 보낼 메시지
        WebSocketResponse memberResponse = new WebSocketResponse(
                "WORKSPACE",
                "DELETE_WORKSPACE",
                new WorkspaceDeleteData(workspaceId)
        );

        // 2. Board 구독자들에게 보낼 메시지
        Long boardId = 1L;
        WebSocketResponse boardResponse = new WebSocketResponse(
                "BOARD",
                "DELETE_BOARD",
                new BoardUpdateData(boardId)
        );

        // topic으로 브로드캐스트 메시지 전송
        messagingTemplate.convertAndSend("/topic/member/1", memberResponse);
        messagingTemplate.convertAndSend("/topic/board/" + boardId, boardResponse);
    }
}
