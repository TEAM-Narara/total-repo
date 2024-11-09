package com.narara.superboard.board.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.list.entity.List;
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
    private final KafkaTemplate<String,String> kafkaTemplate;
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
        sendMessageToKafka(boardMember.getBoard().getId(),diffInfo);
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
        sendMessageToKafka(boardMember.getBoard().getId(),diffInfo);
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
        sendMessageToKafka(boardMember.getBoard().getId(),diffInfo);
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
        sendMessageToKafka(list.getBoard().getId(),diffInfo);
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
