package com.narara.superboard.member.service;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.exception.AccountDeletedException;
import com.narara.superboard.member.exception.InvalidCredentialsException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import com.narara.superboard.member.interfaces.dto.TokenDto;
import com.narara.superboard.member.interfaces.dto.VerifyEmailCodeRequestDto;
import com.narara.superboard.member.service.validator.MemberValidator;
import com.narara.superboard.member.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberValidator memberValidator;

    @Override
    public void sendEmailVerificationCode(String email) {

    }

    @Override
    public void verifyEmailCode(VerifyEmailCodeRequestDto verifyEmailCodeRequestDto) {

    }

    @Override
    public TokenDto register(MemberCreateRequestDto memberCreateRequestDto) {
        memberValidator.registerValidate(memberCreateRequestDto);

        Member newMember = createNewMember(memberCreateRequestDto);
        Authentication authentication = createAuthentication(newMember);
        TokenDto tokenDto = createTokens(authentication);
        saveRefreshToken(newMember, tokenDto.refreshToken());

        return tokenDto;
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

    // 인증 객체 생성
    private Authentication createAuthentication(Member member) {
        return new UsernamePasswordAuthenticationToken(member.getId().toString(), null, new ArrayList<>());
    }

    // 토큰 생성 (AccessToken 및 RefreshToken)
    private TokenDto createTokens(Authentication authentication) {
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        return new TokenDto(accessToken, refreshToken);
    }

    // RefreshToken을 회원 엔티티에 저장
    private void saveRefreshToken(Member member, String refreshToken) {
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);
    }

    @Override
    public TokenDto login(MemberLoginRequestDto memberLoginRequestDto) {
        // 1. 유효성 검증
        memberValidator.loginValidate(memberLoginRequestDto);

        // 2. 이메일로 사용자 찾기 (없으면 예외 발생)
        Member member = memberRepository.findByEmail(memberLoginRequestDto.email())
                .orElseThrow(InvalidCredentialsException::new);

        // 3. 탈퇴된 계정인 경우 예외 발생
        if (Boolean.TRUE.equals(member.getIsDeleted())) {
            throw new AccountDeletedException();
        }

        // 4. 비밀번호가 일치하지 않으면 예외 발생
        if (!passwordEncoder.matches(memberLoginRequestDto.password(), member.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // 5. 인증 객체 생성 및 토큰 발급
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId().toString(), null, new ArrayList<>());
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        return new TokenDto(accessToken, refreshToken);
    }

    @Override
    public void logout(Long memberId) {

    }

    @Override
    public void withdrawal(Long memberId) {

    }

    @Override
    public void refreshAccessToken(String refreshToken) {

    }
}
