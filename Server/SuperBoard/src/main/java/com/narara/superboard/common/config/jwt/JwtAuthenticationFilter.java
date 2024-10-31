package com.narara.superboard.common.config.jwt;

import com.narara.superboard.member.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 JWT 토큰을 추출
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        if (jwtTokenProvider.validateToken(accessToken)) {
            setAuthenticationContext(accessToken, request);
        }

        filterChain.doFilter(request,response);
    }

    /**
     * SecurityContext에 인증 정보를 설정
     * @param token 유효한 JWT 토큰
     * @param request HttpServletRequest 객체
     */
    private void setAuthenticationContext(String token, HttpServletRequest request) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        if (authentication == null) {
            return; // 인증이 null일 경우 바로 리턴
        }

        // UsernamePasswordAuthenticationToken을 설정하여 인증 정보를 컨텍스트에 등록
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                null,
                authentication.getAuthorities()
        );

        // 요청 정보를 설정한 뒤, SecurityContext에 등록
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
