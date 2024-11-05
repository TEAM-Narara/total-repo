package com.narara.superboard.common.document;

import com.narara.superboard.reply.entity.Reply;
import lombok.Builder;


@Builder
public record Target(
        Long targetId,
        String targetName,
        AdditionalDetails additionalDetails
) {
    public static Target of(Identifiable identifiable, AdditionalDetails additionalDetails) {
        return Target.builder()
                .targetId(identifiable.getId())
                .targetName(identifiable.getName())
                .additionalDetails(additionalDetails)
                .build();
    }

    public static Target of(Reply reply, AdditionalDetails additionalDetails) {
        return Target.builder()
                .targetId(reply.getId())
                .additionalDetails(additionalDetails)
                .build();
    }
}