package com.narara.superboard.boardmember.interfaces;

import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import com.narara.superboard.boardmember.interfaces.dto.MemberResponseDto;
import com.narara.superboard.boardmember.service.BoardMemberService;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardMemberController implements BoardMemberAPI{

    private final BoardMemberService boardMemberService;

    @Override
    public ResponseEntity<DefaultResponse<MemberCollectionResponseDto>> getBoardMembers(Long boardId) {
        MemberCollectionResponseDto memberCollectionResponseDto = boardMemberService.getBoardMemberCollectionResponseDto(
                boardId);
        return new ResponseEntity<>(DefaultResponse.res(
                StatusCode.OK, ResponseMessage.BOARD_MEMBER_FETCH_SUCCESS, memberCollectionResponseDto)
                , HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DefaultResponse<Boolean>> getWatchStatus(Long boardId, Member member) {
        Boolean watchStatus = boardMemberService.getWatchStatus(boardId, member);
        return new ResponseEntity<>(DefaultResponse.res(
                StatusCode.OK, ResponseMessage.BOARD_MEMBER_WATCH_STATUS_SUCCESS, watchStatus)
                , HttpStatus.OK);

    }

    @Override
    public ResponseEntity<DefaultResponse<Void>> updateWatchStatus(Long boardId, Member member) {
        boardMemberService.updateWatchStatus(boardId, member);
        return new ResponseEntity<>(DefaultResponse.res(
                StatusCode.OK, ResponseMessage.BOARD_MEMBER_WATCH_STATUS_UPDATE_SUCCESS),
                HttpStatus.OK);
    }

    @Operation(summary = "보드 멤버 추가", description = "이미 보드의 멤버로 추가되어 있는 경우에는 요청을 무시하고 있는 값을 보내줌")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'ADMIN')") //boardMember 추가는 ADMIN만 가능
    @PostMapping("/member")
    public ResponseEntity<DefaultResponse> addBoardMember(@PathVariable("boardId") Long boardId, @RequestBody AddMemberDto dto) {
        BoardMember boardMember = boardMemberService.addBoardMember(boardId, dto.memberId());

        return ResponseEntity.ok(
                DefaultResponse.res(
                        StatusCode.OK,
                        ResponseMessage.BOARD_MEMBER_CREATE_SUCCESS,
                        MemberResponseDto.builder()
                                .memberId(boardMember.getMember().getId())
                                .memberEmail(boardMember.getMember().getEmail())
                                .memberNickname(boardMember.getMember().getNickname())
                                .memberProfileImgUrl(boardMember.getMember().getProfileImgUrl())
                                .authority(boardMember.getAuthority().name())
                                .build()
                )
        );
    }
}
