package com.narara.superboard.board.interfaces.dto;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;

public record BoardReplyCollectionResponseDto(
        Long replyId,String content,Boolean isDeleted,Long updatedAt, // 댓글
        Long memberId,String email, // 멤버
        Long cardId,String cardName, // 카드
        Long listId,String listName // 리스트
) {
}
