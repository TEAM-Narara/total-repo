package com.narara.superboard.cardmember.interfaces;

import com.narara.superboard.cardmember.interfaces.dto.UpdateCardMemberRequestDto;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/card-member")
public interface CardMemberAPI {

    @GetMapping("/{cardId}/alert/{memberId}")
    @Operation(summary = "카드 멤버 알림 상태 조회", description = "특정 카드에서 멤버의 알림 상태를 조회합니다.")
    ResponseEntity<DefaultResponse<Boolean>> getCardMemberIsAlert(
            @AuthenticationPrincipal Member member,
            @PathVariable Long cardId);

    @PatchMapping("/{cardId}/alert/{memberId}")
    @Operation(summary = "카드 멤버 알림 상태 설정", description = "특정 카드에서 멤버의 알림 상태를 설정합니다.")
    ResponseEntity<DefaultResponse<Void>> setCardMemberIsAlert(
            @AuthenticationPrincipal Member member,
            @PathVariable Long cardId);

    @PatchMapping("/representative")
    @Operation(summary = "카드 멤버 대표자 설정", description = "특정 카드 멤버를 대표자로 설정합니다.")
    ResponseEntity<DefaultResponse<Void>> setCardMemberIsRepresentative(
            @RequestBody UpdateCardMemberRequestDto updateCardMemberRequestDto);
}
