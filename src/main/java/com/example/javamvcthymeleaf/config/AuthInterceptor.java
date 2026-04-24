package com.example.javamvcthymeleaf.config;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final boolean authEnabled;

    public AuthInterceptor(@Value("${app.auth.enabled:true}") boolean authEnabled) {
        this.authEnabled = authEnabled;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!authEnabled) {
            return true;
        }

        String uri = request.getRequestURI();

        if (isPublicPath(uri)) {
            return true;
        }

        Object userId = request.getSession(false) != null ? request.getSession(false).getAttribute(AuthSession.USER_ID) : null;
        if (userId != null) {
            return true;
        }

        if (uri.startsWith("/api/")) {
            writeApiUnauthorized(response);
            return false;
        }

        response.sendRedirect("/login");
        return false;
    }

    private boolean isPublicPath(String uri) {
        return uri.equals("/login")
                || uri.equals("/api/auth/login")
                || uri.equals("/error")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/webjars/")
                || uri.startsWith("/h2-console");
    }

    private void writeApiUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"Não autenticado\"}");
    }
}
