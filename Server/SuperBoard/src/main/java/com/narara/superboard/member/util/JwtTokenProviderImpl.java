package com.narara.superboard.member.util;

import com.narara.superboard.common.entity.CustomUserDetails;
import com.narara.superboard.common.exception.TokenException;
import com.narara.superboard.common.service.CustomUserDetailsService;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.exception.InvalidRefreshTokenException;
import com.narara.superboard.member.exception.MemberNotFoundException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static com.narara.superboard.common.exception.TokenException.INVALID_JWT_SIGNATURE;
import static com.narara.superboard.common.exception.TokenException.INVALID_TOKEN;

@Component
@RequiredArgsConstructor
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private final CustomUserDetailsService customUserDetailsService;
    @Value("${spring.jwt.secretKey}")
    private String key; //비밀키
    private SecretKey secretKey;
    @Value("${token.expire-time.access-token}")
    private long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${token.expire-time.refresh-token}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    private final MemberRepository memberRepository;

    // 시크릿키 암호화
    @PostConstruct
    private void setSecretKey() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }

    /**
     * AccessToken 생성
     * @param authentication
     * @return
     */
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication,ACCESS_TOKEN_EXPIRE_TIME);
    }

    /**
     * RefreshToken 생성
     * @param authentication
     * @return
     */
    public String generateRefreshToken(Authentication authentication) {
        String refreshToken = generateToken(authentication,REFRESH_TOKEN_EXPIRE_TIME);
        // Member 정보 업데이트 및 저장
        updateMemberRefreshToken(authentication.getName(), refreshToken);
        return refreshToken;
    }

    /**
     * Member 엔티티의 refreshToken을 업데이트하고 저장하는 메서드
     * @param memberIdStr 사용자 ID
     * @param refreshToken 새로 생성된 RefreshToken
     */
    private void updateMemberRefreshToken(String memberIdStr, String refreshToken) {
        Long memberId = Long.parseLong(memberIdStr);
        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        member.setRefreshToken(refreshToken);
        memberRepository.save(member);
    }

    /**
     * 토큰 생성
     * @param authentication
     * @param expireTime
     * @return
     */
    private String generateToken(Authentication authentication,long expireTime){
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 타당한 토큰인지 검증
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date());
    }

    /**
     * 주어진 JWT 토큰을 기반으로 사용자의 인증 정보를 가져오는 기능
     * @param token
     * @return
     */
    public Authentication getAuthentication(String token) {
        System.out.println("getAuthentication" + token);
        Claims claims = parseClaims(token);
        System.out.println(claims);
        String memberId = claims.getSubject(); //토큰에서 사용자Id 추출
        System.out.println("getAuthentication" + memberId);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(memberId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * JWT Claims를 파싱하고 검증
     * @param token
     * @return
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우 claims를 리턴 (다시 사용할 수 있도록)
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new TokenException(INVALID_TOKEN);
        } catch (SecurityException e) {
            throw new TokenException(INVALID_JWT_SIGNATURE);
        }catch (Exception e) {
            throw new TokenException("An unexpected error occurred while parsing the token.", e);
        }
    }

    /**
     * JWT 토큰에서 사용자id 추출
     * @param token
     * @return
     */
    public Long getMemberIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject()); //사용자 Id 반환
    }

    /**
     * 요청 헤더에서 Access Token 추출
     * @param request
     * @return
     */
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 요청 헤더에서 Refresh Token 추출
     * @param request
     * @return
     */
    public String resolveRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenException("Refresh Token is missing or invalid.");
        }
        return refreshToken;
    }

    /**
     * 토큰이 만료되었는지 확인
     * @param token
     * @return
     */
    public boolean isTokenExpired(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().before(new Date()); //만료된 경우 true 반환
    }

    /**
     * refreshToken이 유효한 경우, accessToken 재발급
     * @param refreshToken
     * @return
     */
    public String refreshAccessToken(String refreshToken) {
        // 1. refresh 토큰 유효한지 확인
        if (!validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        // 2. refreshToken에서 사용자 ID 추출
        Long memberId = getMemberIdFromToken(refreshToken);

        // 3. 해당 사용자의 정보를 가져옴
        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 4. refreshToken이 일치하는지 확인 (DB에 저장된 값과 비교)
        if (!refreshToken.equals(member.getRefreshToken())) {
            throw new InvalidRefreshTokenException();
        }

        // 5. 새로운 accessToken 발급
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(memberId.toString());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        return generateAccessToken(authentication);
    }

}
