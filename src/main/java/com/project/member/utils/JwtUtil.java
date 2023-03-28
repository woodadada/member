package com.project.member.utils;

import com.project.member.constant.DictionaryKey;
import com.project.member.service.MemberDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * packageName   : com.project.member.utils
 * fileName      : JwtUtil
 * author        : kang_jungwoo
 * date          : 2023/03/27
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/27       kang_jungwoo         최초 생성
 */
@Component
public class JwtUtil {

    @Autowired
    private MemberDetailsService memberDetailsService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    // 토큰 유효시간 (10시간)
    private long tokenValidTime = 10 * 60 * 60 * 1000L;

    public String createJwt(String email) {
        Claims claims = Jwts.claims();
        claims.put(DictionaryKey.EMAIL.getKey(), email);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(DictionaryKey.BEARER.getKey())) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String jwtToken) {
        if (jwtToken == null) {
            return false;
        }
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // 인증 정보 조회
    public Authentication getAuthentication(String token) {
        String email = this.getEmail(token);
        UserDetails userDetails = memberDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(DictionaryKey.EMAIL.getKey(), String.class);
    }
}
