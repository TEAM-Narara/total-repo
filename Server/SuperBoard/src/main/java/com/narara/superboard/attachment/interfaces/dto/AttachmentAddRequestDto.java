package com.narara.superboard.attachment.interfaces.dto;

public record AttachmentAddRequestDto(Long cardId,String url,String type) {

    // 커버가 null이면 첨부파일 넣어주기
}
