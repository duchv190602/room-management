package com.vietsoftware.roommanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietsoftware.roommanagement.dto.response.ErrorResponse;
import com.vietsoftware.roommanagement.enums.ApiPermission;
import com.vietsoftware.roommanagement.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom Authorization filter enforcing role-based access control (RBAC) per request URI and HTTP method.
 *
 * <p>Authorization rules are defined in {@link ApiPermission} which serves as the single source of truth.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    /**
     * Filters request execution by matching requested URI and method against {@link ApiPermission} rules and user roles.
     *
     * @param request     incoming HTTP servlet request
     * @param response    outgoing HTTP servlet response
     * @param filterChain filter chain execution handle
     * @throws ServletException in case of servlet errors
     * @throws IOException      in case of I/O errors
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestUri = resolveRequestUri(request);
        String httpMethod = request.getMethod();

        Optional<ApiPermission> permissionOpt = ApiPermission.findMatch(requestUri, httpMethod);

        // 1. No matching permission definition → non-existent endpoint (404 NOT_FOUND)
        if (permissionOpt.isEmpty()) {
            writeErrorResponse(response, ErrorCode.RESOURCE_NOT_FOUND);
            return;
        }

        ApiPermission permission = permissionOpt.get();

        // 2. Public endpoint → allow without authentication
        if (permission.isPublic()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Protected endpoint → require authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            writeErrorResponse(response, ErrorCode.UNAUTHENTICATED);
            return;
        }

        // 4. Check role authorization
        Set<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.startsWith("ROLE_") ? authority.substring(5) : authority)
                .collect(Collectors.toSet());

        if (!permission.isAccessibleBy(userRoles)) {
            log.warn("Access denied for principal [{}] requesting [{} {}] with roles {}",
                    authentication.getPrincipal(), httpMethod, requestUri, userRoles);
            writeErrorResponse(response, ErrorCode.ACCESS_DENIED);
            return;
        }

        filterChain.doFilter(request, response);
    }


    /**
     * Resolves the effective request URI by stripping the context path prefix if present.
     *
     * @param request incoming HTTP servlet request
     * @return URI path without context path
     */
    private String resolveRequestUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    /**
     * Writes a standardized error response JSON payload directly to the response output stream.
     *
     * @param response  HTTP servlet response
     * @param errorCode error code enum specifying HTTP status and error details
     * @throws IOException in case of output stream write errors
     */
    private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponseBody = ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getDetail())
                .build();

        objectMapper.writeValue(response.getWriter(), errorResponseBody);
    }
}
