package com.narara.superboard.member.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final String SECRET_KEY = "your_secret_key"; // 비밀 키
    private final long VALIDITY_TIME = 3600000; // 1시간 유효 시간
    private final UserDetailsService userDetailsService;


    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

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

    public String resolveAccessToken(HttpServletRequest request) {
        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return null;
    }

    public boolean isTokenExpired(String token) {
        return false;
    }

    public boolean validateRefeshToken(String token) {
        return false;
    }

    public String refeshAccessToken(String token) {
        return null;
    }

}
