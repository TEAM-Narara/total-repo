package com.narara.superboard.member.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.interfaces.dto.VerifyEmailCodeRequestDto;
import com.narara.superboard.member.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController implements EmailAPI{

    private final EmailService emailService;

    @Override
    public ResponseEntity<?> sendEmailVerificationCode(String email) {
        emailService.sendEmailVerificationCode(email);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.SEND_EMAIL_AUTHENTICATION_CODE), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> verifyEmailCode(VerifyEmailCodeRequestDto verifyEmailCodeRequestDto) {
        emailService.verifyEmailCode(verifyEmailCodeRequestDto);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.EMAIL_AUTHENTICATION_SUCCESS), HttpStatus.OK);
    }
}
