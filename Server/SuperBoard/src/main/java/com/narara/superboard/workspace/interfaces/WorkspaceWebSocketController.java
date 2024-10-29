package com.narara.superboard.workspace.interfaces;

import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketResponse;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceCreateData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
}
