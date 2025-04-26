package com.x1.groo.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {
        log.error(authException.getMessage(), authException);
        if (authException instanceof InsufficientAuthenticationException) {
            // 인증에 부족한 부분이 있을 경우
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        } else {
            // 인증 실패 시 기본적으로 401 반환
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized Access");
        }
    }
}