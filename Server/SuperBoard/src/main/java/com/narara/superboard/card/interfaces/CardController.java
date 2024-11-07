package com.narara.superboard.card.interfaces;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.interfaces.dto.*;
import com.narara.superboard.card.interfaces.dto.log.CardActivityDetailResponseDto;
import com.narara.superboard.card.service.CardService;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.interfaces.log.ActivityDetailResponseDto;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;
import java.util.ArrayList;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "카드")
@CrossOrigin
@Controller
@RequiredArgsConstructor
public class CardController implements CardAPI {

    private final CardService cardService;
    private final CoverHandler coverHandler;

    @Override
    @Operation(summary = "카드 생성", description = "")
    public ResponseEntity<DefaultResponse<CardSimpleResponseDto>> createCard(
            @AuthenticationPrincipal Member member,
            @RequestBody CardCreateRequestDto cardCreateRequestDto) {

        Card card = cardService.createCard(member, cardCreateRequestDto);

        CardSimpleResponseDto cardSimpleResponseDto = CardSimpleResponseDto.of(card);

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
    public ResponseEntity<DefaultResponse<List<CardActivityDetailResponseDto>>> getCardActivity(Long cardId) {
        List<CardActivityDetailResponseDto> cardActivity = cardService.getCardActivity(cardId);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_ACTIVITY_FETCH_SUCCESS, cardActivity), HttpStatus.OK);
    }
}
