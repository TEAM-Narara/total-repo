package com.narara.superboard.cardlabel.interfaces;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.service.CardService;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.cardlabel.interfaces.dto.CardLabelDto;
import com.narara.superboard.cardlabel.service.CardLabelService;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.service.LabelService;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "카드 라벨")
@CrossOrigin
@Controller
@RequiredArgsConstructor
public class CardLabelController implements CardLabelAPI {

    private final CardLabelService cardLabelService;
    private final CardService cardService;
    private final LabelService labelService;

    @Override
    public ResponseEntity<DefaultResponse<CardLabelDto>> createCardLabel(
            @AuthenticationPrincipal Member member,
            @RequestBody Long cardId,
            @RequestBody Long labelId) {

        Card card = cardService.getCard(cardId);
        Label label = labelService.getLabel(labelId);
        CardLabel cardLabel = cardLabelService.createCardLabel(card, label);
        CardLabelDto cardLabelDto = CardLabelDto.of(cardLabel);

        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.CREATED, ResponseMessage.CARD_LABEL_CREATE_SUCCESS, cardLabelDto),
                HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<CardLabelDto>> changeCardLabelIsActivated(
            @AuthenticationPrincipal Member member,
            @RequestBody Long cardId,
            @RequestBody Long labelId) {

        Card card = cardService.getCard(cardId);
        Label label = labelService.getLabel(labelId);
        CardLabel updatedCardLabel = cardLabelService.changeCardLabelIsActivated(card, label);
        CardLabelDto cardLabelDto = CardLabelDto.of(updatedCardLabel);

        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_LABEL_ACTIVATION_STATUS_CHANGED, cardLabelDto)
                ,HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<List<CardLabelDto>>> getCardLabelCollection(@PathVariable Long cardId) {
        List<CardLabelDto> cardLabelCollection = cardLabelService.getCardLabelCollection(cardId);

        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_LABEL_COLLECTION_FETCH_SUCCESS, cardLabelCollection)
                ,HttpStatus.OK
        );
    }
}
