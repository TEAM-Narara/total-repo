package com.narara.superboard.board.interfaces.dto.activity;

import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.interfaces.dto.ReplySimpleResponseDto;
import lombok.Builder;

@Builder
public record BoardCombinedActivityDto(
        Object activity,
        Long when
) implements Comparable<BoardCombinedActivityDto> {

    @Override
    public int compareTo(BoardCombinedActivityDto other) {
        return other.when.compareTo(this.when); // 최신순 정렬
    }

//    public static BoardCombinedActivityDto of(CardHistory cardHistory){
//        CardLogDetailResponseDto activityDetailResponseDto = CardLogDetailResponseDto.createLogDetailResponseDto(
//                cardHistory);
//        return BoardCombinedActivityDto.builder()
//                .activity(activityDetailResponseDto)
//                .when(activityDetailResponseDto.when())
//                .build();
//    }
//
//    public static BoardCombinedActivityDto of(BoardHistory boardHistory){
//        BoardLogDetailResponseDto.createLogDetailResponseDto(
//                boardHistory);
//        return BoardCombinedActivityDto.builder()
//                .activity(activityDetailResponseDto)
//                .when(activityDetailResponseDto.when())
//                .build();
//    }

    public static BoardCombinedActivityDto of(Reply reply){
        ReplySimpleResponseDto replySimpleResponseDto = ReplySimpleResponseDto.of(reply);
        return BoardCombinedActivityDto.builder()
                .activity(replySimpleResponseDto)
                .when(replySimpleResponseDto.lastUpdatedAt())
                .build();
    }

}
