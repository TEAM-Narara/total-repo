package com.narara.superboard.member.service;

import com.narara.superboard.member.interfaces.dto.VerifyEmailCodeRequestDto;

public interface EmailService {
    void sendEmailVerificationCode(String email);
    void verifyEmailCode(VerifyEmailCodeRequestDto verifyEmailCodeRequestDto);
}
