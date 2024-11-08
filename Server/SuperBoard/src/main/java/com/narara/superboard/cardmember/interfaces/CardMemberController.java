package com.narara.superboard.cardmember.interfaces;

import com.narara.superboard.cardmember.interfaces.dto.UpdateCardMemberRequestDto;
import com.narara.superboard.cardmember.service.CardMemberService;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name = "카드 회원")
@RestController
@RequiredArgsConstructor
public class CardMemberController implements CardMemberAPI {

    private final CardMemberService cardMemberService;

    @Override
    public ResponseEntity<DefaultResponse<Boolean>> getCardMemberIsAlert(
            @AuthenticationPrincipal Member member,
            @PathVariable Long cardId) {

        boolean isAlert = cardMemberService.getCardMemberIsAlert(member, cardId);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_MEMBER_ALERT_STATUS_FETCH_SUCCESS, isAlert),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<Void>> setCardMemberIsAlert(
            @AuthenticationPrincipal Member member,
            @PathVariable Long cardId) {

        cardMemberService.setCardMemberIsAlert(member, cardId);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_MEMBER_ALERT_STATUS_UPDATE_SUCCESS),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<Void>> setCardMemberIsRepresentative(
            @RequestBody UpdateCardMemberRequestDto updateCardMemberRequestDto) {

        cardMemberService.setCardMemberIsRepresentative(updateCardMemberRequestDto);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.CARD_MEMBER_REPRESENTATIVE_UPDATE_SUCCESS),
                HttpStatus.OK
        );
    }
}
