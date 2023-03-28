package com.project.member.config;

import com.project.member.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * packageName   : com.project.member.config
 * fileName      : JwtFilter
 * author        : kang_jungwoo
 * date          : 2023/03/27
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/27       kang_jungwoo         최초 생성
 */
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtil.resolveAccessToken((HttpServletRequest) request);

        if(token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 여부
        if(!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        setAuthentication(token);
        filterChain.doFilter(request, response);
    }

    // SecurityContext 에 Authentication 객체 저장
    public void setAuthentication(String token) {
        // 토큰으로부터 유저 정보를 받아옴
        Authentication authentication = jwtUtil.getAuthentication(token);
        // SecurityContext 에 Authentication 객체를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
