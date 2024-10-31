package com.narara.superboard.reply.interfaces;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketResponse;

public interface ReplyDestination {

    WebSocketResponse createReply(Member member, ReplyCreateRequestDto replyCreateRequestDto);
}
