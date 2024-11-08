package com.narara.superboard.cardlabel.interfaces;

import com.narara.superboard.cardlabel.interfaces.dto.CardLabelDto;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/card-label")
public interface CardLabelAPI {

    @PostMapping("/create")
    @Operation(summary = "카드 라벨 생성", description = "특정 카드에 라벨을 생성합니다.")
    ResponseEntity<DefaultResponse<CardLabelDto>> createCardLabel(
            @AuthenticationPrincipal Member member,
            @RequestBody Long cardId,
            @RequestBody Long labelId);

    @PatchMapping("/activate")
    @Operation(summary = "카드에 라벨 활성화 상태 변경", description = "카드 라벨의 활성화 상태를 변경합니다.")
    ResponseEntity<DefaultResponse<CardLabelDto>> changeCardLabelIsActivated(
            @AuthenticationPrincipal Member member,
            @RequestBody Long cardId,
            @RequestBody Long labelId);

    @GetMapping("/{cardId}")
    @Operation(summary = "카드 라벨 조회", description = "특정 카드에 속한 모든 라벨을 조회합니다.")
    ResponseEntity<DefaultResponse<List<CardLabelDto>>> getCardLabelCollection(@PathVariable Long cardId);

}
