package com.bpe.security.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.filter.OncePerRequestFilter;

import com.bpe.security.common.AuthenticatedUser;
import com.bpe.security.common.SecurityContextHolderExt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class SecurityContextFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-Auth-UserId";
    private static final String HEADER_ROLES = "X-Auth-Roles";
    private static final String HEADER_SESSION_ID = "X-Auth-SessionId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String userId = request.getHeader(HEADER_USER_ID);
            String rolesHeader = request.getHeader(HEADER_ROLES);
            String sessionId = request.getHeader(HEADER_SESSION_ID);

            if (userId != null && rolesHeader != null && sessionId != null) {

                List<String> roles = Arrays.stream(rolesHeader.split(","))
                        .map(String::trim)
                        .toList();

                AuthenticatedUser user = new AuthenticatedUser(
                        userId,
                        roles,
                        sessionId
                );

                SecurityContextHolderExt.setUser(user);
            }

            filterChain.doFilter(request, response);

        } finally {
            // 🔴 MUST clear to avoid thread leaks
            SecurityContextHolderExt.clear();
        }
    }
}
