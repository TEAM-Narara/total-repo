package com.narara.superboard.fcmtoken.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.attachment.entity.Attachment;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.infrastructure.BoardMemberRepository;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.cardmember.infrastructure.CardMemberRepository;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.service.ReplyServiceImpl;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AlarmServiceImpl implements AlarmService {
    private final FcmTokenService fcmTokenService;

    private final BoardMemberRepository boardMemberRepository;
    private final CardMemberRepository cardMemberRepository;
    private final ReplyServiceImpl replyServiceImpl;

    @Override
    public void sendAddWorkspaceMemberAlarm(Member manOfAction, WorkSpaceMember workSpaceMember)
            throws FirebaseMessagingException {
        HashMap<String, String> data = new HashMap<>();
        data.put("type", "ME_ADD_WORKSPACE_MEMBER");
        data.put("goTo", "WORKSPACE");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(workSpaceMember.getWorkSpace().getId()));

        //"*사용자이름* added you to the Workspace *워크스페이스이름* as an admin"
        String title = String.format(
                "*%s* added you to the Workspace *%s* as an %s",
                manOfAction.getNickname(),
                workSpaceMember.getWorkSpace().getName(),
                workSpaceMember.getAuthority().name()
        );

        //대상자에게만 알람
        fcmTokenService.sendMessage(workSpaceMember.getMember(), title, "", data);
    }

    @Override
    public void sendDeleteWorkspaceMemberAlarm(Member manOfAction, WorkSpaceMember workSpaceMember)
            throws FirebaseMessagingException {
        HashMap<String, String> data = new HashMap<>();
        data.put("type", "ME_REMOVE_WORKSPACE_MEMBER");
        data.put("goTo", "HOME");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));

        //"*사용자이름* removed you from the Workspace *워크스페이스이름*"
        String title = String.format("*%s* removed you from the Workspace *%s*", manOfAction.getNickname(),
                workSpaceMember.getWorkSpace().getName());

        //대상자에게만 알람
        fcmTokenService.sendMessage(workSpaceMember.getMember(), title, "", data);
    }

    @Override
    public void sendArchiveBoard(Member manOfAction, Board board) throws FirebaseMessagingException {
        HashMap<String, String> data = new HashMap<>();
        data.put("type", "CLOSE_BOARD");
        data.put("goTo", "WORKSPACE");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(board.getWorkSpace().getId()));

        String title = String.format("*%s* closed the board *%s*", manOfAction.getNickname(), board.getName());

        //모든 board watch 인원에게
        Set<Member> allMemberByBoardAndWatchTrue = boardMemberRepository.findAllMemberByBoardAndWatchTrue(
                board.getId());
        for (Member toMember : allMemberByBoardAndWatchTrue) {
            fcmTokenService.sendMessage(toMember, title, "", data);
        }
    }

    @Override
    public void sendAddBoardMemberAlarm(Member manOfAction, BoardMember boardMember) throws FirebaseMessagingException {
        Board board = boardMember.getBoard();
        WorkSpace workSpace = board.getWorkSpace();

        HashMap<String, String> data = new HashMap<>();
        data.put("type", "ME_ADD_BOARD_MEMBER");
        data.put("goTo", "BOARD");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(workSpace.getId()));
        data.put("boardId", String.valueOf(board.getId()));

        //"*사용자이름* made you an admin on the board *보드이름*"
        String title = String.format("*%s* made you an %s on the board *%s*", manOfAction.getNickname(),
                boardMember.getAuthority().name(), board.getName());

        //대상자에게만 알람
        fcmTokenService.sendMessage(boardMember.getMember(), title, "", data);
    }

    @Override
    public void sendDeleteBoardMemberAlarm(Member manOfAction, BoardMember boardMember)
            throws FirebaseMessagingException {
        Board board = boardMember.getBoard();
        WorkSpace workSpace = board.getWorkSpace();

        HashMap<String, String> data = new HashMap<>();
        data.put("type", "ME_DELETE_BOARD_MEMBER");
        data.put("goTo", "BOARD");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(workSpace.getId()));
        data.put("boardId", String.valueOf(board.getId()));

        //"*사용자이름* made you an admin on the board *보드이름*"
        String title = String.format(
                "*%s* removed you from the Board *%s*",
                manOfAction.getNickname(),
                board.getName()
        );

        //대상자에게만 알람
        fcmTokenService.sendMessage(boardMember.getMember(), title, "", data);
    }

    @Override
    public void sendAddCardAlarm(Member manOfAction, Card card) throws FirebaseMessagingException {
        Board board = card.getList().getBoard();
        WorkSpace workSpace = board.getWorkSpace();

        HashMap<String, String> data = new HashMap<>();
        data.put("type", "ADD_CARD");
        data.put("goTo", "CARD");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(workSpace.getId()));
        data.put("boardId", String.valueOf(board.getId()));
        data.put("listId", String.valueOf(card.getList().getId()));
        data.put("cardId", String.valueOf(card.getId()));

        // "*사용자이름* created *카드이름* in *리스트이름* on *보드이름*"
        String title = String.format("*%s* created *%s* in *%s* on *%s*", manOfAction.getNickname(), card.getName(),
                card.getList().getName(), board.getName());

        //모든 카드, 보드 watch 인원에게
        Set<Member> cardAndBoardMembers = getCardAndBoardMembers(card, board);

        for (Member toMember : cardAndBoardMembers) {
            fcmTokenService.sendMessage(toMember, title, "", data);
        }
    }

    @Override
    public void sendMoveCardAlarm(Member manOfAction, Card card) throws FirebaseMessagingException {
        Board board = card.getList().getBoard();
        WorkSpace workSpace = board.getWorkSpace();

        HashMap<String, String> data = new HashMap<>();
        data.put("type", "MOVE_CARD");
        data.put("goTo", "CARD");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(workSpace.getId()));
        data.put("boardId", String.valueOf(board.getId()));
        data.put("listId", String.valueOf(card.getList().getId()));
        data.put("cardId", String.valueOf(card.getId()));

        //"*사용자이름* moved the card *카드이름* to *리스트이름* on *보드이름*"
        String title = String.format(
                "*%s* moved the card *%s* in *%s* on *%s*",
                manOfAction.getNickname(),
                card.getName(),
                card.getList().getName(),
                board.getName()
        );

        //모든 카드, 보드 watch 인원에게
        Set<Member> cardAndBoardMembers = getCardAndBoardMembers(card, board);

        for (Member toMember : cardAndBoardMembers) {
            fcmTokenService.sendMessage(toMember, title, "", data);
        }
    }

    @Override
    public void sendAddCardAttachmentAlarm(Member manOfAction, Attachment attachment)
            throws FirebaseMessagingException {
        Card card = attachment.getCard();
        Board board = card.getList().getBoard();
        WorkSpace workSpace = board.getWorkSpace();

        HashMap<String, String> data = new HashMap<>();
        data.put("type", "ADD_CARD_ATTACHMENT");
        data.put("goTo", "CARD");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(workSpace.getId()));
        data.put("boardId", String.valueOf(board.getId()));
        data.put("listId", String.valueOf(card.getList().getId()));
        data.put("cardId", String.valueOf(card.getId()));

        //"%s attached image to %s on %s + [사용자 프로필사진]",
        String title = String.format(
                "*%s* attached image to *%s* on *%s*",
                manOfAction.getNickname(),
                card.getName(),
                board.getName()
        );

        //모든 카드, 보드 watch 인원에게
        Set<Member> cardAndBoardMembers = getCardAndBoardMembers(card, board);

        for (Member toMember : cardAndBoardMembers) {
            fcmTokenService.sendMessage(toMember, title, "", data);
        }
    }

    @Override
    public void sendAddReplyAlarm(Member manOfAction, Reply reply) throws FirebaseMessagingException {
        Card card = reply.getCard();
        Board board = card.getList().getBoard();
        WorkSpace workSpace = board.getWorkSpace();

        HashMap<String, String> data = new HashMap<>();
        data.put("type", "ADD_REPLY");
        data.put("goTo", "CARD");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(workSpace.getId()));
        data.put("boardId", String.valueOf(board.getId()));
        data.put("listId", String.valueOf(card.getList().getId()));
        data.put("cardId", String.valueOf(card.getId()));

        //"[사용자 이름] commented on the card [카드 이름] on [보드이름] + [사용자 프로필사진]", "[댓글 내용]"
        String title = String.format(
                "*%s* commented on the card *%s* on *%s*",
                manOfAction.getNickname(),
                card.getName(),
                board.getName()
        );

        //모든 카드, 보드 watch 인원에게
        Set<Member> cardAndBoardMembers = getCardAndBoardMembers(card, board);

        for (Member toMember : cardAndBoardMembers) {
            fcmTokenService.sendMessage(toMember, title, reply.getContent(), data);
        }
    }

    @Override
    public void sendAddCardMemberAlarm(Member manOfAction, CardMember cardMember) throws FirebaseMessagingException {
        Card card = cardMember.getCard();
        Board board = card.getList().getBoard();
        WorkSpace workSpace = board.getWorkSpace();

        HashMap<String, String> data = new HashMap<>();
        data.put("type", "ME_ADD_CARD_MEMBER");
        data.put("goTo", "CARD");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(workSpace.getId()));
        data.put("boardId", String.valueOf(board.getId()));
        data.put("listId", String.valueOf(card.getList().getId()));
        data.put("cardId", String.valueOf(card.getId()));

        //"*사용자이름* added you to the card *카드이름* on *보드이름*"
        String title = String.format(
                "*%s* added you to the card *%s* on *%s*",
                manOfAction.getNickname(),
                card.getName(),
                board.getName()
        );

        //모든 카드, 보드 watch 인원에게
        Set<Member> cardAndBoardMembers = getCardAndBoardMembers(card, board);

        for (Member toMember : cardAndBoardMembers) {
            fcmTokenService.sendMessage(toMember, title, "", data);
        }
    }

    @Override
    public void sendDeleteCardMemberAlarm(Member manOfAction, CardMember cardMember) throws FirebaseMessagingException {
        Card card = cardMember.getCard();
        Board board = card.getList().getBoard();
        WorkSpace workSpace = board.getWorkSpace();

        HashMap<String, String> data = new HashMap<>();
        data.put("type", "ME_ADD_CARD_MEMBER");
        data.put("goTo", "CARD");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(workSpace.getId()));
        data.put("boardId", String.valueOf(board.getId()));
        data.put("listId", String.valueOf(card.getList().getId()));
        data.put("cardId", String.valueOf(card.getId()));

        //"*사용자이름* added you to the card *카드이름* on *보드이름*"
        String title = String.format(
                "*%s* removed you from the card *%s* on *%s*",
                manOfAction.getNickname(),
                card.getName(),
                board.getName()
        );

        //모든 카드, 보드 watch 인원에게
        Set<Member> cardAndBoardMembers = getCardAndBoardMembers(card, board);

        for (Member toMember : cardAndBoardMembers) {
            fcmTokenService.sendMessage(toMember, title, "", data);
        }
    }

    @Override
    public void sendArchiveCard(Member manOfAction, Card card) throws FirebaseMessagingException {
        Board board = card.getList().getBoard();
        WorkSpace workSpace = board.getWorkSpace();

        HashMap<String, String> data = new HashMap<>();
        data.put("type", "ARCHIVE_CARD");
        data.put("goTo", "BOARD");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(workSpace.getId()));
        data.put("boardId", String.valueOf(board.getId()));

        String title;
        if (card.getIsArchived()) {
            //"%s archived the card %s on %s + [사용자 프로필사진]"
            title = String.format(
                    "%s archived the card %s on %s",
                    manOfAction.getNickname(),
                    card.getName(),
                    card.getList().getBoard().getName()
            );
        } else {
            //"%s unarchived the card %s on %s + [사용자 프로필사진]"
            title = String.format(
                    "%s unarchived the card %s on %s",
                    manOfAction.getNickname(),
                    card.getName(),
                    card.getList().getBoard().getName()
            );
        }

        //모든 카드, 보드 watch 인원에게
        Set<Member> cardAndBoardMembers = getCardAndBoardMembers(card, board);

        for (Member toMember : cardAndBoardMembers) {
            fcmTokenService.sendMessage(toMember, title, "", data);
        }
    }

    @Override
    public void sendAddCardDueDateAlarm(Member manOfAction, Card card) throws FirebaseMessagingException {
        Board board = card.getList().getBoard();
        WorkSpace workSpace = board.getWorkSpace();

        HashMap<String, String> data = new HashMap<>();
        data.put("type", "EDIT_CARD_DUE_DATE");
        data.put("goTo", "CARD");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(workSpace.getId()));
        data.put("boardId", String.valueOf(board.getId()));
        data.put("listId", String.valueOf(card.getList().getId()));
        data.put("cardId", String.valueOf(card.getId()));

        //"*사용자이름* added a due date to the card *카드이름* on *보드이름*"
        String title = String.format(
                "*%s* added a due date to the card *%s* on *%s*",
                manOfAction.getNickname(),
                card.getName(),
                board.getName()
        );

        //모든 카드, 보드 watch 인원에게
        Set<Member> cardAndBoardMembers = getCardAndBoardMembers(card, board);

        for (Member toMember : cardAndBoardMembers) {
            fcmTokenService.sendMessage(toMember, title, "", data);
        }
    }

    private Set<Member> getCardAndBoardMembers(Card card, Board board) {
        Set<Member> allMemberByBoardAndWatchTrue = boardMemberRepository.findAllMemberByBoardAndWatchTrue(
                board.getId());
        Set<Member> allMemberByCardAndWatchTrue = cardMemberRepository.findAllMemberByCardAndWatchTrue(card.getId());

        return Stream.concat(
                allMemberByBoardAndWatchTrue.stream(),
                allMemberByCardAndWatchTrue.stream()
        ).collect(Collectors.toSet());
    }
}
