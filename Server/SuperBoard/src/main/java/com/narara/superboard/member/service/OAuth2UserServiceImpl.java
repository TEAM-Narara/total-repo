package com.narara.superboard.member.service;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.enums.LoginType;
import com.narara.superboard.member.enums.OAuthAttributes;
import com.narara.superboard.member.exception.AlreadyRegisteredLoginException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.member.interfaces.dto.MemberProfile;
import com.narara.superboard.member.interfaces.dto.TokenDto;
import com.narara.superboard.member.util.JwtTokenProvider;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate(); // @Bean으로 주입받도록 수정

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String naverUserInfoUri;

    @Value("${spring.security.oauth2.client.provider.github.user-info-uri}")
    private String githubUserInfoUri;

    private static final String PROVIDER_NAVER = "naver";
    private static final String PROVIDER_GITHUB = "github";

    @Override
    public TokenDto getUserInfo(String accessToken, String provider) {
        log.info("getUserInfo called with provider: {}", provider);

        Map<String, Object> attributes = fetchUserInfo(accessToken, provider);
        log.debug("User attributes fetched: {}", attributes);

        MemberProfile memberProfile = OAuthAttributes.extract(provider, attributes);
        log.debug("Extracted MemberProfile: {}", memberProfile);

        Member member = saveOrUpdateMember(memberProfile, provider);
        log.info("Member saved or updated with ID: {}", member.getId());

        return generateTokenDto(member);
    }

    private Map<String, Object> fetchUserInfo(String accessToken, String provider) {
        log.info("Fetching user info from provider: {}", provider);

        String userInfoUri = getUserInfoUri(provider);
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(accessToken));

        try {
            ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, Map.class);
            log.debug("User info response: {}", response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Failed to fetch user info from {}: {}", provider, e.getMessage());
            throw new RuntimeException("Failed to fetch user info from provider");
        }
    }

    private String getUserInfoUri(String provider) {
        log.debug("Resolving user info URI for provider: {}", provider);
        return PROVIDER_NAVER.equalsIgnoreCase(provider) ? naverUserInfoUri : githubUserInfoUri;
    }

    private HttpHeaders createHeaders(String accessToken) {
        log.debug("Creating headers with access token.");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }

    private Member saveOrUpdateMember(MemberProfile memberProfile, String provider) {
        log.info("Saving or updating member with email: {}", memberProfile.email());

        return memberRepository.findByEmail(memberProfile.email())
                .map(existingMember -> {
                    log.debug("Member with email {} exists. Validating provider type.", memberProfile.email());
                    return validateExistingMember(existingMember, provider, memberProfile);
                })
                .orElseGet(() -> {
                    log.debug("Creating new member for email: {}", memberProfile.email());
                    return createNewMember(memberProfile, provider);
                });
    }

    private Member validateExistingMember(Member existingMember, String provider, MemberProfile memberProfile) {
        log.debug("Validating existing member with provider type: {}", provider);

        if (!existingMember.getLoginType().name().equalsIgnoreCase(provider)) {
            log.error("Already registered with different provider: {}", existingMember.getLoginType().name());
            throw new AlreadyRegisteredLoginException(memberProfile.email(), existingMember.getLoginType().name());
        }
        return existingMember;
    }

    private Member createNewMember(MemberProfile memberProfile, String provider) {
        log.info("Creating a new member for provider: {}", provider);

        Member newMember = Member.builder()
                .nickname(memberProfile.nickname())
                .email(memberProfile.email())
                .loginType(LoginType.valueOf(provider.toUpperCase()))
                .build();

        Member savedMember = memberRepository.save(newMember);
        log.info("New member created with ID: {}", savedMember.getId());

        return savedMember;
    }

    private TokenDto generateTokenDto(Member member) {
        log.info("Generating TokenDto for member ID: {}", member.getId());

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId().toString(), null, new ArrayList<>());
        String jwtAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        log.debug("Generated access token: {}", jwtAccessToken);
        log.debug("Generated refresh token: {}", jwtRefreshToken);

        return new TokenDto(jwtAccessToken, jwtRefreshToken);
    }

}
