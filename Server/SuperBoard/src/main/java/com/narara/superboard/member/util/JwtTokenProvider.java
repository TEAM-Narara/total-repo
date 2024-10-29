package com.narara.superboard.member.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface JwtTokenProvider {
    String generateAccessToken(Authentication authentication);
    String generateRefreshToken(Authentication authentication);
    Authentication getAuthentication(String token);
    boolean validateToken(String token);
    Long getMemberIdFromToken(String token);
    String resolveAccessToken(HttpServletRequest request);
    String resolveRefreshToken(HttpServletRequest request);
    boolean isTokenExpired(String token);
    String refreshAccessToken(String refreshToken);
}
