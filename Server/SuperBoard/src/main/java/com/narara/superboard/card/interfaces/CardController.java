package com.narara.superboard.card.interfaces;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.card.interfaces.dto.CardDetailResponseDto;
import com.narara.superboard.card.interfaces.dto.CardSimpleResponseDto;
import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.card.service.CardService;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;
import java.util.ArrayList;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@Controller
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController implements CardAPI {

    private final CardService cardService;
    private final CoverHandler coverHandler;

    @PostMapping("/")
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
    @DeleteMapping("/{cardId}")
    public ResponseEntity<DefaultResponse<Void>> deleteCard(@AuthenticationPrincipal Member member,
                                                            @PathVariable Long cardId) {

        cardService.deleteCard(member, cardId);
        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_DELETE_SUCCESS)
        );
    }

    @Override
    @PatchMapping("/{cardId}")
    public ResponseEntity<DefaultResponse<CardDetailResponseDto>> updateCard(
            @AuthenticationPrincipal Member member,
            @PathVariable Long cardId,
            @RequestBody CardUpdateRequestDto cardUpdateRequestDto) {

        Card card = cardService.updateCard(member, cardId, cardUpdateRequestDto);
        Map<String, Object> cover = card.getCover();

        // coverType과 coverValue는 기본값을 null로 설정하여 필요할 때만 값 할당
        String coverType = cover.isEmpty() ? null : coverHandler.getTypeValue(cover);
        String coverValue = cover.isEmpty() ? null : coverHandler.getValue(cover);

        CardDetailResponseDto cardDetailResponseDto = CardDetailResponseDto.of(card, coverType, coverValue);

        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_UPDATE_SUCCESS, cardDetailResponseDto)
        );
    }


    @Override
    @GetMapping("/archived/{boardId}")
    public ResponseEntity<DefaultResponse<List<CardSimpleResponseDto>>> getArchivedCardList(
            @AuthenticationPrincipal Member member,
            @PathVariable Long boardId) {

        List<Card> archivedCards = cardService.getArchivedCardList(member, boardId);
        List<CardSimpleResponseDto> cardSimpleResponseDtoList = new ArrayList<>();
        for (Card card : archivedCards) {
            cardSimpleResponseDtoList.add(CardSimpleResponseDto.of(card));
        }
        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.ARCHIVED_CARD_LIST_SUCCESS,
                        cardSimpleResponseDtoList)
        );
    }

    @Override
    @PatchMapping("/{cardId}/archive")
    public ResponseEntity<DefaultResponse<Void>> changeArchiveStatusByCard(
            @AuthenticationPrincipal Member member,
            @PathVariable Long cardId) {

        cardService.changeArchiveStatusByCard(member, cardId);

        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_ARCHIVE_STATUS_CHANGE_SUCCESS)
        );
    }
}
