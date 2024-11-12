package com.narara.superboard.list.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import com.narara.superboard.list.interfaces.dto.ListMoveCollectionRequest;
import com.narara.superboard.list.interfaces.dto.ListMoveResult;
import com.narara.superboard.list.interfaces.dto.ListSimpleResponseDto;
import com.narara.superboard.list.interfaces.dto.ListUpdateRequestDto;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/list")
public interface ListAPI {

    @PostMapping
    ResponseEntity<DefaultResponse<ListSimpleResponseDto>> createList(@AuthenticationPrincipal Member member, @RequestBody ListCreateRequestDto listCreateRequestDto);

    @PatchMapping("/{listId}")
    ResponseEntity<DefaultResponse<ListSimpleResponseDto>> updateList(@AuthenticationPrincipal Member member, @PathVariable Long listId, @RequestBody ListUpdateRequestDto listUpdateRequestDto);

    @PatchMapping("/{listId}/archive")
    ResponseEntity<DefaultResponse<ListSimpleResponseDto>> changeListIsArchived(@AuthenticationPrincipal Member member, @PathVariable Long listId);

    @GetMapping("/{boardId}/archived")
    ResponseEntity<DefaultResponse<java.util.List<ListSimpleResponseDto>>> getArchivedList(@AuthenticationPrincipal Member member, @PathVariable Long boardId);

    @PatchMapping("/{listId}/move/top")
    ResponseEntity<DefaultResponse<ListMoveResult>> moveListToTop(@AuthenticationPrincipal Member member, @PathVariable Long listId);

    @PatchMapping("/{listId}/move/bottom")
    ResponseEntity<DefaultResponse<ListMoveResult>> moveListToBottom(@AuthenticationPrincipal Member member, @PathVariable Long listId);

    @PatchMapping("/{listId}/move/between")
    ResponseEntity<DefaultResponse<ListMoveResult>> moveListBetween(
            @AuthenticationPrincipal Member member,
            @PathVariable Long listId,
            @RequestParam Long previousListId,
            @RequestParam Long nextListId
    );

    @PatchMapping("/move")
    @Operation(summary = "리스트를 지정한 위치로 이동", description = "지정된 리스트를 직접 지정한 위치로 이동시킵니다.")
    ResponseEntity<DefaultResponse<ListMoveResult>> moveList(
            @AuthenticationPrincipal Member member,
            @RequestBody ListMoveCollectionRequest listMoveCollectionRequest
    );

    @GetMapping("/{boardId}/lists")
    ResponseEntity<DefaultResponse<List<ListSimpleResponseDto>>> getListsByBoardId(@PathVariable Long boardId);
}
