package com.narara.superboard.reply.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.interfaces.dto.ReplyUpdateRequestDto;
import java.util.List;

public interface ReplyService {
    Reply createReply(Member member, ReplyCreateRequestDto replyCreateRequestDto) throws FirebaseMessagingException;
    Reply getReply(Long replyId);
    Reply updateReply(Member member, Long replyId, ReplyUpdateRequestDto replyUpdateRequestDto);
    Reply deleteReply(Member member, Long replyId);
    List<Reply> getRepliesByCardId(Long cardId);

    

}
