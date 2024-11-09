package com.narara.superboard.board.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.websocket.enums.BoardAction;
import com.narara.superboard.workspace.entity.mongo.WorkspaceOffset.DiffInfo;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardOffsetService {
    public static final String BOARD = "BOARD";
    public static final String BOARD_ID_COLUMN = "boardId";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    //boardMember 추가
    public void saveAddMemberDiff(BoardMember boardMember) {
        Map<String, Object> data = new HashMap<>();

        data.put("boardMemberId", boardMember.getId());
        data.put(BOARD_ID_COLUMN, boardMember.getBoard().getId());
        data.put("memberId", boardMember.getMember().getId());
        data.put("memberEmail", boardMember.getMember().getEmail());
        data.put("memberName", boardMember.getMember().getNickname());
        data.put("profileImgUrl", boardMember.getMember().getProfileImgUrl());
        data.put("authority", boardMember.getAuthority());
        data.put("isDeleted", boardMember.getIsDeleted());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                boardMember.getUpdatedAt(),
                BOARD,
                BoardAction.ADD_BOARD_MEMBER.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(boardMember.getBoard().getId(), diffInfo);
    }

    public void saveDeleteBoardMemberDiff(BoardMember boardMember) {
        Map<String, Object> data = new HashMap<>();

        data.put("boardMemberId", boardMember.getId());
        data.put(BOARD_ID_COLUMN, boardMember.getBoard().getId());
        data.put("memberId", boardMember.getMember().getId());
        data.put("memberEmail", boardMember.getMember().getEmail());
        data.put("memberName", boardMember.getMember().getNickname());
        data.put("profileImgUrl", boardMember.getMember().getProfileImgUrl());
        data.put("authority", boardMember.getAuthority());
        data.put("isDeleted", boardMember.getIsDeleted());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                boardMember.getUpdatedAt(),
                BOARD,
                BoardAction.DELETE_BOARD_MEMBER.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(boardMember.getBoard().getId(), diffInfo);
    }

    public void saveEditBoardMemberDiff(BoardMember boardMember) {
        Map<String, Object> data = new HashMap<>();

        data.put("boardMemberId", boardMember.getId());
        data.put(BOARD_ID_COLUMN, boardMember.getBoard().getId());
        data.put("memberId", boardMember.getMember().getId());
        data.put("memberEmail", boardMember.getMember().getEmail());
        data.put("memberName", boardMember.getMember().getNickname());
        data.put("profileImgUrl", boardMember.getMember().getProfileImgUrl());
        data.put("authority", boardMember.getAuthority());
        data.put("isDeleted", boardMember.getIsDeleted());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                boardMember.getUpdatedAt(),
                BOARD,
                BoardAction.EDIT_BOARD_MEMBER.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(boardMember.getBoard().getId(), diffInfo);
    }

    public void saveAddListDiff(List list) {
        Map<String, Object> data = new HashMap<>();

        data.put("listId", list.getId());
        data.put(BOARD_ID_COLUMN, list.getBoard().getId());
        data.put("name", list.getName());
        data.put("myOrder", list.getMyOrder());
        data.put("isArchive", list.getIsArchived());
        data.put("isDeleted", list.getIsDeleted());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                list.getUpdatedAt(),
                BOARD,
                BoardAction.ADD_LIST.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(list.getBoard().getId(), diffInfo);
    }

    public void saveEditListArchiveDiff(List list) {
        Map<String, Object> data = new HashMap<>();

        data.put("listId", list.getId());
        data.put(BOARD_ID_COLUMN, list.getBoard().getId());
        data.put("name", list.getName());
        data.put("myOrder", list.getMyOrder());
        data.put("isArchive", list.getIsArchived());
        data.put("isDeleted", list.getIsDeleted());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                list.getUpdatedAt(),
                BOARD,
                BoardAction.EDIT_LIST_ARCHIVE.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(list.getBoard().getId(), diffInfo);
    }

    public void saveEditListDiff(List list) {
        Map<String, Object> data = new HashMap<>();

        data.put("listId", list.getId());
        data.put(BOARD_ID_COLUMN, list.getBoard().getId());
        data.put("name", list.getName());
        data.put("myOrder", list.getMyOrder());
        data.put("isArchive", list.getIsArchived());
        data.put("isDeleted", list.getIsDeleted());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                list.getUpdatedAt(),
                BOARD,
                BoardAction.EDIT_LIST.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(list.getBoard().getId(), diffInfo);
    }

    public void saveAddCard(Card card) {
        Map<String, Object> data = new HashMap<>();
        Board board = card.getList().getBoard();

        data.put("cardId", card.getId());
        data.put("listId", card.getList().getId());
        data.put("name", card.getName());
        data.put("description", card.getDescription());
        data.put("startAt", card.getStartAt());
        data.put("endAt", card.getEndAt());
        data.put("coverType", card.getCover().get("type"));
        data.put("coverValue", card.getCover().get("value"));
        data.put("isDeleted", card.getIsDeleted());
        data.put("isArchived", card.getIsArchived());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                card.getUpdatedAt(),
                BOARD,
                BoardAction.ADD_CARD.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(board.getId(), diffInfo);
    }

    public void saveEditCard(Card card) {
        Map<String, Object> data = new HashMap<>();
        Board board = card.getList().getBoard();

        data.put("cardId", card.getId());
        data.put("listId", card.getList().getId());
        data.put("name", card.getName());
        data.put("description", card.getDescription());
        data.put("startAt", card.getStartAt());
        data.put("endAt", card.getEndAt());
        data.put("coverType", card.getCover().get("type"));
        data.put("coverValue", card.getCover().get("value"));
        data.put("isDeleted", card.getIsDeleted());
        data.put("isArchived", card.getIsArchived());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                card.getUpdatedAt(),
                BOARD,
                BoardAction.EDIT_CARD.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(board.getId(), diffInfo);
    }

    public void saveArchiveCard(Card card) {
        Map<String, Object> data = new HashMap<>();
        Board board = card.getList().getBoard();

        data.put("cardId", card.getId());
        data.put("listId", card.getList().getId());
        data.put("name", card.getName());
        data.put("description", card.getDescription());
        data.put("startAt", card.getStartAt());
        data.put("endAt", card.getEndAt());
        data.put("coverType", card.getCover().get("type"));
        data.put("coverValue", card.getCover().get("value"));
        data.put("isDeleted", card.getIsDeleted());
        data.put("isArchived", card.getIsArchived());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                card.getUpdatedAt(),
                BOARD,
                BoardAction.ARCHIVE_CARD.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(board.getId(), diffInfo);
    }

    public void saveDeleteCard(Card card) {
        Map<String, Object> data = new HashMap<>();
        Board board = card.getList().getBoard();

        data.put("cardId", card.getId());
        data.put("listId", card.getList().getId());
        data.put("isDeleted", card.getIsDeleted());
        data.put("isArchived", card.getIsArchived());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                card.getUpdatedAt(),
                BOARD,
                BoardAction.DELETE_CARD.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(board.getId(), diffInfo);
    }

    public void saveAddCardMember(CardMember cardMember) {
        Map<String, Object> data = new HashMap<>();
        Card card = cardMember.getCard();
        Board board = card.getList().getBoard();

        data.put("cardMemberId", cardMember.getId());
        data.put("cardId", card.getId());
        data.put("memberId", cardMember.getMember().getId());
        data.put("isAlert", cardMember.isAlert());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                card.getUpdatedAt(),
                BOARD,
                BoardAction.ADD_CARD_MEMBER.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(board.getId(), diffInfo);
    }

    public void saveDeleteCardMember(CardMember cardMember) {
        Map<String, Object> data = new HashMap<>();
        Card card = cardMember.getCard();
        Board board = card.getList().getBoard();

        data.put("cardMemberId", cardMember.getId());
        data.put("cardId", card.getId());
        data.put("memberId", cardMember.getMember().getId());
        data.put("isAlert", cardMember.isAlert());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                card.getUpdatedAt(),
                BOARD,
                BoardAction.DELETE_CARD_MEMBER.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(board.getId(), diffInfo);
    }

    public void saveEditCardMember(CardMember cardMember) {
        Map<String, Object> data = new HashMap<>();
        Card card = cardMember.getCard();
        Board board = card.getList().getBoard();

        data.put("cardMemberId", cardMember.getId());
        data.put("cardId", card.getId());
        data.put("memberId", cardMember.getMember().getId());
        data.put("isAlert", cardMember.isAlert());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                card.getUpdatedAt(),
                BOARD,
                BoardAction.EDIT_CARD_MEMBER.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(board.getId(), diffInfo);
    }

    public void saveAddReply(Reply reply) {
        Map<String, Object> data = new HashMap<>();
        Card card = reply.getCard();
        Board board = card.getList().getBoard();

        data.put("cardId", card.getId());
        data.put("replyId", reply.getId());
        data.put("memberId", reply.getMember().getId());
        data.put("content", reply.getContent());
        data.put("isDeleted", reply.getIsDeleted());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                card.getUpdatedAt(),
                BOARD,
                BoardAction.ADD_REPLY.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(board.getId(), diffInfo);
    }

    public void saveDeleteReply(Reply reply) {
        Map<String, Object> data = new HashMap<>();
        Card card = reply.getCard();
        Board board = card.getList().getBoard();

        data.put("cardId", card.getId());
        data.put("replyId", reply.getId());
        data.put("isDeleted", reply.getIsDeleted());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                card.getUpdatedAt(),
                BOARD,
                BoardAction.DELETE_REPLY.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(board.getId(), diffInfo);
    }

    public void saveEditReply(Reply reply) {
        Map<String, Object> data = new HashMap<>();
        Card card = reply.getCard();
        Board board = card.getList().getBoard();

        data.put("cardId", card.getId());
        data.put("replyId", reply.getId());
        data.put("memberId", reply.getMember().getId());
        data.put("content", reply.getContent());
        data.put("isDeleted", reply.getIsDeleted());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                card.getUpdatedAt(),
                BOARD,
                BoardAction.EDIT_REPLY.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(board.getId(), diffInfo);
    }

    public void saveEditCardArchiveDiff(Card card) {
        Map<String, Object> data = new HashMap<>();
        Board board = card.getList().getBoard();

        data.put("cardId", card.getId());
        data.put("listId", card.getList().getId());
        data.put("isArchived", card.getIsArchived());

        DiffInfo diffInfo = new DiffInfo(
                1L, //offset 임시값
                card.getUpdatedAt(),
                BOARD,
                BoardAction.ARCHIVE_CARD.name(),
                data
        );

        // 카프카로 메시지 전송
        sendMessageToKafka(board.getId(), diffInfo);
    }

    private <T> void sendMessageToKafka(Long boardId, T object) {
        String topic = "board-" + boardId;

        // DiffInfo 객체를 JSON 문자열로 변환
        String jsonMessage = null;
        try {
            jsonMessage = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // Kafka에 메시지 전송
        kafkaTemplate.send(topic, jsonMessage);
        System.out.println("Message sent to Kafka: " + jsonMessage);
    }
}
