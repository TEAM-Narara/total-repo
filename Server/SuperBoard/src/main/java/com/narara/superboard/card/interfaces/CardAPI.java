package com.narara.superboard.card.interfaces;

import com.narara.superboard.card.interfaces.dto.*;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.member.entity.Member;
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
}
