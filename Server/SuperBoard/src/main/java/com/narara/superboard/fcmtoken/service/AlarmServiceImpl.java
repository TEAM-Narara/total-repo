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

    @Override
    public void sendAddWorkspaceMemberAlarm(Member manOfAction, WorkSpaceMember workSpaceMember)
            throws FirebaseMessagingException {
        HashMap<String, String> data = new HashMap<>();
        data.put("type", "ME_ADD_WORKSPACE_MEMBER");
        data.put("goTo", "WORKSPACE");
        data.put("manOfActionId", String.valueOf(manOfAction.getId()));
        data.put("workspaceId", String.valueOf(workSpaceMember.getWorkSpace().getId()));

        //주효림이 당신을 날아라 워크스페이스에 admin으로 추가하였습니다
        String title = String.format(
                "*%s*님이 당신을 *%s* 워크스페이스에 %s 권한으로 추가하였습니다",
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

        //주효림이 당신을 날아라 워크스페이스에서 삭제하였습니다
        String title = String.format(
                "*%s*님이 당신을 *%s* 워크스페이스에서 삭제하였습니다",
                manOfAction.getNickname(),
                workSpaceMember.getWorkSpace().getName()
        );

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

        String title = String.format("*%s*님이 *%s* 보드를 닫았습니다", manOfAction.getNickname(), board.getName());

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

        //주효림이 당신을 백엔드 보드에 admin으로 추가하였습니다.
        String title = String.format(
                "*%s*님이 당신을 *%s* 보드에 %s 권한으로 추가하였습니다",
                manOfAction.getNickname(),
                board.getName(),
                boardMember.getAuthority().name()
        );

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

        //주효림이 당신을 백엔드 보드에서 삭제하였습니다
        String title = String.format(
                "*%s*님이 당신을 *%s* 보드에서 삭제하였습니다",
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

        //남경민님이 백엔드 보드 오늘할일 리스트에 QA하기 카드를 만들었습니다.
        String title = String.format(
                "*%s*님이 *%s* 보드 *%s* 리스트에 *%s* 카드를 만들었습니다",
                manOfAction.getNickname(),
                board.getName(),
                card.getList().getName(),
                card.getName()
        );

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

        //여창민님이 QA하기 카드를 백엔드 보드의 내일 할 일 리스트로 이동하였습니다.
        String title = String.format(
                "*%s*님이 *%s* 카드를 *%s* 보드의 *%s* 리스트로 이동하였습니다",
                manOfAction.getNickname(),
                card.getName(),
                board.getName(),
                card.getList().getName()
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

        //박준식님이 백엔드 보드의 알림만들기 카드에 이미지를 추가하였습니다
        String title = String.format(
                "*%s*님이 *%s* 보드의 *%s* 카드에 이미지를 추가하였습니다",
                manOfAction.getNickname(),
                board.getName(),
                card.getName()
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

        //조시현님이 백엔드 보드의 알림만들기 카드에 댓글을 달았습니다.
        String title = String.format(
                "*%s*님이 *%s* 보드의 *%s* 키드에 댓글을 달았습니다",
                manOfAction.getNickname(),
                board.getName(),
                card.getName()
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

        //여창민님이 백엔드 보드의 알림만들기 카드에 나를 추가하였습니다.
        String title = String.format(
                "*%s*님이 *%s* 보드의 *%s* 카드에 나를 추가하였습니다",
                manOfAction.getNickname(),
                board.getName(),
                card.getName()
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

        //여창민님이 백엔드 보드의 알림만들기 카드에서 나를 삭제하였습니다.
        String title = String.format(
                "*%s*님이 *%s* 보드의 *%s* 카드에서 나를 삭제하였습니다",
                manOfAction.getNickname(),
                board.getName(),
                card.getName()
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
            //남경민님이 백엔드 보드에서 QA하기 카드를 아카이브로 보냈습니다.
            title = String.format(
                    "*%s*님이 *%s* 보드에서 *%s* 카드를 아카이브로 보냈습니다",
                    manOfAction.getNickname(),
                    card.getList().getBoard().getName(),
                    card.getName()
            );
        } else {
            //남경민님이 백엔드 보드에서 QA하기 카드를 아카이브에서 복구하였습니다.
            title = String.format(
                    "*%s*님이 *%s* 보드에서 *%s* 카드를 아카이브에서 복구하였습니다",
                    manOfAction.getNickname(),
                    card.getList().getBoard().getName(),
                    card.getName()
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

        //남경민님이 백엔드 보드 QA하기 카드에 due date를 추가하였습니다.
        String title = String.format(
                "*%s*님이 *%s* 보드 *%s* 카드에 due date를 추가하였습니다",
                manOfAction.getNickname(),
                board.getName(),
                card.getName()
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
