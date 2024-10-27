package com.narara.superboard.reply.service;

import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.interfaces.dto.ReplyUpdateRequestDto;

public interface ReplyService {
    Reply createReply(ReplyCreateRequestDto replyCreateRequestDto);
    Reply getReply(Long replyId);
    Reply updateReply(Long replyId, ReplyUpdateRequestDto replyUpdateRequestDto);
    void deleteReply(Long replyId);
}
