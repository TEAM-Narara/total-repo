package com.narara.superboard.attachment.interfaces.dto;

import com.narara.superboard.attachment.entity.Attachment;
import lombok.Builder;

@Builder
public record AttachmentDto(
        Long attachmentId,
        String imgURL,
        String type,
        Boolean isCover,
        Long createdAt
) {
    public static AttachmentDto of(Attachment attachment){
        return AttachmentDto.builder()
                .attachmentId(attachment.getId())
                .imgURL(attachment.getUrl())
                .type(attachment.getType())
                .isCover(attachment.getIsCover())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}
