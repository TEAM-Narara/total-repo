package com.narara.superboard.card.interfaces;

import com.narara.superboard.card.interfaces.dto.*;
import com.narara.superboard.card.interfaces.dto.log.CardLogDetailResponseDto;
import com.narara.superboard.card.interfaces.dto.activity.CardCombinedActivityResponseDto;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/card")
public interface CardAPI {

    @PostMapping
    ResponseEntity<DefaultResponse<CardSimpleResponseDto>> createCard(
            @AuthenticationPrincipal Member member,
            @RequestBody CardCreateRequestDto cardCreateRequestDto);

    @DeleteMapping("/{cardId}")
    ResponseEntity<DefaultResponse<Void>> deleteCard(
            @AuthenticationPrincipal Member member,
            @PathVariable Long cardId);

    @PatchMapping("/{cardId}")
    ResponseEntity<DefaultResponse<CardDetailResponseDto>> updateCard(
            @AuthenticationPrincipal Member member,
            @PathVariable Long cardId,
            @RequestBody CardUpdateRequestDto cardUpdateRequestDto);

    @GetMapping("/archived/{boardId}")
    ResponseEntity<DefaultResponse<CardArchiveCollectionResponseDto>> getArchivedCardList(
            @AuthenticationPrincipal Member member,
            @PathVariable Long boardId);

    @PatchMapping("/{cardId}/archive")
    ResponseEntity<DefaultResponse<Void>> changeArchiveStatusByCard(
            @AuthenticationPrincipal Member member,
            @PathVariable Long cardId);

    @GetMapping("/{cardId}/log")
    @Operation(summary = "카드 로그 조회")
    ResponseEntity<DefaultResponse<List<CardLogDetailResponseDto>>> getCardActivity(@PathVariable Long cardId);


    @GetMapping("/{cardId}/activity")
    @Operation(summary = "카드 액티비티(댓글+로그) 조회", description = "카드의 활동 및 댓글을 최신순으로 정렬하여 반환합니다.")
    ResponseEntity<DefaultResponse<CardCombinedActivityResponseDto>> getCardCombinedLog(
            @PathVariable Long cardId,
            @RequestParam int page,
            @RequestParam int size);
}
