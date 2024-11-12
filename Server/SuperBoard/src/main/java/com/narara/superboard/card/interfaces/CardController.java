package com.narara.superboard.card.interfaces;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.interfaces.dto.*;
import com.narara.superboard.card.interfaces.dto.log.CardLogDetailResponseDto;
import com.narara.superboard.card.interfaces.dto.activity.CardCombinedActivityResponseDto;
import com.narara.superboard.card.service.CardMoveService;
import com.narara.superboard.card.service.CardService;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;

import java.util.ArrayList;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "7. 카드")
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class CardController implements CardAPI {

    private final CardService cardService;
    private final CardMoveService cardMoveService;

    private final CoverHandler coverHandler;

    @Override
    @Operation(summary = "카드 생성", description = "")
    public ResponseEntity<DefaultResponse<CardDetailResponseDto>> createCard(
            @AuthenticationPrincipal Member member,
            @RequestBody CardCreateRequestDto cardCreateRequestDto) {

        Card card = cardService.createCard(member, cardCreateRequestDto);

        CardDetailResponseDto cardSimpleResponseDto = CardDetailResponseDto.from(card);

        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.CREATED, ResponseMessage.CARD_CREATE_SUCCESS, cardSimpleResponseDto),
                HttpStatus.CREATED
        );
    }

    @Override
    @Operation(summary = "카드 삭제", description = "")
    public ResponseEntity<DefaultResponse<Void>> deleteCard(@AuthenticationPrincipal Member member,
                                                            @PathVariable Long cardId) {

        cardService.deleteCard(member, cardId);
        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_DELETE_SUCCESS)
        );
    }

    @Override
    @Operation(summary = "카드 수정", description = "")
    public ResponseEntity<DefaultResponse<CardDetailResponseDto>> updateCard(
            @AuthenticationPrincipal Member member,
            @PathVariable Long cardId,
            @RequestBody CardUpdateRequestDto cardUpdateRequestDto) {

        Card card = cardService.updateCard(member, cardId, cardUpdateRequestDto);
        Map<String, Object> cover = card.getCover();

        // coverType과 coverValue는 기본값을 null로 설정하여 필요할 때만 값 할당
        String coverType = cover.isEmpty() ? null : coverHandler.getTypeValue(cover);
        String coverValue = cover.isEmpty() ? null : coverHandler.getValue(cover);

        CardDetailResponseDto cardDetailResponseDto = CardDetailResponseDto.from(card, coverType, coverValue);

        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_UPDATE_SUCCESS, cardDetailResponseDto)
        );
    }

    @Override
    @Operation(summary = "카드 아카이브 리스트 조회", description = "")
    public ResponseEntity<DefaultResponse<CardArchiveCollectionResponseDto>> getArchivedCardList(
            @AuthenticationPrincipal Member member,
            @PathVariable Long boardId) {

        List<Card> archivedCards = cardService.getArchivedCardList(member, boardId);
        List<CardSimpleResponseDto> cardSimpleResponseDtoList = new ArrayList<>();
        for (Card card : archivedCards) {
            cardSimpleResponseDtoList.add(CardSimpleResponseDto.of(card));
        }

        CardArchiveCollectionResponseDto dto = new CardArchiveCollectionResponseDto(cardSimpleResponseDtoList);

        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.ARCHIVED_CARD_LIST_SUCCESS, dto)
        );
    }

    @Override
    @Operation(summary = "카드 아카이브 여부 변경", description = "")
    public ResponseEntity<DefaultResponse<Void>> changeArchiveStatusByCard(
            @AuthenticationPrincipal Member member,
            @PathVariable Long cardId) {

        cardService.changeArchiveStatusByCard(member, cardId);

        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_ARCHIVE_STATUS_CHANGE_SUCCESS)
        );
    }

    @Override
    @Operation(summary = "카드의 액티비티 목록 조회", description = "카드의 액티비티 목록 조회")
    public ResponseEntity<DefaultResponse<List<CardLogDetailResponseDto>>> getCardActivity(Long cardId) {
        List<CardLogDetailResponseDto> cardActivity = cardService.getCardActivity(cardId);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_ACTIVITY_FETCH_SUCCESS, cardActivity),
                HttpStatus.OK);
    }

    @Parameters({
            @Parameter(name = "page", description = "조회할 페이지 번호 (1부터 시작)", example = "1", schema = @Schema(defaultValue = "1")),
            @Parameter(name = "size", description = "페이지당 항목 수", example = "10", schema = @Schema(defaultValue = "10"))
    })
    @Override
    public ResponseEntity<DefaultResponse<CardCombinedActivityResponseDto>> getCardCombinedLog(
            @PathVariable Long cardId,
            @RequestParam int page,
            @RequestParam int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        CardCombinedActivityResponseDto combinedLogs = cardService.getCardCombinedLog(cardId, pageable);

        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_ACTIVITY_FETCH_SUCCESS, combinedLogs)
        );
    }

    @Override
    @Operation(summary = "다른 리스트의 맨 위로 카드 이동", description = "특정 카드를 지정된 이동할 리스트의 맨 위로 이동합니다.")
    public ResponseEntity<DefaultResponse<CardMoveResult>> moveCardToTop(
            @Parameter(description = "현재 사용자 정보", required = true) Member member,
            @Parameter(description = "이동할 카드의 ID", required = true) @PathVariable Long cardId,
            @Parameter(description = "이동할 리스트의 ID", required = true) @PathVariable Long targetListId) {

        CardMoveResult result = cardMoveService.moveCardToTop(member, cardId, targetListId);

        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_MOVE_SUCCESS, result),
                HttpStatus.OK);
    }


    @Override
    @Operation(summary = "다른 리스트의 맨 아래로 카드 이동", description = "특정 카드를 지정된 이동할 리스트의 맨 아래로 이동합니다.")
    public ResponseEntity<DefaultResponse<CardMoveResult>> moveCardToBottom(
            @Parameter(description = "현재 사용자 정보", required = true) Member member,
            @Parameter(description = "이동할 카드의 ID", required = true) @PathVariable Long cardId,
            @Parameter(description = "이동할 리스트의 ID", required = true) @PathVariable Long targetListId) {

        CardMoveResult result = cardMoveService.moveCardToBottom(member, cardId, targetListId);

        // FIXME: 반환 값과 응답 값이 로컬 서버에서 다른 경우 수정해야함.
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_MOVE_SUCCESS, result),
                HttpStatus.OK);
    }

    @Override
    @Operation(summary = "다른 리스트의 카드들 사이로 이동", description = "특정 카드를 지정된 이동할 리스트의 두 카드 사이에 위치시킵니다.")
    public ResponseEntity<DefaultResponse<CardMoveResult>> moveCardBetween(
            @Parameter(description = "현재 사용자 정보", required = true) @AuthenticationPrincipal Member member,
            @Parameter(description = "이동할 카드의 ID", required = true) @PathVariable Long cardId,
            @Parameter(description = "이전 카드의 ID", required = true) @RequestParam Long previousCardId,
            @Parameter(description = "다음 카드의 ID", required = true) @RequestParam Long nextCardId) {

        CardMoveResult result = cardMoveService.moveCardBetween(member, cardId, previousCardId, nextCardId);
        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_MOVE_SUCCESS, result));
    }


    @Override
    @Operation(summary = "리스트 내의 카드 조회")
    public ResponseEntity<DefaultResponse<List<CardSimpleResponseDto>>> getCardsByListId(@PathVariable Long listId) {
        List<CardSimpleResponseDto> cards = cardService.getCardsByListId(listId);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_FETCH_SUCCESS, cards)
                , HttpStatus.OK);
    }
}
