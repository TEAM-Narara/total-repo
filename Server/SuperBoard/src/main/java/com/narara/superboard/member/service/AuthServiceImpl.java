package com.narara.superboard.member.service;

import com.narara.superboard.common.infrastructure.redis.RedisService;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.exception.AccountDeletedException;
import com.narara.superboard.member.exception.InvalidCredentialsException;
import com.narara.superboard.member.exception.MemberNotFoundException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import com.narara.superboard.member.interfaces.dto.TokenDto;
import com.narara.superboard.member.interfaces.dto.VerifyEmailCodeRequestDto;
import com.narara.superboard.member.service.validator.MemberValidator;
import com.narara.superboard.member.util.JwtTokenProvider;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.ArrayList;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberValidator memberValidator;

    @Override
    public TokenDto register(MemberCreateRequestDto memberCreateRequestDto) {
        memberValidator.registerValidate(memberCreateRequestDto);

        Member newMember = createNewMember(memberCreateRequestDto);
        TokenDto tokenDto = createTokens(newMember);
        saveRefreshToken(newMember, tokenDto.refreshToken());

        return tokenDto;
    }

    @Override
    public TokenDto login(MemberLoginRequestDto memberLoginRequestDto) {
        memberValidator.loginValidate(memberLoginRequestDto);

        Member member = findMemberByEmail(memberLoginRequestDto.email());

        checkAccountStatus(member);
        validatePassword(memberLoginRequestDto.password(), member.getPassword());
        TokenDto tokenDto = createTokens(member);
        saveRefreshToken(member, tokenDto.refreshToken());

        return tokenDto;
    }

    @Override
    public void logout(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // Refresh Token을 null로 설정
        member.setRefreshToken(null);
        memberRepository.save(member);
    }

    @Override
    public void withdrawal(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if(member.getIsDeleted()) throw new AccountDeletedException();

        member.setRefreshToken(null);
        member.setIsDeleted(true);
        memberRepository.save(member);
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        // 1. refreshToken 검증
        // 검증 실패시, Header에 실패했다는 것을 담기(?)
        // 2. refreshToken 기반 사용자 정보 조회
        // 3. refreshToken과 해당 사용자의 db에 저장되 refreshToken과 동일한지 비교
        // 4. accessToken 재발급
        return null;
    }

    // 새로운 회원 엔티티 생성 및 저장
    private Member createNewMember(MemberCreateRequestDto memberCreateRequestDto) {
        String encodedPassword = passwordEncoder.encode(memberCreateRequestDto.password());

        Member newMember = Member.builder()
                .nickname(memberCreateRequestDto.nickname())
                .email(memberCreateRequestDto.email())
                .password(encodedPassword)
                .build();

        return memberRepository.save(newMember);
    }

    // 토큰 생성 (AccessToken 및 RefreshToken)
    private TokenDto createTokens(Member member) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId().toString(), null, new ArrayList<>());
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        return new TokenDto(accessToken, refreshToken);
    }

    // RefreshToken을 회원 엔티티에 저장
    private void saveRefreshToken(Member member, String refreshToken) {
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
    }

    private void checkAccountStatus(Member member) {
        if (Boolean.TRUE.equals(member.getIsDeleted())) {
            throw new AccountDeletedException();
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new InvalidCredentialsException();
        }
    }

}
