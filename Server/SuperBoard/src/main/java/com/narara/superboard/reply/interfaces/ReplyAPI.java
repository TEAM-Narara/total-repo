package com.narara.superboard.reply.interfaces;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.interfaces.dto.ReplySimpleResponseDto;
import com.narara.superboard.reply.interfaces.dto.ReplyUpdateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/reply")
public interface ReplyAPI {

    @PostMapping
    ResponseEntity<DefaultResponse<ReplySimpleResponseDto>> createReply(Member member, ReplyCreateRequestDto replyCreateRequestDto)
            throws FirebaseMessagingException;

    @PatchMapping("/{replyId}")
    ResponseEntity<DefaultResponse<ReplySimpleResponseDto>> updateReply(Member member, ReplyUpdateRequestDto updateRequestDto,
                                                        @PathVariable Long replyId);
    @DeleteMapping("/{replyId}")
    ResponseEntity<DefaultResponse<Void>> deleteReply(Member member, @PathVariable Long replyId);
}
