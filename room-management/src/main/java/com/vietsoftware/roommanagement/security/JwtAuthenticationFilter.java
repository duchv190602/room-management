package com.vietsoftware.roommanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietsoftware.roommanagement.dto.JwtPayload;
import com.vietsoftware.roommanagement.dto.response.ErrorResponse;
import com.vietsoftware.roommanagement.enums.ApiPermission;
import com.vietsoftware.roommanagement.exception.ErrorCode;
import com.vietsoftware.roommanagement.repository.IInvalidatedTokenRepository;
import com.vietsoftware.roommanagement.service.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Filter that intercepts each incoming HTTP request once to validate the JWT access token.
 *
 * <p>Processing steps:
 * <ol>
 *   <li>Check whether the requested URI + HTTP method exists in {@link ApiPermission}.
 *       If no permission matches, returns 404 NOT_FOUND immediately (before processing JWT).</li>
 *   <li>Extract {@code Bearer} token from the {@code Authorization} header.</li>
 *   <li>Parse all JWT claims in a single cryptographic operation via {@link JwtTokenProvider#extractAllClaims}.</li>
 *   <li>Check whether the token's {@code jti} exists in the invalidated tokens blacklist (post-logout protection).
 *       If blacklisted, rejects immediately with 401.</li>
 *   <li>If valid and not blacklisted, build a {@link UsernamePasswordAuthenticationToken} and set {@link SecurityContextHolder}.</li>
 * </ol>
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final IInvalidatedTokenRepository invalidatedTokenRepository;
    private final ObjectMapper objectMapper;

    /**
     * Validates and processes the JWT token on each request.
     *
     * @param request     incoming HTTP servlet request
     * @param response    outgoing HTTP servlet response
     * @param filterChain downstream filter chain
     * @throws ServletException in case of servlet processing errors
     * @throws IOException      in case of I/O errors
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestUri = resolveRequestUri(request);
        Optional<ApiPermission> permissionOpt = ApiPermission.findMatch(requestUri, request.getMethod());

        // 1. Check 404 FIRST before checking JWT token
        if (permissionOpt.isEmpty()) {
            log.debug("No ApiPermission matched for [{} {}] — returning 404 NOT_FOUND", request.getMethod(), requestUri);
            writeErrorResponse(response, ErrorCode.RESOURCE_NOT_FOUND);
            return;
        }

        // 2. Validate JWT token after confirming endpoint exists
        String bearerToken = JwtTokenProvider.extractBearerToken(request);

        if (StringUtils.hasText(bearerToken)) {
            try {
                JwtPayload payload = jwtTokenProvider.extractAllClaims(bearerToken);

                // Reject blacklisted tokens immediately with 401
                if (invalidatedTokenRepository.existsByJti(payload.getJti())) {
                    writeErrorResponse(response, ErrorCode.INVALID_TOKEN);
                    return;
                }

                List<SimpleGrantedAuthority> authorities = payload.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(payload.getUserId().toString(), payload.getUsername(), authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException e) {
                SecurityContextHolder.clearContext();
                writeErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
                return;
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
                writeErrorResponse(response, ErrorCode.INVALID_TOKEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Skips this filter for public endpoints defined in {@link ApiPermission}.
     * Uses {@link ApiPermission} as the single source of truth for public endpoint definitions.
     *
     * @param request incoming HTTP servlet request
     * @return {@code true} if the request URI matches any public endpoint, {@code false} otherwise
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {

        String requestUri = resolveRequestUri(request);
        return ApiPermission.findMatch(requestUri, request.getMethod())
                .map(ApiPermission::isPublic)
                .orElse(false);
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
        if (StringUtils.hasText(contextPath) && uri.startsWith(contextPath)) {
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
