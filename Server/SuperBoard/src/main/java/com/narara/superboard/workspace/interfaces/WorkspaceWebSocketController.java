package com.narara.superboard.workspace.interfaces;

import com.narara.superboard.board.enums.BoardAction;
import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.interfaces.dto.websocket.BoardUpdateData;
import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.websocket.enums.WorkspaceAction;
import com.narara.superboard.websocket.interfaces.dto.websocket.BoardAddMemberDto;
import com.narara.superboard.websocket.interfaces.dto.websocket.BoardMemberAddResponse;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.AddWorkspaceMemberDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketResponse;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkSpaceAddMemberDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceCreateData;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceDeleteData;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceMemberAddResponse;
import com.narara.superboard.workspace.service.WorkSpaceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Slf4j
@CrossOrigin
@RequiredArgsConstructor
@Controller
public class WorkspaceWebSocketController {
    private static final String BOARD = "BOARD";
    private static final String WORKSPACE = "WORKSPACE";

    private final SimpMessagingTemplate messagingTemplate;
    private final WorkSpaceService workspaceService;
    private final BoardService boardService;

    @MessageMapping("/workspaces/create")
    public WebSocketResponse createWorkspace(WorkSpaceCreateRequestDto dto) {
        //workspace 추가로직
        //workspace에 권한이 있는 사람에게 broadcast 메시지 보내기

        return new WebSocketResponse(
                WORKSPACE,
                WorkspaceAction.ADD_WORKSPACE.toString(),
                new WorkspaceCreateData(1L, "새워크")
        );
    }

    @MessageMapping("/workspace/{workspaceId}/delete")
    public void deleteWorkspace(@DestinationVariable Long workspaceId) {
        //삭제할 수 있는권한이 있는지 확인
        //workspace 밑에있는 보드들 조회

        // 워크스페이스 삭제 로직 수행

        //⇒ 모든 워크스페이스 멤버에게
        // 1. Member 구독자들에게 보낼 메시지
        WebSocketResponse memberResponse = new WebSocketResponse(
                WORKSPACE,
                WorkspaceAction.DELETE_WORKSPACE.toString(),
                new WorkspaceDeleteData(workspaceId)
        );

        // 2. Board 구독자들에게 보낼 메시지
        Long boardId = 1L;
        WebSocketResponse boardResponse = new WebSocketResponse(
                BOARD,
                BoardAction.DELETE_BOARD.toString(),
                new BoardUpdateData(boardId)
        );

        // topic으로 브로드캐스트 메시지 전송
        messagingTemplate.convertAndSend("/topic/member/1", memberResponse);
        messagingTemplate.convertAndSend("/topic/board/" + boardId, boardResponse);
    }

    //워크스페이스 멤버추가
    @MessageMapping("/workspace/{workspaceId}/member/add")
    public void addWorkspaceMember(@DestinationVariable Long workspaceId, AddWorkspaceMemberDto requestDto) {
        //TODO 서비스로직
//        workspaceService.addWorkspaceMember(requestDto); //워크스페이스 멤버 추가

        //TODO 서비스로직에서 받아오는 데이터여야함
        WorkSpaceAddMemberDto workspace = new WorkSpaceAddMemberDto(
                1L,
                1L,
                List.of(new BoardAddMemberDto(1L, 1L, Visibility.WORKSPACE))  //워크스페이스에 포함된 board
        );

        // workspace 브로드캐스트
        WebSocketResponse memberResponse = new WebSocketResponse(
                WORKSPACE,
                WorkspaceAction.ADD_MEMBER.toString(),
                new WorkspaceMemberAddResponse(workspaceId, 1L)
        );
        messagingTemplate.convertAndSend("/topic/workspaces/" + workspaceId, memberResponse);

        // board 브로드캐스트
        for (BoardAddMemberDto boardAddMemberDto : workspace.getBoardAddMemberDtoList()) {
            if (boardAddMemberDto.getVisibility() != Visibility.WORKSPACE) {
                continue;
            }

            WebSocketResponse boardResponse = new WebSocketResponse(
                    BOARD,
                    BoardAction.ADD_MEMBER.toString(),
                    new BoardMemberAddResponse(boardAddMemberDto.getBoardId(), boardAddMemberDto.getBoardOffset())
            );

            messagingTemplate.convertAndSend("/topic/boards/" + boardAddMemberDto.getBoardId(), boardResponse);
        }
    }
}
