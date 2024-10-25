package com.narara.superboard.member.util;

import com.narara.superboard.common.exception.TokenException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static com.narara.superboard.common.exception.TokenException.INVALID_JWT_SIGNATURE;
import static com.narara.superboard.common.exception.TokenException.INVALID_TOKEN;

@Component
@RequiredArgsConstructor
public class JwtTokenProviderImpl implements JwtTokenProvider {

    @Value("${spring.jwt.secretKey}")
    private String key; //비밀키
    private SecretKey secretKey;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 10L; //10분 
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24; //1일

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
        // TODO : DB에 저장

        return refreshToken;
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
                .signWith(secretKey,Jwts.SIG.HS512)
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
     * 주어진 JWT 토큰을 기반으로 사용자의 인증 정보를 가져오는 기능
     * @param token
     * @return
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String memberId = claims.getSubject();
        // UserDetails userDetails = userDetailsService.loadUserByUsername(memberId);
        // return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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
