package com.narara.superboard.member.interfaces;

import com.narara.superboard.member.interfaces.dto.VerifyEmailCodeRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "이메일", description = "이메일 관련 API")
@RequestMapping("/api/v1/members")
public interface EmailAPI {

    @Operation(summary = "이메일 인증 코드 전송")
    @PostMapping("/email-code")
    ResponseEntity<?> sendEmailVerificationCode(String email);

    @Operation(summary = "이메일 인증")
    @PostMapping("/email-code/verify")
    ResponseEntity<?> verifyEmailCode(VerifyEmailCodeRequestDto verifyEmailCodeRequestDto);
}
