package com.narara.superboard.cardmember.interfaces;

import com.narara.superboard.cardmember.interfaces.dto.UpdateCardMemberRequestDto;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/card-member")
public interface CardMemberAPI {

    @GetMapping("/{cardId}/alert/{memberId}")
    @Operation(summary = "카드 멤버 알림 상태 조회", description = "특정 카드에서 멤버의 알림 상태를 조회합니다.")
    ResponseEntity<DefaultResponse<Boolean>> getCardMemberIsAlert(
            @PathVariable Long memberId,
            @PathVariable Long cardId);

    @PatchMapping("/{cardId}/alert/{memberId}")
    @Operation(summary = "카드 멤버 알림 상태 설정", description = "특정 카드에서 멤버의 알림 상태를 설정합니다.")
    ResponseEntity<DefaultResponse<Void>> setCardMemberIsAlert(
            @PathVariable Long memberId,
            @PathVariable Long cardId);

    @PatchMapping("/representative")
    @Operation(summary = "카드 멤버 대표자 설정", description = "특정 카드 멤버를 대표자로 설정합니다.")
    ResponseEntity<DefaultResponse<Void>> setCardMemberIsRepresentative(
            @RequestBody UpdateCardMemberRequestDto updateCardMemberRequestDto);
}
