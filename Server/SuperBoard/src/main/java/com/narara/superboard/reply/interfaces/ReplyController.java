package com.narara.superboard.reply.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.interfaces.dto.ReplySimpleResponseDto;
import com.narara.superboard.reply.interfaces.dto.ReplyUpdateRequestDto;
import com.narara.superboard.reply.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "8. 댓글")
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class ReplyController implements ReplyAPI {

    private final ReplyService replyService;

    @Override
    @Operation(summary = "댓글 생성", description = "")
    public ResponseEntity<DefaultResponse<ReplySimpleResponseDto>> createReply(@AuthenticationPrincipal Member member, @RequestBody ReplyCreateRequestDto replyCreateRequestDto) {
        Reply reply = replyService.createReply(member, replyCreateRequestDto);

        ReplySimpleResponseDto replySimpleResponseDto = ReplySimpleResponseDto.of(reply);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.REPLY_CREATE_SUCCESS,replySimpleResponseDto),HttpStatus.CREATED);
    }

    @Override
    @Operation(summary = "댓글 수정", description = "")
    public ResponseEntity<DefaultResponse<ReplySimpleResponseDto>> updateReply(@AuthenticationPrincipal Member member, ReplyUpdateRequestDto updateRequestDto,
                                                               Long replyId) {
        Reply reply = replyService.updateReply(member, replyId, updateRequestDto);

        ReplySimpleResponseDto replySimpleResponseDto = ReplySimpleResponseDto.of(reply);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.REPLY_UPDATE_SUCCESS,replySimpleResponseDto),HttpStatus.OK);
    }

    @Override
    @Operation(summary = "댓글 삭제", description = "")
    public ResponseEntity<DefaultResponse<Void>> deleteReply(@AuthenticationPrincipal Member member, Long replyId) {
        replyService.deleteReply(member, replyId);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.REPLY_DELETE_SUCCESS),HttpStatus.OK);
    }

}
