package com.narara.superboard.member.service.validator;

import com.narara.superboard.member.exception.*;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import com.narara.superboard.member.interfaces.dto.VerifyEmailCodeRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MemberValidator {

    // 회원가입 유효성 검증
    public void registerValidate(MemberCreateRequestDto memberCreateRequestDto) {
        validateEmail(memberCreateRequestDto.email());
        validateNickname(memberCreateRequestDto.nickname());
        validatePassword(memberCreateRequestDto.password());
    }

    // 로그인 유효성 검증
    public void loginValidate(MemberLoginRequestDto memberLoginRequestDto) {
        validateEmail(memberLoginRequestDto.email());
        validatePassword(memberLoginRequestDto.password());
    }

    // 이메일 인증 유효성 검증
    public void verifyEmailCodeValidate(VerifyEmailCodeRequestDto verifyEmailCodeRequestDto){
        validateEmail(verifyEmailCodeRequestDto.email());
        validateVerificationCode(verifyEmailCodeRequestDto.code());
    }



    // 이메일 검증
    public void validateEmail(String email) {
        if(!StringUtils.hasText(email)) {
            throw new MemberEmailNotFoundException();
        }

        if(!isValidEmailFormat(email)){
            throw new MemberInvalidEmailFormatException();
        }
    }

    // 닉네임 검증
    public void validateNickname(String nickname) {
        if(!StringUtils.hasText(nickname)) {
            throw new MemberNicknameNotFoundException();
        }
    }

    // 비밀번호 검증
    public void validatePassword(String password) {
        if(!StringUtils.hasText(password)) {
            throw new MemberPasswordNotFoundException();
        }

        if (password.length() < 4 || password.length() > 30) {
            throw new MemberInvalidPasswordFormatException();
        }
    }

    // 유저 검색어 검증
    public void validateSearchTerm(String searchTerm) {
        if(!StringUtils.hasText(searchTerm)) {
            throw new SearchTermNotFoundException();
        }

        if (searchTerm.length() < 3 ) {
            throw new InvalidSearchTermFormatException();
        }
    }

    // 인증 코드 검증
    public void validateVerificationCode(String code) {
        if(!StringUtils.hasText(code)) {
            throw new MemberVerificationCodeNotFoundException();
        }
    }

    // 이메일 형식 유효성 검사 (간단한 형식 검증 예시)
    private boolean isValidEmailFormat(String email) {
        // 소문자만 허용하는 정규식
        String emailRegex = "^[a-z0-9+_.-]+@[a-z0-9.-]+$";
        return email.matches(emailRegex);  // 이메일을 소문자 형식으로만 검증
    }
}
