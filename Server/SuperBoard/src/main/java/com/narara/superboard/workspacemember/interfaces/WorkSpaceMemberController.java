package com.narara.superboard.workspacemember.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceResponseDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkSpaceMemberDetailResponseDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberCollectionResponseDto;
import com.narara.superboard.workspacemember.service.WorkSpaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class WorkSpaceMemberController implements WorkSpaceMemberAPI {

    private final WorkSpaceMemberService workSpaceMemberService;

    @Override
    public ResponseEntity<DefaultResponse<List<WorkSpaceMemberDetailResponseDto>>> getWorkspaceMemberCollectionResponseDto(
            Long workspaceId) {
        List<WorkSpaceMemberDetailResponseDto> responseDto = workSpaceMemberService.getWorkspaceMemberCollectionResponseDto(
                workspaceId).workspaceMemberList();
        return new ResponseEntity<>(DefaultResponse.res(
                StatusCode.OK, ResponseMessage.WORKSPACE_MEMBER_FETCH_SUCCESS, responseDto)
                , HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DefaultResponse<List<WorkSpaceResponseDto>>> getMemberWorkspaceList(
            @AuthenticationPrincipal Member member) {
        List<WorkSpaceResponseDto> responseDto = workSpaceMemberService.getMemberWorkspaceList(member).workSpaceResponseDtoList();

        return new ResponseEntity<>(DefaultResponse.res(
                StatusCode.OK, ResponseMessage.MEMBER_WORKSPACE_LIST_FETCH_SUCCESS, responseDto)
                , HttpStatus.OK);
    }
}
