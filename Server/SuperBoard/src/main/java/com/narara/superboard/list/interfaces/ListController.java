package com.narara.superboard.list.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import com.narara.superboard.list.interfaces.dto.ListMoveResult;
import com.narara.superboard.list.interfaces.dto.ListSimpleResponseDto;
import com.narara.superboard.list.interfaces.dto.ListUpdateRequestDto;
import com.narara.superboard.list.service.ListMoveService;
import com.narara.superboard.list.service.ListService;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "6. 리스트")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/list")
public class ListController implements ListAPI {

    private final ListService listService;
    private final ListMoveService listMoveService;

    @Override
    @Operation(summary = "리스트 생성", description = "새로운 리스트를 생성합니다.")
    public ResponseEntity<DefaultResponse<ListSimpleResponseDto>> createList(@AuthenticationPrincipal Member member, @RequestBody ListCreateRequestDto listCreateRequestDto) {
        List list = listService.createList(member, listCreateRequestDto);
        ListSimpleResponseDto responseDto = ListSimpleResponseDto.of(list);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.LIST_CREATE_SUCCESS, responseDto), HttpStatus.CREATED);
    }

    @Override
    @Operation(summary = "리스트 수정", description = "기존 리스트의 정보를 수정합니다.")
    public ResponseEntity<DefaultResponse<ListSimpleResponseDto>> updateList(@AuthenticationPrincipal Member member, @PathVariable Long listId, @RequestBody ListUpdateRequestDto listUpdateRequestDto) {
        List list = listService.updateList(member, listId, listUpdateRequestDto);
        ListSimpleResponseDto responseDto = ListSimpleResponseDto.of(list);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.LIST_UPDATE_SUCCESS, responseDto), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "리스트 아카이브 상태 변경", description = "리스트의 아카이브 상태를 변경합니다.")
    public ResponseEntity<DefaultResponse<ListSimpleResponseDto>> changeListIsArchived(@AuthenticationPrincipal Member member, @PathVariable Long listId) {
        List list = listService.changeListIsArchived(member, listId);
        ListSimpleResponseDto responseDto = ListSimpleResponseDto.of(list);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.LIST_ARCHIVE_CHANGE_SUCCESS, responseDto), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "아카이브된 리스트 조회", description = "특정 리스트의 아카이브된 항목들을 조회합니다.")
    public ResponseEntity<DefaultResponse<java.util.List<ListSimpleResponseDto>>> getArchivedList(@AuthenticationPrincipal Member member, @PathVariable Long boardId) {
        java.util.List<List> archivedLists = listService.getArchivedList(member, boardId);
        java.util.List<ListSimpleResponseDto> responseDtos = archivedLists.stream()
                .map(ListSimpleResponseDto::of)
                .toList();
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.LIST_GET_ARCHIVED_SUCCESS, responseDtos), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "리스트를 맨 위로 이동", description = "지정된 리스트를 보드의 맨 위로 이동시킵니다.")
    public ResponseEntity<DefaultResponse<ListMoveResult>> moveListToTop(@AuthenticationPrincipal Member member, @PathVariable Long listId) {
        ListMoveResult result = listMoveService.moveListToTop(member, listId);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.MOVE_LIST_TOP_SUCCESS, result), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "리스트를 맨 아래로 이동", description = "지정된 리스트를 보드의 맨 아래로 이동시킵니다.")
    public ResponseEntity<DefaultResponse<ListMoveResult>> moveListToBottom(@AuthenticationPrincipal Member member, @PathVariable Long listId) {
        ListMoveResult result = listMoveService.moveListToBottom(member, listId);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.MOVE_LIST_BOTTOM_SUCCESS, result), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "리스트를 특정 위치(다른 리스트 사이)로 이동", description = "지정된 리스트를 두 리스트 사이의 위치로 이동시킵니다.")
    public ResponseEntity<DefaultResponse<ListMoveResult>> moveListBetween(
            @AuthenticationPrincipal Member member,
            @PathVariable Long listId,
            @RequestParam Long previousListId,
            @RequestParam Long nextListId
    ) {
        ListMoveResult result = listMoveService.moveListBetween(member, listId, previousListId, nextListId);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.MOVE_LIST_BETWEEN_SUCCESS, result), HttpStatus.OK);
    }
}
