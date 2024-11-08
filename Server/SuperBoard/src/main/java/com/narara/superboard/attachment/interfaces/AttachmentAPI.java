package com.narara.superboard.attachment.interfaces;

import com.narara.superboard.attachment.interfaces.dto.AttachmentDto;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/attachment")
public interface AttachmentAPI {

    @PostMapping
    @Operation(summary = "첨부파일 등록", description = "특정 카드에 첨부파일을 등록합니다.")
    ResponseEntity<DefaultResponse<AttachmentDto>> addAttachment(
            @AuthenticationPrincipal Member member,
            @RequestParam Long cardId,
            @RequestParam String url);

    @DeleteMapping("/{attachmentId}")
    @Operation(summary = "첨부파일 삭제", description = "특정 첨부파일을 삭제합니다.")
    ResponseEntity<DefaultResponse<Void>> deleteAttachment(
            @AuthenticationPrincipal Member member,
            @PathVariable Long attachmentId);

    @PatchMapping("/{attachmentId}/cover")
    @Operation(summary = "첨부파일 커버 상태 변경", description = "특정 첨부파일의 커버 여부를 수정합니다.")
    ResponseEntity<DefaultResponse<Void>> updateAttachmentIsCover(
            @PathVariable Long attachmentId);
}
