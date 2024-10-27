package com.narara.superboard.reply.service;

import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;

public interface ReplyService {
    Reply createReply(ReplyCreateRequestDto replyCreateRequestDto);
}
