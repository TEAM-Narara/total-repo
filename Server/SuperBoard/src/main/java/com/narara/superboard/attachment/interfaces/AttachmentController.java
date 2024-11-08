package com.narara.superboard.attachment.interfaces;

import com.narara.superboard.attachment.entity.Attachment;
import com.narara.superboard.attachment.interfaces.dto.AttachmentDto;
import com.narara.superboard.attachment.service.AttachmentService;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name = "첨부파일")
@RestController
@RequiredArgsConstructor
public class AttachmentController implements AttachmentAPI {

    private final AttachmentService attachmentService;

    @Override
    public ResponseEntity<DefaultResponse<AttachmentDto>> addAttachment(
            @AuthenticationPrincipal Member member,
            @RequestParam Long cardId,
            @RequestParam String url) {

        Attachment attachment = attachmentService.addAttachment(member, cardId, url);
        AttachmentDto attachmentDto = AttachmentDto.of(attachment);

        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.CREATED, ResponseMessage.ATTACHMENT_ADD_SUCCESS, attachmentDto),
                HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<Void>> deleteAttachment(
            @AuthenticationPrincipal Member member,
            @PathVariable Long attachmentId) {

        attachmentService.deleteAttachment(member, attachmentId);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.ATTACHMENT_DELETE_SUCCESS),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<Void>> updateAttachmentIsCover(
            @PathVariable Long attachmentId) {

        attachmentService.updateAttachmentIsCover(attachmentId);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.ATTACHMENT_COVER_STATUS_CHANGE_SUCCESS),
                HttpStatus.OK
        );
    }
}
