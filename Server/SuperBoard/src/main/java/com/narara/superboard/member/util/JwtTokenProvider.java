package com.narara.superboard.member.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface JwtTokenProvider {
    public String generateAccessToken(Authentication authentication);
    public String generateRefreshToken(Authentication authentication);
    public Authentication getAuthentication(String token);
    public boolean validateToken(String token);
    public Long getMemberIDFromToken(String token);
    public String resolveAccessToken(HttpServletRequest request);
    public String resolveRefreshToken(HttpServletRequest request);
    public boolean isTokenExpired(String token);
    public String refreshAccessToken(String accessToken);

}
