package com.narara.superboard.fcmtoken.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.attachment.entity.Attachment;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.fcmtoken.entity.Alarm;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import java.util.List;
import org.springframework.data.domain.Page;

public interface AlarmService {
    List<Alarm> getAlarmsByMember(Member member);

    // 페이징 처리가 필요한 경우
    Page<Alarm> getAlarmsByMemberIdWithPaging(Member toMember, int page, int size);

    void sendAddWorkspaceMemberAlarm(Member manOfAction, WorkSpaceMember workSpaceMember) throws FirebaseMessagingException;

    void sendDeleteWorkspaceMemberAlarm(Member manOfAction,
                                        WorkSpaceMember workSpaceMember) throws FirebaseMessagingException;

    void sendArchiveBoard(Member manOfAction, Board board) throws FirebaseMessagingException;

    void sendAddBoardMemberAlarm(Member manOfAction, BoardMember boardMember) throws FirebaseMessagingException;

    void sendAddCardAlarm(Member manOfAction, Card card) throws FirebaseMessagingException;

    void sendMoveCardAlarm(Member manOfAction, Card card) throws FirebaseMessagingException;

    void sendAddCardAttachmentAlarm(Member manOfAction, Attachment attachment) throws FirebaseMessagingException;

    void sendAddReplyAlarm(Member manOfAction, Reply reply) throws FirebaseMessagingException;

    void sendAddCardMemberAlarm(Member manOfAction, CardMember cardMember) throws FirebaseMessagingException;

    void sendDeleteCardMemberAlarm(Member manOfAction, CardMember cardMember) throws FirebaseMessagingException;

    void sendArchiveCard(Member manOfAction, Card card) throws FirebaseMessagingException;

    void sendAddCardDueDateAlarm(Member manOfAction, Card card) throws FirebaseMessagingException;

    void sendDeleteBoardMemberAlarm(Member manOfAction, BoardMember boardMember) throws FirebaseMessagingException;
}
