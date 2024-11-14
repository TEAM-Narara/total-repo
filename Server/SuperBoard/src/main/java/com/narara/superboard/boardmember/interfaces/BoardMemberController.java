package com.narara.superboard.boardmember.interfaces;

import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.interfaces.dto.*;
import com.narara.superboard.boardmember.service.BoardMemberService;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "b. 보드 회원")
@RestController
@RequiredArgsConstructor
public class BoardMemberController implements BoardMemberAPI{

    private final BoardMemberService boardMemberService;

    @Override
    public ResponseEntity<DefaultResponse<BoardMemberResponseDto>> getBoardMembers(Long boardId) {
        BoardMemberResponseDto boardMemberResponseDto = boardMemberService.getBoardMemberCollectionResponseDto(
                boardId);

        return ResponseEntity.ok(
                DefaultResponse.res(
                        StatusCode.OK,
                        ResponseMessage.BOARD_MEMBER_FETCH_SUCCESS,
                        boardMemberResponseDto
                ));
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

    @Override
    public ResponseEntity<DefaultResponse<BoardMemberDto>> addBoardMember(@PathVariable("boardId") Long boardId, @RequestBody AddMemberDto dto) {
        BoardMember boardMember = boardMemberService.addBoardMember(boardId, dto.memberId());

        return ResponseEntity.ok(
                DefaultResponse.res(
                        StatusCode.OK,
                        ResponseMessage.BOARD_MEMBER_CREATE_SUCCESS,
                        BoardMemberDto.builder()
                                .boardId(boardMember.getBoard().getId())
                                .boardMemberId(boardMember.getId())
                                .memberId(boardMember.getMember().getId())
                                .memberEmail(boardMember.getMember().getEmail())
                                .memberNickname(boardMember.getMember().getNickname())
                                .memberProfileImgUrl(boardMember.getMember().getProfileImgUrl())
                                .authority(boardMember.getAuthority().name())
                                .isDeleted(boardMember.getIsDeleted())
                                .build()
                )
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<BoardMemberDto>> deleteBoardMember(@PathVariable("boardId") Long boardId, @RequestBody AddMemberDto dto) {
        BoardMember boardMember = boardMemberService.deleteMember(boardId, dto.memberId());

        return ResponseEntity.ok(
                DefaultResponse.res(
                        StatusCode.OK,
                        ResponseMessage.BOARD_MEMBER_DELETE_SUCCESS,
                        BoardMemberDto.builder()
                                .boardId(boardMember.getBoard().getId())
                                .boardMemberId(boardMember.getId())
                                .memberId(boardMember.getMember().getId())
                                .memberEmail(boardMember.getMember().getEmail())
                                .memberNickname(boardMember.getMember().getNickname())
                                .memberProfileImgUrl(boardMember.getMember().getProfileImgUrl())
                                .authority(boardMember.getAuthority().name())
                                .isDeleted(boardMember.getIsDeleted())
                                .build()
                )
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<BoardMemberDto>> editBoardMemberAuthority(@PathVariable("boardId") Long boardId, @RequestBody EditBoardMemberAuthorityDto dto) {
        BoardMember boardMember = boardMemberService.editBoardMemberAuthority(boardId, dto.memberId(), dto.authority());

        return ResponseEntity.ok(
                DefaultResponse.res(
                        StatusCode.OK,
                        ResponseMessage.BOARD_MEMBER_AUTHORITY_UPDATE_SUCCESS,
                        BoardMemberDto.builder()
                                .boardId(boardMember.getBoard().getId())
                                .boardMemberId(boardMember.getId())
                                .memberId(boardMember.getMember().getId())
                                .memberEmail(boardMember.getMember().getEmail())
                                .memberNickname(boardMember.getMember().getNickname())
                                .memberProfileImgUrl(boardMember.getMember().getProfileImgUrl())
                                .authority(boardMember.getAuthority().name())
                                .isDeleted(boardMember.getIsDeleted())
                                .build()
                )
        );
    }
}
