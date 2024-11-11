package com.narara.superboard.card.interfaces.dto.activity;

import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.card.interfaces.dto.log.CardLogDetailResponseDto;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.interfaces.dto.ReplySimpleResponseDto;
import lombok.Builder;

@Builder
public record CardCombinedActivityDto(
        Object activity,
        Long when
) implements Comparable<CardCombinedActivityDto> {

    @Override
    public int compareTo(CardCombinedActivityDto other) {
        return other.when.compareTo(this.when); // 최신순 정렬
    }

    public static CardCombinedActivityDto of(CardHistory cardHistory){
        CardLogDetailResponseDto activityDetailResponseDto = CardLogDetailResponseDto.createLogDetailResponseDto(
                cardHistory);
        return CardCombinedActivityDto.builder()
                .activity(activityDetailResponseDto)
                .when(activityDetailResponseDto.when())
                .build();
    }

    public static CardCombinedActivityDto of(Reply reply){
        ReplySimpleResponseDto replySimpleResponseDto = ReplySimpleResponseDto.of(reply);
        return CardCombinedActivityDto.builder()
                .activity(replySimpleResponseDto)
                .when(replySimpleResponseDto.lastUpdatedAt())
                .build();
    }
}
