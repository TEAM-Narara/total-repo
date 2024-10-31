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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin
@Controller
@RequiredArgsConstructor
@RequestMapping("/reply")
public class ReplyController implements ReplyAPI {

    private final ReplyService replyService;

    @Override
    public ResponseEntity<?> createReply(@AuthenticationPrincipal Member member, @RequestBody ReplyCreateRequestDto replyCreateRequestDto) {
        Reply reply = replyService.createReply(member, replyCreateRequestDto);

        ReplySimpleResponseDto replySimpleResponseDto = ReplySimpleResponseDto.of(reply);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.REPLY_CREATE_SUCCESS,replySimpleResponseDto),HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> updateReply(Member member, ReplyUpdateRequestDto updateRequestDto,
                                                               Long replyId) {
        Reply reply = replyService.updateReply(member, replyId, updateRequestDto);

        ReplySimpleResponseDto replySimpleResponseDto = ReplySimpleResponseDto.of(reply);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.REPLY_UPDATE_SUCCESS,replySimpleResponseDto),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deleteReply(Member member, Long replyId) {
        replyService.deleteReply(member, replyId);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.REPLY_DELETE_SUCCESS),HttpStatus.OK);
    }

}
