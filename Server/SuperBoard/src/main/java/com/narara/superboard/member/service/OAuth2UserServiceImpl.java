package com.narara.superboard.member.service;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.enums.LoginType;
import com.narara.superboard.member.enums.OAuthAttributes;
import com.narara.superboard.member.exception.AlreadyRegisteredLoginException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.member.interfaces.dto.MemberDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginResponseDto;
import com.narara.superboard.member.interfaces.dto.MemberProfile;
import com.narara.superboard.member.interfaces.dto.TokenDto;
import com.narara.superboard.member.util.JwtTokenProvider;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate(); // @Bean으로 주입받도록 수정

    private final WorkSpaceService workSpaceService;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String naverUserInfoUri;

    @Value("${spring.security.oauth2.client.provider.github.user-info-uri}")
    private String githubUserInfoUri;

    private static final String PROVIDER_NAVER = "naver";
    private static final String PROVIDER_GITHUB = "github";

    @Override
    public MemberLoginResponseDto getUserInfo(String accessToken, String provider) {
        Map<String, Object> attributes = fetchUserInfo(accessToken, provider);
        MemberProfile memberProfile = OAuthAttributes.extract(provider, attributes);
        Member member = saveOrUpdateMember(memberProfile, provider);

        MemberDto memberDto = new MemberDto(member.getId(), member.getEmail(), member.getNickname(), member.getProfileImgUrl());
        TokenDto tokenDto = generateTokenDto(member);

        return new MemberLoginResponseDto(memberDto, tokenDto);
    }

    private Map<String, Object> fetchUserInfo(String accessToken, String provider) {

        String userInfoUri = getUserInfoUri(provider);
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(accessToken));

        try {
            ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch user info from provider");
        }
    }

    private String getUserInfoUri(String provider) {
        return PROVIDER_NAVER.equalsIgnoreCase(provider) ? naverUserInfoUri : githubUserInfoUri;
    }

    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }

    private Member saveOrUpdateMember(MemberProfile memberProfile, String provider) {
        return memberRepository.findByEmail(memberProfile.email())
                .map(existingMember -> validateExistingMember(existingMember, provider, memberProfile))
                .orElseGet(() -> createNewMember(memberProfile, provider));
    }

    private Member validateExistingMember(Member existingMember, String provider, MemberProfile memberProfile) {

        if (!existingMember.getLoginType().name().equalsIgnoreCase(provider)) {

            throw new AlreadyRegisteredLoginException(memberProfile.email(), existingMember.getLoginType().name());
        }
        return existingMember;
    }

    private Member createNewMember(MemberProfile memberProfile, String provider) {

        Member newMember = Member.builder()
                .nickname(memberProfile.nickname())
                .email(memberProfile.email())
                .loginType(LoginType.valueOf(provider.toUpperCase()))
                .build();

        Member member = memberRepository.save(newMember);
        workSpaceService.createWorkSpace(member.getId(), new WorkSpaceCreateRequestDto(member.getNickname() + "의 워크스페이스"));

        return member;
    }

    private TokenDto generateTokenDto(Member member) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId().toString(), null, new ArrayList<>());
        String jwtAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        return new TokenDto(jwtAccessToken, jwtRefreshToken);
    }

}
