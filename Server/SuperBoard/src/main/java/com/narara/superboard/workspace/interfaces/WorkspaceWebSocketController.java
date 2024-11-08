package com.narara.superboard.workspace.interfaces;

import com.narara.superboard.board.enums.BoardAction;
import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.interfaces.dto.websocket.BoardUpdateData;
import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.common.interfaces.dto.AckMessage;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.websocket.enums.WorkspaceAction;
import com.narara.superboard.websocket.interfaces.dto.websocket.BoardAddMemberDto;
import com.narara.superboard.websocket.interfaces.dto.websocket.BoardMemberAddResponse;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.AddWorkspaceMemberDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.DeleteBoardDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.DeleteWorkspaceDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketResponse;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkSpaceAddMemberDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceDeleteResponse;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceMemberAddResponse;
import com.narara.superboard.workspace.service.WorkSpaceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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

//    @MessageMapping("/test")
//    public void testHandler(@Payload  AckMessage ackMessage) {
//        System.out.println(ackMessage);
//    }

    //워크스페이스 추가(웹소켓 버전, http도 있음)
    @MessageMapping("/workspaces/create")
    public void createWorkspace(WorkSpaceCreateRequestDto dto) {
//        workspaceService.createWorkSpace(dto);

        //TODO 서비스로직에서 받아온 워크스페이스여야함
        WorkSpace workSpace = new WorkSpace(
                1L, "나의 워크스페이스", 1L
        );

        //workspace에 권한이 있는 사람에게 broadcast 메시지 보내기
//        return new WebSocketResponse(
//                WORKSPACE,
//                WorkspaceAction.ADD_WORKSPACE.toString(),
//                new WorkspaceCreateData(1L, "새워크")
//        );
    }

    //워크스페이스 삭제
    @MessageMapping("/workspace/{workspaceId}/delete")
    public void deleteWorkspace(@DestinationVariable Long workspaceId) {
        //삭제할 수 있는권한이 있는지 확인, workspace 밑에있는 보드들 조회, 삭제
//        workspaceService.deleteWorkSpace(workspaceId); //워크스페이스 삭제 로직 수행

        //TODO service 로직에서 받아와야함
        DeleteWorkspaceDto deleteWorkspaceDto = new DeleteWorkspaceDto();

        // workspace 브로드캐스트
        WorkspaceDeleteResponse workspaceDeleteResponse = new WorkspaceDeleteResponse(
                deleteWorkspaceDto.getWorkspaceId()
        );
        messagingTemplate.convertAndSend("/topic/workspace/" + workspaceId, workspaceDeleteResponse);

        // board 브로드캐스트
        for (DeleteBoardDto deleteBoardDto : deleteWorkspaceDto.getDeleteBoardDtoList()) {
            WebSocketResponse boardResponse = new WebSocketResponse(
                    BOARD,
                    BoardAction.DELETE_BOARD.toString(),
                    new BoardUpdateData(deleteBoardDto.getBoardId())
            );
            messagingTemplate.convertAndSend("/topic/board/" + deleteBoardDto.getBoardId(), boardResponse);
        }
    }

    //워크스페이스 멤버추가
    @MessageMapping("/workspace/{workspaceId}/member/add")
    public void addWorkspaceMember(@DestinationVariable Long workspaceId, AddWorkspaceMemberDto requestDto) {
        //TODO 서비스로직
//        workspaceService.addWorkspaceMember(requestDto); //워크스페이스 멤버 추가

        //TODO 서비스로직에서 받아오는 데이터여야함
        WorkSpaceAddMemberDto workSpaceAddMemberDto = new WorkSpaceAddMemberDto(
                1L,
                1L,
                Authority.ADMIN,
                new Member(1L, "마루", "asdf@gmail.com", "http"),
                List.of(new BoardAddMemberDto(1L, 1L, 1L, "주효림", Authority.ADMIN, Visibility.WORKSPACE))
                //워크스페이스에 포함된 board
        );

        Member member = workSpaceAddMemberDto.getMember();

        // workspace 브로드캐스트
        WebSocketResponse memberResponse = new WebSocketResponse(
                WORKSPACE,
                WorkspaceAction.ADD_MEMBER.toString(),
                new WorkspaceMemberAddResponse(
                        workspaceId,
                        member.getId(),
                        member.getNickname(),
                        member.getEmail(),
                        member.getProfileImgUrl(),
                        workSpaceAddMemberDto.getAuthority(),
                        1L
                )
        );

        messagingTemplate.convertAndSend("/topic/workspaces/" + workspaceId, memberResponse);

        // board 브로드캐스트
        for (BoardAddMemberDto boardAddMemberDto : workSpaceAddMemberDto.getBoardAddMemberDtoList()) {
            if (boardAddMemberDto.getVisibility() != Visibility.WORKSPACE) {
                continue;
            }

            WebSocketResponse boardResponse = new WebSocketResponse(
                    BOARD,
                    BoardAction.ADD_MEMBER.toString(),
                    new BoardMemberAddResponse(
                            boardAddMemberDto.getBoardId(),
                            boardAddMemberDto.getMemberId(),
                            boardAddMemberDto.getMemberName(),
                            boardAddMemberDto.getAuthority(),
                            boardAddMemberDto.getBoardOffset()
                    )
            );

            messagingTemplate.convertAndSend("/topic/boards/" + boardAddMemberDto.getBoardId(), boardResponse);
        }
    }
}
