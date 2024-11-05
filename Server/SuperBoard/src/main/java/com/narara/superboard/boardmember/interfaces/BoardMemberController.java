package com.narara.superboard.boardmember.interfaces;

import com.narara.superboard.boardmember.interfaces.dto.BoardMemberCollectionResponseDto;
import com.narara.superboard.boardmember.interfaces.dto.BoardMemberResponseDto;
import com.narara.superboard.boardmember.service.BoardMemberService;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardMemberController implements BoardMemberAPI{

    private final BoardMemberService boardMemberService;

    @Override
    public ResponseEntity<DefaultResponse<List<BoardMemberResponseDto>>> getBoardMembers(Long boardId) {
        List<BoardMemberResponseDto> boardMemberResponseDtoList = boardMemberService.getBoardMemberCollectionResponseDto(
                boardId).boardMemberResponseDtoList();
        return new ResponseEntity<>(DefaultResponse.res(
                StatusCode.OK, ResponseMessage.BOARD_MEMBER_FETCH_SUCCESS, boardMemberResponseDtoList)
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
}
