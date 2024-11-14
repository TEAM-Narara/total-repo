package com.narara.superboard.common.config.jwt;

import com.narara.superboard.common.entity.CustomUserDetails;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.service.CustomUserDetailsService;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.member.util.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 JWT 토큰을 추출
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            setAuthenticationContext(accessToken, request);

            // @AuthenticationPrincipal annotation 을 사용한 인증 객체 생성

            Long userId = jwtTokenProvider.getMemberIdFromToken(accessToken);
            // 사용자 ID로 UserDetails 객체 생성
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(String.valueOf(userId));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            // SecurityContext에 인증 객체 저장

            SecurityContextHolder.getContext().setAuthentication(authentication);
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

        Long userId = Long.valueOf(authentication.getName());
        Member member = memberRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new NotFoundEntityException(userId, "멤버"));
        CustomUserDetails customUserDetails = new CustomUserDetails(member, userId, authentication.getAuthorities());
        System.out.println("member Email: " + member.getEmail());
        System.out.println("member nickname: " + member.getNickname());

        // CustomUserDetails를 이용해 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 요청 정보를 설정하여 SecurityContext에 등록
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

//        // UsernamePasswordAuthenticationToken을 설정하여 인증 정보를 컨텍스트에 등록
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                authentication.getPrincipal(),
//                null,
//                authentication.getAuthorities()
//        );
//
//        // 요청 정보를 설정한 뒤, SecurityContext에 등록
//        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
