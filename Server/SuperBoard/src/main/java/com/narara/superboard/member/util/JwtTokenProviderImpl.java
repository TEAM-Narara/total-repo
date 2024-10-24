package com.narara.superboard.member.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JwtTokenProviderImpl implements JwtTokenProvider {

    // @Value("${jwt.key}")
    private String key; //비밀키
    private SecretKey SECRET_KEY;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 10L; //10분 
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24; //1일


    public String generateAccessToken(Authentication authentication) {
        return null;
    }

    public String generateRefreshToken(Authentication authentication) {
        return null;
    }

    public boolean validateToken(String token) {
        return false;
    }

    public Authentication getAuthentication(String token) {
        return null;
    }

    public Long getMemberIDFromToken(String token) {return null;}

    public String resolveAccessToken(HttpServletRequest request) {
        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return null;
    }

    public boolean isTokenExpired(String token) {
        return false;
    }

    public String refreshAccessToken(String token) {
        return null;
    }

}
